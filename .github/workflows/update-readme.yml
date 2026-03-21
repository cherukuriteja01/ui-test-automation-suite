name: Auto Update README

on:
  push:
    branches: [ main ]

permissions:
  contents: write

jobs:
  update-readme:
    runs-on: ubuntu-latest

    steps:

      - name: Checkout repository
        uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Collect stats from source code
        id: stats
        run: |
          TESTS=$(grep -r "@Test" src/test/java --include="*.java" | wc -l | tr -d ' ')
          echo "tests=$TESTS" >> $GITHUB_OUTPUT
          FILES=$(find src/test/java -name "*Test.java" | wc -l | tr -d ' ')
          echo "files=$FILES" >> $GITHUB_OUTPUT
          echo "date=$(git log -1 --format='%d %b %Y')" >> $GITHUB_OUTPUT
          echo "commit=$(git log -1 --pretty=%B | head -1)" >> $GITHUB_OUTPUT

      - name: Detect day modules
        id: days
        run: |
          check() {
            [ -f "src/test/java/org/example/tests/$1" ] && echo "Complete" || echo "Coming Soon"
          }
          echo "login=$(check LoginTest.java)"         >> $GITHUB_OUTPUT
          echo "signup=$(check SignupTest.java)"       >> $GITHUB_OUTPUT
          echo "inventory=$(check InventoryTest.java)" >> $GITHUB_OUTPUT
          echo "cart=$(check CartTest.java)"           >> $GITHUB_OUTPUT
          echo "checkout=$(check CheckoutTest.java)"   >> $GITHUB_OUTPUT

      - name: Generate README
        env:
          TESTS:     ${{ steps.stats.outputs.tests }}
          FILES:     ${{ steps.stats.outputs.files }}
          DATE:      ${{ steps.stats.outputs.date }}
          COMMIT:    ${{ steps.stats.outputs.commit }}
          LOGIN:     ${{ steps.days.outputs.login }}
          SIGNUP:    ${{ steps.days.outputs.signup }}
          INVENTORY: ${{ steps.days.outputs.inventory }}
          CART:      ${{ steps.days.outputs.cart }}
          CHECKOUT:  ${{ steps.days.outputs.checkout }}
        run: |
          cat > README.md << 'EOF'
          # UI Test Automation Suite
          EOF

          python3 << PYEOF
import os

tests     = os.environ.get("TESTS", "0")
files     = os.environ.get("FILES", "0")
date      = os.environ.get("DATE", "")
commit    = os.environ.get("COMMIT", "")
login     = os.environ.get("LOGIN", "Coming Soon")
signup    = os.environ.get("SIGNUP", "Coming Soon")
inventory = os.environ.get("INVENTORY", "Coming Soon")
cart      = os.environ.get("CART", "Coming Soon")
checkout  = os.environ.get("CHECKOUT", "Coming Soon")

content = f"""# UI Test Automation Suite

![Java](https://img.shields.io/badge/Java-17-orange?logo=java)
![Selenium](https://img.shields.io/badge/Selenium-4.21.0-43B02A?logo=selenium)
![TestNG](https://img.shields.io/badge/TestNG-7.10.2-red)
![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?logo=apachemaven)
![Tests](https://img.shields.io/badge/Tests-{tests}%20Written-brightgreen)
![Updated](https://img.shields.io/badge/Last%20Updated-{date.replace(' ', '%20')}-blue)

> Automated UI test suite for [saucedemo.com](https://www.saucedemo.com)
> built with **Selenium WebDriver**, **TestNG**, and **Page Object Model**.
> Currently **{tests} tests** across **{files} test files** — new modules added daily.

---

## About

This project demonstrates a professional UI test automation framework built from scratch.

- **Page Object Model (POM)** — separates test logic from page interactions
- **Base Test class** — driver setup, session recovery, failure screenshots
- **Slow-motion execution** — colour-coded highlights, keystroke-by-keystroke typing
- **Automatic failure screenshots** — saved on every test failure
- **Headless support** — runs in CI with `-Dheadless=true`
- **Auto README** — updates automatically on every push via GitHub Actions

---

## Tech Stack

| Tool | Version | Purpose |
|------|---------|---------|
| Java | 17 | Programming language |
| Selenium WebDriver | 4.21.0 | Browser automation |
| TestNG | 7.10.2 | Test framework |
| Maven | 3.8+ | Build and dependency management |
| ChromeDriver | Auto-managed | Chrome browser driver |
| GitHub Actions | — | CI / Auto README updates |

---

## Project Structure

```
ui-test-automation-suite/
│
├── .github/workflows/
│   └── update-readme.yml      # Auto-updates README on every push
│
├── src/test/java/org/example/
│   ├── base/
│   │   └── BaseTest.java      # Driver setup, session recovery, screenshots
│   ├── pages/                 # Page Object Model classes
│   │   └── LoginPage.java
│   └── tests/                 # TestNG test classes
│       └── LoginTest.java
│
├── src/test/resources/
│   └── testng.xml
├── .gitignore
├── pom.xml
└── README.md                  # Auto-generated — do not edit manually
```

---

## Test Scenarios

### Day 1 — Login Tests

| # | Test | Scenario | Expected |
|---|------|----------|---------|
| 1 | lockedUserTest | Locked-out user | Error: locked out |
| 2 | emptyUsernameTest | Empty username | Error: Username is required |
| 3 | emptyPasswordTest | Empty password | Error: Password is required |
| 4 | invalidLoginTest | Wrong user + wrong pass | Error: do not match |
| 5 | wrongPasswordTest | Valid user + wrong pass | Error: do not match |
| 6 | wrongUsernameTest | Wrong user + valid pass | Error: do not match |
| 7 | validLoginTest | Correct credentials (last) | Inventory page |

---

## Getting Started

```bash
git clone https://github.com/cherukuriteja01/ui-test-automation-suite.git
cd ui-test-automation-suite
mvn clean install -DskipTests
```

---

## How to Run

```bash
# All tests — browser visible
mvn test

# Headless — no browser window
mvn test -Dheadless=true

# Single test method
mvn test -Dtest=LoginTest#validLoginTest
```

---

## Visual Indicators

| Colour | Meaning |
|--------|---------|
| Orange border | Field located — about to interact |
| Green border | Input typed successfully |
| Blue border | Button about to be clicked |
| Red border | Error message appeared |
| Green tint | Success — page loaded |

---

## Roadmap

| Day | Module | Status |
|-----|--------|--------|
| Day 1 | Login Tests | {login} |
| Day 2 | Signup Tests | {signup} |
| Day 3 | Inventory Tests | {inventory} |
| Day 4 | Cart Tests | {cart} |
| Day 5 | Checkout Tests | {checkout} |

---

## Auto-Generated

This README is automatically regenerated by GitHub Actions on every push.

| | |
|-|-|
| Last updated | {date} |
| Last commit | {commit} |
| Total tests | {tests} |
| Test files | {files} |

---

**Author: Teja**
GitHub: [cherukuriteja01](https://github.com/cherukuriteja01)

> Star this repo if you find it useful — more modules added daily!
"""

with open("README.md", "w") as f:
    f.write(content)

print("README.md generated successfully")
PYEOF

      - name: Commit and push README
        run: |
          git config user.name  "github-actions[bot]"
          git config user.email "github-actions[bot]@users.noreply.github.com"
          git add README.md
          git diff --staged --quiet && echo "No changes to commit" || (
            git commit -m "Auto README update — ${{ steps.stats.outputs.tests }} tests | ${{ steps.stats.outputs.date }}" &&
            git push
          )
