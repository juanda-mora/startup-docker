package com.usta.startup.controllers;

import com.usta.startup.entities.ConvocatoriaEntity;
import com.usta.startup.entities.PostulacionEntity;
import com.usta.startup.entities.SesionMentoriaEntity;
import com.usta.startup.entities.UsuarioEntity;
import com.usta.startup.models.services.ConvocatoriaService;
import com.usta.startup.models.services.PostulacionService;
import com.usta.startup.models.services.SesionMentoriaService;
import com.usta.startup.repository.ConvocatoriaRepository;
import com.usta.startup.repository.PostulacionRepository;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class ConvocatoriaController {

    @Autowired
    private ConvocatoriaRepository convocatoriaRepository;

    @Autowired
    private PostulacionRepository postulacionRepository;

    @Autowired
    private ConvocatoriaService convocatoriaService;

    @Autowired
    private SesionMentoriaService sesionMentoriaService;

    @Autowired
    private PostulacionService postulacionService;

    // Vista de administración: listar todas las convocatorias
    @GetMapping("/convocatoria")
    public String listar(Model model) {
        List<ConvocatoriaEntity> lista = convocatoriaService.findAll();
        model.addAttribute("convocatorias", lista);
        model.addAttribute("title", "Lista de Convocatorias");
        return "/convocatoria/listar";
    }

    @GetMapping("/administrador/convocatoria")
    public String vistaAdministradorConvocatorias(Model model) {
        List<ConvocatoriaEntity> lista = convocatoriaService.findAll();
        model.addAttribute("convocatorias", lista);
        model.addAttribute("title", "Convocatorias para Administrador");
        return "/convocatoria/listarConvocatorias"; // O una vista diferente si es otra
    }

    @GetMapping("/emprendedor/convocatoria")
    public String vistaEmprendedorConvocatorias(Model model) {
        List<ConvocatoriaEntity> lista = convocatoriaService.findAll();
        model.addAttribute("convocatorias", lista);
        model.addAttribute("title", "Convocatorias para Administrador");
        return "/convocatoria/convocatoriaEmprendedor"; // O una vista diferente si es otra
    }

    // Formulario de creación básico (sin imagen)
    @GetMapping("/crear")
    public String crearForm(Model model) {
        model.addAttribute("convocatoria", new ConvocatoriaEntity());
        model.addAttribute("title", "Crear Convocatoria");
        return "/convocatoria/form";
    }

    // Guardar convocatoria (básico sin imagen)
    @PostMapping("/crear")
    public String guardar(@ModelAttribute("convocatoria") ConvocatoriaEntity convocatoria,
                          RedirectAttributes redirectAttrs) {
        convocatoriaService.save(convocatoria);
        redirectAttrs.addFlashAttribute("mensajeExito", "Convocatoria creada con éxito");
        return "redirect:/convocatoria";
    }

    // Formulario para editar (básico)
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        ConvocatoriaEntity convocatoria = convocatoriaService.findById(id);
        model.addAttribute("convocatoria", convocatoria);
        model.addAttribute("title", "Editar Convocatoria");
        return "/convocatoria/form";
    }

    // Actualizar convocatoria (básico)
    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute("convocatoria") ConvocatoriaEntity convocatoria,
                             RedirectAttributes redirectAttrs) {
        convocatoriaService.actualizarConvocatoria(convocatoria);
        redirectAttrs.addFlashAttribute("mensajeExito", "Convocatoria actualizada con éxito");
        return "redirect:/convocatoria";
    }

    // Eliminar convocatoria (básico)
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        convocatoriaService.deleteById(id);
        redirectAttrs.addFlashAttribute("mensajeExito", "Convocatoria eliminada");
        return "redirect:/convocatoria";
    }

    // Vista de convocatorias para mentor
    @GetMapping("/mentor/convocatorias")
    public String redirigirVistaMentor(Model model) {
        List<ConvocatoriaEntity> convocatorias = convocatoriaService.findAll();
        model.addAttribute("convocatorias", convocatorias);
        return "mentoria/convocatoriaMentor";
    }

    // Vista de convocatorias para emprendedor
    @GetMapping("/emprendedor")
    public String mostrarConvocatoriasEmprendedor() {
        return "sesion/sesionEmprendedor";
    }

    // Ver detalles de convocatoria
    @GetMapping("/convocatoria/ver/{id}")
    public String verConvocatoria(@PathVariable Long id, Model model, HttpSession session) {
        ConvocatoriaEntity convocatoria = convocatoriaService.findById(id);
        List<SesionMentoriaEntity> sesiones = sesionMentoriaService.findAll();
        List<ConvocatoriaEntity> convocatorias = convocatoriaService.findAll();

        model.addAttribute("convocatoria", convocatoria);
        model.addAttribute("sesiones", sesiones);
        model.addAttribute("convocatorias", convocatorias);

        return "mentoria/vistaConvocatoria";
    }

    // Subida de imagen a ImgBB
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

    // Formulario avanzado de creación (con imagen y fechas)
    @GetMapping("/crearConvocatoria")
    public String crearConvocatoria(Model model) {
        model.addAttribute("title", "Crear Nueva Convocatoria");
        model.addAttribute("convocatoria", new ConvocatoriaEntity());
        return "convocatoria/crearConvocatorias";
    }

    // Guardar convocatoria (con imagen y fechas)
    @PostMapping("/crearConvocatoria")
    public String guardarConvocatoria(@ModelAttribute("convocatoria") ConvocatoriaEntity convocatoria,
                                      @RequestParam("fechaInicioStr") String fechaInicioStr,
                                      @RequestParam("fechaFinStr") String fechaFinStr,
                                      @RequestParam("imagenFile") MultipartFile imagenFile,
                                      RedirectAttributes redirectAttributes) {
        try {
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            convocatoria.setFechaInicio(formato.parse(fechaInicioStr));
            convocatoria.setFechaFin(formato.parse(fechaFinStr));

            if (!imagenFile.isEmpty()) {
                String urlImagen = subirImagenAImgBB(imagenFile);
                convocatoria.setImagen(urlImagen != null ? urlImagen : "https://via.placeholder.com/150");
            }

            convocatoriaRepository.save(convocatoria);
            redirectAttributes.addFlashAttribute("success", "Convocatoria creada con éxito.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al crear convocatoria.");
        }

        return "redirect:/administrador/convocatoria";
    }

    // Formulario de edición avanzado
    @GetMapping("/editarConvocatoria/{id}")
    public String editarConvocatoria(@PathVariable("id") Long id, Model model) {
        ConvocatoriaEntity convocatoria = convocatoriaRepository.findById(id).orElse(null);
        model.addAttribute("convocatoria", convocatoria);
        model.addAttribute("title", "Editar Convocatoria");
        model.addAttribute("imagenActual", convocatoria != null ? convocatoria.getImagen() : "");
        return "convocatoria/editarConvocatoriaAdmin";
    }

    // Actualizar convocatoria (con imagen y fechas)
    @PostMapping("/editarConvocatoria/{id}")
    public String actualizarConvocatoria(@ModelAttribute("convocatoria") ConvocatoriaEntity convocatoria,
                                         @RequestParam("fechaInicioStr") String fechaInicioStr,
                                         @RequestParam("fechaFinStr") String fechaFinStr,
                                         @RequestParam("imagenFile") MultipartFile imagenFile,
                                         @RequestParam("imagenActual") String imagenActual,
                                         @PathVariable("id") Long id,
                                         RedirectAttributes redirectAttributes) {
        try {
            ConvocatoriaEntity existente = convocatoriaRepository.findById(id).orElse(null);
            if (existente == null) {
                redirectAttributes.addFlashAttribute("error", "Convocatoria no encontrada.");
                return "redirect:/convocatoria";
            }

            existente.setTitulo(convocatoria.getTitulo());
            existente.setDescripcion(convocatoria.getDescripcion());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            existente.setFechaInicio(sdf.parse(fechaInicioStr));
            existente.setFechaFin(sdf.parse(fechaFinStr));

            if (!imagenFile.isEmpty()) {
                String nuevaUrl = subirImagenAImgBB(imagenFile);
                existente.setImagen(nuevaUrl != null ? nuevaUrl : imagenActual);
            } else {
                existente.setImagen(imagenActual);
            }

            convocatoriaRepository.save(existente);
            redirectAttributes.addFlashAttribute("success", "Convocatoria actualizada correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al actualizar convocatoria.");
        }

        return "redirect:/administrador/convocatoria";
    }

    // Eliminar convocatoria con validación de postulaciones
    @PostMapping("/eliminarConvocatoria/{id}")
    public String eliminarConvocatoria(@PathVariable("id") Long id,
                                       RedirectAttributes redirectAttributes) {
        if (id != null && id > 0) {
            ConvocatoriaEntity convocatoria = convocatoriaRepository.findById(id).orElse(null);
            if (convocatoria == null) {
                redirectAttributes.addFlashAttribute("error", "La convocatoria no existe.");
            } else {
                boolean tienePostulaciones = postulacionRepository.countByConvocatoriaId(id) > 0;
                if (tienePostulaciones) {
                    redirectAttributes.addFlashAttribute("error", "No se puede eliminar: la convocatoria tiene postulaciones asociadas.");
                } else {
                    convocatoriaRepository.deleteById(id);
                    redirectAttributes.addFlashAttribute("success", "Convocatoria eliminada exitosamente.");
                }
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "ID inválido.");
        }
        return "redirect:/administrador/convocatoria";
    }

    @GetMapping("/emprendedor/inscribirse/{id}")
    public String mostrarFormularioPostulacion(@PathVariable Long id, Model model) {
        ConvocatoriaEntity convocatoria = convocatoriaService.findById(id);
        model.addAttribute("convocatoria", convocatoria);
        return "postulacionEmprendedor";
    }
}
