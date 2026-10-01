package com.walkmates.lab2;

import com.walkmates.model.Booking;
import com.walkmates.model.BookingStatus;
import com.walkmates.model.Listing;
import com.walkmates.model.ListingType;
import com.walkmates.model.Provider;
import com.walkmates.model.Seeker;
import com.walkmates.repository.BookingRepository;
import com.walkmates.repository.ListingRepository;
import com.walkmates.repository.ProviderRepository;
import com.walkmates.repository.SeekerRepository;
import com.walkmates.repository.inmemory.InMemoryBookingRepository;
import com.walkmates.repository.inmemory.InMemoryListingRepository;
import com.walkmates.repository.inmemory.InMemoryProviderRepository;
import com.walkmates.repository.inmemory.InMemorySeekerRepository;
import com.walkmates.service.BookingService;
import com.walkmates.service.NotificationService;
import com.walkmates.service.PaymentService;
import com.walkmates.service.PricingCalculator;
import com.walkmates.service.SeekerService;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookingServiceTest {

    private final InMemorySeekerRepository seekers = new InMemorySeekerRepository();
    private final InMemoryListingRepository listings = new InMemoryListingRepository();
    private final InMemoryProviderRepository providers = new InMemoryProviderRepository();
    private final InMemoryBookingRepository bookings = new InMemoryBookingRepository();

    private final BookingService bookingService = new BookingService(seekers, listings, providers,
            bookings, new PricingCalculator(), mock(NotificationService.class));

    private Seeker seeker() {
        Seeker seeker = new Seeker("christian@gmail.com", "Christian", "0701234567");
        seeker.addFunds(100.00);
        return seeker;
    }

    private Provider provider() {
        return new Provider("DoggyDawgDogCare", 50.3, 10);
    }

    private Listing listing(Provider provider) {
        return new Listing(provider.getId(), "Dog walk", "A walk", ListingType.DOG_WALK);
    }

    @Test
    void rejectsBookingWhenSeekerAlreadyHasMaxActiveBookings() {
        // create a seeker, provider and listing
        Seeker seeker = seeker();
        Provider provider = provider();
        Listing listing = listing(provider);

        // save in repositories
        seekers.save(seeker);
        providers.save(provider);
        listings.save(listing);

        // seeker has active booking and should be denied
        Booking existingBooking = new Booking(seeker.getId(), listing.getId(), 30);
        bookings.save(existingBooking);

        // assert throws correct exception
        assertThrows(BookingService.BookingRejectedException.class,
                () -> bookingService.createBooking(seeker.getId(), listing.getId(), 30));
    }

    @Nested
    class SeekerTopUpTest {

        @Test
        void topUp_successCreditsWallet() throws Exception {
            // Mock repositories/services
            SeekerRepository seekers = mock(SeekerRepository.class);
            PaymentService paymentService = mock(PaymentService.class);
            NotificationService notifications = mock(NotificationService.class);

            Seeker seeker = new Seeker("christian@gmail.com", "Christian", "0701234567");

            // what mocks should return when services call them
            when(seekers.findById(seeker.getId())).thenReturn(Optional.of(seeker));
            when(paymentService.charge(seeker.getId(), "payment-method-1", 100.0))
                    .thenReturn("transaction-1");
            when(seekers.save(seeker)).thenReturn(seeker);

            SeekerService seekerService = new SeekerService(seekers, paymentService, notifications);

            // run top up and make sure balance is increased
            Seeker result = seekerService.topUp(seeker.getId(), "payment-method-1", 100.0);

            assertThat(result.getBalance()).isEqualTo(100.0);
        }

        @Test
        void topUp_declinedDoesNotCreditWallet() throws Exception {
            SeekerRepository seekers = mock(SeekerRepository.class);
            PaymentService paymentService = mock(PaymentService.class);
            NotificationService notifications = mock(NotificationService.class);

            Seeker seeker = new Seeker("christian@gmail.com", "Christian", "0701234567");
            seeker.addFunds(50.0);

            when(seekers.findById(seeker.getId())).thenReturn(Optional.of(seeker));
            when(paymentService.charge(seeker.getId(), "payment-method-1", 100.0))
                    .thenThrow(new PaymentService.PaymentException("declined"));

            SeekerService seekerService = new SeekerService(seekers, paymentService, notifications);

            assertThatThrownBy(() -> seekerService.topUp(seeker.getId(), "payment-method-1", 100.0))
                    .isInstanceOf(PaymentService.PaymentException.class);

            // balance should be unchanged when payment fails
            assertThat(seeker.getBalance()).isEqualTo(50.0);
        }

        @Test
        void topUp_timeoutDoesNotCreditWallet() throws Exception {
            SeekerRepository seekers = mock(SeekerRepository.class);
            PaymentService paymentService = mock(PaymentService.class);
            NotificationService notifications = mock(NotificationService.class);

            // add seeker
            Seeker seeker = new Seeker("christian@gmail.com", "Christian", "0701234567");
            seeker.addFunds(50.0);

            // paymentservice doesnt answer in time
            when(seekers.findById(seeker.getId())).thenReturn(Optional.of(seeker));
            when(paymentService.charge(seeker.getId(), "payment-method-1", 100.0))
                    .thenThrow(new PaymentService.PaymentTimeoutException("timeout"));

            SeekerService seekerService = new SeekerService(seekers, paymentService, notifications);

            assertThatThrownBy(() -> seekerService.topUp(seeker.getId(), "payment-method-1", 100.0))
                    .isInstanceOf(PaymentService.PaymentTimeoutException.class);

            // wallet does not topup during timeout
            assertThat(seeker.getBalance()).isEqualTo(50.0);
        }
    }

    @Nested
    class BookingConfirmationTest {

        @Test
        void createBooking_successSendsConfirmationNotification() {
            // mock all repositories and notification service
            SeekerRepository seekers = mock(SeekerRepository.class);
            ListingRepository listings = mock(ListingRepository.class);
            ProviderRepository providers = mock(ProviderRepository.class);
            BookingRepository bookings = mock(BookingRepository.class);
            NotificationService notifications = mock(NotificationService.class);

            Seeker seeker = new Seeker("christian@gmail.com", "Christian", "0701234567");
            seeker.addFunds(500.0);

            Provider provider = new Provider("DoggyDawgDogCare", 50.3, 10);
            Listing listing = new Listing(provider.getId(), "Dog walk", "Evening walk",
                    ListingType.DOG_WALK);

            when(seekers.findById(seeker.getId())).thenReturn(Optional.of(seeker));
            when(listings.findById(listing.getId())).thenReturn(Optional.of(listing));
            when(providers.findById(provider.getId())).thenReturn(Optional.of(provider));
            when(bookings.findBySeekerId(seeker.getId())).thenReturn(List.of());
            when(listings.findByProviderId(provider.getId())).thenReturn(List.of());
            when(bookings.findByListingId(listing.getId())).thenReturn(List.of());

            BookingService bookingService = new BookingService(seekers, listings, providers,
                    bookings, new PricingCalculator(), notifications);

            Booking booking = bookingService.createBooking(seeker.getId(), listing.getId(), 60);

            assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);

            // confirm bookingnotification was sent
            verify(notifications).sendBookingConfirmed(seeker, booking);
        }
    }
}