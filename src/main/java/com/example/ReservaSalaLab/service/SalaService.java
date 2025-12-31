package com.example.ReservaSalaLab.service;

import com.example.ReservaSalaLab.model.Laboratorio;
import com.example.ReservaSalaLab.model.Sala;
import com.example.ReservaSalaLab.repository.SalaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SalaService {
    private final SalaRepository repo;

    public SalaService(SalaRepository repo) {
        this.repo = repo;
    }

    //Método de serviço que retorna uma lista de todas as Salas
    public List<Sala> listall(){
        return repo.findAll();
    }

    //Método de serviço para salvar ou atualizar
    public void save(Sala sala){
        repo.save(sala);
    }

    //Método de serviço para buscar uma sala pelo ID
    public Optional<Sala> get(Long id){
        return repo.findById(id);
    }

    //Método de serviço para excluir uma sala pelo ID
    public void delete(Long id){
        repo.deleteById(id);
    }

    //Método de serviço que retorna uma lista com apenas as Salas
    public List<Sala> listallSalas() {
        return repo.findSalas();
    }

    //Método de serviço que retorna uma lista de todos os Laboratórios
    public List<Laboratorio> listallLaboratorios() {
        return repo.findLaboratorios();
    }
}
