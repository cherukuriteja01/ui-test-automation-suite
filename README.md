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

      - name: Collect stats
        id: stats
        run: |
          TESTS=$(grep -r "@Test" src/test/java --include="*.java" | wc -l | tr -d ' ')
          FILES=$(find src/test/java -name "*Test.java" | wc -l | tr -d ' ')
          DATE=$(git log -1 --format='%d %b %Y')
          COMMIT=$(git log -1 --pretty=%B | head -1)
          echo "tests=$TESTS" >> $GITHUB_OUTPUT
          echo "files=$FILES" >> $GITHUB_OUTPUT
          echo "date=$DATE"   >> $GITHUB_OUTPUT
          echo "commit=$COMMIT" >> $GITHUB_OUTPUT

      - name: Detect modules
        id: days
        run: |
          LOGIN="Coming Soon"
          SIGNUP="Coming Soon"
          INVENTORY="Coming Soon"
          CART="Coming Soon"
          CHECKOUT="Coming Soon"
          [ -f "src/test/java/org/example/tests/LoginTest.java" ]     && LOGIN="Complete"
          [ -f "src/test/java/org/example/tests/SignupTest.java" ]    && SIGNUP="Complete"
          [ -f "src/test/java/org/example/tests/InventoryTest.java" ] && INVENTORY="Complete"
          [ -f "src/test/java/org/example/tests/CartTest.java" ]      && CART="Complete"
          [ -f "src/test/java/org/example/tests/CheckoutTest.java" ]  && CHECKOUT="Complete"
          echo "login=$LOGIN"         >> $GITHUB_OUTPUT
          echo "signup=$SIGNUP"       >> $GITHUB_OUTPUT
          echo "inventory=$INVENTORY" >> $GITHUB_OUTPUT
          echo "cart=$CART"           >> $GITHUB_OUTPUT
          echo "checkout=$CHECKOUT"   >> $GITHUB_OUTPUT

      - name: Generate README
        run: |
          TESTS="${{ steps.stats.outputs.tests }}"
          FILES="${{ steps.stats.outputs.files }}"
          DATE="${{ steps.stats.outputs.date }}"
          COMMIT="${{ steps.stats.outputs.commit }}"
          LOGIN="${{ steps.days.outputs.login }}"
          SIGNUP="${{ steps.days.outputs.signup }}"
          INVENTORY="${{ steps.days.outputs.inventory }}"
          CART="${{ steps.days.outputs.cart }}"
          CHECKOUT="${{ steps.days.outputs.checkout }}"

          python3 -c "
import os
tests     = '$TESTS'
files     = '$FILES'
date      = '$DATE'
commit    = '$COMMIT'
login     = '$LOGIN'
signup    = '$SIGNUP'
inventory = '$INVENTORY'
cart      = '$CART'
checkout  = '$CHECKOUT'

lines = []
lines.append('# UI Test Automation Suite')
lines.append('')
lines.append('![Java](https://img.shields.io/badge/Java-17-orange?logo=java)')
lines.append('![Selenium](https://img.shields.io/badge/Selenium-4.21.0-43B02A?logo=selenium)')
lines.append('![TestNG](https://img.shields.io/badge/TestNG-7.10.2-red)')
lines.append('![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?logo=apachemaven)')
lines.append('![Tests](https://img.shields.io/badge/Tests-' + tests + '%20Written-brightgreen)')
lines.append('')
lines.append('> Automated UI test suite for [saucedemo.com](https://www.saucedemo.com)')
lines.append('> Built with **Selenium WebDriver**, **TestNG**, and **Page Object Model**.')
lines.append('> Currently **' + tests + ' tests** across **' + files + ' test files**.')
lines.append('')
lines.append('---')
lines.append('')
lines.append('## About')
lines.append('')
lines.append('- Page Object Model (POM)')
lines.append('- Session recovery and failure screenshots')
lines.append('- Slow-motion execution with colour-coded highlights')
lines.append('- Headless support: \`mvn test -Dheadless=true\`')
lines.append('- Auto README via GitHub Actions')
lines.append('')
lines.append('---')
lines.append('')
lines.append('## Tech Stack')
lines.append('')
lines.append('| Tool | Version | Purpose |')
lines.append('|------|---------|---------|')
lines.append('| Java | 17 | Programming language |')
lines.append('| Selenium WebDriver | 4.21.0 | Browser automation |')
lines.append('| TestNG | 7.10.2 | Test framework |')
lines.append('| Maven | 3.8+ | Build and dependency management |')
lines.append('| GitHub Actions | — | CI / Auto README |')
lines.append('')
lines.append('---')
lines.append('')
lines.append('## How to Run')
lines.append('')
lines.append('\`\`\`bash')
lines.append('mvn test')
lines.append('mvn test -Dheadless=true')
lines.append('mvn test -Dtest=LoginTest#validLoginTest')
lines.append('\`\`\`')
lines.append('')
lines.append('---')
lines.append('')
lines.append('## Roadmap')
lines.append('')
lines.append('| Day | Module | Status |')
lines.append('|-----|--------|--------|')
lines.append('| Day 1 | Login Tests | ' + login + ' |')
lines.append('| Day 2 | Signup Tests | ' + signup + ' |')
lines.append('| Day 3 | Inventory Tests | ' + inventory + ' |')
lines.append('| Day 4 | Cart Tests | ' + cart + ' |')
lines.append('| Day 5 | Checkout Tests | ' + checkout + ' |')
lines.append('')
lines.append('---')
lines.append('')
lines.append('## Auto-Generated')
lines.append('')
lines.append('| | |')
lines.append('|-|-|')
lines.append('| Last updated | ' + date + ' |')
lines.append('| Last commit  | ' + commit + ' |')
lines.append('| Total tests  | ' + tests + ' |')
lines.append('| Test files   | ' + files + ' |')
lines.append('')
lines.append('---')
lines.append('')
lines.append('**Author: Teja** — [cherukuriteja01](https://github.com/cherukuriteja01)')
lines.append('')
lines.append('> Star this repo — more modules added daily!')

with open('README.md', 'w') as f:
    f.write('\n'.join(lines))

print('README.md generated OK — ' + tests + ' tests found')
"

      - name: Commit and push README
        run: |
          git config user.name  "github-actions[bot]"
          git config user.email "github-actions[bot]@users.noreply.github.com"
          git add README.md
          git diff --staged --quiet && echo "No changes" || (git commit -m "Auto README — ${{ steps.stats.outputs.tests }} tests | ${{ steps.stats.outputs.date }}" && git push)
