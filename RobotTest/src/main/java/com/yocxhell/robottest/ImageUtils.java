package com.yocxhell.robottest;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility for image management.
 */
public class ImageUtils {

    
    /**
     * Compare two images to determine if they are identical.
     *
     * @param img1 The first image
     * @param img2 The second image
     * @return true If the images are identical, return true; otherwise, return false.
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
        int toleranceSquared = tolerance * tolerance; // Use squared tolerance for optimization.

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
                if (distanceSquared > toleranceSquared * 3) { // Multiplied by 3 for the three color channels.
                    return false;
                }
            }
        }
        return true;
    }
    
    


    /**
     * Find the positions of the images that match within an area.
     *
     * @param image The large image in which to search.
     * @param target The image to search
     * @return A list of coordinates where the target image is found.
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
    * Find the position of the target image within the source image with tolerance.
    *
    * @param screenCapture The source image in which to search.
    * @param confirmationImage The target image to search for.
    * @return The (x, y) position of the target image within the source image, or null if not found.
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
        return null; // Image not found
    }
    
    /**
     * Convert pixels that are not black or the colors of the reference images to white.
     *
     * @param img The image to modify
     * @param referenceImg1 First reference image
     * @param referenceImg2 Second reference image
     * @return Image with modified pixels
     */
    public static BufferedImage convertNonBlackOrReferenceColorsToWhite(BufferedImage img,BufferedImage referenceImg1,BufferedImage referenceImg2) {
        
        // Create a set of colors from the reference images.
        Set<Integer> referenceColors = new HashSet<>();

        // Add colors from the reference images.
        addColorsFromImage(referenceColors, referenceImg1);
        addColorsFromImage(referenceColors, referenceImg2);
        
        // Add colors similar to black
        addBlackAndSimilarColors(referenceColors, 30);


        // Create output image
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgba = img.getRGB(x, y);
                int color = rgba & 0xFFFFFF; // Use only the color, not the alpha.

                if (referenceColors.contains(color)) {
                    result.setRGB(x, y, color); //Maintain the original color
                } else {
                    result.setRGB(x, y, Color.WHITE.getRGB()); // Set pixel color to white
                }
            }
        }

        return result;
    }

    /**
     * Add the colors from an image to the set of reference colors.
     *
     * @param referenceColors The set of reference colors.
     * @param referenceImg The reference image.
     */
    private static void addColorsFromImage(Set<Integer> referenceColors, BufferedImage referenceImg) {
        for (int y = 0; y < referenceImg.getHeight(); y++) {
            for (int x = 0; x < referenceImg.getWidth(); x++) {
                int rgba = referenceImg.getRGB(x, y);
                referenceColors.add(rgba & 0xFFFFFF); // Use only the color, not the alpha
            }
        }
    }
    
    /**
     * Add black and colors close to black to the set of reference colors.
     *
     * @param referenceColors The set of reference colors.
     * @param threshold The threshold value for colors close to black.
     */
    private static void addBlackAndSimilarColors(Set<Integer> referenceColors, int threshold) {
        // Add the color black
        referenceColors.add(Color.BLACK.getRGB() & 0xFFFFFF);

        // Add the colors similar to black
        for (int r = 0; r <= threshold; r++) {
            for (int g = 0; g <= threshold; g++) {
                for (int b = 0; b <= threshold; b++) {
                    referenceColors.add(new Color(r, g, b).getRGB() & 0xFFFFFF);
                }
            }
        }
    }
    
    /**
     * Check if an image contains black or nearly black pixels (with tolerance).
     *
     * @param image The image to check
     * @param tolerance The tolerance value for considering a color "nearly black."
     * @return true If the image contains black or nearly black pixels, return true; otherwise, return false.
     */
    public static boolean containsBlack(BufferedImage image, int tolerance) {
        // Ensure that the tolerance is valid.
        if (tolerance < 0) tolerance = 0;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = image.getRGB(x, y);
                Color color = new Color(pixel, true);

                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                // Verify if the pixel is "Almost black"
                if (isAlmostBlack(red, green, blue, tolerance)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if a color is considered "nearly black" based on a tolerance.
     *
     * @param red The red value of the pixel.
     * @param green The green value of the pixel.
     * @param blue The blue value of the pixel.
     * @param tolerance The tolerance for considering a color "nearly black".
     * @return true if the color is "nearly black", false otherwise.
     */
    private static boolean isAlmostBlack(int red, int green, int blue, int tolerance) {
        return red <= tolerance && green <= tolerance && blue <= tolerance;
    }

}
