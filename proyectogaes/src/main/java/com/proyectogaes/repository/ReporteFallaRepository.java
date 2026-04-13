package com.proyectogaes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; 
import com.proyectogaes.entity.ReporteFalla;

@Repository
public interface ReporteFallaRepository extends JpaRepository<ReporteFalla, Long> {
    long countByEstado(String estado);
    
}