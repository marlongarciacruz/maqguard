package com.proyectogaes.service;

import com.proyectogaes.entity.Maquina;
import com.proyectogaes.repository.MaquinasRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MaquinaService {

    private final MaquinasRepository repository;

    public MaquinaService(MaquinasRepository repository) {
        this.repository = repository;
    }

    public List<Maquina> listar() {
        return repository.findAll();
    }

    public Maquina obtener(Long id) {
        return repository.findById(id).orElse(null);
    }
}