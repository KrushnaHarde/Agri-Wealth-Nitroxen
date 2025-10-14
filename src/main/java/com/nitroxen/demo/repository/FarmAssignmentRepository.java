package com.nitroxen.demo.repository;

import com.nitroxen.demo.entity.FarmAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FarmAssignmentRepository extends JpaRepository<FarmAssignment, Long> {

    /**
     * Find all active farm assignments for a specific manager
     */
    List<FarmAssignment> findByManagerIdAndActiveTrue(Long managerId);

    /**
     * Find an active farm assignment for a specific farm and manager
     */
    boolean existsByFarmIdAndManagerIdAndActiveTrue(Long farmId, Long managerId);

    /**
     * Get farm IDs assigned to a manager
     */
    @Query("SELECT fa.farm.id FROM FarmAssignment fa WHERE fa.manager.id = :managerId AND fa.active = true")
    List<Long> findAssignedFarmIdsByManagerId(Long managerId);
}
