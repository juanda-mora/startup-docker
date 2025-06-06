package com.usta.startup.models.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class AlmacenamientoService {

    private final Path rootLocation = Paths.get("src/uploads");

    public String guardarImagen(MultipartFile archivo) {
        try {
            if (archivo.isEmpty()) {
                throw new RuntimeException("El archivo está vacío");
            }

            // Asegura que el directorio exista
            Files.createDirectories(rootLocation);

            String filename = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
            Path destinationFile = rootLocation.resolve(
                            Paths.get(filename))
                    .normalize().toAbsolutePath();

            archivo.transferTo(destinationFile);

            // Retorna una ruta relativa (por ejemplo, para usar en HTML <img>)
            return "/src/uploads/" + filename;

        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el archivo", e);
        }
    }
}
