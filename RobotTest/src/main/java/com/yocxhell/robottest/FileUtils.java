package com.yocxhell.robottest;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

/**
 * Classe di utilità per la gestione dei file.
 */
public class FileUtils {

    private static final String GRAYLIST_FILE_PATH_RELATIVE = "screenshots/lists/graylist.txt";
    private static final String WARNLIST_FILE_PATH_RELATIVE = "screenshots/lists/warnlist.txt";
    
    private static Path graylistFilePath;
    private static Path warnlistFilePath;

    private static List<String> graylist;
    private static List<String> warnlist;

    static {
        // Risolvi i percorsi assoluti
        graylistFilePath = Paths.get(GRAYLIST_FILE_PATH_RELATIVE).toAbsolutePath();
        warnlistFilePath = Paths.get(WARNLIST_FILE_PATH_RELATIVE).toAbsolutePath();
    }

    /**
     * Inizializza la blacklist, la graylist e la warnlist.
     *
     * @throws IOException Se si verifica un errore durante la lettura dei file.
     */
    public static void initializeLists() throws IOException {
        // Verifica se i file esistono
        if (!Files.exists(graylistFilePath)) {
            throw new FileNotFoundException("Graylist file not found: " + graylistFilePath);
        }
        if (!Files.exists(warnlistFilePath)) {
            throw new FileNotFoundException("Warnlist file not found: " + warnlistFilePath);
        }

        reloadGraylist();
        reloadWarnlist();
    }

    /**
     * Ricarica la graylist.
     *
     * @throws IOException Se si verifica un errore durante la lettura del file.
     */
    public static void reloadGraylist() throws IOException {
        // Verifica se il file esiste prima di leggerlo
        if (!Files.exists(graylistFilePath)) {
            throw new FileNotFoundException("Graylist file not found: " + graylistFilePath);
        }
        graylist = Files.readAllLines(graylistFilePath);
    }

    /**
     * Ricarica la warnlist.
     *
     * @throws IOException Se si verifica un errore durante la lettura del file.
     */
    public static void reloadWarnlist() throws IOException {
        // Verifica se il file esiste prima di leggerlo
        if (!Files.exists(warnlistFilePath)) {
            throw new FileNotFoundException("Warnlist file not found: " + warnlistFilePath);
        }
        warnlist = Files.readAllLines(warnlistFilePath);
    }

    /**
     * Restituisce la graylist.
     *
     * @return Una lista di stringhe contenente gli elementi della graylist.
     */
    public static List<String> getGraylist() {
        return graylist;
    }

    /**
     * Restituisce la warnlist.
     *
     * @return Una lista di stringhe contenente gli elementi della warnlist.
     */
    public static List<String> getWarnlist() {
        return warnlist;
    }


    /**
     * Aggiunge il testo alla graylist se non è già presente.
     *
     * @param text Il testo da aggiungere.
     * @throws IOException Se si verifica un errore durante la lettura o scrittura del file.
     */
    public static void appendToGraylistIfNotExists(String text) throws IOException {
        appendIfNotExists(text, graylistFilePath);
    }

    /**
     * Aggiunge il testo alla warnlist se non è già presente.
     *
     * @param text Il testo da aggiungere.
     * @throws IOException Se si verifica un errore durante la lettura o scrittura del file.
     */
    public static void appendToWarnlistIfNotExists(String text) throws IOException {
        appendIfNotExists(text, warnlistFilePath);
    }

    /**
     * Aggiunge il testo al file specificato se non è già presente.
     *
     * @param text Il testo da aggiungere.
     * @param filePath Il percorso del file.
     * @throws IOException Se si verifica un errore durante la lettura o scrittura del file.
     */
    private static void appendIfNotExists(String text, Path filePath) throws IOException {
        Set<String> existingLines = new HashSet<>();

        // Leggi il contenuto esistente
        if (Files.exists(filePath)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    existingLines.add(line);
                }
            }
        } else {
            System.out.print("File not found: " + filePath);
            Files.createFile(filePath); // Crea il file se non esiste
        }

        // Se il testo non è già presente, aggiungilo
        if (!existingLines.contains(text)) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), true))) {
                writer.newLine();
                writer.write(text);
            }
        }
    }
    
    public static void downloadWarnlist () {
        downloadFile ("https://raw.githubusercontent.com/Yocxhell/GEB/main/screenshots/lists/warnlist.txt", warnlistFilePath.toString());
    }
    
    private static void downloadFile(String fileURL, String savePath) {
        try (InputStream in = new BufferedInputStream(new URL(fileURL).openStream());
             FileOutputStream out = new FileOutputStream(savePath)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, dataBuffer.length)) != -1) {
                out.write(dataBuffer, 0, bytesRead);
            }
            System.out.println("\nDownload complete: " + savePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
