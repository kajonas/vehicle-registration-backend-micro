package com.example.demo.catalog.application;

import com.example.demo.catalog.domain.Make;
import com.example.demo.catalog.domain.Model;
import com.example.demo.catalog.repository.MakeRepository;
import com.example.demo.catalog.repository.ModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogQueryService {

    @Autowired
    private MakeRepository makeRepository;

    @Autowired
    private ModelRepository modelRepository;

    public List<Make> getMakes() {
        return makeRepository.findAll();
    }

    public List<Model> getModelsByMake(Long makeId) {
        return modelRepository.findByMakeId(makeId);
    }

    public List<Model> getModels() {
        return modelRepository.findAll();
    }

    public List<Model> getModelByName(String modelName) {
        return modelRepository.findByName(modelName);
    }
}

