package com.example.ReservaSalaLab.controller;

import com.example.ReservaSalaLab.model.Admin;
import com.example.ReservaSalaLab.service.AdminService;
import com.example.ReservaSalaLab.validation.FormValidation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admins")
public class AdminController {
    private final AdminService service;

    public AdminController(AdminService service) {
        this.service = service;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model, HttpSession session){
        model.addAttribute("admin", new Admin());

        if (session.getAttribute("adminLogado") != null) {
            model.addAttribute("troca", true);
        } else {
            model.addAttribute("troca", false);
        }
        return "admins/login";
    }

    @PostMapping("/login/verificar")
    public String verificarLogin(@ModelAttribute Admin admin, RedirectAttributes ra, HttpServletRequest request, Model model){
        boolean loginValido = service.verificarLogin(admin.getCpf(), admin.getSenha());

        if (loginValido) {
            ra.addFlashAttribute("success", "Administrador logado com sucesso");
            request.getSession().setAttribute("adminLogado", admin.getCpf());
            return "redirect:/admins/menu";
        } else {
            model.addAttribute("error", "CPF ou Senha inválidos");
            return "admins/login";
        }
    }

    @GetMapping("/menu")
    public String menuController(Model model, HttpSession session){
        String cpf = (String) session.getAttribute("adminLogado");
        if (cpf == null) {
            return "redirect:/admins/login";
        }

        Admin adminLogado = service.get(cpf).orElse(null);
        if (adminLogado == null) {
            session.invalidate();
            return "redirect:/admins/login";
        }
        model.addAttribute("adminLogado", adminLogado);
        return "admins/menu";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

    //Método para listar os Admins
    @GetMapping("/list")
    public String listAdmins(Model model, HttpSession session){
        String cpf = (String) session.getAttribute("adminLogado");
        if (cpf == null) {
            return "redirect:/admins/login";
        }

        //Chamada do service para buscar todos os admins cadastrados
        model.addAttribute("admins", service.listall());
        return "admins/list";
    }

    //Método para inicializar um formulário
    @GetMapping("/new")
    public String showCreateForm(Model model, HttpSession session){
        String cpf = (String) session.getAttribute("adminLogado");
        if (cpf == null) {
            return "redirect:/admins/login";
        }

        model.addAttribute("admin", new Admin());
        return "admins/form";
    }

    @PostMapping("/save")
    public String saveAdmin(@Validated(FormValidation.class) Admin admin, BindingResult result, RedirectAttributes ra, Model model,
                            @RequestParam(name = "isUpdate", required = false) String isUpdate){
        if (isUpdate == null) {
            if (service.cpfJaExiste(admin.getCpf())) {
                result.rejectValue("cpf", "cpf.duplicate", "Este CPF já está cadastrado");
            }
        }
        if (!admin.getSenha().equals(admin.getConfirmarSenha())) {
            result.rejectValue("confirmarSenha", "password.mismatch", "As senhas não conferem");
        }
        if (result.hasErrors()) {
            if (isUpdate != null) {
                model.addAttribute("isUpdate", true);
            }
            return "admins/form";
        }

        try {
            service.save(admin);
            ra.addFlashAttribute("success", "Administrador salvo com sucesso");
            return "redirect:/admins/list";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "admins/list";
        }
    }

    //Mapeia para URLs como edit/123
    @GetMapping("/edit/{cpf}")
    public String showEditForm(@PathVariable String cpf, Model model, RedirectAttributes ra, HttpSession session){
        String cpfhttp = (String) session.getAttribute("adminLogado");
        if (cpfhttp == null) {
            return "redirect:/admins/login";
        }

        var opt = service.get(cpf); //Tenta buscar o cpf no banco
        if (opt.isPresent()){ //Se o admin for encontrado
            model.addAttribute("admin", opt.get());
            model.addAttribute("isUpdate", true);
            return "admins/form";
        } else {
            ra.addFlashAttribute("error", "Administrador não encontrado");
            return "redirect:/admins/list";
        }
    }

    @GetMapping("/delete/{cpf}")
    public String deleteAdmin(@PathVariable String cpf, Model model, RedirectAttributes ra, HttpSession session){
        String cpfhttp = (String) session.getAttribute("adminLogado");
        if (cpfhttp == null) {
            return "redirect:/admins/login";
        }

        try {
            service.delete(cpf); //Chama o service para remover o admin
            ra.addFlashAttribute("success", "Administrador removido");
            return "redirect:/admins/list";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "admins/list";
        }
    }
}
