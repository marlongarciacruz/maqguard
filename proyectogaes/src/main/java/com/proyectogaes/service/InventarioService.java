package com.proyectogaes.service;

import com.proyectogaes.entity.Inventario;
import com.proyectogaes.repository.InventarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InventarioService {

    private final InventarioRepository repo;

    public InventarioService(InventarioRepository repo) {
        this.repo = repo;
    }

    public List<Inventario> listar() {
        return repo.findAll();
    }

    public void guardar(Inventario inv) {
        repo.save(inv);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    public Inventario obtener(Long id) {
        return repo.findById(id).orElse(null);
    }
}