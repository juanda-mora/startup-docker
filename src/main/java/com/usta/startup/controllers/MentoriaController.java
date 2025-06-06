package com.usta.startup.controllers;

import com.usta.startup.entities.MentoriaEntity;
import com.usta.startup.entities.UsuarioEntity;
import com.usta.startup.models.services.MentoriaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/mentoria")
public class MentoriaController {

    @Autowired
    private MentoriaService mentoriaService;

    @GetMapping("/mentor/sesiones")
    public String verSesionesMentor(Model model, HttpSession session) {
        UsuarioEntity mentor = (UsuarioEntity) session.getAttribute("usuario");

        if (mentor == null) {
            return "redirect:/login";
        }

        List<MentoriaEntity> sesiones = mentoriaService.findAll()
                .stream()
                .filter(s -> s.getUsuario().getId().equals(mentor.getId()))
                .toList();

        model.addAttribute("usuario", mentor);
        model.addAttribute("sesiones", sesiones);
        return "mentoria/sesionMentor";
    }

    @GetMapping("/crearSesion")
    public String mostrarFormularioCrearSesion(Model model, HttpSession session) {
        UsuarioEntity usuario = (UsuarioEntity) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("mentoria", new MentoriaEntity());
        return "mentoria/crearSesion";
    }

    @PostMapping("/crear-sesion")
    public String guardarSesion(@ModelAttribute("mentoria") MentoriaEntity mentoria, HttpSession session) {
        UsuarioEntity usuario = (UsuarioEntity) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        mentoria.setUsuario(usuario);
        mentoriaService.save(mentoria);

        return "redirect:/mentoria/sesiones";
    }

    @GetMapping("/sesion/{id}")
    public String verDetalleSesion(@PathVariable Long id, Model model, HttpSession session) {
        UsuarioEntity usuario = (UsuarioEntity) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        MentoriaEntity sesion = mentoriaService.findById(id);
        if (sesion == null || !sesion.getUsuario().getId().equals(usuario.getId())) {
            return "redirect:/mentoria/sesiones";
        }

        model.addAttribute("sesion", sesion);
        return "mentoria/vistaSesion";
    }

//    @GetMapping("/emprendedor/mentorias")
//    public String sesionEmprendedor(HttpSession session, Model model) {
//        UsuarioEntity emprendedor = (UsuarioEntity) session.getAttribute("usuario");
//
//        if (emprendedor == null || !"EMPRENDEDOR".equalsIgnoreCase(emprendedor.getRol().getNombre())) {
//            return "redirect:/login";
//        }
//
//        List<MentoriaEntity> disponibles = mentoriaService.obtenerMentoriasDisponibles();
//        List<MentoriaEntity> reservadas = mentoriaService.findByEmprendedorId(emprendedor.getId());
//
//        model.addAttribute("disponibles", disponibles);
//        model.addAttribute("reservadas", reservadas);
//        return "mentoria/mentoriaEmprendedor";
//    }
//
//    @PostMapping("/sesion/programar/{id}")
//    public String programarMentoria(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttrs) {
//        UsuarioEntity emprendedor = (UsuarioEntity) session.getAttribute("usuario");
//
//        if (emprendedor == null || !"EMPRENDEDOR".equalsIgnoreCase(emprendedor.getRol().getNombre())) {
//            redirectAttrs.addFlashAttribute("error", "Debes iniciar sesión como emprendedor.");
//            return "redirect:/login";
//        }
//
//        MentoriaEntity mentoria = mentoriaService.findById(id);
//
//        if (mentoria == null) {
//            redirectAttrs.addFlashAttribute("error", "La mentoría no existe.");
//            return "redirect:/mentoria/sesionEmprendedor";
//        }
//
//        if (mentoria.isReservada()) {
//            redirectAttrs.addFlashAttribute("error", "La mentoría ya ha sido reservada.");
//            return "redirect:/mentoria/sesionEmprendedor";
//        }
//
//        mentoria.setReservada(true);
//        mentoria.setEmprendedor(emprendedor);
//        mentoriaService.save(mentoria);
//
//        redirectAttrs.addFlashAttribute("success", "Mentoría reservada con éxito.");
//        return "redirect:/mentoria/sesionEmprendedor";
//    }

    @PostMapping("/sesion/cancelar/{id}")
    public String cancelarMentoria(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttrs) {
        UsuarioEntity emprendedor = (UsuarioEntity) session.getAttribute("usuario");

        if (emprendedor == null || !"EMPRENDEDOR".equalsIgnoreCase(emprendedor.getRol().getNombre())) {
            redirectAttrs.addFlashAttribute("error", "Debes iniciar sesión como emprendedor.");
            return "redirect:/login";
        }

        MentoriaEntity mentoria = mentoriaService.findById(id);

        if (mentoria == null || !mentoria.isReservada() || mentoria.getEmprendedor() == null ||
                !mentoria.getEmprendedor().getId().equals(emprendedor.getId())) {
            redirectAttrs.addFlashAttribute("error", "No puedes cancelar esta mentoría.");
            return "redirect:/mentoria/sesionEmprendedor";
        }

        mentoria.setReservada(false);
        mentoria.setEmprendedor(null);
        mentoriaService.save(mentoria);

        redirectAttrs.addFlashAttribute("success", "Mentoría cancelada con éxito.");
        return "redirect:/mentoria/sesionEmprendedor";
    }
}