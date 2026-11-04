package com.example.gestionstationskii;

import com.example.gestionstationskii.entities.*;
import com.example.gestionstationskii.repositories.ICourseRepository;
import com.example.gestionstationskii.repositories.IRegistrationRepository;
import com.example.gestionstationskii.repositories.ISkierRepository;
import com.example.gestionstationskii.services.RegistrationServicesImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServicesImplMockTest {

    @Mock
    private IRegistrationRepository registrationRepository;

    @Mock
    private ISkierRepository skierRepository;

    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private RegistrationServicesImpl registrationServices;

    private Registration registration;
    private Skier skier;
    private Course course;

    @BeforeEach
    void setUp() {
        skier = new Skier();
        skier.setNumSkier(1L);
        skier.setFirstName("Mohamed");
        skier.setDateOfBirth(LocalDate.of(2000, 1, 1));

        course = new Course();
        course.setNumCourse(10L);
        course.setTypeCourse(TypeCourse.INDIVIDUAL);

        registration = new Registration();
        registration.setNumRegistration(100L);
        registration.setNumWeek(5);
    }

    @Test
    void testAddRegistrationAndAssignToSkier() {
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);

        Registration saved = registrationServices.addRegistrationAndAssignToSkier(registration, 1L);

        Assertions.assertNotNull(saved);
        Assertions.assertEquals(skier, registration.getSkier());
        verify(skierRepository, times(1)).findById(1L);
        verify(registrationRepository, times(1)).save(registration);
    }

    @Test
    void testAssignRegistrationToCourse() {
        when(registrationRepository.findById(100L)).thenReturn(Optional.of(registration));
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);

        Registration result = registrationServices.assignRegistrationToCourse(100L, 10L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(course, registration.getCourse());
        verify(registrationRepository, times(1)).save(registration);
    }

    @Test
    void testAddRegistrationAndAssignToSkierAndCourse_Individual() {
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
        when(registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(5, 1L, 10L)).thenReturn(0L);
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);

        Registration result = registrationServices.addRegistrationAndAssignToSkierAndCourse(registration, 1L, 10L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(skier, registration.getSkier());
        Assertions.assertEquals(course, registration.getCourse());
        verify(registrationRepository, times(1)).save(registration);
    }

    @Test
    void testNumWeeksCourseOfInstructorBySupport() {
        List<Integer> weeks = Collections.singletonList(5);
        when(registrationRepository.numWeeksCourseOfInstructorBySupport(1L, Support.SKI)).thenReturn(weeks);

        List<Integer> result = registrationServices.numWeeksCourseOfInstructorBySupport(1L, Support.SKI);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(5, result.get(0));
        verify(registrationRepository, times(1)).numWeeksCourseOfInstructorBySupport(1L, Support.SKI);
    }
}
