package com.example.gestionstationskii;

import com.example.gestionstationskii.entities.Course;
import com.example.gestionstationskii.entities.Support;
import com.example.gestionstationskii.entities.TypeCourse;
import com.example.gestionstationskii.repositories.ICourseRepository;
import com.example.gestionstationskii.services.CourseServicesImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServicesImplMockTest {

    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private CourseServicesImpl courseServices;

    private Course course;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setNumCourse(1L);
        course.setLevel(2);
        course.setTypeCourse(TypeCourse.INDIVIDUAL);
        course.setSupport(Support.SKI);
        course.setPrice(120.0f);
        course.setTimeSlot(10);
    }

    @Test
    void testAddCourse() {
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        Course savedCourse = courseServices.addCourse(course);

        Assertions.assertNotNull(savedCourse);
        Assertions.assertEquals(1L, savedCourse.getNumCourse());
        Assertions.assertEquals(TypeCourse.INDIVIDUAL, savedCourse.getTypeCourse());
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    void testRetrieveCourse() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Course foundCourse = courseServices.retrieveCourse(1L);

        Assertions.assertNotNull(foundCourse);
        Assertions.assertEquals(120.0f, foundCourse.getPrice());
        verify(courseRepository, times(1)).findById(1L);
    }

    @Test
    void testRetrieveAllCourses() {
        List<Course> list = new ArrayList<>();
        list.add(course);
        when(courseRepository.findAll()).thenReturn(list);

        List<Course> result = courseServices.retrieveAllCourses();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void testUpdateCourse() {
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        Course updated = courseServices.updateCourse(course);

        Assertions.assertNotNull(updated);
        verify(courseRepository, times(1)).save(course);
    }
}
