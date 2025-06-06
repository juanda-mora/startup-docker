package com.usta.startup.controllers;

import com.usta.startup.entities.MentoriaEntity;
import com.usta.startup.entities.SesionMentoriaEntity;
import com.usta.startup.entities.StartupEntity;
import com.usta.startup.entities.UsuarioEntity;
import com.usta.startup.models.services.MentoriaService;
import com.usta.startup.models.services.SesionMentoriaService;
import com.usta.startup.models.services.StartupService;
import com.usta.startup.repository.SesionMentoriaRepository;
import com.usta.startup.repository.StartupRepository;
import com.usta.startup.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Controller
public class SesionMentoriaController {
    @Autowired
    private SesionMentoriaService sesionMentoriaService;

    @Autowired
    private StartupService startupService;

    @Autowired
    private MentoriaService mentoriaService;

    @Autowired
    private SesionMentoriaRepository sesionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private StartupRepository startupRepository;

    @GetMapping("/sesion")
    public String listarSesiones(Model model) {
        List<SesionMentoriaEntity> lista = sesionRepository.findAll();
        model.addAttribute("sesiones", lista);
        return "sesion/listarSesiones";
    }

    @GetMapping("/crearSesion")
    public String crearSesion(Model model, HttpSession session) {
        // === Lógica para obtener el rol y agregarlo al modelo ===
        UsuarioEntity usuario = (UsuarioEntity) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol() != null) {
            model.addAttribute("rol", usuario.getRol().getNombre().toUpperCase());
        } else {
            model.addAttribute("rol", "INVITADO"); // respaldo por si no hay sesión
        }

        // === Datos necesarios para el formulario de crear sesión ===
        model.addAttribute("sesion", new SesionMentoriaEntity());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("startups", startupRepository.findAll());

