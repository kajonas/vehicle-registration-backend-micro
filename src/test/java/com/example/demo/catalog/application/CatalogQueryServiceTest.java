package com.example.demo.catalog.application;

import com.example.demo.catalog.domain.Make;
import com.example.demo.catalog.domain.Model;
import com.example.demo.catalog.repository.MakeRepository;
import com.example.demo.catalog.repository.ModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogQueryServiceTest {

    @Mock
    private MakeRepository makeRepository;

    @Mock
    private ModelRepository modelRepository;

    @InjectMocks
    private CatalogQueryService catalogQueryService;

    private Make testMake;
    private Model testModel1;
    private Model testModel2;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testMake = new Make();
        testMake.setId(1L);
        testMake.setName("Toyota");

        testModel1 = new Model();
        testModel1.setId(1L);
        testModel1.setName("Camry");
        testModel1.setMake(testMake);

        testModel2 = new Model();
        testModel2.setId(2L);
        testModel2.setName("Corolla");
        testModel2.setMake(testMake);
    }

    @Test
    void testGetMakes_Success() {
        // Arrange
        List<Make> expectedMakes = Arrays.asList(testMake);
        when(makeRepository.findAll()).thenReturn(expectedMakes);

        // Act
        List<Make> actualMakes = catalogQueryService.getMakes();

        // Assert
        assertNotNull(actualMakes);
        assertEquals(1, actualMakes.size());
        assertEquals("Toyota", actualMakes.get(0).getName());
        verify(makeRepository, times(1)).findAll();
    }

    @Test
    void testGetMakes_EmptyList() {
        // Arrange
        when(makeRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Make> actualMakes = catalogQueryService.getMakes();

        // Assert
        assertNotNull(actualMakes);
        assertTrue(actualMakes.isEmpty());
        verify(makeRepository, times(1)).findAll();
    }

    @Test
    void testGetModelsByMake_WithValidMakeId() {
        // Arrange
        Long makeId = 1L;
        List<Model> expectedModels = Arrays.asList(testModel1, testModel2);
        when(modelRepository.findByMakeId(makeId)).thenReturn(expectedModels);

        // Act
        List<Model> actualModels = catalogQueryService.getModelsByMake(makeId);

        // Assert
        assertNotNull(actualModels);
        assertEquals(2, actualModels.size());
        assertEquals("Camry", actualModels.get(0).getName());
        assertEquals("Corolla", actualModels.get(1).getName());
        verify(modelRepository, times(1)).findByMakeId(makeId);
    }

    @Test
    void testGetModelsByMake_WithInvalidMakeId() {
        // Arrange
        Long makeId = 999L;
        when(modelRepository.findByMakeId(makeId)).thenReturn(Collections.emptyList());

        // Act
        List<Model> actualModels = catalogQueryService.getModelsByMake(makeId);

        // Assert
        assertNotNull(actualModels);
        assertTrue(actualModels.isEmpty());
        verify(modelRepository, times(1)).findByMakeId(makeId);
    }

    @Test
    void testGetModels_Success() {
        // Arrange
        List<Model> expectedModels = Arrays.asList(testModel1, testModel2);
        when(modelRepository.findAll()).thenReturn(expectedModels);

        // Act
        List<Model> actualModels = catalogQueryService.getModels();

        // Assert
        assertNotNull(actualModels);
        assertEquals(2, actualModels.size());
        verify(modelRepository, times(1)).findAll();
    }

    @Test
    void testGetModels_EmptyList() {
        // Arrange
        when(modelRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Model> actualModels = catalogQueryService.getModels();

        // Assert
        assertNotNull(actualModels);
        assertTrue(actualModels.isEmpty());
        verify(modelRepository, times(1)).findAll();
    }

    @Test
    void testGetModelByName_WithValidName() {
        // Arrange
        String modelName = "Camry";
        List<Model> expectedModels = Arrays.asList(testModel1);
        when(modelRepository.findByName(modelName)).thenReturn(expectedModels);

        // Act
        List<Model> actualModels = catalogQueryService.getModelByName(modelName);

        // Assert
        assertNotNull(actualModels);
        assertEquals(1, actualModels.size());
        assertEquals("Camry", actualModels.get(0).getName());
        verify(modelRepository, times(1)).findByName(modelName);
    }

    @Test
    void testGetModelByName_WithInvalidName() {
        // Arrange
        String modelName = "NonExistent";
        when(modelRepository.findByName(modelName)).thenReturn(Collections.emptyList());

        // Act
        List<Model> actualModels = catalogQueryService.getModelByName(modelName);

        // Assert
        assertNotNull(actualModels);
        assertTrue(actualModels.isEmpty());
        verify(modelRepository, times(1)).findByName(modelName);
    }

    @Test
    void testGetModelByName_WithNullName() {
        // Arrange
        when(modelRepository.findByName(null)).thenReturn(Collections.emptyList());

        // Act
        List<Model> actualModels = catalogQueryService.getModelByName(null);

        // Assert
        assertNotNull(actualModels);
        assertTrue(actualModels.isEmpty());
        verify(modelRepository, times(1)).findByName(null);
    }
}

