package com.example.demo.catalog.repository;

import com.example.demo.catalog.domain.Model;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ModelRepository extends JpaRepository<Model, Long> {
    List<Model> findByMakeId(Long makeId);
    List<Model> findByName(String name);
    Optional<Model> findByNameIgnoreCase(String name);
}
