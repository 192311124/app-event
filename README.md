# Enterprise Appium E2E Automation Framework for Android Application (Node.js)

An enterprise-grade, scalable, and maintainable End-to-End (E2E) mobile automation framework for Android applications built with **Node.js**, **Appium 2.x**, **UiAutomator2 Driver**, **WebdriverIO**, **Mocha**, **Chai**, **Winston Logger**, **ExcelJS**, and **GitHub Actions**.

---

## 🏛️ Technology Stack & Architecture

- **Language**: JavaScript (ES6+)
- **Runtime**: Node.js v18+ / v20+ / v22+
- **Automation Tool**: Appium 2.x
- **Mobile Automation**: UiAutomator2 Driver (`appium-uiautomator2-driver`)
- **Test Runner**: Mocha (BDD style)
- **Assertion Library**: Chai
- **Reporting**:
  - **Mochawesome** (Interactive HTML & JSON reports)
  - **ExcelJS** (4-Sheet formatted executive workbook: `Mobile_E2E_Report.xlsx`)
- **Logging**: Winston (Console & file appenders: `execution.log`, `error.log`)
- **CI/CD**: GitHub Actions (`.github/workflows/appium-e2e.yml`)
- **Architecture**: Page Object Model (POM) with component delegation & Smart AI screen discovery

---

## 📁 Project Structure

```
.
├── config/
│   ├── appium.config.js          # Dynamic Appium capability provider (APK vs Installed App)
│   └── env.config.js             # Environment variable loader (.env)
├── drivers/
│   └── driver.factory.js         # Dynamic device detection (ADB) & WebdriverIO session manager
├── utilities/
│   ├── logger.js                 # Winston logger with in-memory step buffer for Excel report
│   ├── wait.util.js               # Explicit wait abstractions & element state sync
│   ├── gesture.util.js            # W3C gesture engine (Tap, Double Tap, Long Press, Swipe, Scroll, Drag, Pinch, Zoom)
│   ├── device.util.js             # Logcat logs, current activity, alerts, back button, relaunch, deep link
│   ├── screenshot.util.js         # Failure screenshot manager
│   ├── performance.util.js        # Cold launch time, screen render time & metric tracer
│   ├── retry.util.js              # Resilient operation retry wrapper
│   ├── smart.analyzer.js          # AI screen scanner, form discovery & dynamic test case generator
│   └── excel.reporter.js          # 4-sheet Excel report generator (Summary, Test Cases, Failed Tests, Execution Logs)
├── pages/
│   ├── base.page.js               # Core POM parent class
│   ├── login.page.js              # Authentication POM
│   ├── dashboard.page.js          # Dashboard & catalog POM
│   ├── form.page.js               # Registration & validation POM
│   └── components/
│       ├── navigation.drawer.js   # Side hamburger drawer component
│       ├── bottom.nav.js          # Bottom navigation bar component
│       └── dialog.component.js    # Alerts, dialogs, toasts, snackbars component
├── testdata/
│   ├── user.data.js               # Authentication test data sets
│   └── form.data.js               # Form input validation boundary sets
├── tests/
│   ├── setup/
│   │   └── base.test.js          # Mocha global before/beforeEach/afterEach/after hooks
│   └── e2e/
│       ├── auth.spec.js           # Authentication E2E test suite
│       ├── form_validation.spec.js# Form validation E2E test suite
│       ├── ui_components.spec.js  # Mobile UI elements & dialogs test suite
│       ├── gestures.spec.js       # W3C Touch gestures test suite
│       ├── navigation.spec.js     # Nav bar, drawer, back button & deep link test suite
│       ├── performance.spec.js    # App launch timing & latency suite
│       └── smart_ai_discovery.spec.js # Smart AI screen analyzer suite
├── .github/
│   └── workflows/
│       └── appium-e2e.yml        # GitHub Actions CI/CD workflow
├── .env.example                   # Environment defaults template
├── .mocharc.json                  # Mocha test runner options
├── package.json                   # Dependencies & npm run scripts
└── README.md                      # Framework documentation
```

