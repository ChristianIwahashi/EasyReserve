package com.example.ReservaSalaLab.service;

import com.example.ReservaSalaLab.model.Admin;
import com.example.ReservaSalaLab.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private final AdminRepository repo;
    private final PasswordEncoder passwordEncoder;
    @Value("${app.security.root-admin-cpf}")
    private String rootAdminCpf;

    public AdminService(AdminRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    //Método de serviço que retorna uma lista de todos os Admins
    public List<Admin> listall(){
        List<Admin> lista = repo.findAll();
        List<Admin> listaSemRoot = lista.stream().
                filter(admin -> !admin.getCpf().equals(rootAdminCpf))
                .collect(Collectors.toList());
        return listaSemRoot;
    }

    //Método de serviço para salvar ou atualizar
    public void save(Admin admin){
        if (rootAdminCpf.equals(admin.getCpf())) {
            throw new IllegalArgumentException("A conta do administrador Root não pode ser modificada.");
        }
        String senha = admin.getSenha();
        String senhaCripto = passwordEncoder.encode(senha);
        admin.setSenha(senhaCripto);
        repo.save(admin);
    }

    //Método de serviço para buscar um admin pelo CPF
    public Optional<Admin> get(String cpf){
        return repo.findById(cpf);
    }

    //Método de serviço para excluir um admin pelo CPF
    public void delete(String cpf){
        if (rootAdminCpf.equals(cpf)) {
            throw new IllegalArgumentException("A conta do administrador Root não pode ser excluída.");
        }
        repo.deleteById(cpf);
    }

    public boolean verificarLogin(String cpf, String senha){
        Optional<Admin> adminOpt = repo.findById(cpf);
        if (adminOpt.isEmpty()){
            return false;
        }
        Admin admin = adminOpt.get();
        return passwordEncoder.matches(senha,admin.getSenha());
    }

    public boolean cpfJaExiste(String cpf) {
        return repo.existsById(cpf);
    }
}
