# Lab 2 Regression Test Analysis

A weekend surcharge is added to PricingCalculator.
Given the change, the existing tests that must re-run are the ones directly or indirectly using PricingCalculator.

### Priority 1: Direct PricingCalculator tests

All tests in PricingCalculatorStructuralTest should re-run because they call **PricingCalculator.priceFor()** directly.

| Test                                              | Reason                                                                                          |
| ------------------------------------------------- | ----------------------------------------------------------------------------------------------- |
| shortWalkPrice                                    | Testing the normal price calculation after surcharge logic is added                             |
| freeShelterVolunteerListing                       | Verifies that a free listing still cost 0.00 and no surcharge is added                          |
| overnightBookingIncludesSurcharge                 | Checks that the new surcharge logic does not break the existing overnight surcharge-calculation |
| exactlyEigthHoursDoesNotTriggerOvernightSurcharge | Tests the 480-min rule still is correct after changing the surcharge logic                      |

### Priority 2: Indirect BookingService test

**BookingServiceTest$BookingConfirmationTest.createBooking_successSendsConfirmationNotification** should also re-run since BookingService is calling PricingCalculator when creating a booking.
This test verifies that the booking still reaches confirmation after the pricing change.

### Other tests

The TopUp tests in BookingServiceTest do not use PricingCalculator which means they aren't relevant to a weekend surcharge change.

### Test Order

1. Run PricingCalculatorStructuralTest
2. Run the BookingConfirmationTest in BookingServiceTest
3. Run the full test suite
