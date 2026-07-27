const logger = require('./logger');

class SmartAnalyzer {
  async analyzeCurrentScreen() {
    logger.info('=== [Smart AI Analyzer] Scanning current screen UI components ===');
    const sourceXml = await browser.getPageSource();

    const analysis = {
      timestamp: new Date().toISOString(),
      activity: await browser.getCurrentActivity().catch(() => 'UnknownActivity'),
      textFields: [],
      buttons: [],
      checkboxes: [],
      radioButtons: [],
      dropdowns: [],
      recyclerViews: [],
      dialogs: [],
      detectedForms: [],
      suggestedValidationScenarios: [],
    };

    const textViews = await browser.$$('//android.widget.EditText');
    for (const input of textViews) {
      const resourceId = await input.getAttribute('resource-id').catch(() => '');
      const text = await input.getText().catch(() => '');
      const hint = await input.getAttribute('content-desc').catch(() => '');
      analysis.textFields.push({ resourceId, text, hint });
    }

    const buttonEls = await browser.$$('//android.widget.Button | //*[contains(@class, "Button")]');
    for (const btn of buttonEls) {
      const resourceId = await btn.getAttribute('resource-id').catch(() => '');
      const text = await btn.getText().catch(() => '');
      analysis.buttons.push({ resourceId, text });
    }

    const checkEls = await browser.$$('//android.widget.CheckBox');
    for (const chk of checkEls) {
      const resourceId = await chk.getAttribute('resource-id').catch(() => '');
      const checked = await chk.getAttribute('checked').catch(() => 'false');
      analysis.checkboxes.push({ resourceId, checked });
    }

    const recyclerEls = await browser.$$('//androidx.recyclerview.widget.RecyclerView');
    for (const r of recyclerEls) {
      const resourceId = await r.getAttribute('resource-id').catch(() => '');
      analysis.recyclerViews.push({ resourceId });
    }

    if (analysis.textFields.length > 0) {
      analysis.detectedForms.push({
        formId: `Form_${analysis.activity}`,
        fieldsCount: analysis.textFields.length,
        hasSubmitButton: analysis.buttons.length > 0,
        fields: analysis.textFields,
      });

      analysis.textFields.forEach((field, idx) => {
        const fieldName = field.resourceId || `field_${idx}`;
        analysis.suggestedValidationScenarios.push({
          scenarioId: `TC_AUTO_VAL_${fieldName.replace(/[^a-zA-Z0-9]/g, '_')}_EMPTY`,
          targetField: fieldName,
          testInput: '',
          expectedRule: 'Required Field Validation',
        });
        analysis.suggestedValidationScenarios.push({
          scenarioId: `TC_AUTO_VAL_${fieldName.replace(/[^a-zA-Z0-9]/g, '_')}_MAX_LENGTH`,
          targetField: fieldName,
          testInput: 'A'.repeat(256),
          expectedRule: 'Max Length Boundary Validation',
        });
        analysis.suggestedValidationScenarios.push({
          scenarioId: `TC_AUTO_VAL_${fieldName.replace(/[^a-zA-Z0-9]/g, '_')}_SPECIAL_CHARS`,
          targetField: fieldName,
          testInput: `<script>alert('xss')</script>' OR '1'='1`,
          expectedRule: 'Sanitization & Special Character Validation',
        });
      });
    }

    logger.info(`[Smart AI Analyzer] Screen Analysis Complete:
    - Text Fields Detected: ${analysis.textFields.length}
    - Buttons Detected: ${analysis.buttons.length}
    - Forms Discovered: ${analysis.detectedForms.length}
    - Generated Dynamic Scenarios: ${analysis.suggestedValidationScenarios.length}`);

    return analysis;
  }
}

module.exports = new SmartAnalyzer();
