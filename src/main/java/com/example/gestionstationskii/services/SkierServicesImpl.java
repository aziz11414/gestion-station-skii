package com.example.gestionstationskii.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.gestionstationskii.entities.*;
import com.example.gestionstationskii.repositories.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@Service
public class SkierServicesImpl implements ISkierServices {

    private ISkierRepository skierRepository;

    private IPisteRepository pisteRepository;

    private ICourseRepository courseRepository;

    private IRegistrationRepository registrationRepository;

    private ISubscriptionRepository subscriptionRepository;


    @Override
    public List<Skier> retrieveAllSkiers() {
        log.info("Récupération de la liste de tous les skieurs...");
        List<Skier> skiers = skierRepository.findAll();
        log.info("Total de skieurs récupérés : {}", skiers.size());
        return skiers;
    }

    @Override
    public Skier addSkier(Skier skier) {
        log.info("Tentative d'ajout d'un nouveau skieur : {} {}", skier.getFirstName(), skier.getLastName());
        if (skier.getSubscription() != null && skier.getSubscription().getTypeSub() != null) {
            switch (skier.getSubscription().getTypeSub()) {
                case ANNUAL:
                    skier.getSubscription().setEndDate(skier.getSubscription().getStartDate().plusYears(1));
                    break;
                case SEMESTRIEL:
                    skier.getSubscription().setEndDate(skier.getSubscription().getStartDate().plusMonths(6));
                    break;
                case MONTHLY:
                    skier.getSubscription().setEndDate(skier.getSubscription().getStartDate().plusMonths(1));
                    break;
            }
        }
        Skier savedSkier = skierRepository.save(skier);
        log.info("Skieur ajouté avec succès ! [ID: {}, Nom: {} {}, Ville: {}, Abonnement: {}]",
                savedSkier.getNumSkier(),
                savedSkier.getFirstName(),
                savedSkier.getLastName(),
                savedSkier.getCity(),
                savedSkier.getSubscription() != null ? savedSkier.getSubscription().getTypeSub() : "AUCUN");
        return savedSkier;
    }

    @Override
    public Skier assignSkierToSubscription(Long numSkier, Long numSubscription) {
        log.info("Assignation du skieur ID: {} à l'abonnement ID: {}", numSkier, numSubscription);
        Skier skier = skierRepository.findById(numSkier).orElse(null);
        Subscription subscription = subscriptionRepository.findById(numSubscription).orElse(null);
        if (skier == null || subscription == null) {
            log.warn("Échec de l'assignation : Skieur ({}) ou Abonnement ({}) introuvable", numSkier, numSubscription);
            return null;
        }
        skier.setSubscription(subscription);
        Skier updated = skierRepository.save(skier);
        log.info("Skieur ID: {} assigné avec succès à l'abonnement [ID: {}, Type: {}]",
                numSkier, subscription.getNumSub(), subscription.getTypeSub());
        return updated;
    }

    @Override
    public Skier addSkierAndAssignToCourse(Skier skier, Long numCourse) {
        log.info("Ajout du skieur {} {} et assignation au cours ID: {}", skier.getFirstName(), skier.getLastName(), numCourse);
        Skier savedSkier = skierRepository.save(skier);
        Course course = courseRepository.getById(numCourse);
        Set<Registration> registrations = savedSkier.getRegistrations();
        if (registrations != null) {
            for (Registration r : registrations) {
                r.setSkier(savedSkier);
                r.setCourse(course);
                registrationRepository.save(r);
            }
        }
        log.info("Skieur ID: {} ajouté et assigné avec succès au cours ID: {}", savedSkier.getNumSkier(), numCourse);
        return savedSkier;
    }

    @Override
    public void removeSkier(Long numSkier) {
        log.warn("Suppression du skieur ID: {}", numSkier);
        skierRepository.deleteById(numSkier);
        log.info("Skieur ID: {} supprimé avec succès.", numSkier);
    }

    @Override
    public Skier retrieveSkier(Long numSkier) {
        log.info("Recherche du skieur ID: {}", numSkier);
        Skier skier = skierRepository.findById(numSkier).orElse(null);
        if (skier != null) {
            log.info("Skieur trouvé : {} {} (ID: {})", skier.getFirstName(), skier.getLastName(), numSkier);
        } else {
            log.warn("Aucun skieur trouvé avec l'ID: {}", numSkier);
        }
        return skier;
    }

    @Override
    public Skier assignSkierToPiste(Long numSkieur, Long numPiste) {
        log.info("Assignation du skieur ID: {} à la piste ID: {}", numSkieur, numPiste);
        Skier skier = skierRepository.findById(numSkieur).orElse(null);
        Piste piste = pisteRepository.findById(numPiste).orElse(null);
        if (skier == null || piste == null) {
            log.warn("Échec assignation piste : Skieur ({}) ou Piste ({}) introuvable", numSkieur, numPiste);
            return null;
        }
        try {
            skier.getPistes().add(piste);
        } catch (NullPointerException exception) {
            Set<Piste> pisteList = new HashSet<>();
            pisteList.add(piste);
            skier.setPistes(pisteList);
        }

        Skier updated = skierRepository.save(skier);
        log.info("Skieur ID: {} assigné avec succès à la piste : {} [Couleur: {}]",
                numSkieur, piste.getNamePiste(), piste.getColor());
        return updated;
    }

    @Override
    public List<Skier> retrieveSkiersBySubscriptionType(TypeSubscription typeSubscription) {
        log.info("Recherche des skieurs par type d'abonnement : {}", typeSubscription);
        List<Skier> skiers = skierRepository.findBySubscription_TypeSub(typeSubscription);
        log.info("{} skieur(s) trouvé(s) pour l'abonnement {}", skiers.size(), typeSubscription);
        return skiers;
    }
}
