package com.usta.startup.controllers;

import com.usta.startup.entities.RolEntity;
import com.usta.startup.models.services.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/rol")
public class RolController {

    @Autowired
    private RolService rolService;

    @GetMapping
    public String listar(Model model) {
        List<RolEntity> lista = rolService.findAll();
        model.addAttribute("roles", lista);
        model.addAttribute("title", "Lista de Roles");
        return "/rol/listar";
    }

    @GetMapping("/crear")
    public String crearForm(Model model) {
        model.addAttribute("rol", new RolEntity());
        model.addAttribute("title", "Crear Rol");
        return "/rol/form";
    }

    @PostMapping("/crear")
    public String guardar(@ModelAttribute("rol") RolEntity rol, RedirectAttributes redirectAttrs) {
        rolService.save(rol);
        redirectAttrs.addFlashAttribute("mensajeExito", "Rol creado con éxito");
        return "redirect:/rol";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        RolEntity rol = rolService.findById(id);
        model.addAttribute("rol", rol);
        model.addAttribute("title", "Editar Rol");
        return "/rol/form";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute("rol") RolEntity rol, RedirectAttributes redirectAttrs) {
        rolService.actualizarRol(rol);
        redirectAttrs.addFlashAttribute("mensajeExito", "Rol actualizado con éxito");
        return "redirect:/rol";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        rolService.deleteById(id);
        redirectAttrs.addFlashAttribute("mensajeExito", "Rol eliminado");
        return "redirect:/rol";
    }
}