package com.usta.startup.controllers;

import com.usta.startup.entities.ConvocatoriaEntity;
import com.usta.startup.entities.MentoriaEntity;
import com.usta.startup.entities.UsuarioEntity;
import com.usta.startup.models.services.MentoriaService;
import com.usta.startup.models.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class PerfilController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MentoriaService mentoriaService;

    // ===================== PERFIL ADMIN =====================
    @GetMapping("/admin/perfil")
    public String perfilAdmin(Model model, HttpSession session) {
        UsuarioEntity usuario = (UsuarioEntity) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        return "perfil/perfilAdministrador";
    }

    // ===================== PERFIL MENTOR =====================
    @GetMapping("/mentor/perfil")
    public String perfilMentor(Model model, HttpSession session) {
        UsuarioEntity usuario = (UsuarioEntity) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        return "perfil/perfilMentor";
    }

    @GetMapping("/emprendedor/perfil")
    public String perfilEmprendedor(Model model, HttpSession session) {
        UsuarioEntity usuario = (UsuarioEntity) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        return "perfil/perfilEmprendedor";
    }

    // ===================== EDITAR PERFIL =====================
    @GetMapping("/editarPerfil")
    public String editarPerfilSesion(HttpSession session) {
        UsuarioEntity usuario = (UsuarioEntity) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        return "redirect:/editarPerfil/" + usuario.getId();
    }

    @GetMapping("/editarPerfil/{id}")
    public String editarPerfil(@PathVariable Long id, Model model, HttpSession session) {
        UsuarioEntity usuario = usuarioService.findById(id);
        if (usuario == null) {
            return "redirect:/";
        }
        model.addAttribute("usuario", usuario);

        // Obtener usuario desde sesión para tener el rol actualizado
        UsuarioEntity usuarioSesion = (UsuarioEntity) session.getAttribute("usuario");
        if (usuarioSesion != null && usuarioSesion.getRol() != null) {
            model.addAttribute("rol", usuarioSesion.getRol().getNombre().toUpperCase());
        } else {
            model.addAttribute("rol", "INVITADO"); // Por si acaso
        }

        return "perfil/editarPerfil";
    }

    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(
            @RequestParam("id") Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("correo") String correo,
            @RequestParam("password") String password,
            @RequestParam("imagen") MultipartFile imagenFile,
            HttpSession session
    ) throws IOException {

        UsuarioEntity usuarioExistente = usuarioService.findById(id);

        if (usuarioExistente != null) {
            usuarioExistente.setNombre(nombre);
            usuarioExistente.setCorreo(correo);

            // Solo actualizar la contraseña si no está vacía
            if (password != null && !password.isBlank()) {
                usuarioExistente.setPassword(passwordEncoder.encode(password));
            }

            // Subida de imagen a ImgBB si se proporciona una nueva
            if (!imagenFile.isEmpty()) {
                String urlImagen = subirImagenAImgBB(imagenFile);
                if (urlImagen != null) {
                    usuarioExistente.setImagen(urlImagen);
                }
            }

            usuarioService.save(usuarioExistente);
            session.setAttribute("usuario", usuarioExistente);
        }
        String rol = usuarioExistente.getRol().getNombre(); // Asegúrate que este método existe
        String redirectRol = switch (rol.toLowerCase()) {
            case "administrador" -> "admin";
            case "mentor" -> "mentor";
            case "emprendedor" -> "emprendedor";
            default -> "login";
        };

        return "redirect:/" + redirectRol + "/perfil";

    }


    // ===================== CARGA DE IMAGEN A IMGBB =====================
    private String subirImagenAImgBB(MultipartFile imagen) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("https://api.imgbb.com/1/upload");
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            builder.addTextBody("key", "015fce6b31623ef978860915ee9b7ffc", ContentType.TEXT_PLAIN);
            builder.addBinaryBody("image", imagen.getInputStream(), ContentType.DEFAULT_BINARY, imagen.getOriginalFilename());

            httpPost.setEntity(builder.build());

            HttpResponse response = httpClient.execute(httpPost);
            String responseString = EntityUtils.toString(response.getEntity());

            if (response.getStatusLine().getStatusCode() == 200) {
                JSONObject json = new JSONObject(responseString);
                if (json.getBoolean("success")) {
                    return json.getJSONObject("data").getString("url");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}