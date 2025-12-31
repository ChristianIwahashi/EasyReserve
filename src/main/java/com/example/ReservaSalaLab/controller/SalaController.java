package com.example.ReservaSalaLab.controller;

import com.example.ReservaSalaLab.model.Sala;
import com.example.ReservaSalaLab.service.SalaService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/salas")
public class SalaController {
    private final SalaService service;

    public SalaController(SalaService service) {
        this.service = service;
    }

    @GetMapping("/agenda/{id}")
    public String agendaSala(@PathVariable Long id, Model model, HttpSession session){
        Optional<Sala> salaOpt = service.get(id);
        Sala sala = salaOpt.get();
        model.addAttribute("sala", sala);
        model.addAttribute("reservas", sala.getReservas());

        if (session.getAttribute("adminLogado") != null) {
            model.addAttribute("admin", true);
        } else {
            model.addAttribute("admin", false);
        }
        return "salas/agenda";
    }

    //Método para listar as Salas
    @GetMapping("/list")
    public String listSalas(Model model, HttpSession session){
        //Chamada do service para buscar todos as salas cadastradas
        model.addAttribute("salas", service.listallSalas());

        if (session.getAttribute("adminLogado") != null) {
            model.addAttribute("admin", true);
        } else {
            model.addAttribute("admin", false);
        }
        return "salas/list";
    }

    //Método para inicializar um formulário
    @GetMapping("/new")
    public String showCreateForm(Model model, HttpSession session){
        String cpf = (String) session.getAttribute("adminLogado");
        if (cpf == null) {
            return "redirect:/admins/login";
        }

        model.addAttribute("sala", new Sala());
        return "salas/form";
    }

    @PostMapping("/save")
    public String saveSala(@Valid Sala sala, BindingResult result, RedirectAttributes ra){
        if (result.hasErrors()) {
            return "salas/form";
        }
        service.save(sala);
        ra.addFlashAttribute("success", "Sala salva com sucesso");
        return "redirect:/salas/list";
    }

    //Mapeia para URLs como edit/123
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes ra, HttpSession session){
        String cpf = (String) session.getAttribute("adminLogado");
        if (cpf == null) {
            return "redirect:/admins/login";
        }

        var opt = service.get(id); //Tenta buscar o id no banco
        if (opt.isPresent()){ //Se a Sala for encontrada
            model.addAttribute("sala", opt.get());
            return "salas/form";
        } else {
            ra.addFlashAttribute("error", "Sala não encontrada");
            return "redirect:/salas/list";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteSala(@PathVariable Long id, Model model, RedirectAttributes ra, HttpSession session){
        String cpf = (String) session.getAttribute("adminLogado");
        if (cpf == null) {
            return "redirect:/admins/login";
        }

        service.delete(id); //Chama o service para remover a Sala
        ra.addFlashAttribute("success", "Sala removida");
        return "redirect:/salas/list";
    }
}
