package com.example.rent.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.example.rent.BuildConfig
import com.example.rent.ui.screens.DecorDesignModel
import com.example.rent.ui.screens.VenueAnalysisResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object GeminiImageAnalyzer {

    // Configure your Gemini API Key here or in local.properties
    var GEMINI_API_KEY: String = BuildConfig.GEMINI_API_KEY

    suspend fun analyzeImageWithGemini(
        context: Context,
        imageUri: Uri,
        apiKey: String = GEMINI_API_KEY
    ): VenueAnalysisResult? = withContext(Dispatchers.IO) {
        val keyToUse = if (apiKey.isNotBlank()) apiKey else GEMINI_API_KEY
        if (keyToUse.isBlank()) return@withContext null

        try {
            val base64Image = getBase64ImageFromUri(context, imageUri) ?: return@withContext null

            // Construct Gemini Vision API request payload
            val promptText = """
                You are an expert event venue and decor visual analyzer. Analyze this uploaded photo and return ONLY raw JSON (no markdown ticks) with this exact schema:
                {
                  "venueType": "string title describing venue category with emoji",
                  "spatialFeatures": "Detailed AI analysis of spatial clearance, lighting, ceiling height, and decor placement points detected in photo",
                  "designModels": [
                    {
                      "id": "model_1",
                      "title": "Model A: Theme Title",
                      "theme": "Theme Style Name",
                      "matchPercentage": "98% AI Match",
                      "budget": "₹20,000 - ₹35,000",
                      "description": "2 sentence description of setup details",
                      "recommendedItems": ["Item 1", "Item 2", "Item 3", "Item 4"],
                      "imageUrl": "https://images.unsplash.com/photo-1545232979-fbfd42e000b9?auto=format&fit=crop&w=600&q=80"
                    },
                    {
                      "id": "model_2",
                      "title": "Model B: Theme Title",
                      "theme": "Theme Style Name",
                      "matchPercentage": "94% AI Match",
                      "budget": "₹30,000 - ₹45,000",
                      "description": "2 sentence description of setup details",
                      "recommendedItems": ["Item 1", "Item 2", "Item 3", "Item 4"],
                      "imageUrl": "https://images.unsplash.com/photo-1527529482837-4698179dc6ce?auto=format&fit=crop&w=600&q=80"
                    },
                    {
                      "id": "model_3",
                      "title": "Model C: Theme Title",
                      "theme": "Theme Style Name",
                      "matchPercentage": "91% AI Match",
                      "budget": "₹15,000 - ₹25,000",
                      "description": "2 sentence description of setup details",
                      "recommendedItems": ["Item 1", "Item 2", "Item 3", "Item 4"],
                      "imageUrl": "https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=600&q=80"
                    }
                  ]
                }
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contentsArray = org.json.JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = org.json.JSONArray().apply {
                            put(JSONObject().put("text", promptText))
                            put(JSONObject().put("inline_data", JSONObject().apply {
                                put("mime_type", "image/jpeg")
                                put("data", base64Image)
                            }))
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)
            }

            val endpoints = listOf(
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$keyToUse",
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$keyToUse"
            )

            var responseJsonStr: String? = null

            for (endpointUrl in endpoints) {
                try {
                    val url = URL(endpointUrl)
                    val conn = (url.openConnection() as HttpURLConnection).apply {
                        requestMethod = "POST"
                        setRequestProperty("Content-Type", "application/json")
                        doOutput = true
                        connectTimeout = 15000
                        readTimeout = 15000
                    }

                    conn.outputStream.use { os ->
                        os.write(requestJson.toString().toByteArray(Charsets.UTF_8))
                    }

                    if (conn.responseCode == 200) {
                        responseJsonStr = conn.inputStream.bufferedReader().use { it.readText() }
                        break
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            if (responseJsonStr == null) return@withContext null

            // Parse Gemini AI Response
            val rootObj = JSONObject(responseJsonStr)
            val candidates = rootObj.optJSONArray("candidates") ?: return@withContext null
            if (candidates.length() == 0) return@withContext null
            val contentObj = candidates.getJSONObject(0).optJSONObject("content") ?: return@withContext null
            val parts = contentObj.optJSONArray("parts") ?: return@withContext null
            if (parts.length() == 0) return@withContext null
            
            var textResult = parts.getJSONObject(0).optString("text", "")
            textResult = textResult.replace("```json", "").replace("```", "").trim()

            val aiResultObj = JSONObject(textResult)
            val venueType = aiResultObj.optString("venueType", "Real-Time AI Venue Analysis ✨")
            val spatialFeatures = aiResultObj.optString("spatialFeatures", "Real-time Gemini Vision analyzed venue structure and lighting.")
            val modelsArray = aiResultObj.optJSONArray("designModels") ?: org.json.JSONArray()

            val designModels = mutableListOf<DecorDesignModel>()
            for (i in 0 until modelsArray.length()) {
                val mObj = modelsArray.getJSONObject(i)
                val itemsArr = mObj.optJSONArray("recommendedItems") ?: org.json.JSONArray()
                val itemsList = mutableListOf<String>()
                for (j in 0 until itemsArr.length()) {
                    itemsList.add(itemsArr.getString(j))
                }
                designModels.add(
                    DecorDesignModel(
                        id = mObj.optString("id", "model_$i"),
                        title = mObj.optString("title", "AI Recommended Design ${i + 1}"),
                        theme = mObj.optString("theme", "AI Custom Theme"),
                        matchPercentage = mObj.optString("matchPercentage", "95% AI Match"),
                        budget = mObj.optString("budget", "₹20,000 - ₹35,000"),
                        description = mObj.optString("description", "Custom visual design proposed by Gemini AI engine."),
                        recommendedItems = if (itemsList.isNotEmpty()) itemsList else listOf("Decor Backdrop", "Focus Lighting", "Seating Accent"),
                        imageUrl = mObj.optString("imageUrl", "https://images.unsplash.com/photo-1545232979-fbfd42e000b9?auto=format&fit=crop&w=600&q=80")
                    )
                )
            }

            if (designModels.isEmpty()) return@withContext null

            return@withContext VenueAnalysisResult(
                venueType = venueType,
                spatialFeatures = spatialFeatures,
                designModels = designModels
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    private fun getBase64ImageFromUri(context: Context, uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream) ?: return null
            val resizedBitmap = resizeBitmap(bitmap, 1024)
            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            val byteArray = outputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun resizeBitmap(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDimension && height <= maxDimension) return bitmap

        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int
        if (width > height) {
            newWidth = maxDimension
            newHeight = (maxDimension / ratio).toInt()
        } else {
            newHeight = maxDimension
            newWidth = (maxDimension * ratio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
