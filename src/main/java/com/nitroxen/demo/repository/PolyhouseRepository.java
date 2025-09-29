package com.nitroxen.demo.repository;

import com.nitroxen.demo.entity.Farm;
import com.nitroxen.demo.entity.Polyhouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolyhouseRepository extends JpaRepository<Polyhouse, Long> {
    List<Polyhouse> findByFarm(Farm farm);
    List<Polyhouse> findByFarmId(Long farmId);
    boolean existsByNameAndFarmId(String name, Long farmId);
    int countByFarmId(Long farmId);
}
