package com.example.ReservaSalaLab.repository;

import com.example.ReservaSalaLab.model.Laboratorio;
import com.example.ReservaSalaLab.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {
    @Query("SELECT s FROM Sala s WHERE TYPE(s) = Sala")
    List<Sala> findSalas();

    @Query("SELECT l FROM Laboratorio l")
    List<Laboratorio> findLaboratorios();
}
