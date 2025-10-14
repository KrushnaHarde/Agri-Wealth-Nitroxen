package com.nitroxen.demo.repository;

import com.nitroxen.demo.entity.Farm;
import com.nitroxen.demo.entity.Reservoir;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservoirRepository extends JpaRepository<Reservoir, Long> {
    List<Reservoir> findByFarmId(Long farmId);
    List<Reservoir> findByFarm(Farm farm);
    boolean existsByNameAndFarmId(String name, Long farmId);
}
