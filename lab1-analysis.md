# Lab 1 Analysis

## Activity 1.1 — Quality-attribute analysis (ISO/IEC 25010)

### FR-1.1 Registration

- **Interaction Capability** - User needs to understand what is required at registration and why a value is not accepted.
- **Reliability** - The system should be able to handle incorrect registration information without the registration creating an incorrect user or causing the system to stop working.

### FR-1.3 Wallet

- **Security** - The system should prevent an user from manipulating their balance or making a top-up that they are not entitled to.
- **Functional suitability** - The system should handle the wallet function correctly, e.g. a valid deposit of 15 000 SEK should increase the balance by the corresponding amount.

### FR-1.4 Swedish identity (optional verification)

- **Functional suitability** - The system must correctly check the personnummer and only change to VERIFIED when it has actually succeeded.
- **Security** - The system must handle identity information securely so that unauthorized persons cannot gain access.

- **Testable Quality Requirement:** A seeker with a valid personnummer that passes the Luhn checksum should be changed from status NEW to VERIFIED

## Activity 1.2 — Bug analysis (error → fault → failure)

**FAULT**

The FR-4.4 requirement says: "The Seeker's active Bookings `<` the trust-tier max (FR-1.2)." but in BookingService.java the code checks: "if (seekerActive > seeker.getMaxConcurrentBookings())". This means that the fault is that the programmer used ">" instead of ">=". Since the trust-tier max for a NEW seeker is 1, a Seeker who already has 1 active booking shouldn't be allowed to create a second one. The requirement says that the number of active bookings must be less than the trust-tier max.

**HUMAN ERROR**

The human error seems to be that the programmer didn't take into consideration the case where the number of bookings is the same as the max limit.

**FAILURE**

The user was able to make a second booking even though they had already reached the maximum number of active bookings for their trust-tier.

**Test level & test technique**

Unit testing should have caught this since it's good for testing boundary behaviour and business rules, furthermore it's good with testing individual classes/functions.
BVA - Boundary value analysis should have caught this by testing the boundary (0-1-2) where the number of bookings is equal to the max limit - which is 1 for a NEW seeker.

## Activity 2.1 — Equivalence Partitioning

**EMAIL FORMAT:**
| Partition | Meaning | Representative |
| --------- | ------- | -------------- |
| INVALID | Email contains zero @ | nameemail.com |
| VALID | Email contains one @ | name@email.com |
| INVALID | Email contains two or more @ | name@@email.com |
| INVALID | Local part empty | @email.com |
| VALID | Local part is non-empty | name@email.com |
| INVALID | Domain contains zero . | name@emailcom |
| VALID | Domain contains one or more . | name@email.com |

**EMAIL LENGTH:**
| Partition | Meaning | Representative |
| --------- | ------- | -------------- |
| VALID | Length between 1 and 254 characters | name@email.com |
| INVALID | Length above the maximum | 255 character email |

**DISPLAY NAME:**
| Partition | Meaning | Representative |
| --------- | ------- | -------------- |
| INVALID | Length below 2 characters | N |
| VALID | Length between 2 and 40 characters, inclusive letters/spaces/hyphens/apostrophes | Name |
| INVALID | Length above 40 characters | 41 character name |
| INVALID | Contains characters other than letters/spaces/hyphens/apostrophes | Name99 |

**PHONE NUMBER:**
| Partition | Meaning | Representative |
| --------- | ------- | -------------- |
| VALID | Swedish format with 10 digits and starts with 07 | 0712345678 |
| VALID | International format starts with +467 followed by 8 digits | +46712345678 |
| INVALID | Doesn't follow Swedish or international format | 301234567 |

**WALLET TOP-UP AMOUNT:**
| Partition | Meaning | Representative |
| --------- | ------- | -------------- |
| INVALID | Top up with less than 10.00 SEK | 9.00 SEK |
| VALID | Top up with between 10.00 SEK and 5 000.00 SEK | 2 500.00 SEK |
| INVALID | Top up above 5 000.00 SEK | 5 001.00 SEK |
| INVALID | Top up that would make the wallet exceed 20 000.00 SEK | 100.00 SEK with wallet balance 19 999.00 SEK |

## Activity 2.2 — Boundary Value Analysis

**WALLET TOP-UP BOUNDARIES:**
| Purpose | Value | Expected result |
| --------- | ------- | -------------- |
| Just below lower boundary | 9.99 SEK | Reject |
| Lower boundary | 10.00 SEK | Accept |
| Just above lower boundary | 10.01 SEK | Accept |
| Just below upper boundary | 4 999.99 SEK | Accept |
| Upper boundary | 5 000.00 SEK | Accept |
| Just above upper boundary | 5 000.01 SEK | Reject |

**MAXIMUM BALANCE BOUNDARIES:**
| Purpose | Value | Expected result |
| --------- | ------- | -------------- |
| Just below upper boundary | 19 999.99 SEK | Accept |
| Upper boundary | 20 000.00 SEK | Accept |
| Just above upper boundary | 20 000.01 SEK | Reject

## Activity 2.3 — Decision table (trust tier → limits)

| Trust-tier | Max concurrent bookings | Platform fee |
| ---------- | ----------------------- | ------------ |
| NEW        | 1                       | 15%          |
| VERIFIED   | 3                       | 12%          |
| TRUSTED    | 5                       | 8%           |
| PRO_SITTER | 10                      | 5%           |
