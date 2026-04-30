package com.example.demo.registration.application;

import com.example.demo.catalog.domain.Make;
import com.example.demo.catalog.domain.Model;
import com.example.demo.registration.domain.Vehicle;
import com.example.demo.registration.repository.VehicleRepository;
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
class VehicleRegistrationServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleRegistrationService vehicleRegistrationService;

    private Vehicle testVehicle1;
    private Vehicle testVehicle2;
    private Make testMake;
    private Model testModel;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testMake = new Make();
        testMake.setId(1L);
        testMake.setName("Toyota");

        testModel = new Model();
        testModel.setId(1L);
        testModel.setName("Camry");
        testModel.setMake(testMake);

        testVehicle1 = new Vehicle();
        testVehicle1.setId(1L);
        testVehicle1.setOwner("John Doe");
        testVehicle1.setVin("JTDKN3AU5E0123456");
        testVehicle1.setYear(2021);
        testVehicle1.setMake(testMake);
        testVehicle1.setModel(testModel);

        testVehicle2 = new Vehicle();
        testVehicle2.setId(2L);
        testVehicle2.setOwner("Jane Smith");
        testVehicle2.setVin("JTDKN3AU5E0123457");
        testVehicle2.setYear(2022);
        testVehicle2.setMake(testMake);
        testVehicle2.setModel(testModel);
    }

    @Test
    void testGetAllVehicles_Success() {
        // Arrange
        List<Vehicle> expectedVehicles = Arrays.asList(testVehicle1, testVehicle2);
        when(vehicleRepository.findAll()).thenReturn(expectedVehicles);

        // Act
        List<Vehicle> actualVehicles = vehicleRegistrationService.getAllVehicles();

        // Assert
        assertNotNull(actualVehicles);
        assertEquals(2, actualVehicles.size());
        assertEquals("John Doe", actualVehicles.get(0).getOwner());
        assertEquals("Jane Smith", actualVehicles.get(1).getOwner());
        verify(vehicleRepository, times(1)).findAll();
    }

    @Test
    void testGetAllVehicles_EmptyList() {
        // Arrange
        when(vehicleRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Vehicle> actualVehicles = vehicleRegistrationService.getAllVehicles();

        // Assert
        assertNotNull(actualVehicles);
        assertTrue(actualVehicles.isEmpty());
        verify(vehicleRepository, times(1)).findAll();
    }

    @Test
    void testSaveVehicle_Success() {
        // Arrange
        Vehicle vehicleToSave = new Vehicle();
        vehicleToSave.setOwner("John Doe");
        vehicleToSave.setVin("JTDKN3AU5E0123456");
        vehicleToSave.setYear(2021);
        vehicleToSave.setMake(testMake);
        vehicleToSave.setModel(testModel);

        Vehicle savedVehicle = new Vehicle();
        savedVehicle.setId(1L);
        savedVehicle.setOwner("John Doe");
        savedVehicle.setVin("JTDKN3AU5E0123456");
        savedVehicle.setYear(2021);
        savedVehicle.setMake(testMake);
        savedVehicle.setModel(testModel);

        when(vehicleRepository.save(vehicleToSave)).thenReturn(savedVehicle);

        // Act
        Vehicle actualSavedVehicle = vehicleRegistrationService.saveVehicle(vehicleToSave);

        // Assert
        assertNotNull(actualSavedVehicle);
        assertEquals(1L, actualSavedVehicle.getId());
        assertEquals("John Doe", actualSavedVehicle.getOwner());
        assertEquals("JTDKN3AU5E0123456", actualSavedVehicle.getVin());
        assertEquals(2021, actualSavedVehicle.getYear());
        verify(vehicleRepository, times(1)).save(vehicleToSave);
    }

    @Test
    void testSaveVehicle_WithAllFields() {
        // Arrange
        Vehicle vehicleToSave = testVehicle1;
        vehicleToSave.setId(null); // Reset ID as it would be null for a new vehicle

        Vehicle savedVehicle = testVehicle1;

        when(vehicleRepository.save(vehicleToSave)).thenReturn(savedVehicle);

        // Act
        Vehicle actualSavedVehicle = vehicleRegistrationService.saveVehicle(vehicleToSave);

        // Assert
        assertNotNull(actualSavedVehicle);
        assertEquals("John Doe", actualSavedVehicle.getOwner());
        assertEquals("JTDKN3AU5E0123456", actualSavedVehicle.getVin());
        assertEquals(2021, actualSavedVehicle.getYear());
        assertNotNull(actualSavedVehicle.getMake());
        assertNotNull(actualSavedVehicle.getModel());
        verify(vehicleRepository, times(1)).save(vehicleToSave);
    }

    @Test
    void testSaveVehicle_WithMinimalFields() {
        // Arrange
        Vehicle vehicleToSave = new Vehicle();
        vehicleToSave.setOwner("Test Owner");
        vehicleToSave.setVin("TESTVINN123456");

        Vehicle savedVehicle = new Vehicle();
        savedVehicle.setId(3L);
        savedVehicle.setOwner("Test Owner");
        savedVehicle.setVin("TESTVINN123456");

        when(vehicleRepository.save(vehicleToSave)).thenReturn(savedVehicle);

        // Act
        Vehicle actualSavedVehicle = vehicleRegistrationService.saveVehicle(vehicleToSave);

        // Assert
        assertNotNull(actualSavedVehicle);
        assertEquals(3L, actualSavedVehicle.getId());
        assertEquals("Test Owner", actualSavedVehicle.getOwner());
        assertEquals("TESTVINN123456", actualSavedVehicle.getVin());
        verify(vehicleRepository, times(1)).save(vehicleToSave);
    }

    @Test
    void testSaveVehicle_UpdateExistingVehicle() {
        // Arrange
        Vehicle vehicleToUpdate = testVehicle1;
        vehicleToUpdate.setOwner("Updated Owner");

        Vehicle updatedVehicle = testVehicle1;
        updatedVehicle.setOwner("Updated Owner");

        when(vehicleRepository.save(vehicleToUpdate)).thenReturn(updatedVehicle);

        // Act
        Vehicle actualUpdatedVehicle = vehicleRegistrationService.saveVehicle(vehicleToUpdate);

        // Assert
        assertNotNull(actualUpdatedVehicle);
        assertEquals(1L, actualUpdatedVehicle.getId());
        assertEquals("Updated Owner", actualUpdatedVehicle.getOwner());
        verify(vehicleRepository, times(1)).save(vehicleToUpdate);
    }

    @Test
    void testSaveVehicle_ReturnsExactSavedObject() {
        // Arrange
        Vehicle vehicleToSave = new Vehicle();
        vehicleToSave.setOwner("Test");
        vehicleToSave.setVin("TEST123");

        Vehicle savedVehicle = new Vehicle();
        savedVehicle.setId(99L);
        savedVehicle.setOwner("Test");
        savedVehicle.setVin("TEST123");

        when(vehicleRepository.save(vehicleToSave)).thenReturn(savedVehicle);

        // Act
        Vehicle result = vehicleRegistrationService.saveVehicle(vehicleToSave);

        // Assert
        assertEquals(99L, result.getId());
        assertEquals(savedVehicle, result);
        verify(vehicleRepository, times(1)).save(vehicleToSave);
    }

    @Test
    void testGetAllVehicles_WithMultipleVehicles() {
        // Arrange
        Vehicle vehicle3 = new Vehicle();
        vehicle3.setId(3L);
        vehicle3.setOwner("Bob Johnson");
        vehicle3.setVin("JTDKN3AU5E0123458");
        vehicle3.setYear(2023);

        List<Vehicle> expectedVehicles = Arrays.asList(testVehicle1, testVehicle2, vehicle3);
        when(vehicleRepository.findAll()).thenReturn(expectedVehicles);

        // Act
        List<Vehicle> actualVehicles = vehicleRegistrationService.getAllVehicles();

        // Assert
        assertNotNull(actualVehicles);
        assertEquals(3, actualVehicles.size());
        assertEquals("Bob Johnson", actualVehicles.get(2).getOwner());
        verify(vehicleRepository, times(1)).findAll();
    }
}

