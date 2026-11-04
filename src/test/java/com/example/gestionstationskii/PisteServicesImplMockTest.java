package com.example.gestionstationskii;

import com.example.gestionstationskii.entities.Color;
import com.example.gestionstationskii.entities.Piste;
import com.example.gestionstationskii.repositories.IPisteRepository;
import com.example.gestionstationskii.services.PisteServicesImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitaire AVEC MOCK (Mockito) :
 * Ce test est isole de la base de donnees. Les appels au repository sont simules (mockes).
 * Il s'execute tres rapidement sans demarrer Spring ni se connecter a MySQL.
 */
@ExtendWith(MockitoExtension.class)
class PisteServicesImplMockTest {

    @Mock
    private IPisteRepository pisteRepository;

    @InjectMocks
    private PisteServicesImpl pisteServices;

    private Piste piste;

    @BeforeEach
    void setUp() {
        piste = new Piste();
        piste.setNumPiste(1L);
        piste.setNamePiste("Piste Verte Sud");
        piste.setColor(Color.GREEN);
        piste.setLength(800);
        piste.setSlope(10);
    }

    @Test
    void testAddPisteAvecMock() {
        // Simulation du comportement du repository sans toucher a la DB
        when(pisteRepository.save(any(Piste.class))).thenReturn(piste);

        Piste savedPiste = pisteServices.addPiste(piste);

        Assertions.assertNotNull(savedPiste);
        Assertions.assertEquals(1L, savedPiste.getNumPiste());
        Assertions.assertEquals("Piste Verte Sud", savedPiste.getNamePiste());
        Assertions.assertEquals(Color.GREEN, savedPiste.getColor());

        // Verifier que la methode save a bien ete appelee 1 seule fois
        verify(pisteRepository, times(1)).save(piste);
    }

    @Test
    void testRetrievePisteAvecMock() {
        when(pisteRepository.findById(1L)).thenReturn(Optional.of(piste));

        Piste foundPiste = pisteServices.retrievePiste(1L);

        Assertions.assertNotNull(foundPiste);
        Assertions.assertEquals("Piste Verte Sud", foundPiste.getNamePiste());
        verify(pisteRepository, times(1)).findById(1L);
    }

    @Test
    void testRetrieveAllPistesAvecMock() {
        List<Piste> list = new ArrayList<>();
        list.add(piste);

        when(pisteRepository.findAll()).thenReturn(list);

        List<Piste> result = pisteServices.retrieveAllPistes();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        verify(pisteRepository, times(1)).findAll();
    }

    @Test
    void testRemovePisteAvecMock() {
        doNothing().when(pisteRepository).deleteById(1L);

        pisteServices.removePiste(1L);

        verify(pisteRepository, times(1)).deleteById(1L);
    }
}
