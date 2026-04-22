package com.example.demo.registration.application;

import com.example.demo.registration.domain.Vehicle;
import com.example.demo.registration.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class VehicleRegistrationService {


    @Autowired
    private VehicleRepository vehicleRepo;

    public List<Vehicle> getAllVehicles() {
        return vehicleRepo.findAll();
    }

    public Vehicle saveVehicle(Vehicle vehicle) {
        return vehicleRepo.save(vehicle);
    }
}

