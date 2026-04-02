package com.proyectogaes.service;

import com.proyectogaes.entity.*;
import com.proyectogaes.repository.MantenimientoRepository;
import com.proyectogaes.repository.MaquinaRepository;
import com.proyectogaes.repository.InventarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class MantenimientoService {

    private final MantenimientoRepository mantenimientoRepo;
    private final MaquinaRepository       maquinaRepo;
    private final InventarioRepository     inventarioRepo;

    public MantenimientoService(MantenimientoRepository mantenimientoRepo,
                                 MaquinaRepository maquinaRepo,
                                 InventarioRepository inventarioRepo) {
        this.mantenimientoRepo = mantenimientoRepo;
        this.maquinaRepo       = maquinaRepo;
        this.inventarioRepo    = inventarioRepo;
    }

    // ── Mis Mantenimientos activos ─────────────────────────────
    // PHP: MisMantenimientos::index()
    public List<MantenimientoDTO> getMisMantenimientos(Long idUsuario) {
        return mantenimientoRepo.findAsignadosByTecnico(idUsuario);
    }

    // ── Historial finalizado del técnico ───────────────────────
    // PHP: obtenerHistorialPorTecnico($idTecnico, 'Finalizado')
    public List<MantenimientoDTO> getHistorialTecnico(Long idUsuario) {
        return mantenimientoRepo.findHistorialByTecnico(idUsuario, "Finalizado");
    }

    // ── Datos para la pantalla de iniciar ─────────────────────
    // PHP: $this->mantenimientosModel->select(...)->join('maquinas',...)->where(...)->first()
    public MantenimientoDTO getDetalleConMaquina(Long id) {
        return mantenimientoRepo.findDetalleConMaquina(id)
                .orElseThrow(() -> new RuntimeException("Mantenimiento no encontrado: " + id));
    }

    // ── Datos de la máquina para mostrar el intervalo actual ──
    // PHP: $maquina_info = $this->maquinasModel->find($mantenimiento['id_maquina'])
    public Maquina getMaquinaDeMantenimiento(Long idMantenimiento) {
        Mantenimiento m = mantenimientoRepo.findById(idMantenimiento)
                .orElseThrow(() -> new RuntimeException("Mantenimiento no encontrado"));
        return maquinaRepo.findById(m.getIdMaquina())
                .orElseThrow(() -> new RuntimeException("Máquina no encontrada"));
    }

    // ── Repuestos disponibles para el modal ────────────────────
    // PHP: $this->repuestosModel->where('cantidad >', 0)->findAll()
    public List<Inventario> getRepuestosDisponibles() {
        return inventarioRepo.findByCantidadGreaterThan(0);
    }

    // ── Detalle completo para detalle.html ─────────────────────
    // PHP: MantenimientosModel::obtenerDetalleCompleto($id)
    // Hace JOIN con maquinas + usuarios + detalle_mantenimiento (LEFT JOIN)
    public MantenimientoDetalleDTO getDetalleCompleto(Long id) {
        return mantenimientoRepo.findDetalleCompleto(id).orElse(null);
    }

    // ── Finalizar mantenimiento completo ───────────────────────
    // PHP: MisMantenimientos::terminar() — hace todo de una sola vez:
    //   1. Actualiza descripción y estado del mantenimiento → 'Finalizado'
    //   2. Descuenta repuestos usados del inventario
    //   3. Actualiza la máquina (fecha_ultimo_mantenimiento + intervalo)
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
        // PHP: foreach ($repuestosUsados as $id_repuesto => $cantidad) { if ($cantidad > 0) ... }
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
        // PHP: $this->maquinasModel->update($id_maquina, ['fecha_ultimo_mantenimiento'=>..., 'intervalo_mantenimiento'=>...])
        maquinaRepo.findById(m.getIdMaquina()).ifPresent(maquina -> {
            maquina.setFechaUltimoMantenimiento(fechaUltimo);
            maquina.setIntervaloMantenimiento(intervalo);
            maquinaRepo.save(maquina);
        });
    }

    // ── Contadores para el dashboard ───────────────────────────
    // PHP: $mantenimientos->where('id_usuario', $idUsuario)->countAllResults()
    public long countAsignados(Long idUsuario) {
        return mantenimientoRepo.countByIdUsuario(idUsuario);
    }

    public long countPendientes(Long idUsuario) {
        return mantenimientoRepo.countByIdUsuarioAndEstadoNot(idUsuario, "Finalizado");
    }
}