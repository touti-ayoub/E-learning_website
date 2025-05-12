package tn.esprit.microservice4.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    private final String storageDir = "uploads/";
    private final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private final String[] ALLOWED_EXTENSIONS = {"pdf"};

    public String saveFile(MultipartFile file) {
        try {
            validateFile(file);
            String fileName = generateUniqueFileName(file.getOriginalFilename());
            Path filePath = createFilePath(fileName);
            saveFileToDisk(file, filePath);
            return fileName; // Retourne uniquement le nom du fichier
        } catch (IOException e) {
            logger.error("Erreur lors de l'enregistrement du fichier", e);
            throw new RuntimeException("Erreur lors de l'enregistrement du fichier: " + e.getMessage(), e);
        }
    }

    public Resource downloadFile(String filename) {
        try {
            // Nettoyer le nom du fichier pour éviter les attaques par injection de chemin
            String cleanFilename = Paths.get(filename).getFileName().toString();
            Path path = Paths.get(storageDir, cleanFilename);
            logger.info("Tentative de téléchargement du fichier: {}", path.toString());

            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() && resource.isReadable()) {
                logger.info("Fichier trouvé et accessible: {}", path.toString());
                return resource;
            } else {
                logger.error("Fichier non trouvé ou non accessible: {}", path.toString());
                throw new RuntimeException("Le fichier n'existe pas ou n'est pas accessible: " + path.toString());
            }
        } catch (IOException e) {
            logger.error("Erreur lors du téléchargement du fichier", e);
            throw new RuntimeException("Erreur lors du téléchargement du fichier: " + e.getMessage(), e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("La taille du fichier dépasse la limite maximale de 10MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("Nom de fichier invalide");
        }

        String extension = getFileExtension(originalFilename);
        if (!isValidExtension(extension)) {
            throw new IllegalArgumentException("Seuls les fichiers PDF sont autorisés");
        }
    }

    private String generateUniqueFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        return UUID.randomUUID().toString() + "." + extension;
    }

    private Path createFilePath(String fileName) throws IOException {
        Path filePath = Paths.get(storageDir, fileName);
        Files.createDirectories(filePath.getParent());
        return filePath;
    }

    private void saveFileToDisk(MultipartFile file, Path filePath) throws IOException {
        Files.write(filePath, file.getBytes());
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isValidExtension(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equals(extension)) {
                return true;
            }
        }
        return false;
    }
}