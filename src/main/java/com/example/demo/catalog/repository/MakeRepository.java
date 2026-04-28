package com.example.demo.catalog.repository;

import com.example.demo.catalog.domain.Make;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MakeRepository extends JpaRepository<Make, Long> {
    Optional<Make> findByNameIgnoreCase(String name);
}
