package com.proyectogaes.service;

import org.springframework.stereotype.Service;
import com.proyectogaes.entity.ReporteFalla;
import com.proyectogaes.repository.ReporteFallaRepository;
import java.util.List;

@Service
public class ReporteFallaService {

    // Inyectamos el repositorio para poder hablar con la DB
    private final ReporteFallaRepository repository;

    public ReporteFallaService(ReporteFallaRepository repository) {
        this.repository = repository;
    }

    // LISTAR: Ahora sí devuelve la lista real de la DB
    public List<ReporteFalla> listar() {
        return repository.findAll();
    }

    // GUARDAR: Persiste el objeto en MySQL
    public void guardar(ReporteFalla reporte) {
        repository.save(reporte);
    }

    // OBTENER: Busca por ID
    public ReporteFalla obtener(Long id) {
        return repository.findById(id).orElse(null);
    }

    // ELIMINAR: Borra el registro
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}