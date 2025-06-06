package com.usta.startup.controllers;

import com.usta.startup.entities.UsuarioEntity;
import com.usta.startup.models.services.UsuarioService;
import com.usta.startup.models.services.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolService rolService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        model.addAttribute("title", "Lista de Usuarios");
        return "usuario/listar"; // solo si tienes esta vista
    }

    // Acceso desde el panel CRUD interno
    @GetMapping("/crear")
    public String crearForm(Model model) {
        model.addAttribute("usuario", new UsuarioEntity());
        model.addAttribute("roles", rolService.findAll());
        return "login/registrar"; // usa registrar.html en carpeta login
    }

    @PostMapping("/crear")
    public String guardar(@ModelAttribute("usuario") UsuarioEntity usuario, RedirectAttributes redirectAttrs) {
        usuarioService.save(usuario);
        redirectAttrs.addFlashAttribute("mensajeExito", "Usuario creado con éxito");
        return "redirect:/usuario";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        UsuarioEntity usuario = usuarioService.findById(id);
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", rolService.findAll());
        return "login/registrar"; // también usa registrar.html
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute("usuario") UsuarioEntity usuario, RedirectAttributes redirectAttrs) {
        usuarioService.actualizarUsuario(usuario);
        redirectAttrs.addFlashAttribute("mensajeExito", "Usuario actualizado con éxito");
        return "redirect:/usuario";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        usuarioService.deleteById(id);
        redirectAttrs.addFlashAttribute("mensajeExito", "Usuario eliminado");
        return "redirect:/usuario";
    }

    // Registro público
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new UsuarioEntity());
        model.addAttribute("roles", rolService.findAll());
        return "login/registrar"; // también usa esta vista
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute("usuario") UsuarioEntity usuario, RedirectAttributes redirectAttrs) {
        usuarioService.save(usuario);
        redirectAttrs.addFlashAttribute("mensaje", "Usuario registrado exitosamente");
        return "redirect:/login";
    }

    @GetMapping("/emprendedor")
    public String mostrarPerfilEmprendedor() {
        return "perfil/perfilEmprendedor";
    }

    @GetMapping("/administrador")
    public String mostrarPerfilAdministrador() {
        return "perfil/perfilAdministrador";
    }


}