package com.yocxhell.robottest;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

/**
 * Utility class for file management.
 */
public class FileUtils {

    private static final String GRAYLIST_FILE_PATH_RELATIVE = "screenshots/lists/graylist.txt";
    private static final String WARNLIST_FILE_PATH_RELATIVE = "screenshots/lists/warnlist.txt";
    
    private static Path graylistFilePath;
    private static Path warnlistFilePath;

    private static List<String> graylist;
    private static List<String> warnlist;

    static {
        // Resolve absolute paths
        graylistFilePath = Paths.get(GRAYLIST_FILE_PATH_RELATIVE).toAbsolutePath();
        warnlistFilePath = Paths.get(WARNLIST_FILE_PATH_RELATIVE).toAbsolutePath();
    }

    /**
     * Initialize the graylist and the warnlist
     *
     * @throws IOException If an error occurs while reading the files.
     */
    public static void initializeLists() throws IOException {
        // Verify if files exist.
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
     * Reload the graylist.
     *
     * @throws IOException If an error occurs while reading the file.
     */
    public static void reloadGraylist() throws IOException {
        // Check if the file exists before reading it.
        if (!Files.exists(graylistFilePath)) {
            throw new FileNotFoundException("Graylist file not found: " + graylistFilePath);
        }
        graylist = Files.readAllLines(graylistFilePath);
    }

    /**
     * Reload the warnlist.
     *
     * @throws IOException If an error occurs during the reading of the file.
     */
    public static void reloadWarnlist() throws IOException {
        // Check if the file exists before reading it.
        if (!Files.exists(warnlistFilePath)) {
            throw new FileNotFoundException("Warnlist file not found: " + warnlistFilePath);
        }
        warnlist = Files.readAllLines(warnlistFilePath);
    }

    /**
     * Returns the graylist.
     *
     * @return A list of strings containing the elements of the graylist.
     */
    public static List<String> getGraylist() {
        return graylist;
    }

    /**
     * Returns the warnlist.
     *
     * @return A list of strings containing the elements of the warnlist.
     */
    public static List<String> getWarnlist() {
        return warnlist;
    }


    /**
     * Add the text to the graylist if it is not already present.
     *
     * @param text The text to add.
     * @throws IOException If an error occurs during the reading or writing of the file.
     */
    public static void appendToGraylistIfNotExists(String text) throws IOException {
        appendIfNotExists(text, graylistFilePath);
    }

    /**
     * Add the text to the warnlist if it is not already present.
     *
     * @param text The text to add.
     * @throws IOException If an error occurs during the reading or writing of the file.
     */
    public static void appendToWarnlistIfNotExists(String text) throws IOException {
        appendIfNotExists(text, warnlistFilePath);
    }

    /**
     * Add the text to the specified file if it is not already present.
     *
     * @param text The text to add.
     * @param filePath The file path.
     * @throws IOException If an error occurs during the reading or writing of the file.
     */
    private static void appendIfNotExists(String text, Path filePath) throws IOException {
        Set<String> existingLines = new HashSet<>();

        // Read the existing content.
        if (Files.exists(filePath)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    existingLines.add(line);
                }
            }
        } else {
            System.out.print("File not found: " + filePath);
            Files.createFile(filePath); // Create the file if it doesnt exist
        }

        // if the text isnt already present, add it
        if (!existingLines.contains(text)) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), true))) {
                writer.newLine();
                writer.write(text);
            }
        }
    }
    
    public static void downloadWarnlist () {
        downloadFile ("https://raw.githubusercontent.com/Yocxhell/GEB/refs/heads/main/RobotTest/screenshots/lists/warnlist.txt", warnlistFilePath.toString());
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
