package com.example.ReservaSalaLab.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "salas")
@Inheritance(strategy = InheritanceType.JOINED)
public class Sala {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "O Tipo não pode ficar em branco.")
    private String tipo;
    @Positive(message = "A Capacidade deve ser um número positivo.")
    private int capacidade;
    @Positive(message = "O Andar deve ser um número positivo.")
    private int andar;
    @Positive(message = "O Corredor deve ser um número positivo.")
    private int corredor;

    @OneToMany(
            mappedBy = "sala",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )

    @OrderBy("dataInicio ASC")
    private List<Reserva> reservas = new ArrayList<>();

    public Sala() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }

    public int getAndar() {
        return andar;
    }

    public void setAndar(int andar) {
        this.andar = andar;
    }

    public int getCorredor() {
        return corredor;
    }

    public void setCorredor(int corredor) {
        this.corredor = corredor;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }
}
