package com.usta.startup.controllers;

import com.usta.startup.entities.StartupEntity;
import com.usta.startup.repository.PostulacionRepository;
import com.usta.startup.repository.SesionMentoriaRepository;
import com.usta.startup.repository.StartupRepository;
import com.usta.startup.repository.UsuarioRepository;
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

import java.util.List;

@Controller
public class StartupController {

    @Autowired
    private StartupRepository startupRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PostulacionRepository postulacionRepository;

    @Autowired
    private SesionMentoriaRepository sesionMentoriaRepository;

    // LISTAR
    @GetMapping("/startup")
    public String listarStartups(Model model) {
        List<StartupEntity> lista = startupRepository.findAll();
        model.addAttribute("startups", lista);
        return "startup/listarStartups";
    }

    // CREAR FORM
    @GetMapping("/crearStartup")
    public String crearStartup(Model model) {
        model.addAttribute("startup", new StartupEntity());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "startup/crearStartup";
    }

    @GetMapping("/emprendedor/crearStartup")
    public String crearStartupEm(Model model) {
        model.addAttribute("startup", new StartupEntity());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "startup/startupEmprendedor";
    }

    // GUARDAR NUEVA STARTUP
    @PostMapping("/crearStartup")
    public String guardarStartup(@ModelAttribute("startup") StartupEntity startup,
                                 @RequestParam("logoFile") MultipartFile logoFile,
                                 RedirectAttributes redirectAttributes) {

        try {
            if (!logoFile.isEmpty()) {
                String urlLogo = subirImagenAImgBB(logoFile);
                startup.setLogoUrl(urlLogo != null ? urlLogo : "https://via.placeholder.com/150");
            }
            startupRepository.save(startup);
            redirectAttributes.addFlashAttribute("success", "Startup creada exitosamente.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al crear la startup.");
        }

        return "redirect:/startup";
    }

    // EDITAR FORM
    @GetMapping("/editarStartup/{id}")
    public String editarStartup(@PathVariable("id") Long id, Model model) {
        StartupEntity startup = startupRepository.findById(id).orElse(null);
        model.addAttribute("startup", startup);
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "startup/editarStartup";
    }

    // ACTUALIZAR STARTUP
    @PostMapping("/editarStartup/{id}")
    public String actualizarStartup(@ModelAttribute("startup") StartupEntity startup,
                                    @RequestParam("logoFile") MultipartFile logoFile,
                                    @RequestParam("logoActual") String logoActual,
                                    @PathVariable("id") Long id,
                                    RedirectAttributes redirectAttributes) {

        try {
            StartupEntity existente = startupRepository.findById(id).orElse(null);
            if (existente != null) {
                existente.setNombre(startup.getNombre());
                existente.setDescripcion(startup.getDescripcion());
                existente.setSector(startup.getSector());
                existente.setUsuario(startup.getUsuario());

                if (!logoFile.isEmpty()) {
                    String nuevaUrl = subirImagenAImgBB(logoFile);
                    existente.setLogoUrl(nuevaUrl != null ? nuevaUrl : logoActual);
                } else {
                    existente.setLogoUrl(logoActual);
                }

                startupRepository.save(existente);
                redirectAttributes.addFlashAttribute("success", "Startup actualizada correctamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la startup.");
        }

        return "redirect:/startup";
    }

    // ELIMINAR
    @PostMapping("/eliminarStartup/{id}")
    public String eliminarStartup(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        if (id > 0) {
            StartupEntity startup = startupRepository.findById(id).orElse(null);
            if (startup != null) {
                boolean tienePostulaciones = postulacionRepository.countByStartupId(id) > 0;
                boolean tieneSesiones = sesionMentoriaRepository.countByStartupId(id) > 0;

                if (tienePostulaciones || tieneSesiones) {
                    redirectAttributes.addFlashAttribute("error", "No se puede eliminar: tiene postulaciones o sesiones asociadas.");
                } else {
                    startupRepository.deleteById(id);
                    redirectAttributes.addFlashAttribute("success", "Startup eliminada correctamente.");
                }
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "ID inválido.");
        }
        return "redirect:/startup";
    }

    // SUBIDA A IMGBB
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}