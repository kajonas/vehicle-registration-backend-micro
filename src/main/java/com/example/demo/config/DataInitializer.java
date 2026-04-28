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
        Make toyota = getOrCreateMake("Toyota");
        Make ford = getOrCreateMake("Ford");

        // Models
        Model camry = getOrCreateModel("Camry", toyota);
        getOrCreateModel("Corolla", toyota);
        getOrCreateModel("Tundra", toyota);
        getOrCreateModel("Mustang", ford);

        // Seed Vehicle
        Vehicle sample = new Vehicle();
        sample.setOwner("Alice Johnson");
        sample.setVin("1HGCM82633A000001");
        sample.setYear(2023);
        sample.setMake(toyota);
        sample.setModel(camry);
        vehicleRepository.save(sample);
    }

    private Make getOrCreateMake(String name) {
        return makeRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    Make make = new Make();
                    make.setName(name);
                    return makeRepository.save(make);
                });
    }

    private Model getOrCreateModel(String name, Make make) {
        return modelRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    Model model = new Model();
                    model.setName(name);
                    model.setMake(make);
                    return modelRepository.save(model);
                });
    }
}
