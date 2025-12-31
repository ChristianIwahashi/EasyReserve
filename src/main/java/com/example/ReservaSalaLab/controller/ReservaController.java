package com.example.ReservaSalaLab.controller;

import com.example.ReservaSalaLab.exception.ConflitoReservaException;
import com.example.ReservaSalaLab.model.Sala;
import com.example.ReservaSalaLab.service.SalaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import com.example.ReservaSalaLab.model.Reserva;
import com.example.ReservaSalaLab.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Optional;

@Controller
@RequestMapping("/reservas")
public class ReservaController {
    private final ReservaService reservasService;
    private final SalaService salaService;

    public ReservaController(ReservaService reservasService, SalaService salaService) {
        this.reservasService = reservasService;
        this.salaService = salaService;
    }

    @GetMapping("/escolher-tipo")
    public String escolherTipo(Model model, HttpSession session) {
        if (session.getAttribute("adminLogado") != null) {
            model.addAttribute("admin", true);
        } else {
            model.addAttribute("admin", false);
        }
        return "reservas/escolher-tipo";
    }

    @GetMapping("/select-sala")
    public String selectSala(Model model) {
        model.addAttribute("salas", salaService.listallSalas());
        return "reservas/select-sala";
    }

    @GetMapping("/select-laboratorio")
    public String selectLaboratorio(Model model) {
        model.addAttribute("laboratorios", salaService.listallLaboratorios());
        return "reservas/select-laboratorio";
    }

    //Método para listar as Reservas
    @GetMapping("/list")
    public String listReservas(Model model, HttpSession session){
        //Chamada do service para buscar todas as reservas cadastradas
        model.addAttribute("reservas", reservasService.listall());

        if (session.getAttribute("adminLogado") != null) {
            model.addAttribute("admin", true);
        } else {
            model.addAttribute("admin", false);
        }
        return "reservas/list";
    }

    //Método para inicializar um formulário
    @GetMapping("/new/{salaId}")
    public String showCreateForm(@PathVariable Long salaId, Model model, RedirectAttributes ra){
        Optional<Sala> salaOpt = salaService.get(salaId);

        if (salaOpt.isEmpty()) {
            ra.addFlashAttribute("error", "A sala ou laboratório selecionado não foi encontrado.");
            return "redirect:/reservas/escolher-tipo";
        }

        Sala sala = salaOpt.get();
        Reserva novaReserva = new Reserva();
        novaReserva.setSala(sala);
        model.addAttribute("reservas", sala.getReservas());
        model.addAttribute("reserva", novaReserva);
        return "reservas/form";
    }

    @PostMapping("/save")
    public String saveReserva(@Valid Reserva reserva, BindingResult result, RedirectAttributes ra, Model model){
        Sala salaCompleta = null;

        if (reserva.getSala() != null && reserva.getSala().getId() != null) {
            salaCompleta = salaService.get(reserva.getSala().getId()).orElse(null);
            reserva.setSala(salaCompleta);
        }
        if (result.hasErrors()) {
            if (salaCompleta != null) {
                model.addAttribute("reservas", salaCompleta.getReservas());
            } else {
                model.addAttribute("reservas", Collections.emptyList());
            }
            return "reservas/form";
        }

        try{
            reservasService.save(reserva);
            ra.addFlashAttribute("success", "Reserva salva com sucesso");
            return "redirect:/reservas/list";
        } catch (ConflitoReservaException e) {
            model.addAttribute("erroConflito", e.getMessage());
            if (salaCompleta != null) {
                model.addAttribute("reservas", salaCompleta.getReservas());
            }
            return "reservas/form";
        }
    }

    //Mapeia para URLs como edit/123
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes ra){
        Optional<Reserva> opt = reservasService.get(id);
        if (opt.isPresent()){ //Se a Reserva for encontrada
            Reserva reserva = opt.get();
            model.addAttribute("reservas", reserva.getSala().getReservas());
            model.addAttribute("reserva", reserva);
            return "reservas/form";
        } else {
            ra.addFlashAttribute("error", "Reserva não encontrada");
            return "redirect:/reservas/list";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteReserva(@PathVariable Long id, RedirectAttributes ra){
        reservasService.delete(id); //Chama o service para remover a Reserva
        ra.addFlashAttribute("success", "Reserva removida");
        return "redirect:/reservas/list";
    }
}
