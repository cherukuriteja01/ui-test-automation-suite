name: Auto Update README

on:
  push:
    branches: [ main ]
  workflow_dispatch:

permissions:
  contents: write

jobs:
  update-readme:
    runs-on: ubuntu-latest

    steps:

      - name: Checkout
        uses: actions/checkout@v4

      - name: Build README
        run: |
          TESTS=$(grep -r "@Test" src/test/java --include="*.java" 2>/dev/null | wc -l | tr -d ' ')
          FILES=$(find src/test/java -name "*Test.java" 2>/dev/null | wc -l | tr -d ' ')
          DATE=$(date '+%d %b %Y')

          LOGIN="🔜 Coming"
          SIGNUP="🔜 Coming"
          INVENTORY="🔜 Coming"
          CART="🔜 Coming"
          CHECKOUT="🔜 Coming"

          [ -f "src/test/java/org/example/tests/LoginTest.java" ]     && LOGIN="✅ Complete"
          [ -f "src/test/java/org/example/tests/SignupTest.java" ]    && SIGNUP="✅ Complete"
          [ -f "src/test/java/org/example/tests/InventoryTest.java" ] && INVENTORY="✅ Complete"
          [ -f "src/test/java/org/example/tests/CartTest.java" ]      && CART="✅ Complete"
          [ -f "src/test/java/org/example/tests/CheckoutTest.java" ]  && CHECKOUT="✅ Complete"

          {
            echo "# UI Test Automation Suite"
            echo ""
            echo "![Java](https://img.shields.io/badge/Java-17-orange?logo=java)"
            echo "![Selenium](https://img.shields.io/badge/Selenium-4.21.0-43B02A?logo=selenium)"
            echo "![TestNG](https://img.shields.io/badge/TestNG-7.10.2-red)"
            echo "![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?logo=apachemaven)"
            echo "![Tests](https://img.shields.io/badge/Tests-${TESTS}%20Written-brightgreen)"
            echo ""
            echo "> Automated UI test suite for [saucedemo.com](https://www.saucedemo.com)"
            echo "> Built with **Selenium WebDriver**, **TestNG**, and **Page Object Model**."
            echo "> Currently **${TESTS} tests** across **${FILES} test files**."
            echo ""
            echo "---"
            echo ""
            echo "## About"
            echo ""
            echo "This project demonstrates a professional UI test automation framework."
            echo ""
            echo "- **Page Object Model (POM)** — separates test logic from page interactions"
            echo "- **Base Test class** — driver setup, session recovery, failure screenshots"
            echo "- **Slow-motion execution** — colour-coded highlights, character-by-character typing"
            echo "- **Headless support** — runs in CI with \`mvn test -Dheadless=true\`"
            echo "- **Auto README** — this file updates automatically on every push"
            echo ""
            echo "---"
            echo ""
            echo "## Tech Stack"
            echo ""
            echo "| Tool | Version | Purpose |"
            echo "|------|---------|---------|"
            echo "| Java | 17 | Programming language |"
            echo "| Selenium WebDriver | 4.21.0 | Browser automation |"
            echo "| TestNG | 7.10.2 | Test framework |"
            echo "| Maven | 3.8+ | Build and dependency management |"
            echo "| ChromeDriver | Auto-managed | Chrome browser driver |"
            echo "| GitHub Actions | — | CI / Auto README |"
            echo ""
            echo "---"
            echo ""
            echo "## Project Structure"
            echo ""
            echo "\`\`\`"
            echo "ui-test-automation-suite/"
            echo "├── .github/workflows/"
            echo "│   └── update-readme.yml"
            echo "├── src/test/java/org/example/"
            echo "│   ├── base/BaseTest.java"
            echo "│   ├── pages/LoginPage.java"
            echo "│   └── tests/LoginTest.java"
            echo "├── src/test/resources/testng.xml"
            echo "├── pom.xml"
            echo "└── README.md"
            echo "\`\`\`"
            echo ""
            echo "---"
            echo ""
            echo "## How to Run"
            echo ""
            echo "\`\`\`bash"
            echo "# Run all tests"
            echo "mvn test"
            echo ""
            echo "# Run headless"
            echo "mvn test -Dheadless=true"
            echo ""
            echo "# Run single test"
            echo "mvn test -Dtest=LoginTest#validLoginTest"
            echo "\`\`\`"
            echo ""
            echo "---"
            echo ""
            echo "## Test Scenarios"
            echo ""
            echo "### Day 1 — Login Tests"
            echo ""
            echo "| # | Test | Scenario | Expected |"
            echo "|---|------|----------|----------|"
            echo "| 1 | lockedUserTest | Locked-out user | Error: locked out |"
            echo "| 2 | emptyUsernameTest | Empty username | Error: Username is required |"
            echo "| 3 | emptyPasswordTest | Empty password | Error: Password is required |"
            echo "| 4 | invalidLoginTest | Wrong user + wrong pass | Error: do not match |"
            echo "| 5 | wrongPasswordTest | Valid user + wrong pass | Error: do not match |"
            echo "| 6 | wrongUsernameTest | Wrong user + valid pass | Error: do not match |"
            echo "| 7 | validLoginTest | Correct credentials | Inventory page ✅ |"
            echo ""
            echo "---"
            echo ""
            echo "## Roadmap"
            echo ""
            echo "| Day | Module | Status |"
            echo "|-----|--------|--------|"
            echo "| Day 1 | Login Tests | ${LOGIN} |"
            echo "| Day 2 | Signup Tests | ${SIGNUP} |"
            echo "| Day 3 | Inventory Tests | ${INVENTORY} |"
            echo "| Day 4 | Cart Tests | ${CART} |"
            echo "| Day 5 | Checkout Tests | ${CHECKOUT} |"
            echo ""
            echo "---"
            echo ""
            echo "## Auto-Generated"
            echo ""
            echo "| | |"
            echo "|-|-|"
            echo "| 📅 Last updated | ${DATE} |"
            echo "| 🧪 Total tests | ${TESTS} |"
            echo "| 📁 Test files | ${FILES} |"
            echo ""
            echo "---"
            echo ""
            echo "**Author: Teja**"
            echo "GitHub: [cherukuriteja01](https://github.com/cherukuriteja01)"
            echo ""
            echo "> ⭐ Star this repo — more modules added daily!"
          } > README.md

          echo "README generated — ${TESTS} tests found"

      - name: Commit and push
        run: |
          git config user.name  "github-actions[bot]"
          git config user.email "github-actions[bot]@users.noreply.github.com"
          git add README.md
          git diff --staged --quiet && echo "No changes" || (git commit -m "Auto README update" && git push)
