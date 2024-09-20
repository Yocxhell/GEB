package com.yocxhell.robottest;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utilità per la gestione delle immagini.
 */
public class ImageUtils {

    
    /**
     * Confronta due immagini per determinare se sono uguali.
     *
     * @param img1 La prima immagine
     * @param img2 La seconda immagine
     * @return true se le immagini sono uguali, false altrimenti
     */
    public static boolean imagesAreEqual(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            return false;
        }

        int width = img1.getWidth();
        int height = img1.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    
    public static boolean imagesAreSimilar(BufferedImage img1, BufferedImage img2, int tolerance) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            return false;
        }

        int width = img1.getWidth();
        int height = img1.getHeight();
        int toleranceSquared = tolerance * tolerance; // Usare la tolleranza al quadrato per ottimizzare

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb1 = img1.getRGB(x, y);
                int rgb2 = img2.getRGB(x, y);

                int r1 = (rgb1 >> 16) & 0xFF;
                int g1 = (rgb1 >> 8) & 0xFF;
                int b1 = rgb1 & 0xFF;

                int r2 = (rgb2 >> 16) & 0xFF;
                int g2 = (rgb2 >> 8) & 0xFF;
                int b2 = rgb2 & 0xFF;

                int dr = r1 - r2;
                int dg = g1 - g2;
                int db = b1 - b2;

                int distanceSquared = dr * dr + dg * dg + db * db;
                if (distanceSquared > toleranceSquared * 3) { // Moltiplicato per 3 per i tre canali di colore
                    return false;
                }
            }
        }
        return true;
    }
    
    


    /**
     * Trova le posizioni delle immagini corrispondenti all'interno di un'area.
     *
     * @param image La grande immagine in cui cercare
     * @param target L'immagine da cercare
     * @return Una lista di coordinate dove l'immagine target è trovata
     */
    public static List<int[]> imageCompareEqual(BufferedImage image, BufferedImage target) {
        List<int[]> matches = new ArrayList<>();
        int width = image.getWidth();
        int height = image.getHeight();
        int targetWidth = target.getWidth();
        int targetHeight = target.getHeight();

        for (int y = 0; y <= height - targetHeight; y++) {
            for (int x = 0; x <= width - targetWidth; x++) {
                BufferedImage subImage = image.getSubimage(x, y, targetWidth, targetHeight);
                if (imagesAreEqual(subImage, target)) {
                    matches.add(new int[]{x, y});
                }
            }
        }

        return matches;
    }
    
    public static List<int[]> imageCompareSimilar(BufferedImage image, BufferedImage target, int tolerance) {
        List<int[]> matches = new ArrayList<>();
        int width = image.getWidth();
        int height = image.getHeight();
        int targetWidth = target.getWidth();
        int targetHeight = target.getHeight();

        for (int y = 0; y <= height - targetHeight; y++) {
            for (int x = 0; x <= width - targetWidth; x++) {
                BufferedImage subImage = image.getSubimage(x, y, targetWidth, targetHeight);
                if (imagesAreSimilar(subImage, target, tolerance)) {
                    matches.add(new int[]{x, y});
                }
            }
        }

        return matches;
    }
    
    /**
    * Trova la posizione dell'immagine target all'interno dell'immagine di origine con tolleranza.
    *
    * @param screenCapture L'immagine di origine in cui cercare
    * @param confirmationImage L'immagine target da cercare
    * @return La posizione (x, y) dell'immagine target all'interno dell'immagine di origine, o null se non trovata
    */
    public static Point findImagePosition(BufferedImage screenCapture, BufferedImage confirmationImage) {
        int imgWidth = confirmationImage.getWidth();
        int imgHeight = confirmationImage.getHeight();

        for (int y = 0; y <= screenCapture.getHeight() - imgHeight; y++) {
            for (int x = 0; x <= screenCapture.getWidth() - imgWidth; x++) {
                BufferedImage subImage = screenCapture.getSubimage(x, y, imgWidth, imgHeight);

                if (imagesAreEqual(subImage, confirmationImage)) {
                    return new Point(x, y);
                }
            }
        }
        return null; // Immagine non trovata
    }
    
    /**
     * Converte i pixel che non sono il colore nero o i colori delle immagini di riferimento in bianco.
     *
     * @param img L'immagine da modificare
     * @param referenceImg1 La prima immagine di riferimento
     * @param referenceImg2 La seconda immagine di riferimento
     * @return L'immagine con i pixel modificati
     */
    public static BufferedImage convertNonBlackOrReferenceColorsToWhite(BufferedImage img,BufferedImage referenceImg1,BufferedImage referenceImg2) {
        
        // Crea un insieme di colori dalle immagini di riferimento
        Set<Integer> referenceColors = new HashSet<>();

        // Aggiungi colori dalle immagini di riferimento
        addColorsFromImage(referenceColors, referenceImg1);
        addColorsFromImage(referenceColors, referenceImg2);
        
        // Aggiungi colori simili al nero
        addBlackAndSimilarColors(referenceColors, 30);


        // Crea l'immagine di output
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgba = img.getRGB(x, y);
                int color = rgba & 0xFFFFFF; // Usa solo il colore, non l'alfa

                if (referenceColors.contains(color)) {
                    result.setRGB(x, y, color); // Mantieni il colore originale
                } else {
                    result.setRGB(x, y, Color.WHITE.getRGB()); // Imposta il pixel a bianco
                }
            }
        }

        return result;
    }

    /**
     * Aggiunge i colori di un'immagine all'insieme di colori di riferimento.
     *
     * @param referenceColors L'insieme di colori di riferimento
     * @param referenceImg L'immagine di riferimento
     */
    private static void addColorsFromImage(Set<Integer> referenceColors, BufferedImage referenceImg) {
        for (int y = 0; y < referenceImg.getHeight(); y++) {
            for (int x = 0; x < referenceImg.getWidth(); x++) {
                int rgba = referenceImg.getRGB(x, y);
                referenceColors.add(rgba & 0xFFFFFF); // Usa solo il colore, non l'alfa
            }
        }
    }
    
    /**
     * Aggiunge il colore nero e i colori vicini al nero all'insieme dei colori di riferimento.
     *
     * @param referenceColors L'insieme di colori di riferimento
     * @param threshold Il valore soglia per i colori vicini al nero
     */
    private static void addBlackAndSimilarColors(Set<Integer> referenceColors, int threshold) {
        // Aggiungi il colore nero
        referenceColors.add(Color.BLACK.getRGB() & 0xFFFFFF);

        // Aggiungi colori vicini al nero
        for (int r = 0; r <= threshold; r++) {
            for (int g = 0; g <= threshold; g++) {
                for (int b = 0; b <= threshold; b++) {
                    referenceColors.add(new Color(r, g, b).getRGB() & 0xFFFFFF);
                }
            }
        }
    }
    
    /**
     * Verifica se un'immagine contiene pixel neri o quasi neri (con tolleranza).
     *
     * @param image L'immagine da controllare.
     * @param tolerance Il valore di tolleranza per considerare un colore "quasi nero".
     * @return true se l'immagine contiene pixel neri o quasi neri, false altrimenti.
     */
    public static boolean containsBlack(BufferedImage image, int tolerance) {
        // Assicurati che la tolleranza sia valida
        if (tolerance < 0) tolerance = 0;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = image.getRGB(x, y);
                Color color = new Color(pixel, true);

                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                // Verifica se il pixel è "quasi nero"
                if (isAlmostBlack(red, green, blue, tolerance)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Verifica se un colore è considerato "quasi nero" basato su una tolleranza.
     *
     * @param red Il valore del rosso del pixel.
     * @param green Il valore del verde del pixel.
     * @param blue Il valore del blu del pixel.
     * @param tolerance La tolleranza per considerare un colore "quasi nero".
     * @return true se il colore è "quasi nero", false altrimenti.
     */
    private static boolean isAlmostBlack(int red, int green, int blue, int tolerance) {
        return red <= tolerance && green <= tolerance && blue <= tolerance;
    }

}
