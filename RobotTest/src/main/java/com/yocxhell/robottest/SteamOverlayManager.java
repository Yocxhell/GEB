package com.yocxhell.robottest;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Manages interactions with the Steam overlay.
 */
public class SteamOverlayManager {

    private Robot robot;
    private Map<String, String> warnlist;

    public SteamOverlayManager(Robot robot) throws IOException {
        this.robot = robot;
    }

    /**
     * Clicks on the icon and performs actions in the Steam overlay.
     *
     * @param x The x coordinate of the icon
     * @param y The y coordinate of the icon
     */
    public void clickIcon(int x, int y) {
        ScreenManager.click(robot, x, y);
        // Perform actions after the click, waiting for the overlay to open up to the provided maximum time, otherwise proceed to the next
        performActions("screenshots/icons/steam_overlay_option_bar.png", 10000, 500);
    }
    
    public void clickIcon() {
        // Perform actions after the click, waiting for the overlay to open up to the provided maximum time, otherwise proceed to the next
        performActions("screenshots/icons/steam_overlay_option_bar.png", 10000, 500);
    }

    /**
     * Performs necessary actions on the Steam overlay and waits until the confirmation image is found.
     *
     * @param confirmationImagePath The path of the confirmation image to find
     * @param maxWaitTime The maximum wait time in milliseconds
     * @param checkInterval The check interval in milliseconds
     */
    public void performActions(String confirmationImagePath, int maxWaitTime, int checkInterval) {
        try {
            // Load the confirmation image (url containing the steamid)
            File confirmationImageFile = new File(confirmationImagePath);
            BufferedImage confirmationImage = ImageIO.read(confirmationImageFile.getAbsoluteFile());

            // Get the screen size
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Rectangle screenRect = new Rectangle(toolkit.getScreenSize());

            long startTime = System.currentTimeMillis();
            // While the waiting time to find a match hasn't expired
            while (System.currentTimeMillis() - startTime < maxWaitTime) {
                BufferedImage screenCapture = robot.createScreenCapture(screenRect);

                // Find the position of the confirmation image in the screenshot to locate the url with steamid to copy
                Point imagePosition = ImageUtils.findImagePosition(screenCapture, confirmationImage);

                if (imagePosition != null) {
                    // System.out.println("Menu option found.");
                    // Perform desired actions here
                    executeActions(imagePosition, confirmationImage);

                    return; // Exit the method after executing actions
                }

                // Delay between attempts
                robot.delay(checkInterval);
            }

            // System.out.println("Maximum wait time exceeded without finding the confirmation image.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Executes a series of specific actions once the confirmation image is found.
     *
     * @param position The position of the found image
     * @param confirmationImage The confirmation image
     */
    private void executeActions(Point position, BufferedImage confirmationImage) {
        int xStart = position.x + confirmationImage.getWidth() / 2 + 95; // Centered horizontally on the image
        int yStart = position.y + confirmationImage.getHeight() / 2; // Centered vertically on the image

        // Print the coordinates for debugging
        // System.out.println("Calculated coordinates for click: (" + xStart + ", " + yStart + ")");

        // Get the screen size
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Rectangle screenRect = new Rectangle(toolkit.getScreenSize());
        int screenWidth = screenRect.width;

        // Perform the click and start dragging
        robot.mouseMove(xStart, yStart);
        robot.delay(100);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

        // Drag the mouse to the screen limits
        int stepSize = 10; // Step size for movement
        for (int x = xStart; x <= screenWidth; x += stepSize) {
            robot.mouseMove(x, yStart);
            robot.delay(1);
        }

        // Release the click
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

        // Copy the highlighted text using mouse dragging to the clipboard
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_C);
        robot.delay(50);
        robot.keyRelease(KeyEvent.VK_C);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.delay(50);

        // Get the copied text from the clipboard
        String copiedText = ClipboardUtils.getClipboardText().trim();

        // Add the text to the graylist if it is not already present
        try {
            FileUtils.appendToGraylistIfNotExists(copiedText);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (warnlist.containsKey(copiedText)) {
            String reportLink = warnlist.get(copiedText);
            System.out.println("Warning | Requesting evidence for: " + copiedText);
            System.out.println("Reports: " + reportLink);
        }

        try {
            File targetImageFile = new File("screenshots/icons/steam_overlay_exit_bar.png");
            BufferedImage targetImage = ImageIO.read(targetImageFile.getAbsoluteFile());
            BufferedImage screenCapture = robot.createScreenCapture(screenRect);

            // Try to find the exit bar
            Point imagePosition = ImageUtils.findImagePosition(screenCapture, targetImage);

            if (imagePosition != null) {
                // System.out.println("Exit image found and clicked.");
                int xClick = imagePosition.x + targetImage.getWidth() / 2; // Centered horizontally on the image
                int yClick = imagePosition.y + targetImage.getHeight() / 2; // Centered vertically on the image
                robot.mouseMove(xClick, yClick);
                robot.delay(200);
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            } else {
                // System.out.println("Exit image not found.");
                // Try other fallback actions if necessary
                robot.mouseMove(1317, 1003);
                robot.delay(100);
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            }
        } catch (IOException e) {
            // System.err.println("Error reading the exit image: " + e.getMessage());
            e.printStackTrace();
        }

        // Press Shift + Tab
        robot.keyPress(KeyEvent.VK_SHIFT);
        robot.keyPress(KeyEvent.VK_TAB);
        robot.delay(50);
        robot.keyRelease(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_SHIFT);
        // Delay to let the Steam overlay fade out to ensure the icons are clickable
        robot.delay(500);
    }
    
    public void getCurrentLists () {
        warnlist = new HashMap<>();
        List<String> lines = FileUtils.getWarnlist();
    
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length == 2) {
                warnlist.put(parts[0].trim(), parts[1].trim()); // Profile URL -> Telegra.ph Report URL
            }
        }
    }
}
