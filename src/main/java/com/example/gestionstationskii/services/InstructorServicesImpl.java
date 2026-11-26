package com.example.gestionstationskii.services;

import com.example.gestionstationskii.entities.*;
import com.example.gestionstationskii.repositories.*;
import com.example.gestionstationskii.repositories.IInstructorRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@Service
public class InstructorServicesImpl implements IInstructorServices {

    private IInstructorRepository instructorRepository;
    private ICourseRepository courseRepository;

    @Override
    public Instructor addInstructor(Instructor instructor) {
        log.info("Tentative d'ajout du moniteur : {} {}", instructor.getFirstName(), instructor.getLastName());
        Instructor saved = instructorRepository.save(instructor);
        log.info("Moniteur ajouté avec succès ! [ID: {}, Nom: {} {}, Embauche: {}]",
                saved.getNumInstructor(), saved.getFirstName(), saved.getLastName(), saved.getDateOfHire());
        return saved;
    }

    @Override
    public List<Instructor> retrieveAllInstructors() {
        log.info("Récupération de la liste des moniteurs...");
        List<Instructor> instructors = instructorRepository.findAll();
        log.info("Total de moniteurs trouvés : {}", instructors.size());
        return instructors;
    }

    @Override
    public Instructor updateInstructor(Instructor instructor) {
        log.info("Mise à jour du moniteur ID: {}", instructor.getNumInstructor());
        Instructor updated = instructorRepository.save(instructor);
        log.info("Moniteur ID: {} mis à jour avec succès.", updated.getNumInstructor());
        return updated;
    }

    @Override
    public Instructor retrieveInstructor(Long numInstructor) {
        log.info("Recherche du moniteur ID: {}", numInstructor);
        Instructor instructor = instructorRepository.findById(numInstructor).orElse(null);
        if (instructor != null) {
            log.info("Moniteur trouvé : {} {} (ID: {})", instructor.getFirstName(), instructor.getLastName(), numInstructor);
        } else {
            log.warn("Aucun moniteur trouvé avec l'ID: {}", numInstructor);
        }
        return instructor;
    }

    @Override
    public Instructor addInstructorAndAssignToCourse(Instructor instructor, Long numCourse) {
        log.info("Ajout et assignation du moniteur {} {} au cours ID: {}",
                instructor.getFirstName(), instructor.getLastName(), numCourse);
        Course course = courseRepository.findById(numCourse).orElse(null);
        Set<Course> courseSet = new HashSet<>();
        if (course != null) {
            courseSet.add(course);
        } else {
            log.warn("Cours ID: {} non trouvé lors de l'assignation du moniteur", numCourse);
        }
        instructor.setCourses(courseSet);
        Instructor saved = instructorRepository.save(instructor);
        log.info("Moniteur {} {} assigné avec succès au cours ID: {}",
                saved.getFirstName(), saved.getLastName(), numCourse);
        return saved;
    }
}
