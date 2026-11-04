package com.example.gestionstationskii;

import com.example.gestionstationskii.entities.Color;
import com.example.gestionstationskii.entities.Piste;
import com.example.gestionstationskii.services.IPisteServices;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * Test d'integration SANS MOCK :
 * Ce test s'execute sur la base de donnees reelle (MySQL) configuree dans application.properties.
 * Il verifie la creation reelle des tables, l'insertion, la lecture et la suppression.
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PisteServicesImplIntegrationTest {

    @Autowired
    private IPisteServices pisteServices;

    private static Long createdPisteId;

    @Test
    @Order(1)
    void testAddPisteSansMock() {
        Piste piste = new Piste();
        piste.setNamePiste("Piste Rouge Nord");
        piste.setColor(Color.RED);
        piste.setLength(1500);
        piste.setSlope(25);

        Piste savedPiste = pisteServices.addPiste(piste);

        Assertions.assertNotNull(savedPiste, "La piste enregistree ne doit pas etre null");
        Assertions.assertNotNull(savedPiste.getNumPiste(), "L'ID auto-genere de la table ne doit pas etre null");
        Assertions.assertEquals("Piste Rouge Nord", savedPiste.getNamePiste());
        createdPisteId = savedPiste.getNumPiste();
    }

    @Test
    @Order(2)
    void testRetrievePisteSansMock() {
        Assertions.assertNotNull(createdPisteId, "L'ID de la piste creee doit exister");
        Piste piste = pisteServices.retrievePiste(createdPisteId);

        Assertions.assertNotNull(piste);
        Assertions.assertEquals("Piste Rouge Nord", piste.getNamePiste());
        Assertions.assertEquals(Color.RED, piste.getColor());
    }

    @Test
    @Order(3)
    void testRetrieveAllPistesSansMock() {
        List<Piste> pistes = pisteServices.retrieveAllPistes();

        Assertions.assertNotNull(pistes);
        Assertions.assertFalse(pistes.isEmpty(), "La liste des pistes ne doit pas etre vide");
    }

    @Test
    @Order(4)
    void testRemovePisteSansMock() {
        if (createdPisteId != null) {
            pisteServices.removePiste(createdPisteId);
            Piste deletedPiste = pisteServices.retrievePiste(createdPisteId);
            Assertions.assertNull(deletedPiste, "La piste doit avoir ete supprimee");
        }
    }
}
