package com.proyectogaes.repository;

import com.proyectogaes.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
      List<Inventario> findByCantidadGreaterThan(int cantidad);

      // Cuenta los registros donde la cantidad sea menor o igual a un límite

      long countByCantidadLessThanEqual(int limite);
}
