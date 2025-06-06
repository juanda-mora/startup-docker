package com.usta.startup.controllers;

import com.usta.startup.entities.ConvocatoriaEntity;
import com.usta.startup.entities.PostulacionEntity;
import com.usta.startup.entities.StartupEntity;
import com.usta.startup.models.services.PostulacionService;
import com.usta.startup.repository.ConvocatoriaRepository;
import com.usta.startup.repository.PostulacionRepository;
import com.usta.startup.repository.StartupRepository;
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
import java.util.Date;
import java.util.List;

@Controller
public class PostulacionController {

    @Autowired
    private PostulacionRepository postulacionRepository;

    @Autowired
    private ConvocatoriaRepository convocatoriaRepository;

    @Autowired
    private StartupRepository startupRepository;

    @Autowired
    private PostulacionService postulacionService;

    @GetMapping("/postulacion")
    public String listarPostulaciones(Model model) {
        List<PostulacionEntity> lista = postulacionRepository.findAll();
        model.addAttribute("postulaciones", lista);
        return "postulacion/listarPostulaciones";
    }

    @GetMapping("/crearPostulacion")
    public String crearPostulacion(Model model) {
        model.addAttribute("postulacion", new PostulacionEntity());
        model.addAttribute("convocatorias", convocatoriaRepository.findAll());
        model.addAttribute("startups", startupRepository.findAll());
        return "postulacion/crearPostulacion";
    }

    @GetMapping("/emprendedor/crearPostulacion")
    public String crearPostulacionEmprendedor(Model model) {
        model.addAttribute("postulacion", new PostulacionEntity());
        model.addAttribute("convocatorias", convocatoriaRepository.findAll());
        model.addAttribute("startups", startupRepository.findAll());
        return "postulacion/postulacionEmprendedor";
    }

    @PostMapping("/crearPostulacion")
    public String guardarPostulacion(@ModelAttribute PostulacionEntity postulacion,
                                     @RequestParam("startupId") Long startupId,
                                     @RequestParam("convocatoriaId") Long convocatoriaId,
                                     @RequestParam("imagenFile") MultipartFile imagenFile,
                                     RedirectAttributes redirectAttributes) {

        try {
            StartupEntity startup = startupRepository.findById(startupId).orElse(null);
            ConvocatoriaEntity convocatoria = convocatoriaRepository.findById(convocatoriaId).orElse(null);

            postulacion.setStartup(startup);
            postulacion.setConvocatoria(convocatoria);
            postulacion.setFechaPostulacion(new Date());

            if (!imagenFile.isEmpty()) {
                String urlImagen = subirImagenAImgBB(imagenFile);
                postulacion.setImagen(urlImagen != null ? urlImagen : "https://via.placeholder.com/150");
            }

            postulacionRepository.save(postulacion);
            redirectAttributes.addFlashAttribute("success", "Postulación guardada correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al guardar la postulación.");
        }

        return "redirect:/postulacion";
    }

    @GetMapping("/editarPostulacion/{id}")
    public String editarPostulacion(@PathVariable("id") Long id, Model model) {
        PostulacionEntity postulacion = postulacionRepository.findById(id).orElse(null);
        model.addAttribute("postulacion", postulacion);
        model.addAttribute("startups", startupRepository.findAll()); // Agregar startups disponibles
        return "postulacion/editarPostulacion";
    }

    @PostMapping("/editarPostulacion/{id}")
    public String actualizarPostulacion(@ModelAttribute PostulacionEntity postulacion,
                                        @RequestParam("startupId") Long startupId,
                                        @RequestParam("imagenFile") MultipartFile imagenFile,
                                        @PathVariable("id") Long id,
                                        RedirectAttributes redirectAttributes) {
        try {
            PostulacionEntity existente = postulacionRepository.findById(id).orElse(null);
            if (existente != null) {
                existente.setNombre(postulacion.getNombre());
                existente.setEmail(postulacion.getEmail());
                existente.setMensaje(postulacion.getMensaje());
                existente.setEstado(postulacion.getEstado());
                existente.setFechaPostulacion(new Date());

                StartupEntity nuevaStartup = startupRepository.findById(startupId).orElse(null);
                existente.setStartup(nuevaStartup); // Actualizar startup

                if (!imagenFile.isEmpty()) {
                    String nuevaUrl = subirImagenAImgBB(imagenFile);
                    if (nuevaUrl != null) {
                        existente.setImagen(nuevaUrl);
                    }
                }

                postulacionRepository.save(existente);
                redirectAttributes.addFlashAttribute("success", "Postulación actualizada.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Postulación no encontrada.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la postulación.");
        }

        return "redirect:/postulacion";
    }
    @PostMapping("/eliminarPostulacion/{id}")
    public String eliminarPostulacion(@PathVariable("id") Long id,
                                      RedirectAttributes redirectAttributes) {
        if (id > 0) {
            PostulacionEntity postulacion = postulacionRepository.findById(id).orElse(null);

            if (postulacion != null) {
                boolean asociadaAStartup = postulacion.getStartup() != null;
                boolean asociadaAConvocatoria = postulacion.getConvocatoria() != null;

                if (asociadaAStartup || asociadaAConvocatoria) {
                    redirectAttributes.addFlashAttribute("error",
                            "No se puede eliminar la postulación: está asociada a una startup o convocatoria.");
                } else {
                    try {
                        postulacionRepository.deleteById(id);
                        redirectAttributes.addFlashAttribute("success", "Postulación eliminada correctamente.");
                    } catch (Exception e) {
                        e.printStackTrace();
                        redirectAttributes.addFlashAttribute("error", "Error al eliminar la postulación.");
                    }
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Postulación no encontrada.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "ID inválido.");
        }

        return "redirect:/postulacion";
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
}
