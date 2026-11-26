package com.example.gestionstationskii.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.gestionstationskii.entities.*;
import com.example.gestionstationskii.repositories.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class PisteServicesImpl implements IPisteServices {

    private IPisteRepository pisteRepository;

    @Override
    public List<Piste> retrieveAllPistes() {
        log.info("Récupération de la liste de toutes les pistes...");
        List<Piste> pistes = pisteRepository.findAll();
        log.info("Total de pistes récupérées : {}", pistes.size());
        return pistes;
    }

    @Override
    public Piste addPiste(Piste piste) {
        log.info("Tentative d'ajout d'une nouvelle piste : {} [Couleur: {}, Longueur: {}m, Pente: {}%]",
                piste.getNamePiste(), piste.getColor(), piste.getLength(), piste.getSlope());
        Piste savedPiste = pisteRepository.save(piste);
        log.info("Piste ajoutée avec succès ! [ID: {}, Nom: {}, Couleur: {}]",
                savedPiste.getNumPiste(), savedPiste.getNamePiste(), savedPiste.getColor());
        return savedPiste;
    }

    @Override
    public void removePiste(Long numPiste) {
        log.warn("Suppression de la piste ID: {}", numPiste);
        pisteRepository.deleteById(numPiste);
        log.info("Piste ID: {} supprimée avec succès.", numPiste);
    }

    @Override
    public Piste retrievePiste(Long numPiste) {
        log.info("Recherche de la piste ID: {}", numPiste);
        Piste piste = pisteRepository.findById(numPiste).orElse(null);
        if (piste != null) {
            log.info("Piste trouvée : {} (Couleur: {})", piste.getNamePiste(), piste.getColor());
        } else {
            log.warn("Aucune piste trouvée avec l'ID: {}", numPiste);
        }
        return piste;
    }
}
