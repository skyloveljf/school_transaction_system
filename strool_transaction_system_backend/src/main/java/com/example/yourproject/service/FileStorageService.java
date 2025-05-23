package com.example.yourproject.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface FileStorageService {
    /**
     * Initializes the storage directory.
     */
    void init();

    /**
     * Stores a file.
     * @param file The file to store.
     * @return The unique name of the stored file.
     */
    String store(MultipartFile file);

    /**
     * Loads all stored file paths.
     * @return A stream of paths.
     */
    Stream<Path> loadAll();

    /**
     * Loads a file as a Path.
     * @param filename The name of the file.
     * @return The path to the file.
     */
    Path load(String filename);

    /**
     * Loads a file as a Resource.
     * @param filename The name of the file.
     * @return The file resource.
     */
    Resource loadAsResource(String filename);

    /**
     * Deletes a file.
     * @param filename The name of the file to delete.
     * @return true if successful, false otherwise.
     */
    boolean delete(String filename);
    
    /**
     * Deletes all stored files. (Use with caution!)
     */
    void deleteAll(); // Potentially for testing or cleanup
}