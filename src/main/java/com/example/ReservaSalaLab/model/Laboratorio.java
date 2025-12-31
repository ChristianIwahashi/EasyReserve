package com.example.ReservaSalaLab.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "laboratorios")
public class Laboratorio extends Sala{
    @Min(value = 0, message = "A Quantidade de Computadores não pode ser negativa")
    private int qtdComputadores;
    private boolean tv;

    public Laboratorio() {
    }

    public int getQtdComputadores() {
        return qtdComputadores;
    }

    public void setQtdComputadores(int qtdComputadores) {
        this.qtdComputadores = qtdComputadores;
    }

    public boolean isTv() {
        return tv;
    }

    public void setTv(boolean tv) {
        this.tv = tv;
    }
}
