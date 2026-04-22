package com.example.demo.config;

import com.example.demo.catalog.domain.Make;
import com.example.demo.catalog.domain.Model;
import com.example.demo.catalog.repository.MakeRepository;
import com.example.demo.catalog.repository.ModelRepository;
import com.example.demo.registration.domain.Vehicle;
import com.example.demo.registration.repository.VehicleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    @Autowired
    private MakeRepository makeRepository;
    @Autowired
    private ModelRepository modelRepository;
    @Autowired
    private VehicleRepository vehicleRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void loadData() {
        // Makes
        Make toyota = new Make();
        toyota.setName("Toyota");
        makeRepository.save(toyota);

        Make ford = new Make();
        ford.setName("Ford");
        makeRepository.save(ford);

        // Models
        Model camry = new Model();
        camry.setName("Camry");
        camry.setMake(toyota);
        modelRepository.save(camry);

        Model corolla = new Model();
        corolla.setName("Corolla");
        corolla.setMake(toyota);
        modelRepository.save(corolla);

        Model tundra = new Model();
        tundra.setName("Tundra");
        tundra.setMake(toyota);
        modelRepository.save(tundra);

        Model mustang = new Model();
        mustang.setName("Mustang");
        mustang.setMake(ford);
        modelRepository.save(mustang);

        // Seed Vehicle
        Vehicle sample = new Vehicle();
        sample.setOwner("Alice Johnson");
        sample.setVin("1HGCM82633A000001");
        sample.setYear(2023);
        sample.setMake(toyota);
        sample.setModel(camry);
        vehicleRepository.save(sample);
    }
}
