package com.example.yourproject.service.impl;

import com.example.yourproject.config.StorageProperties;
import com.example.yourproject.exception.FileStorageException;
import com.example.yourproject.exception.ResourceNotFoundException; // Using existing custom exception
import com.example.yourproject.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileSystemStorageService.class);
    private final Path rootLocation;
    private final StorageProperties storageProperties;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.storageProperties = properties;
        if (properties.getLocation() == null || properties.getLocation().trim().isEmpty()) {
            throw new FileStorageException("File upload location can not be Empty in properties.");
        }
        this.rootLocation = Paths.get(properties.getLocation()).toAbsolutePath().normalize();
    }

    @Override
    @PostConstruct // Ensures this method is called after dependency injection is done
    public void init() {
        try {
            Files.createDirectories(rootLocation);
            logger.info("Initialized file storage directory: {}", rootLocation.toString());
        } catch (IOException e) {
            logger.error("Could not initialize storage location: {}", rootLocation.toString(), e);
            throw new FileStorageException("Could not initialize storage location: " + rootLocation.toString(), e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileStorageException("Failed to store empty file.");
        }
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFilename.contains("..")) {
            // This is a security check
            throw new FileStorageException("Cannot store file with relative path outside current directory " + originalFilename);
        }

        String fileExtension = "";
        try {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")); // Keep the dot
        } catch (Exception e) {
            logger.warn("Could not determine file extension for: {}", originalFilename);
        }
        String storedFilenameWithoutExtension = UUID.randomUUID().toString();
        String storedFilename = storedFilenameWithoutExtension + fileExtension;

        try (InputStream inputStream = file.getInputStream()) {
            Path destinationFile = this.rootLocation.resolve(storedFilename).normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                 // Security check to prevent path traversal
                throw new FileStorageException("Cannot store file outside current directory.");
            }
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Successfully stored file: {} as {}", originalFilename, storedFilename);
            
            // Construct the URL based on WebMvcConfig and expected public path
            // WebMvcConfig maps /uploads/images/** to the storage location.
            // So, the public URL should start with /uploads/images/
            return "/uploads/images/" + storedFilename; 
        } catch (IOException e) {
            logger.error("Failed to store file {} as {}: {}", originalFilename, storedFilename, e.getMessage(), e);
            throw new FileStorageException("Failed to store file " + originalFilename, e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            logger.error("Failed to read stored files", e);
            throw new FileStorageException("Failed to read stored files", e);
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename).normalize().toAbsolutePath();
             if (!file.getParent().equals(this.rootLocation.toAbsolutePath()) || !Files.exists(file)) {
                 // Security check and existence check
                throw new ResourceNotFoundException("File", "name", filename);
            }
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                logger.warn("Could not read file: {}", filename);
                throw new ResourceNotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            logger.error("Malformed URL for file {}: {}", filename, e.getMessage(), e);
            throw new ResourceNotFoundException("Could not read file: " + filename);
        }
    }

    @Override
    public boolean delete(String filename) {
        try {
            Path file = load(filename).normalize().toAbsolutePath();
            if (!file.getParent().equals(this.rootLocation.toAbsolutePath())) {
                logger.warn("Attempt to delete file outside storage root: {}", filename);
                return false; // Security: do not allow deleting outside root
            }
            boolean deleted = Files.deleteIfExists(file);
            if (deleted) {
                logger.info("Successfully deleted file: {}", filename);
            } else {
                logger.warn("File to delete not found: {}", filename);
            }
            return deleted;
        } catch (IOException e) {
            logger.error("Could not delete file {}: {}", filename, e.getMessage(), e);
            throw new FileStorageException("Could not delete file " + filename, e);
        }
    }
    
    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
        logger.info("All files deleted from storage location: {}", rootLocation);
        // Re-initialize the directory after deleting all
        init();
    }
}