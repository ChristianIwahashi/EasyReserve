package com.example.ReservaSalaLab.model;

import com.example.ReservaSalaLab.validation.FormValidation;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "admins")
public class Admin {
    @Id
    @NotBlank(message = "O CPF não pode ficar em branco.")
    @Size(min = 11, max = 11, message = "O CPF deve conter exatamente 11 dígitos.", groups = FormValidation.class)
    private String cpf;
    @NotBlank(message = "O Nome não pode ficar em branco.")
    private String nome;
    @NotBlank(message = "A Senha não pode ficar em branco.")
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%._&-]{8,20}$", message = "A Senha deve conter 8-20 caracteres, usando apenas " +
            "letras, números e os símbolos !@#$%._&-", groups = FormValidation.class)
    @Pattern(regexp = ".*[0-9].*", message = "A senha deve conter pelo menos um número.", groups = FormValidation.class)
    @Pattern(regexp = ".*[a-zA-Z].*", message = "A senha deve conter pelo menos uma letra.", groups = FormValidation.class)
    private String senha;
    @Transient
    private String confirmarSenha;
    public Admin() {
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getConfirmarSenha() {
        return confirmarSenha;
    }

    public void setConfirmarSenha(String confirmarSenha) {
        this.confirmarSenha = confirmarSenha;
    }
}
