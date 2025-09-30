package com.nitroxen.demo.repository;

import com.nitroxen.demo.entity.Agronomist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
public interface AgronomistRepository extends JpaRepository<Agronomist, Long> {
    
    List<Agronomist> findByNameContainingIgnoreCase(String name);
    
    List<Agronomist> findByEmailContainingIgnoreCase(String email);
    
    List<Agronomist> findBySpecializationContainingIgnoreCase(String specialization);
}