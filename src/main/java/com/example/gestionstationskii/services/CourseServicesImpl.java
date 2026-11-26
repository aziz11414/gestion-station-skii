package com.example.gestionstationskii.services;

import com.example.gestionstationskii.entities.Course;
import com.example.gestionstationskii.repositories.ICourseRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class CourseServicesImpl implements ICourseServices {

    private ICourseRepository courseRepository;

    @Override
    public List<Course> retrieveAllCourses() {
        log.info("Récupération de tous les cours de ski...");
        List<Course> courses = courseRepository.findAll();
        log.info("Total de cours trouvés : {}", courses.size());
        return courses;
    }

    @Override
    public Course addCourse(Course course) {
        log.info("Tentative de création d'un cours : [Type: {}, Support: {}, Prix: {} DT, Niveau: {}]",
                course.getTypeCourse(), course.getSupport(), course.getPrice(), course.getLevel());
        Course saved = courseRepository.save(course);
        log.info("Cours créé avec succès ! [ID: {}, Type: {}]", saved.getNumCourse(), saved.getTypeCourse());
        return saved;
    }

    @Override
    public Course updateCourse(Course course) {
        log.info("Mise à jour du cours ID: {}", course.getNumCourse());
        Course updated = courseRepository.save(course);
        log.info("Cours ID: {} mis à jour avec succès.", updated.getNumCourse());
        return updated;
    }

    @Override
    public Course retrieveCourse(Long numCourse) {
        log.info("Recherche du cours ID: {}", numCourse);
        Course course = courseRepository.findById(numCourse).orElse(null);
        if (course != null) {
            log.info("Cours trouvé : ID {}, Type: {}", numCourse, course.getTypeCourse());
        } else {
            log.warn("Aucun cours trouvé avec l'ID: {}", numCourse);
        }
        return course;
    }
}
