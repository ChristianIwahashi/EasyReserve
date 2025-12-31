package com.example.ReservaSalaLab.controller;

import com.example.ReservaSalaLab.model.Laboratorio;
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
@RequestMapping("/laboratorios")
public class LaboratorioController {
    private final SalaService service;

    public LaboratorioController(SalaService service) {
        this.service = service;
    }

    @GetMapping("/agenda/{id}")
    public String agendaLaboratorio(@PathVariable Long id, Model model, HttpSession session){
        Optional<Sala> salaOpt = service.get(id);
        Laboratorio laboratorio = (Laboratorio) salaOpt.get();
        model.addAttribute("laboratorio", laboratorio);
        model.addAttribute("reservas", laboratorio.getReservas());

        if (session.getAttribute("adminLogado") != null) {
            model.addAttribute("admin", true);
        } else {
            model.addAttribute("admin", false);
        }
        return "laboratorios/agenda";
    }

    //Método para listar os Laboratórios
    @GetMapping("/list")
    public String listLaboratorios(Model model, HttpSession session){
        //Chamada do service para buscar todos os Laboratórios cadastrados
        model.addAttribute("laboratorios", service.listallLaboratorios());

        if (session.getAttribute("adminLogado") != null) {
            model.addAttribute("admin", true);
        } else {
            model.addAttribute("admin", false);
        }
        return "laboratorios/list";
    }

    //Método para inicializar um formulário
    @GetMapping("/new")
    public String showCreateForm(Model model, HttpSession session){
        String cpf = (String) session.getAttribute("adminLogado");
        if (cpf == null) {
            return "redirect:/admins/login";
        }

        model.addAttribute("laboratorio", new Laboratorio());
        return "laboratorios/form";
    }

    @PostMapping("/save")
    public String saveLaboratorio(@Valid Laboratorio laboratorio, BindingResult result, RedirectAttributes ra){
        if (result.hasErrors()) {
            return "laboratorios/form";
        }
        service.save(laboratorio);
        ra.addFlashAttribute("success", "Laboratório salvo com sucesso");
        return "redirect:/laboratorios/list";
    }

    //Mapeia para URLs como edit/123
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes ra, HttpSession session){
        String cpf = (String) session.getAttribute("adminLogado");
        if (cpf == null) {
            return "redirect:/admins/login";
        }

        var opt = service.get(id); //Tenta buscar o id no banco
        if (opt.isPresent()){ //Se o Laboratório for encontrado
            model.addAttribute("laboratorio", opt.get());
            return "laboratorios/form";
        } else {
            ra.addFlashAttribute("error", "Laboratório não encontrado");
            return "redirect:/laboratorios/list";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteLaboratorio(@PathVariable Long id, Model model, RedirectAttributes ra, HttpSession session){
        String cpf = (String) session.getAttribute("adminLogado");
        if (cpf == null) {
            return "redirect:/admins/login";
        }

        service.delete(id); //Chama o service para remover o Laboratório
        ra.addFlashAttribute("success", "Laboratório removido");
        return "redirect:/laboratorios/list";
    }
}
