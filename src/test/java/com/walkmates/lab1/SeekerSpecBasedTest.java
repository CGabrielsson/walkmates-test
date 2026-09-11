package com.walkmates.lab1;

import com.walkmates.model.Seeker;
import com.walkmates.model.TrustTier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Lab 1, Part B — specification-based tests for {@link Seeker}.
 *
 * <p>
 * Design your tests on paper first (equivalence partitions, boundary values,
 * decision table)
 * from {@code docs/REQUIREMENTS.md} FR-1.1 / FR-1.3 / FR-1.2, then implement
 * them here. One
 * worked example is provided; the {@code TODO}s are yours.
 * </p>
 */

class SeekerSpecBasedTest {

    // -----------------------------------------------------
    // EP - Equivalence Partitioning
    // FR-1.1: Email
    // -----------------------------------------------------

    @Test
    @DisplayName("Email with zero @ symbols is rejected")
    void emailWithZeroAtSymbolsIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Seeker("nameemail.com", "Sam", "0712345678"));
    }

    @Test
    @DisplayName("Email with exactly one @ symbol is accepted")
    void emailWithOneAtSymbolIsAccepted() {
        Seeker seeker = new Seeker("name@email.com", "Sam", "0712345678");

        assertThat(seeker.getEmail()).isEqualTo("name@email.com");
    }

    @Test
    @DisplayName("Email with two or more @ symbols is rejected")
    void emailWithMultipleAtSymbolsIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Seeker("name@@email.com", "Sam", "0712345678"));
    }

    @Test
    @DisplayName("Email with empty local part is rejected")
    void emailWithEmptyLocalPartIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Seeker("@email.com", "Sam", "0712345678"));
    }

    @Test
    @DisplayName("Email with non-empty local part is accepted")
    void emailWithNonEmptyLocalPartIsAccepted() {
        Seeker seeker = new Seeker("name@email.com", "Sam", "0712345678");

        assertThat(seeker.getEmail()).isEqualTo("name@email.com");
    }

    @Test
    @DisplayName("Email with no dot in domain is rejected")
    void emailWithNoDotInDomainIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Seeker("name@emailcom", "Sam", "0712345678"));
    }

    @Test
    @DisplayName("Email with a dot in domain is accepted")
    void emailWithDotInDomainIsAccepted() {
        Seeker seeker = new Seeker("name@email.com", "Sam", "0712345678");

        assertThat(seeker.getEmail()).isEqualTo("name@email.com");
    }

    @Test
    @DisplayName("Email up to 254 characters is accepted")
    void emailAtMaximumLengthIsAccepted() {
        String localPart = "a".repeat(241);
        String email = localPart + "@a.com";

        Seeker seeker = new Seeker(email, "Sam", "0712345678");

        assertThat(seeker.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("Email above 254 characters is rejected")
    void emailAboveMaximumLengthIsRejected() {
        String localPart = "a".repeat(249);
        String email = localPart + "@a.com";

        assertThrows(IllegalArgumentException.class,
                () -> new Seeker(email, "Sam", "0712345678"));
    }

    // -----------------------------------------------------
    // EP - Equivalence Partitioning
    // FR-1.1: Display name
    // -----------------------------------------------------

    @Test
    @DisplayName("Display name below 2 characters is rejected")
    void displayNameBelowMinimumLengthIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Seeker("name@email.com", "N", "0712345678"));
    }

    @Test
    @DisplayName("Display name between 2 and 40 characters is accepted")
    void validDisplayNameIsAccepted() {
        Seeker seeker = new Seeker("name@email.com", "Name", "0712345678");

        assertThat(seeker.getDisplayName()).isEqualTo("Name");
    }

    @Test
    @DisplayName("Display name above 40 characters is rejected")
    void displayNameAboveMaximumLengthIsRejected() {
        String displayName = "A".repeat(41);

        assertThrows(IllegalArgumentException.class,
                () -> new Seeker("name@email.com", displayName, "0712345678"));
    }

    @Test
    @DisplayName("Display name with invalid characters is rejected")
    void displayNameWithInvalidCharactersIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Seeker("name@email.com", "Name99", "0712345678"));
    }

    // -----------------------------------------------------
    // EP - Equivalence Partitioning
    // FR-1.1: Phone number
    // -----------------------------------------------------

    @Test
    @DisplayName("Valid Swedish phone number is accepted")
    void validSwedishPhoneNumberIsAccepted() {
        Seeker seeker = new Seeker("name@email.com", "Sam", "0712345678");

        assertThat(seeker.getPhoneNumber()).isEqualTo("0712345678");
    }

    @Test
    @DisplayName("Invalid phone number is rejected")
    void invalidPhoneNumberIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Seeker("name@email.com", "Sam", "301234567"));
    }

    // -----------------------------------------------------
    // EP - Equivalence Partitioning
    // FR-1.3: Wallet top-up
    // -----------------------------------------------------

    @Test
    @DisplayName("Top-up below 10 SEK is rejected")
    void topUpBelowMinimumIsRejected() {
        Seeker seeker = new Seeker("name@email.com", "Sam", "0712345678");

        assertThrows(IllegalArgumentException.class,
                () -> seeker.addFunds(9.00));
    }

    @Test
    @DisplayName("Valid top-up between 10 and 5000 SEK is accepted")
    void validTopUpIsAccepted() {
        Seeker seeker = new Seeker("name@email.com", "Sam", "0712345678");

        seeker.addFunds(2500.00);

        assertThat(seeker.getBalance()).isEqualTo(2500.00);
    }

    @Test
    @DisplayName("Top-up above 5000 SEK is rejected")
    void topUpAboveSingleMaximumIsRejected() {
        Seeker seeker = new Seeker("name@email.com", "Sam", "0712345678");

        assertThrows(IllegalArgumentException.class,
                () -> seeker.addFunds(5001.00));
    }

    @Test
    @DisplayName("Top-up that would exceed 20000 SEK balance is rejected")
    void topUpThatExceedsMaximumBalanceIsRejected() {
        Seeker seeker = new Seeker("name@email.com", "Sam", "0712345678");

        seeker.addFunds(5000.00);
        seeker.addFunds(5000.00);
        seeker.addFunds(5000.00);
        seeker.addFunds(4999.00);

        assertThat(seeker.getBalance()).isEqualTo(19999.00);

        assertThrows(IllegalArgumentException.class,
                () -> seeker.addFunds(10.00));
    }

    // -----------------------------------------------------
    // BVA - Boundary Value Analysis
    // FR-1.3: Wallet top-up boundaries
    // -----------------------------------------------------

    // ---- Worked example: boundary value at the maximum single top-up (FR-1.3)
    // ----
    @Test
    @DisplayName("Top-up exactly at the 5000 SEK single-transaction maximum is accepted")
    void topUpAtSingleMaximumIsAccepted() {
        Seeker seeker = new Seeker("sam@example.com", "Sam", "0707654321");

        seeker.addFunds(Seeker.MAX_SINGLE_TOP_UP); // 5000.00, the boundary value

        assertThat(seeker.getBalance()).isEqualTo(Seeker.MAX_SINGLE_TOP_UP);
    }

    @Test
    @DisplayName("Top-up just below single-transaction maximum is accepted")
    void topUpJustBelowSingleMaximumIsAccepted() {
        Seeker seeker = new Seeker("name@email.com", "Sam", "0712345678");

        seeker.addFunds(Seeker.MAX_SINGLE_TOP_UP - 0.01);

        assertThat(seeker.getBalance())
                .isEqualTo(Seeker.MAX_SINGLE_TOP_UP - 0.01);
    }

    @Test
    @DisplayName("Top-up just above single-transaction maximum is rejected")
    void topUpJustAboveSingleMaximumIsRejected() {
        Seeker seeker = new Seeker("name@email.com", "Sam", "0712345678");

        assertThrows(IllegalArgumentException.class,
                () -> seeker.addFunds(Seeker.MAX_SINGLE_TOP_UP + 0.01));
    }

    @Test
    @DisplayName("Top-up just below minimum is rejected")
    void topUpJustBelowMinimumIsRejected() {
        Seeker seeker = new Seeker("name@email.com", "Sam", "0712345678");

        assertThrows(IllegalArgumentException.class,
                () -> seeker.addFunds(Seeker.MIN_TOP_UP - 0.01));
    }

    @Test
    @DisplayName("Top-up at minimum is accepted")
    void topUpAtMinimumIsAccepted() {
        Seeker seeker = new Seeker("name@email.com", "Sam", "0712345678");

        seeker.addFunds(Seeker.MIN_TOP_UP);

        assertThat(seeker.getBalance()).isEqualTo(Seeker.MIN_TOP_UP);
    }

    @Test
    @DisplayName("Top-up just above minimum is accepted")
    void topUpJustAboveMinimumIsAccepted() {
        Seeker seeker = new Seeker("name@email.com", "Sam", "0712345678");

        seeker.addFunds(Seeker.MIN_TOP_UP + 0.01);

        assertThat(seeker.getBalance())
                .isEqualTo(Seeker.MIN_TOP_UP + 0.01);
    }

    // -----------------------------------------------------
    // BVA - Boundary Value Analysis
    // FR-1.3: Maximum wallet balance
    // -----------------------------------------------------

    @Test
    @DisplayName("Balance just below maximum is accepted")
    void balanceJustBelowMaximumIsAccepted() {
        Seeker seeker = new Seeker("name@email.com", "Sam", "0712345678");

        seeker.addFunds(4999.99);
        seeker.addFunds(5000.00);
        seeker.addFunds(5000.00);
        seeker.addFunds(5000.00);

        assertThat(seeker.getBalance())
                .isEqualTo(Seeker.MAX_BALANCE - 0.01);
    }

    @Test
    @DisplayName("Balance at maximum is accepted")
    void balanceAtMaximumIsAccepted() {
        Seeker seeker = new Seeker("name@email.com", "Sam", "0712345678");

        seeker.addFunds(5000.00);
        seeker.addFunds(5000.00);
        seeker.addFunds(5000.00);
        seeker.addFunds(5000.00);

        assertThat(seeker.getBalance()).isEqualTo(Seeker.MAX_BALANCE);
    }

    @Test
    @DisplayName("Balance just above maximum is rejected")
    void balanceJustAboveMaximumIsRejected() {
        Seeker seeker = new Seeker("name@email.com", "Sam", "0712345678");

        seeker.addFunds(5000.00);
        seeker.addFunds(5000.00);
        seeker.addFunds(5000.00);
        seeker.addFunds(4999.99);

        assertThrows(IllegalArgumentException.class,
                () -> seeker.addFunds(0.02));
    }

    // -----------------------------------------------------
    // Decision Table
    // FR-1.2: Trust tiers
    // -----------------------------------------------------

    @Test
    @DisplayName("NEW tier has correct booking limit and platform fee")
    void newTierHasCorrectLimits() {
        Seeker seeker = new Seeker("name@email.com", "Sam", "0712345678");

        assertThat(seeker.getTrustTier()).isEqualTo(TrustTier.NEW);
        assertThat(seeker.getMaxConcurrentBookings()).isEqualTo(1);
        assertThat(seeker.getTrustTier().getPlatformFee()).isEqualTo(0.15);
    }

    @Test
    @DisplayName("VERIFIED tier has correct booking limit and platform fee")
    void verifiedTierHasCorrectLimits() {
        Seeker seeker = new Seeker("name@email.com", "Sam", "0712345678");
        seeker.setTrustTier(TrustTier.VERIFIED);

        assertThat(seeker.getMaxConcurrentBookings()).isEqualTo(3);
        assertThat(seeker.getTrustTier().getPlatformFee()).isEqualTo(0.12);
    }

    @Test
    @DisplayName("TRUSTED tier has correct booking limit and platform fee")
    void trustedTierHasCorrectLimits() {
        Seeker seeker = new Seeker("name@email.com", "Sam", "0712345678");
        seeker.setTrustTier(TrustTier.TRUSTED);

        assertThat(seeker.getMaxConcurrentBookings()).isEqualTo(5);
        assertThat(seeker.getTrustTier().getPlatformFee()).isEqualTo(0.08);
    }

    @Test
    @DisplayName("PRO_SITTER tier has correct booking limit and platform fee")
    void proSitterTierHasCorrectLimits() {
        Seeker seeker = new Seeker("name@email.com", "Sam", "0712345678");
        seeker.setTrustTier(TrustTier.PRO_SITTER);

        assertThat(seeker.getMaxConcurrentBookings()).isEqualTo(10);
        assertThat(seeker.getTrustTier().getPlatformFee()).isEqualTo(0.05);
    }
}