        return "sesion/crearSesion";
    }
    @PostMapping("/crearSesion")
    public String guardarSesion(@ModelAttribute SesionMentoriaEntity sesion,
                                @RequestParam("usuarioId") Long usuarioId,
                                @RequestParam("startupId") Long startupId,
                                @RequestParam("imagenFile") MultipartFile imagenFile,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        try {
            UsuarioEntity usuario = usuarioRepository.findById(usuarioId).orElse(null);
            StartupEntity startup = startupRepository.findById(startupId).orElse(null);

            sesion.setUsuario(usuario);
            sesion.setStartup(startup);
            sesion.setFechaHora(new Date());

            if (!imagenFile.isEmpty()) {
                String urlImagen = subirImagenAImgBB(imagenFile);
                sesion.setImagen(urlImagen != null ? urlImagen : "https://via.placeholder.com/150");
            }

            sesionRepository.save(sesion);
            redirectAttributes.addFlashAttribute("success", "Sesión guardada correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al guardar la sesión.");
            return "redirect:/crearSesion";
        }

        UsuarioEntity usuarioSesion = (UsuarioEntity) session.getAttribute("usuario");

        if (usuarioSesion != null && usuarioSesion.getRol() != null) {
            String rol = usuarioSesion.getRol().getNombre().toLowerCase();
            return switch (rol) {
                case "administrador" -> "redirect:/administrador";
                case "mentor" -> "redirect:/mentor";
                default -> "redirect:/";  // Redirección por defecto
            };
        }

        return "redirect:/";
    }

    @GetMapping("/editarSesion/{id}")
    public String editarSesion(@PathVariable("id") Long id, Model model, HttpSession session) {
        SesionMentoriaEntity sesion = sesionRepository.findById(id).orElse(null);
        if (sesion == null) {
            return "redirect:/sesion";
        }

        // Obtener usuario de la sesión
        UsuarioEntity usuario = (UsuarioEntity) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol() != null) {
            model.addAttribute("rol", usuario.getRol().getNombre().toUpperCase());
        } else {
            model.addAttribute("rol", "INVITADO"); // valor de respaldo
        }

        model.addAttribute("sesion", sesion);
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("startups", startupRepository.findAll());

        return "sesion/editarSesion";
    }

    @PostMapping("/editarSesion/{id}")
    public String actualizarSesion(@ModelAttribute SesionMentoriaEntity sesion,
                                   @RequestParam("usuarioId") Long usuarioId,
                                   @RequestParam("startupId") Long startupId,
                                   @RequestParam("imagenFile") MultipartFile imagenFile,
                                   @PathVariable("id") Long id,
                                   RedirectAttributes redirectAttributes) {
        try {
            SesionMentoriaEntity existente = sesionRepository.findById(id).orElse(null);
            if (existente != null) {
                existente.setTitulo(sesion.getTitulo());
                existente.setNotas(sesion.getNotas());
                existente.setReservada(sesion.isReservada());
                existente.setFechaHora(new Date());

                existente.setUsuario(usuarioRepository.findById(usuarioId).orElse(null));
                existente.setStartup(startupRepository.findById(startupId).orElse(null));

                if (!imagenFile.isEmpty()) {
                    String nuevaUrl = subirImagenAImgBB(imagenFile);
                    if (nuevaUrl != null) {
                        existente.setImagen(nuevaUrl);
                    }
                }

                sesionRepository.save(existente);
                redirectAttributes.addFlashAttribute("success", "Sesión actualizada.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Sesión no encontrada.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la sesión.");
        }

        return "redirect:/sesion";
    }

    @PostMapping("/eliminarSesion/{id}")
    public String eliminarSesion(@PathVariable("id") Long id,
                                 RedirectAttributes redirectAttributes) {
        if (id > 0) {
            SesionMentoriaEntity sesion = sesionRepository.findById(id).orElse(null);

            if (sesion != null) {
                boolean asociadaAStartup = sesion.getStartup() != null;
                boolean asociadaAUsuario = sesion.getUsuario() != null;

                if (asociadaAStartup || asociadaAUsuario) {
                    redirectAttributes.addFlashAttribute("error",
                            "No se puede eliminar la sesión: está asociada a un usuario o startup.");
                } else {
                    try {
                        sesionRepository.deleteById(id);
                        redirectAttributes.addFlashAttribute("success", "Sesión eliminada correctamente.");
                    } catch (Exception e) {
                        e.printStackTrace();
                        redirectAttributes.addFlashAttribute("error", "Error al eliminar la sesión.");
                    }
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Sesión no encontrada.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "ID inválido.");
        }

        return "redirect:/sesion";
    }


    private String subirImagenAImgBB(MultipartFile imagen) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("https://api.imgbb.com/1/upload");

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("key", "015fce6b31623ef978860915ee9b7ffc", ContentType.TEXT_PLAIN);
            builder.addBinaryBody("image", imagen.getInputStream(), ContentType.DEFAULT_BINARY, imagen.getOriginalFilename());

            httpPost.setEntity(builder.build());
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();

            if (response.getStatusLine().getStatusCode() == 200) {
                String responseString = EntityUtils.toString(responseEntity);
                JSONObject json = new JSONObject(responseString);
                if (json.getBoolean("success")) {
                    return json.getJSONObject("data").getString("url");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Página de mentoría para emprendedores
     */
    @GetMapping("/mentoriaEmprendedor")
    public String verMentoriasEmprendedor(HttpSession session, Model model) {
        UsuarioEntity emprendedor = (UsuarioEntity) session.getAttribute("usuario");

        if (emprendedor == null || !emprendedor.getRol().getNombre().equalsIgnoreCase("EMPRENDEDOR")) {
            return "redirect:/login";
        }

        List<MentoriaEntity> sesionesDisponibles = mentoriaService.findAll()
                .stream()
                .filter(m -> m.getFechaHora().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                        .isAfter(LocalDateTime.now()))
                .toList();

        model.addAttribute("sesiones", sesionesDisponibles);
        return "mentoriaEmprendedor"; // templates/mentoria/mentoriaEmprendedor.html
    }

    /**
     * Permitir a un emprendedor reservar una sesión
     */
    @PostMapping("/sesion/programar/{id}")
    public String programarSesion(@PathVariable("id") Long id, HttpSession session) {
        Long startupId = (Long) session.getAttribute("startupId");

        if (startupId == null) {
            return "redirect:/login";
        }

        StartupEntity startup = startupService.findById(startupId);
        SesionMentoriaEntity sesion = sesionMentoriaService.findById(id);

        if (sesion != null && !sesion.isReservada()) {
            sesion.setReservada(true);
            sesion.setStartup(startup);
            sesionMentoriaService.save(sesion);
        }

        return "redirect:/mentoria/mentoriaEmprendedor";
    }

    /**
     * Redirigir al HTML de sesiones del mentor y mostrar sesiones
     */

}