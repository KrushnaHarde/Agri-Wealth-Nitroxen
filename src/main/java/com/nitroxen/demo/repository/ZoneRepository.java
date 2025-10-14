package com.nitroxen.demo.repository;

import com.nitroxen.demo.entity.Polyhouse;
import com.nitroxen.demo.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {
    List<Zone> findByPolyhouseId(Long polyhouseId);
    List<Zone> findByPolyhouse(Polyhouse polyhouse);
    boolean existsByNameAndPolyhouseId(String name, Long polyhouseId);
    int countByPolyhouseId(Long polyhouseId);
}
