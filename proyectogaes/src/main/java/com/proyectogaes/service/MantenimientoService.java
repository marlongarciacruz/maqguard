package com.proyectogaes.service;

import com.proyectogaes.entity.*;
import com.proyectogaes.repository.MantenimientoRepository;
import com.proyectogaes.repository.InventarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class MantenimientoService {

    private final MantenimientoRepository mantenimientoRepo;
    private final InventarioRepository inventarioRepo;

    public MantenimientoService(MantenimientoRepository mantenimientoRepo,
                                InventarioRepository inventarioRepo) {
        this.mantenimientoRepo = mantenimientoRepo;
        this.inventarioRepo    = inventarioRepo;
    }

    // ── Mis Mantenimientos activos ─────────────────────────────
    public List<Mantenimiento> getMisMantenimientos(Long idUsuario) {
        return mantenimientoRepo.buscarPorUsuario(idUsuario.intValue());
    }

    // ── Historial finalizado del técnico ───────────────────────
    public List<Mantenimiento> getHistorialTecnico(Long idUsuario) {
        return mantenimientoRepo.buscarPorTecnicoYEstado(idUsuario.intValue(), "Finalizado");
    }

    // ── Detalle de un mantenimiento ────────────────────────────
    public Mantenimiento getDetalle(Long id) {
        return mantenimientoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Mantenimiento no encontrado: " + id));
    }

    // ── Repuestos disponibles para el modal ────────────────────
    public List<Inventario> getRepuestosDisponibles() {
        return inventarioRepo.findByCantidadGreaterThan(0);
    }

    // ── Finalizar mantenimiento ────────────────────────────────
    @Transactional
    public void finalizarCompleto(Long idMantenimiento,
                                   String descripcionTrabajo,
                                   LocalDate fechaUltimo,
                                   Integer intervalo,
                                   Map<Long, Integer> repuestosUsados) {

        // 1. Actualizar mantenimiento
        Mantenimiento m = mantenimientoRepo.findById(idMantenimiento)
                .orElseThrow(() -> new RuntimeException("Mantenimiento no encontrado"));
        m.setDescripcionTrabajo(descripcionTrabajo);
        m.setEstado("Finalizado");
        mantenimientoRepo.save(m);

        // 2. Descontar repuestos
        if (repuestosUsados != null) {
            repuestosUsados.forEach((idRepuesto, cantidad) -> {
                if (cantidad != null && cantidad > 0) {
                    inventarioRepo.findById(idRepuesto).ifPresent(rep -> {
                        rep.setCantidad(rep.getCantidad() - cantidad);
                        inventarioRepo.save(rep);
                    });
                }
            });
        }

        // 3. Actualizar máquina
        if (m.getMaquina() != null) {
            m.getMaquina().setFechaUltimoMantenimiento(fechaUltimo);
            m.getMaquina().setIntervaloMantenimiento(intervalo);
        }
        mantenimientoRepo.save(m);
    }

    // ── Contadores para el dashboard ───────────────────────────
    public long countAsignados(Long idUsuario) {
        return mantenimientoRepo.buscarPorUsuario(idUsuario.intValue()).size();
    }

    public long countPendientes(Long idUsuario) {
        return mantenimientoRepo.buscarPorTecnicoYEstado(
                idUsuario.intValue(), "Finalizado").size();
    }
}