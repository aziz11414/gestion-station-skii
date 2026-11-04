package com.example.gestionstationskii;

import com.example.gestionstationskii.entities.Subscription;
import com.example.gestionstationskii.entities.TypeSubscription;
import com.example.gestionstationskii.repositories.ISkierRepository;
import com.example.gestionstationskii.repositories.ISubscriptionRepository;
import com.example.gestionstationskii.services.SubscriptionServicesImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServicesImplMockTest {

    @Mock
    private ISubscriptionRepository subscriptionRepository;

    @Mock
    private ISkierRepository skierRepository;

    @InjectMocks
    private SubscriptionServicesImpl subscriptionServices;

    private Subscription subscription;

    @BeforeEach
    void setUp() {
        subscription = new Subscription();
        subscription.setNumSub(1L);
        subscription.setStartDate(LocalDate.of(2026, 1, 1));
        subscription.setPrice(350.0f);
    }

    @Test
    void testAddSubscriptionAnnual() {
        subscription.setTypeSub(TypeSubscription.ANNUAL);
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);

        Subscription saved = subscriptionServices.addSubscription(subscription);

        Assertions.assertNotNull(saved);
        Assertions.assertEquals(LocalDate.of(2027, 1, 1), subscription.getEndDate());
        verify(subscriptionRepository, times(1)).save(subscription);
    }

    @Test
    void testAddSubscriptionMonthly() {
        subscription.setTypeSub(TypeSubscription.MONTHLY);
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);

        Subscription saved = subscriptionServices.addSubscription(subscription);

        Assertions.assertNotNull(saved);
        Assertions.assertEquals(LocalDate.of(2026, 2, 1), subscription.getEndDate());
        verify(subscriptionRepository, times(1)).save(subscription);
    }

    @Test
    void testAddSubscriptionSemestriel() {
        subscription.setTypeSub(TypeSubscription.SEMESTRIEL);
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);

        Subscription saved = subscriptionServices.addSubscription(subscription);

        Assertions.assertNotNull(saved);
        Assertions.assertEquals(LocalDate.of(2026, 7, 1), subscription.getEndDate());
        verify(subscriptionRepository, times(1)).save(subscription);
    }

    @Test
    void testRetrieveSubscriptionById() {
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));

        Subscription found = subscriptionServices.retrieveSubscriptionById(1L);

        Assertions.assertNotNull(found);
        Assertions.assertEquals(1L, found.getNumSub());
        verify(subscriptionRepository, times(1)).findById(1L);
    }

    @Test
    void testGetSubscriptionByType() {
        Set<Subscription> set = new HashSet<>();
        set.add(subscription);
        when(subscriptionRepository.findByTypeSubOrderByStartDateAsc(TypeSubscription.ANNUAL)).thenReturn(set);

        Set<Subscription> result = subscriptionServices.getSubscriptionByType(TypeSubscription.ANNUAL);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        verify(subscriptionRepository, times(1)).findByTypeSubOrderByStartDateAsc(TypeSubscription.ANNUAL);
    }

    @Test
    void testRetrieveSubscriptionsByDates() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 12, 31);
        List<Subscription> list = Collections.singletonList(subscription);
        when(subscriptionRepository.getSubscriptionsByStartDateBetween(start, end)).thenReturn(list);

        List<Subscription> result = subscriptionServices.retrieveSubscriptionsByDates(start, end);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        verify(subscriptionRepository, times(1)).getSubscriptionsByStartDateBetween(start, end);
    }

    @Test
    void testUpdateSubscription() {
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);

        Subscription updated = subscriptionServices.updateSubscription(subscription);

        Assertions.assertNotNull(updated);
        verify(subscriptionRepository, times(1)).save(subscription);
    }
}
