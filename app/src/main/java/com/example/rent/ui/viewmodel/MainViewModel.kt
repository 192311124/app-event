package com.example.rent.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rent.data.model.*
import com.example.rent.data.repository.DbRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val dbRepo = DbRepository()

    private val _currentRoute = MutableStateFlow("onboarding")
    val currentRoute: StateFlow<String> = _currentRoute.asStateFlow()

    private val _routeParams = MutableStateFlow<Map<String, Any>>(emptyMap())
    val routeParams: StateFlow<Map<String, Any>> = _routeParams.asStateFlow()

    private val _currentLocation = MutableStateFlow("Hyderabad")
    val currentLocation: StateFlow<String> = _currentLocation.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _categories = MutableStateFlow<List<EventCategory>>(emptyList())
    val categories: StateFlow<List<EventCategory>> = _categories.asStateFlow()

    private val _items = MutableStateFlow<List<RentalItem>>(emptyList())
    val items: StateFlow<List<RentalItem>> = _items.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _wishlistIds = MutableStateFlow<List<String>>(emptyList())
    val wishlistIds: StateFlow<List<String>> = _wishlistIds.asStateFlow()

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val backStack = mutableListOf<Pair<String, Map<String, Any>>>()

    init {
        loadCatalogData()
    }

    fun navigateTo(route: String, params: Map<String, Any> = emptyMap()) {
        if (route == "back") {
            goBack()
            return
        }
        if (_currentRoute.value != route && _currentRoute.value != "onboarding") {
            backStack.add(Pair(_currentRoute.value, _routeParams.value))
        }
        _currentRoute.value = route
        _routeParams.value = params
    }

    fun goBack(): Boolean {
        if (backStack.isNotEmpty()) {
            val previous = backStack.removeAt(backStack.size - 1)
            _currentRoute.value = previous.first
            _routeParams.value = previous.second
            return true
        } else if (_currentRoute.value != "home" && _currentRoute.value != "onboarding") {
            _currentRoute.value = "home"
            _routeParams.value = emptyMap()
            return true
        }
        return false
    }

    fun setLocation(loc: String, userId: String?) {
        _currentLocation.value = loc
        if (userId != null) {
            viewModelScope.launch {
                dbRepo.updateUserProfile(userId, mapOf("location" to loc))
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun loadCatalogData() {
        viewModelScope.launch {
            _isLoading.value = true
            _categories.value = dbRepo.getCategories()
            _items.value = dbRepo.getItems()
            _isLoading.value = false
        }
    }

    fun refreshUserData(userId: String) {
        viewModelScope.launch {
            _userProfile.value = dbRepo.getUserProfile(userId)
            val profile = _userProfile.value
            if (profile != null && profile.location.isNotEmpty()) {
                _currentLocation.value = profile.location
            }
            _cartItems.value = dbRepo.getCart(userId)
            _wishlistIds.value = dbRepo.getWishlist(userId)
            _bookings.value = dbRepo.getBookings(userId)
            _notifications.value = dbRepo.getNotifications(userId)
        }
    }

    private fun getActiveUserId(passedId: String?): String? {
        if (!passedId.isNullOrBlank()) return passedId
        return _userProfile.value?.userId
            ?: com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
    }

    fun toggleWishlist(userId: String?, itemId: String) {
        val uid = getActiveUserId(userId)
        if (uid == null) {
            showToast("Please sign in to save items.")
            navigateTo("auth", mapOf("initialMode" to "signin"))
            return
        }
        viewModelScope.launch {
            _wishlistIds.value = dbRepo.toggleWishlist(uid, itemId)
            val isSaved = _wishlistIds.value.contains(itemId)
            showToast(if (isSaved) "Item saved to wishlist!" else "Item removed from wishlist.")
        }
    }

    fun addToCart(userId: String?, item: RentalItem, quantity: Int, date: String, time: String) {
        val uid = getActiveUserId(userId)
        if (uid == null) {
            showToast("Please sign in to add items to cart.")
            navigateTo("auth", mapOf("initialMode" to "signin"))
            return
        }
        viewModelScope.launch {
            dbRepo.addToCart(uid, item, quantity, date, time)
            _cartItems.value = dbRepo.getCart(uid)
            showToast("Added ${item.name} to cart!")
        }
    }

    fun updateCartQuantity(userId: String?, docId: String, quantity: Int) {
        val uid = getActiveUserId(userId) ?: return
        viewModelScope.launch {
            dbRepo.updateCartQuantity(docId, quantity)
            _cartItems.value = dbRepo.getCart(uid)
        }
    }

    fun removeFromCart(userId: String?, docId: String) {
        val uid = getActiveUserId(userId) ?: return
        viewModelScope.launch {
            dbRepo.removeFromCart(docId)
            _cartItems.value = dbRepo.getCart(uid)
            showToast("Item removed from cart.")
        }
    }

    fun clearCart(userId: String?) {
        val uid = getActiveUserId(userId) ?: return
        viewModelScope.launch {
            dbRepo.clearCart(uid)
            _cartItems.value = emptyList()
        }
    }

    fun addBooking(userId: String?, bookingData: Map<String, Any>, onSuccess: (String) -> Unit) {
        val uid = getActiveUserId(userId) ?: return
        viewModelScope.launch {
            val bookingId = dbRepo.addBooking(uid, bookingData)
            clearCart(uid)
            refreshUserData(uid)
            onSuccess(bookingId)
        }
    }

    fun cancelBooking(userId: String?, bookingId: String) {
        val uid = getActiveUserId(userId) ?: return
        viewModelScope.launch {
            dbRepo.cancelBooking(uid, bookingId)
            showToast("Booking cancelled. Refund in ₹ initiated.")
            refreshUserData(uid)
        }
    }
}
