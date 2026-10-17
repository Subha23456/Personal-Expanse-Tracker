# Frontend test cases (manual)

Run the app, open http://localhost:8080. Start with an empty table, or load `db/seed.sql`.

| # | Scenario | Steps | Expected |
|---|---|---|---|
| 1 | Empty state | Open the app with no data | "No transactions yet…" message; cards show $0.00; category panel says no expenses |
| 2 | Loading state | In browser dev tools, throttle network to "Slow 3G", reload | "Loading transactions…" appears, then the table |
| 3 | Add expense | Type Expense, title "Coffee", amount 3.50, category Food, today | Toast "Transaction added"; row appears; expenses +3.50; balance −3.50 |
| 4 | Add income | Type Income, title "Salary", amount 1000 | Income and balance update |
| 5 | Required fields | Submit empty form | Inline errors for title, amount, category; no request sent |
| 6 | Amount rules | Enter 0, then −5 | "Amount must be greater than 0." |
| 7 | Edit | Click Edit on a row, change amount, Save changes | Form shows edit mode, row highlighted; after save totals and row update; form returns to Add mode |
| 8 | Cancel edit | Click Edit then Cancel edit | Form clears, highlight removed |
| 9 | Delete confirm | Click Delete, choose "Keep it" | Nothing deleted |
| 10 | Delete | Click Delete, confirm | Toast "Transaction deleted"; row gone; totals update |
| 11 | Delete while editing | Edit a row, then delete that same row | Form resets to Add mode |
| 12 | Type filter | Filter Type = Expense | Only expenses; filtered cards change; all-time line does not |
| 13 | Category filter | Pick a category | Only that category; breakdown only shows it |
| 14 | Date filter | Set From/To | Only rows within range (inclusive) |
| 15 | Reversed dates | From later than To | Message under filters; no request sent |
| 16 | Keyword | Type "groc" | Matches title or notes, case-insensitive, after a short pause |
| 17 | No matches | Search for "zzzz" | "No transactions match these filters." and scope label shows 0 of N |
| 18 | Clear filters | Click Clear filters | Everything returns; label says "All time" |
| 19 | Filtered vs all-time | Apply any filter | Big cards show filtered totals; "All time, ignoring filters" line stays constant |
| 20 | XSS safety | Add title `<img src=x onerror=alert(1)>` and notes `<script>alert(2)</script>` | Text displays literally; no alert; also literal in the delete dialog |
| 21 | Long text | Title of 100 characters without spaces | Wraps inside the cell, no horizontal page scroll |
| 22 | Server error | Stop the app, then click "Clear filters" | Red error banner with "Try again"; start the app, click Try again, data loads |
| 23 | Server-side validation | Using dev tools, edit the amount input to `1.239` and submit (or use curl) | Error banner / field error from the server |
| 24 | Responsive | Resize to ~375px wide | Sidebar becomes top bar; cards stack; table scrolls inside its box |
| 25 | Keyboard | Tab through the page; open and close the delete dialog with keyboard | Visible focus ring; Escape closes the dialog |
| 26 | Persistence | Add a row, restart the app, reload | Row is still there (stored in MySQL) |
