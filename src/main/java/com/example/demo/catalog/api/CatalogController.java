package com.example.demo.catalog.api;

import com.example.demo.catalog.application.CatalogQueryService;
import com.example.demo.catalog.domain.Make;
import com.example.demo.catalog.domain.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CatalogController {

    @Autowired
    private CatalogQueryService catalogQueryService;

    @GetMapping("/makes")
    public List<Make> getMakes() {
        return catalogQueryService.getAllMakes();
    }

    @GetMapping("/models-by-name/{makeName}")
    public List<Model> getModelsByName(@PathVariable String makeName) {
        return catalogQueryService.getModelsByName(makeName);
    }

    @GetMapping("/models")
    public List<Model> getModels() {
        return catalogQueryService.getModels();
    }

    @GetMapping("/models/{makeId}")
    public List<Model> getModelsByMake(@PathVariable Long makeId) {
        return catalogQueryService.getModelsByMake(makeId);
    }
}