---

## ⚡ Prerequisites & Setup

1. **Node.js**: Install Node.js v18+ / v20+ / v22+.
2. **Java JDK**: Install Java JDK 11 or 17 (`JAVA_HOME` configured).
3. **Android SDK**: Install Android Studio & SDK Platform Tools (`ANDROID_HOME` configured).
4. **Appium 2.x**:
   ```bash
   npm install -g appium
   appium driver install uiautomator2
   ```

### Installation

```bash
npm install
```

Create your local `.env` file:
```bash
cp .env.example .env
```

---

## ⚙️ Application Launch Configuration

The framework supports two modes of execution configured in `.env`:

### 1. Installed Application Mode
```env
APP_LAUNCH_TYPE=installed
APP_PACKAGE=com.example.rent
APP_ACTIVITY=com.example.rent.MainActivity
```

### 2. APK File Execution Mode
```env
APP_LAUNCH_TYPE=apk
APK_PATH=./app/build/outputs/apk/debug/app-debug.apk
```

---

## 🚀 Execution Commands

Start the Appium server in a separate terminal:
```bash
appium --port 4723
```

Execute tests using npm scripts:

| Command | Description |
|---|---|
| `npm test` | Run all E2E test suites |
| `npm run test:auth` | Run Authentication tests |
| `npm run test:form` | Run Form Validation tests |
| `npm run test:ui` | Run Mobile UI components tests |
| `npm run test:gestures` | Run W3C Gesture automation tests |
| `npm run test:nav` | Run Navigation & Deep Link tests |
| `npm run test:perf` | Run Performance & Launch timing tests |
| `npm run test:smart` | Run Smart AI Screen Discovery tests |
| `npm run report:excel` | Re-generate 4-Sheet Excel report |
| `npm run clean` | Clean reports, logs, and screenshots |

---

## 📊 Reports & Artifacts

### 1. Custom 4-Sheet Excel Report (`Mobile_E2E_Report.xlsx`)
Saved under `excel/Mobile_E2E_Report.xlsx`:
- **Sheet 1 - Summary**: Date, Device Name, OS Version, Pass/Fail Counts, Pass %, Duration.
- **Sheet 2 - Test Cases**: Test ID, Module, Scenario, Device, Status, Start/End Time, Duration.
- **Sheet 3 - Failed Tests**: Test Name, Failure Reason, Screenshot Path, Device, OS Version, Activity Name.
- **Sheet 4 - Execution Logs**: Step-by-step timestamped execution trace for auditability.

### 2. Mochawesome HTML Report
Generated under `reports/mochawesome/mobile-e2e-report.html` with interactive charts, execution metrics, and stack traces.

### 3. Failure Artifacts
Automatically saved under `reports/failures/`:
- Timestamped failure screenshots (`FAIL_<Test_Title>_<Timestamp>.png`)
- Full device `logcat` logs (`logcat_<Test_Title>_<Timestamp>.log`)
- Current top Android Activity name.

---

## 🤖 Smart Testing Engine (`smart.analyzer.js`)

Designed for AI subagents and automated coverage expansion:
- Scans current active Android screen DOM source XML.
- Categorizes text fields, buttons, radio buttons, checkboxes, dialogs, and RecyclerViews.
- Automatically discovers input forms.
- Generates boundary validation scenarios dynamically (empty inputs, maximum length, XSS/special characters).

---

## 🌐 GitHub Actions CI/CD Pipeline

Configured in `.github/workflows/appium-e2e.yml`:
1. Checks out repository (`actions/checkout@v4`).
2. Configures Node.js (`actions/setup-node@v4` Node 22).
3. Configures Java 17 (`actions/setup-java@v4`).
4. Launches Android Emulator (API 33, Google APIs).
5. Installs Appium 2.x and UiAutomator2 driver.
6. Executes E2E test suites.
7. Generates Mochawesome and 4-sheet Excel reports.
8. Uploads test artifacts (`actions/upload-artifact@v4`).
