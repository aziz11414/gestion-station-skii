package com.example.gestionstationskii;

import com.example.gestionstationskii.entities.*;
import com.example.gestionstationskii.repositories.*;
import com.example.gestionstationskii.services.SkierServicesImpl;
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
class SkierServicesImplMockTest {

    @Mock
    private ISkierRepository skierRepository;

    @Mock
    private IPisteRepository pisteRepository;

    @Mock
    private ICourseRepository courseRepository;

    @Mock
    private IRegistrationRepository registrationRepository;

    @Mock
    private ISubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SkierServicesImpl skierServices;

    private Skier skier;
    private Subscription subscription;

    @BeforeEach
    void setUp() {
        subscription = new Subscription();
        subscription.setNumSub(1L);
        subscription.setStartDate(LocalDate.of(2026, 1, 1));
        subscription.setTypeSub(TypeSubscription.ANNUAL);

        skier = new Skier();
        skier.setNumSkier(1L);
        skier.setFirstName("Mohamed");
        skier.setLastName("Aziz");
        skier.setCity("Tunis");
        skier.setDateOfBirth(LocalDate.of(2000, 5, 20));
        skier.setSubscription(subscription);
    }

    @Test
    void testAddSkier() {
        when(skierRepository.save(any(Skier.class))).thenReturn(skier);

        Skier saved = skierServices.addSkier(skier);

        Assertions.assertNotNull(saved);
        Assertions.assertEquals("Mohamed", saved.getFirstName());
        Assertions.assertEquals(LocalDate.of(2027, 1, 1), skier.getSubscription().getEndDate());
        verify(skierRepository, times(1)).save(skier);
    }

    @Test
    void testRetrieveSkier() {
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));

        Skier found = skierServices.retrieveSkier(1L);

        Assertions.assertNotNull(found);
        Assertions.assertEquals("Aziz", found.getLastName());
        verify(skierRepository, times(1)).findById(1L);
    }

    @Test
    void testRetrieveAllSkiers() {
        List<Skier> list = Collections.singletonList(skier);
        when(skierRepository.findAll()).thenReturn(list);

        List<Skier> result = skierServices.retrieveAllSkiers();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        verify(skierRepository, times(1)).findAll();
    }

    @Test
    void testRemoveSkier() {
        doNothing().when(skierRepository).deleteById(1L);

        skierServices.removeSkier(1L);

        verify(skierRepository, times(1)).deleteById(1L);
    }

    @Test
    void testAssignSkierToSubscription() {
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(subscriptionRepository.findById(2L)).thenReturn(Optional.of(subscription));
        when(skierRepository.save(any(Skier.class))).thenReturn(skier);

        Skier result = skierServices.assignSkierToSubscription(1L, 2L);

        Assertions.assertNotNull(result);
        verify(skierRepository, times(1)).findById(1L);
        verify(subscriptionRepository, times(1)).findById(2L);
        verify(skierRepository, times(1)).save(skier);
    }

    @Test
    void testAssignSkierToPiste() {
        Piste piste = new Piste();
        piste.setNumPiste(5L);
        skier.setPistes(new HashSet<>());

        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(pisteRepository.findById(5L)).thenReturn(Optional.of(piste));
        when(skierRepository.save(any(Skier.class))).thenReturn(skier);

        Skier result = skierServices.assignSkierToPiste(1L, 5L);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(skier.getPistes().contains(piste));
        verify(skierRepository, times(1)).save(skier);
    }

    @Test
    void testRetrieveSkiersBySubscriptionType() {
        List<Skier> list = Collections.singletonList(skier);
        when(skierRepository.findBySubscription_TypeSub(TypeSubscription.ANNUAL)).thenReturn(list);

        List<Skier> result = skierServices.retrieveSkiersBySubscriptionType(TypeSubscription.ANNUAL);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        verify(skierRepository, times(1)).findBySubscription_TypeSub(TypeSubscription.ANNUAL);
    }
}
