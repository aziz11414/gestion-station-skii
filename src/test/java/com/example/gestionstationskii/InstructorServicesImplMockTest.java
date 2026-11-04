package com.example.gestionstationskii;

import com.example.gestionstationskii.entities.Course;
import com.example.gestionstationskii.entities.Instructor;
import com.example.gestionstationskii.repositories.ICourseRepository;
import com.example.gestionstationskii.repositories.IInstructorRepository;
import com.example.gestionstationskii.services.InstructorServicesImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstructorServicesImplMockTest {

    @Mock
    private IInstructorRepository instructorRepository;

    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private InstructorServicesImpl instructorServices;

    private Instructor instructor;

    @BeforeEach
    void setUp() {
        instructor = new Instructor();
        instructor.setNumInstructor(1L);
        instructor.setFirstName("Jean");
        instructor.setLastName("Dupont");
        instructor.setDateOfHire(LocalDate.of(2020, 1, 15));
    }

    @Test
    void testAddInstructor() {
        when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

        Instructor saved = instructorServices.addInstructor(instructor);

        Assertions.assertNotNull(saved);
        Assertions.assertEquals("Jean", saved.getFirstName());
        verify(instructorRepository, times(1)).save(instructor);
    }

    @Test
    void testRetrieveInstructor() {
        when(instructorRepository.findById(1L)).thenReturn(Optional.of(instructor));

        Instructor found = instructorServices.retrieveInstructor(1L);

        Assertions.assertNotNull(found);
        Assertions.assertEquals("Dupont", found.getLastName());
        verify(instructorRepository, times(1)).findById(1L);
    }

    @Test
    void testRetrieveAllInstructors() {
        List<Instructor> list = new ArrayList<>();
        list.add(instructor);
        when(instructorRepository.findAll()).thenReturn(list);

        List<Instructor> result = instructorServices.retrieveAllInstructors();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        verify(instructorRepository, times(1)).findAll();
    }

    @Test
    void testUpdateInstructor() {
        when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

        Instructor updated = instructorServices.updateInstructor(instructor);

        Assertions.assertNotNull(updated);
        verify(instructorRepository, times(1)).save(instructor);
    }

    @Test
    void testAddInstructorAndAssignToCourse() {
        Course course = new Course();
        course.setNumCourse(10L);

        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
        when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

        Instructor assigned = instructorServices.addInstructorAndAssignToCourse(instructor, 10L);

        Assertions.assertNotNull(assigned);
        Assertions.assertNotNull(instructor.getCourses());
        Assertions.assertTrue(instructor.getCourses().contains(course));
        verify(courseRepository, times(1)).findById(10L);
        verify(instructorRepository, times(1)).save(instructor);
    }
}
