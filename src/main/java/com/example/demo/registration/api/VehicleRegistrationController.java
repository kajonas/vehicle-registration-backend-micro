package com.example.demo.registration.api;

import com.example.demo.registration.application.VehicleRegistrationService;
import com.example.demo.registration.domain.Vehicle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicle")
@CrossOrigin(origins = "*")
public class VehicleRegistrationController {

    @Autowired
    private VehicleRegistrationService vehicleRegistrationService;

    @GetMapping("/vehicles")
    public List<Vehicle> getVehicles() {
        return vehicleRegistrationService.getAllVehicles();
    }

    @PostMapping("/save")
    public ResponseEntity<Vehicle> saveVehicle(@RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(vehicleRegistrationService.saveVehicle(vehicle));
    }
}
