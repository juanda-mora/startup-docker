package com.usta.startup.controllers;

import com.usta.startup.entities.UsuarioEntity;
import com.usta.startup.models.services.RolService;
import com.usta.startup.models.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolService rolService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Mostrar login
    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        model.addAttribute("usuario", new UsuarioEntity());
        return "login/login";
    }

    // Procesar login
    @PostMapping("/login")
    public String procesarLogin(@ModelAttribute("usuario") UsuarioEntity usuarioForm,
                                Model model,
                                HttpSession session) {

        Optional<UsuarioEntity> optionalUsuario = usuarioService.findByCorreo(usuarioForm.getCorreo());

        if (optionalUsuario.isPresent()) {
            UsuarioEntity usuario = optionalUsuario.get();

            if (passwordEncoder.matches(usuarioForm.getPassword(), usuario.getPassword())) {
                session.setAttribute("usuario", usuario);
                String rol = usuario.getRol().getNombre();
                return switch (rol) {
                    case "ADMINISTRADOR" -> "indexAdministrador";
                    case "MENTOR" -> "indexMentor";
                    case "EMPRENDEDOR" -> "indexEmprendedor";
                    default -> "login/login";
                };
            }
        }

        model.addAttribute("error", "Correo o contraseña inválidos");
        return "login/login";
    }

    // Cerrar sesión
    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // Mostrar formulario de registro
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new UsuarioEntity());
        model.addAttribute("roles", rolService.findAll());
        return "login/registrar";
    }

    // Procesar registro con imagen
    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute("usuario") UsuarioEntity usuarioForm,
                                   @RequestParam("imagenFile") MultipartFile imagenFile,
                                   Model model) {
        Optional<UsuarioEntity> existente = usuarioService.findByCorreo(usuarioForm.getCorreo());

        if (existente.isPresent()) {
            model.addAttribute("error", "El correo ya está registrado.");
            model.addAttribute("roles", rolService.findAll());
            return "login/registrar";
        }

        if (imagenFile == null || imagenFile.isEmpty()) {
            model.addAttribute("error", "Debes subir una imagen válida.");
            model.addAttribute("roles", rolService.findAll());
            return "login/registrar";
        }

        try {
            // Subida a imgbb
            String apiKey = "a14d7c45f344c50f48e0355c8794c900"; // <-- Puedes mover esto a propiedades
            byte[] imageBytes = imagenFile.getBytes();
            String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            String body = "key=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8)
                    + "&image=" + URLEncoder.encode(imageBase64, StandardCharsets.UTF_8);

            HttpEntity<String> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.imgbb.com/1/upload", request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                JSONObject json = new JSONObject(response.getBody());
                String imageUrl = json.getJSONObject("data").getString("url");
                usuarioForm.setImagen(imageUrl);
            } else {
                model.addAttribute("error", "Error al subir la imagen al servidor.");
                model.addAttribute("roles", rolService.findAll());
                return "login/registrar";
            }

            // Encriptar contraseña y guardar usuario
            usuarioForm.setPassword(passwordEncoder.encode(usuarioForm.getPassword()));
            usuarioService.save(usuarioForm);

            model.addAttribute("mensaje", "Registro exitoso. Ahora puedes iniciar sesión.");
            return "login/login";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error en el registro: " + e.getMessage());
            model.addAttribute("roles", rolService.findAll());
            return "login/registrar";
        }
    }

    @GetMapping("/index")
    public String indexAdmin() {
        return "index";
    }

    @GetMapping("/indexMentor")
    public String indexMentor() {
        return "indexMentor";
    }

    @GetMapping("/indexEmprendedor")
    public String indexEmprendedor() {
        return "indexEmprendedor";
    }
}