package com.usta.startup.controllers;

import com.usta.startup.entities.UsuarioEntity;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String home() { return "index"; }

    @GetMapping("/administrador")
    public String adminHome() {
        return "indexAdministrador";
    }

    @GetMapping("/mentor")
    public String mentorHome() {
        return "indexMentor";
    }
/*--
    @GetMapping("/emprendedor")
    public String emprendedorHome() {
        return "indexEmprendedor";
    }
--*/
    @GetMapping("/home")
    public String redireccionHomePorRol(HttpSession session) {
        UsuarioEntity usuario = (UsuarioEntity) session.getAttribute("usuario");

        if (usuario != null && usuario.getRol() != null) {
            String rol = usuario.getRol().getNombre().toLowerCase();

            return switch (rol) {
                case "administrador" -> "redirect:/administrador";
                case "mentor" -> "redirect:/mentor";
              /*--  case "emprendedor" -> "redirect:/emprendedor"; --*/
                default -> "redirect:/login";
            };
        }

        return "redirect:/login";
    }
}

