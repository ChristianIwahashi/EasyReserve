package com.example.ReservaSalaLab.service;

import com.example.ReservaSalaLab.exception.ConflitoReservaException;
import com.example.ReservaSalaLab.model.Reserva;
import com.example.ReservaSalaLab.repository.ReservaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {
    private final ReservaRepository repo;

    public ReservaService(ReservaRepository repo) {
        this.repo = repo;
    }

    //Método de serviço que retorna uma lista de todas as Reservas
    public List<Reserva> listall(){
        return repo.findAll();
    }

    //Método de serviço para salvar ou atualizar
    public void save(Reserva reserva){
        Long reservaId = reserva.getId() == null ? -1L : reserva.getId();

        List<Reserva> conflitos = repo.findConflitosDeHorario(
                reserva.getSala().getId(),
                reservaId,
                reserva.getDataInicio(),
                reserva.getDataFinal()
        );

        if (!conflitos.isEmpty()) {
            throw new ConflitoReservaException("Já existe uma reserva para este espaço no horário solicitado.");
        }
        if (reserva.getDataFinal().isBefore(reserva.getDataInicio())) {
            throw new ConflitoReservaException("O fim da Reserva não pode ser anterior ao de início.");
        }
        repo.save(reserva);
    }

    //Método de serviço para buscar uma sala pelo ID
    public Optional<Reserva> get(Long id){
        return repo.findById(id);
    }

    //Método de serviço para excluir uma reserva pelo ID
    public void delete(Long id){
        repo.deleteById(id);
    }
}
