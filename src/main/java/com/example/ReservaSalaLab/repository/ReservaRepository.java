package com.example.ReservaSalaLab.repository;

import com.example.ReservaSalaLab.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    @Query("SELECT r FROM Reserva r WHERE r.sala.id = :salaId AND r.id <> :reservaId AND r.dataInicio < :dataFinal AND " +
            "r.dataFinal > :dataInicio")
    List<Reserva> findConflitosDeHorario(
            @Param("salaId") Long salaId,
            @Param("reservaId") Long reservaId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFinal") LocalDateTime dataFinal
    );
}
