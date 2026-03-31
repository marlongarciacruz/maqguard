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
 
   
    public List<MantenimientoDTO> getMisMantenimientos(Long idUsuario) {
        return mantenimientoRepo.findAsignadosByTecnico(idUsuario);
    }
 
    public List<MantenimientoDTO> getHistorialTecnico(Long idUsuario) {
        return mantenimientoRepo.findHistorialByTecnico(idUsuario, "Finalizado");
    }
 
    
    public MantenimientoDTO getDetalleConMaquina(Long id) {
        return mantenimientoRepo.findDetalleConMaquina(id)
                .orElseThrow(() -> new RuntimeException("Mantenimiento no encontrado: " + id));
    }
 
    
    public Maquina getMaquinaDeMantenimiento(Long idMantenimiento) {
        Mantenimiento m = mantenimientoRepo.findById(idMantenimiento)
                .orElseThrow(() -> new RuntimeException("Mantenimiento no encontrado"));
        return maquinaRepo.findById(m.getIdMaquina())
                .orElseThrow(() -> new RuntimeException("Máquina no encontrada"));
    }
 
    
    public List<Inventario> getRepuestosDisponibles() {
        return inventarioRepo.findByCantidadGreaterThan(0);
    }
 
  
    public MantenimientoDetalleDTO getDetalleCompleto(Long id) {
        return mantenimientoRepo.findDetalleCompleto(id).orElse(null);
    }
 

    @Transactional
    public void finalizarCompleto(Long idMantenimiento,
                                   String descripcionTrabajo,
                                   LocalDate fechaUltimo,
                                   Integer intervalo,
                                   Map<Long, Integer> repuestosUsados) {
 
        
                                    
        Mantenimiento m = mantenimientoRepo.findById(idMantenimiento)
                .orElseThrow(() -> new RuntimeException("Mantenimiento no encontrado"));
        m.setDescripcionTrabajo(descripcionTrabajo);
        m.setEstado("Finalizado");
        mantenimientoRepo.save(m);
 

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
 
      
        maquinaRepo.findById(m.getIdMaquina()).ifPresent(maquina -> {
            maquina.setFechaUltimoMantenimiento(fechaUltimo);
            maquina.setIntervaloMantenimiento(intervalo);
            maquinaRepo.save(maquina);
        });
    }
 
    
    public long countAsignados(Long idUsuario) {
        return mantenimientoRepo.countByIdUsuario(idUsuario);
    }
 
    public long countPendientes(Long idUsuario) {
        return mantenimientoRepo.countByIdUsuarioAndEstadoNot(idUsuario, "Finalizado");
    }
}