# Lab Reflection — WalkMates

**Lab:** 2

**Pair:** Christian Gabrielsson

**Repo commit/tag:**

---

### 1. What we did

#### Activity 3.1

Ran JaCoCo to check coverage for PricingCalculator (FR-4.3)

#### Activity 3.2

Tests was added for a free listing and a long booking with overnight surcharge (FR-3.1, FR-4.3). The short booking test was already there.

#### Activity 3.3

Added a test for exactly 480 minutes (FR-4.3) and ran the test class.

#### Activity 4.1

Ran PIT and added a regression test where a NEW Seeker who already had one active booking tries to create another one (FR-1.2, FR-4.4).

#### Activity 4.2

Created Mockito tests for BookingService and SeekerService: top-ups and bookings confirmation (FR-1.3, FR-4.4)

#### Activity 4.3

Selected and prioritized tests for a pricing change:
[Lab 2 Regression Test Analysis](../lab2-regression-selection.md)

### 2. What we found

#### Activity 3.1

**Line Coverage** shows 3 missed Lines out of 15 total, which means a 12/15 (80%) line coverage. **Branch Coverage** shows 50%.

**3 Uncovered branches** are identified:

1. Line 34 which is checking if booking or listing or seeker is null.

   There is no test with any of these being null, which means all 3 true branches is missing

2. Line 40 that is checking if baseRate is 0.0.

   There is no test that is covering testing a free listing (baseRate == 0.0) which means a true branch is missing

3. Line 49 that is checking if DurationMinutes is more or equal than the overnight threshold minutes.

   There is no test covering a booking with duration of 480 minutes or more which means the a true branch is missing here as well.

#### Activity 3.3

Tested the boundary-value 480 minutes. According to the requirement, a booking of exactly 480 minutes shouldnt get a surcharge, but it did. The code comparison was faulty, it used ">=" instead of ">" which resulted in errors when running the test. After correcting the comparison the test ran green.

This shows that a test can "cover" the surcharge line through testing values far from the boundary, but if the test doesn't include the exact boundary value it misses the bug. The code path is executed but the behavior is still incorrect at the threshold. Which is why coverage ≠ correctness because a test can cover the code without verifying it meets the requirements.

#### Activity 4.1

PIT showed that all 14 mutants in PricingCalculator was killed, with 0 surviving mutants. The boundary and negated-condition mutants for seekerActive was also killed.

The regression test failed at first because the system allowed the booking when the Seeker already reached its max limit. The comparison used ">". After the production comparison was corrected to ">=" the booking was rejected.

### 3. AI use (be honest — it doesn't lower your grade)

#### Activity 3.1

I used AI to analyze the JaCoCo report. When examining line 34 (the null-check: booking == null || listing == null || seeker == null), the AI initially claimed only 1 branch was missing. But when I inspected it myself it said "3 of 6 branches missing". This made me challenge the AI and prevented me from blindly accepting the AIs explanation and also helped me understand how branch coverage is measured.

#### Activity 4.1

AI helped me write the regression test for 4.1 because I did not know how to set up the test. The first test was complicated for me, so I simplified it.

#### Activity 4.2

AI helped explain how to mock the repositories and services.

### 4. Judgment

#### Activity 3.1

I checked the JaCoCo report myself instead of only trusting the AIs explanation.

### 5. What we'd test next

If I had more time I would extend Activity 4.3 by implementing a weekend surcharge in practice, adding tests for weekend bookings. Since the current booking and pricing code does not include weekend surcharges, I would add a way to check if a booking takes place on a weekend. Then I would test that the surcharge is applied only on weekends and that free listings still costs 0.00.
