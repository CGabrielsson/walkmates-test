# Lab Reflection — WalkMates

**Lab:** 1

**Pair:** Christian Gabrielsson

**Repo commit/tag:** (link — Labs 1–3; write `N/A` for Lab 4)

---

### 1. What we did

I made some different tests for the Seeker-class.

- **Activity 1.1:** Looked at the requirements and connected some of them (FR-1.1, FR-1.3, FR-1.4) to quality attributes from (ISO/IEC 25010), as well as wrote my own requirement for one of them.
- **Activity 1.2:** For this activity I got a failure report. I compared the booking limit requirement (FR-4.4) then compared to the code. I then analyzed what test technique and test level should have caught this by reading the course notes.
- **Activity 2.1:** Used EP to divide input groups into valid and invalid. Then made some tests for each of these cases.
- **Activity 2.2:** Used BVA for wallet limits to test values just below, at the boundary and just above the boundary (e.g. test 1-2-3 if the boundary is integer 2).
- **Activity 2.3:** Made a decision table for the different trust-tiers in FR-1.2, tested the different tiers booking limits and platform fees.

### 2. What we found

In **Activity 1.2** I found a boundary problem with the booking limit. The requirement in FR-4-4 says: "The Seeker's active Bookings `<` the trust-tier max (FR-1.2)." This means that the number of active bookings must be less than the maximum, but the code checks if the number is greater than the maximum.

This taught me the importance of doing BVA.

### 3. AI use (be honest — it doesn't lower your grade)

Since I've never done JUnit testing before, I used generative AI to help me generate some tests after i manually created the different EP, BVA and decision tables. I checked the code myself as well as asked AI for explanations for every line, I also double checked against the official [JUnit docs](https://docs.junit.org/) to get a better understanding of the syntax.

One thing the AI produced that I thought might be wrong was it sometimes didn't include all the boundary values when generating tests for some reason, which is why I compared the output with the course notes to be sure the tests covered the correct boundaries.

### 4. Judgment

As I mentioned already in **3. AI use** I had to decide which boundary values were needed, since AI sometimes only included the upper and the exact boundary value which wasn't sufficient.

I also had to make several decisions by myself because in the beginning, the difference between EP and BVA wasn't obvious to me. I had to make sure I understood the difference between the two and how to use the tables I made to know what tests to implement.

### 5. What we'd test next

If I had more time I would probably do more testing on the wallet top-up functionality. I think it's an area in which there are a lot of potential failure points, because there are many different values that can be used, as well as several boundaries. A top-up also has to follow the limits for a single transaction where the total balance can't exceed 20 000 SEK.
Therefore I would test more combinations of top-up amounts and wallet balances to see if the function is handling it correctly.
