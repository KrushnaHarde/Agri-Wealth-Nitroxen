package com.nitroxen.demo.repository;

import com.nitroxen.demo.entity.AgronomistReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
public interface AgronomistReportRepository extends JpaRepository<AgronomistReport, Long> {
    
    List<AgronomistReport> findByAgronomistId(Long agronomistId);
    
    List<AgronomistReport> findByFarmId(Long farmId);
}