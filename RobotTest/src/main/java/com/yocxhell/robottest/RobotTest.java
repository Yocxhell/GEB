package com.yocxhell.robottest;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Main class for executing the Robot test.
 */
public class RobotTest {

    /**
     * Merges two lists of coordinates, inserting the first elements of matches2 after the first elements of matches and the second elements of matches2 after the second elements of matches.
     *
     * @param matches The first list of coordinates
     * @param matches2 The second list of coordinates
     * @return A new list with the elements of matches and matches2 combined as specified
     */
    private static List<int[]> mergeMatches(List<int[]> matches, List<int[]> matches2) {
        // Creates a list to hold all the elements from the two input list
        List<int[]> mergedMatches = new ArrayList<>();
        
       // Add all elements from the two lists to the unified list
        mergedMatches.addAll(matches);
        mergedMatches.addAll(matches2);

       // Sort the unified list based on the second element of each array
        Collections.sort(mergedMatches, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return Integer.compare(o1[1], o2[1]);
            }
        });

        return mergedMatches;
    }

    public static void main(String[] args) throws AWTException, IOException {
        Robot robot = new Robot();
        CatchInput in = new CatchInput();
        SteamOverlayManager overlayManager = new SteamOverlayManager(robot);
        Runtime runtime = Runtime.getRuntime();
        boolean end = false;
        boolean soloTargeting = false;
        int choose;
        
        ClipboardUtils.clearClipboard();
        FileUtils.initializeLists();
        overlayManager.getCurrentLists();
        ScreenManager.clearConsole();

        if (!end) {
            System.out.print("DISCLAIMER: This application has only been tested on: Windows 10, Steam, strictly 1920x1080 resolution, and fullscreen DST\n\n\n");
            
            do {
                // Use the ScreenManager method to get the colored text
                String status = ScreenManager.getSoloTargetingStatus(soloTargeting);
                if (!soloTargeting) {
                    choose = in.intValue("\nSOLO_TARGETING: " + status + "\n\n1)Analize players from a server (from server list)\n2)Analize players from the server you are connected to\n3)Reload all lists\n4)Switch solotargeting mode\n5)Download official warnlist\nOther)Quit\n\n");
                    
                    switch (choose) {
                    case 1 -> {
                        
                        ScreenManager.clearConsole();
                        robot.delay(2000);

                        int offsetX = 733;
                        int offsetY = 259;

                        File viewPlayersOutsideMenuImage = new File("screenshots/icons/view_players_outside_menu__steam_icon.png");
                        BufferedImage imgB = ImageIO.read(viewPlayersOutsideMenuImage.getAbsoluteFile());

                        BufferedImage savedImage = null;
                        int lastIconX = -1;
                        int lastIconY = -1;

                        BufferedImage imgA = robot.createScreenCapture(new Rectangle(offsetX, offsetY, 495, 479));
                        List<int[]> matches = ImageUtils.imageCompareEqual(imgA, imgB);
                        
                        // If no Steam icons are found in the screenshot, players are not visible and the action is stopped
                        if (matches.isEmpty()) {
                            System.out.print("\nNo players found, action stopped\n");
                            break;
                        }

                        List<int[]> currentMatches = new ArrayList<>(matches);

                        for (int[] match : currentMatches) {
                            int iconWidth = imgB.getWidth();
                            int iconHeight = imgB.getHeight();
                            int clickX = offsetX + match[0] + iconWidth / 2;
                            int clickY = offsetY + match[1] + iconHeight / 2;
                            overlayManager.clickIcon(clickX, clickY);

                            if (currentMatches.size() == 8) {
                                File scrollbarStartImage = new File("screenshots/icons/scrollbar_start.png");
                                savedImage = ImageIO.read(scrollbarStartImage.getAbsoluteFile());
                                lastIconX = clickX;
                                lastIconY = clickY;
                            }
                        }
                        
                        
                        // If the start of the bar is found, it means there are other hidden matches that need scrolling to be clicked
                        if (savedImage != null && ImageUtils.imageCompareEqual(imgA, savedImage).size() == 1) {
                            try {
                                // Carica l'immagine di conferma (per capire quando terminare lo scroll)
                                File endImageFile = new File("screenshots/icons/scrollbar_end.png");
                                BufferedImage endImage = ImageIO.read(endImageFile.getAbsoluteFile());

                                // Ottieni la dimensione dello schermo
                                Toolkit toolkit = Toolkit.getDefaultToolkit();
                                Rectangle screenRect = new Rectangle(toolkit.getScreenSize());

                                BufferedImage previousCapture = imgA; // Initial capture

                                // As long as the end scrollbar image is not found, continue scrolling and clicking the last found icon
                                while (true) {
                                    imgA = robot.createScreenCapture(screenRect);
                                    robot.mouseWheel(1); // Scroll down
                                    robot.delay(100);

                                    // Verify if the end scrollbar image is visile
                                    if (!ImageUtils.imageCompareEqual(imgA, endImage).isEmpty()) {
                                        //System.out.println("Scrollbar image found.");
                                        break; // // Exit the loop if the end scrollbar image is found
                                    }

                                    // Click the last icon found
                                    overlayManager.clickIcon(lastIconX, lastIconY);

                                    // // Check if the captured image has changed (to avoid infinite loops)
                                    if (ImageUtils.imagesAreEqual(previousCapture, imgA)) {
                                        //System.out.println("// No change on the screen, ending the scroll.");
                                        break; // // Exit the loop if the screen does not change
                                    }
                                    previousCapture = imgA; // // Update the previous capture
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        
                    }


                    case 2 -> {
                        ScreenManager.clearConsole();
                        robot.delay(2000);
                        robot.keyPress(KeyEvent.VK_TAB);
                        robot.delay(100);

                        int offsetX = 1017;
                        int offsetY = 320;


                        int lastIconX = -1;
                        int lastIconY = -1;
                        
                        // Capture the screenshot
                        BufferedImage imgA = robot.createScreenCapture(new Rectangle(offsetX, offsetY, 577, 577));

                        // // Load icon images (main and variant)
                        File characterIconFile = new File("screenshots/icons/view_players_menu_character_icon.png");
                        BufferedImage characterIcon = ImageIO.read(characterIconFile.getAbsoluteFile());

                        File characterIcon2File = new File("screenshots/icons/view_players_menu_character_icon_2.png");
                        BufferedImage characterIcon2 = ImageIO.read(characterIcon2File.getAbsoluteFile());
                        
                        // Convert only imgA to replace transparent pixels with white
                        BufferedImage imgAConverted = ImageUtils.convertNonBlackOrReferenceColorsToWhite(imgA, characterIcon, characterIcon2);
                        
                        robot.delay(100);
                        robot.keyRelease(KeyEvent.VK_TAB);
                        
                        File ingameCharacterListFile = new File("screenshots/ingame_character_list.png");
                        ImageIO.write(imgA, "png", ingameCharacterListFile.getAbsoluteFile());

                        File ingameCharacterListConvertedFile = new File("screenshots/ingame_character_list_converted.png");
                        ImageIO.write(imgAConverted, "png", ingameCharacterListConvertedFile.getAbsoluteFile());

                        // // Compare the converted imgA with the two character buttons
                        List<int[]> matches = ImageUtils.imageCompareSimilar(imgAConverted, characterIcon, 20);
                        List<int[]> matches2 = ImageUtils.imageCompareSimilar(imgAConverted, characterIcon2, 20);
                        // // Merge the results list in an alternating fashion to restore the order
                        List<int[]> mergedMatches = mergeMatches(matches, matches2);

                        // // If character icons are not found, players are not visible and the action is stopped
                        if (mergedMatches.isEmpty()) {
                            System.out.print("\nNo players found, action stopped\n");
                            break;
                        }

                        List<int[]> currentMatches = new ArrayList<>(mergedMatches);
                        
                        for (int[] match : mergedMatches) {
                            //System.out.println("Character Icon found at: (" + match[0] + ", " + match[1] + ")");
                        }

                        // Create a new image from the desired section of the converted image
                        BufferedImage subImage = imgAConverted.getSubimage(517, 481, 54, 85);
                        File scrollbarEndIngameFile = new File("screenshots/scrollbar_end_ingame.png");
                        ImageIO.write(subImage, "png", scrollbarEndIngameFile.getAbsoluteFile());
                        
                        for (int[] match : currentMatches) {
                            robot.keyPress(KeyEvent.VK_TAB);
                            robot.delay(100);
                            int iconWidth = characterIcon.getWidth();
                            int iconHeight = characterIcon.getHeight();
                            int clickX = offsetX + match[0] + iconWidth / 2;
                            int clickY = offsetY + match[1] + iconHeight / 2;
                            // Navigate through the pages to reach the Steam icon by clicking the character icon
                            ScreenManager.click(robot, clickX, clickY);
                            robot.delay(300);
                            robot.keyRelease(KeyEvent.VK_TAB);
                            // After that, we check if the outfit button is present
                            BufferedImage outfitButtonCheck = robot.createScreenCapture(new Rectangle(847, 204, 883, 343));
                            File outfitButtonCheckFile = new File("screenshots/outfit_button_check.png");
                            ImageIO.write(outfitButtonCheck, "png", outfitButtonCheckFile.getAbsoluteFile());
                            
                            File outfitButtonFile = new File("screenshots/icons/outfit_button.png");
                            BufferedImage outfitButton = ImageIO.read(outfitButtonFile.getAbsoluteFile());
                            
                            List<int[]> buttonMatches = ImageUtils.imageCompareSimilar(outfitButtonCheck, outfitButton, 20);
                            // If we find the outfit button, center it and press it before clicking the steam icon
                            if (buttonMatches.size() == 1) {
                                //System.out.print("\nButton found at: (" + match[0] + ", " + match[1] + ")");
                                //Click on button outfit
                                ScreenManager.click(robot, 1044, 259);
                                robot.delay(150);
                            }
                            // If we don't find the outfit button, we can already press the steam icon
                            // Click on the 2 possible points where the steam icon might be (due to whether the character is already selected or not)
                            ScreenManager.click(robot, 1300, 363);                                  
                            ScreenManager.click(robot, 1300, 570);
                            robot.delay(400);
                            //Then we check if the button to restore window is hidden, if yes the steam window is hidden and needs to be clicked, else its already visible
                            BufferedImage restoreWindowIconCheck = robot.createScreenCapture(new Rectangle(0, 960, 1920, 120));
                            File restoreWindowCheckFile = new File("screenshots/steam_restore_window_check.png");
                            ImageIO.write(restoreWindowIconCheck, "png", restoreWindowCheckFile.getAbsoluteFile());
                            
                            File restoreWindowFile = new File("screenshots/icons/steam_restore_window_icon.png");
                            BufferedImage restoreWindowIcon = ImageIO.read(restoreWindowFile.getAbsoluteFile());
                            
                            List<int[]> iconMatches = ImageUtils.imageCompareEqual(restoreWindowIconCheck, restoreWindowIcon);
                            
                            File restoreWindowFile2 = new File("screenshots/icons/steam_restore_window_icon_2.png");
                            BufferedImage restoreWindowIcon2 = ImageIO.read(restoreWindowFile2.getAbsoluteFile());
                            
                            List<int[]> iconMatches2 = ImageUtils.imageCompareEqual(restoreWindowIconCheck, restoreWindowIcon2);
                            
                            if (iconMatches.size() == 1 || iconMatches2.size() == 1) {
                                ScreenManager.click(robot, 1317, 1003);
                            }
                            
                            overlayManager.clickIcon();
                                    
                            robot.keyPress(KeyEvent.VK_ESCAPE);
                            robot.delay(200);
                            robot.keyRelease(KeyEvent.VK_ESCAPE);
                            robot.delay(50);
                            
                            //If there are between 5 and 6 icons, save coordinates of last icon
                            if (currentMatches.size() > 4 && currentMatches.size() < 7) {
                                lastIconX = clickX;
                                lastIconY = clickY;
                            }
                            
                        }
                        
                        // Clean of clipboard at the start
                        ClipboardUtils.clearClipboard();

                       if (subImage != null && ImageUtils.containsBlack(subImage, 30)) {
                            try {
                                String previousClipboardText;
                                while (true) {

                                    robot.keyPress(KeyEvent.VK_TAB);
                                    robot.delay(100);
                                    robot.mouseWheel(1);
                                    robot.delay(100);

                                    ScreenManager.click(robot, lastIconX, lastIconY);
                                    robot.delay(100);
                                    robot.keyRelease(KeyEvent.VK_TAB);
                                    robot.delay(50);

                                    BufferedImage outfitButtonCheck = robot.createScreenCapture(new Rectangle(847, 204, 883, 343));
                                    File outfitButtonFile = new File("screenshots/icons/outfit_button.png");
                                    BufferedImage outfitButton = ImageIO.read(outfitButtonFile.getAbsoluteFile());

                                    List<int[]> buttonMatches = ImageUtils.imageCompareSimilar(outfitButtonCheck, outfitButton, 10);

                                    if (buttonMatches.size() == 1) {
                                        ScreenManager.click(robot, 1044, 259);
                                        robot.delay(150);
                                    }

                                    //Obtain text from clipboard
                                    previousClipboardText = ClipboardUtils.getClipboardText();
                                    
                                    //If we cant find outfit button, we can already click steam icon
                                    //We click on the 2 possible points where steam icon can be (depending if character is selected or not)
                                    ScreenManager.click(robot, 1300, 363);                                  
                                    ScreenManager.click(robot, 1300, 570);
                                    robot.delay(400);
                                    //Then we check if the button to restore the icon is visible, if yes the steam window is hidden and you have to click, otherwise it is already visible
                                    BufferedImage restoreWindowIconCheck = robot.createScreenCapture(new Rectangle(0, 960, 1920, 120));
                                    File restoreWindowCheckFile = new File("screenshots/steam_restore_window_check.png");
                                    ImageIO.write(restoreWindowIconCheck, "png", restoreWindowCheckFile.getAbsoluteFile());
                            
                                    File restoreWindowFile = new File("screenshots/icons/steam_restore_window_icon.png");
                                    BufferedImage restoreWindowIcon = ImageIO.read(restoreWindowFile.getAbsoluteFile());
                            
                                    List<int[]> iconMatches = ImageUtils.imageCompareEqual(restoreWindowIconCheck, restoreWindowIcon);
                            
                                    File restoreWindowFile2 = new File("screenshots/icons/steam_restore_window_icon_2.png");
                                    BufferedImage restoreWindowIcon2 = ImageIO.read(restoreWindowFile2.getAbsoluteFile());
                            
                                    List<int[]> iconMatches2 = ImageUtils.imageCompareEqual(restoreWindowIconCheck, restoreWindowIcon2);
                            
                                    if (iconMatches.size() == 1 || iconMatches2.size() == 1) {
                                        ScreenManager.click(robot, 1317, 1003);
                                    }
                            
                                    overlayManager.clickIcon();
                                    
                                    robot.keyPress(KeyEvent.VK_ESCAPE);
                                    robot.delay(200);
                                    robot.keyRelease(KeyEvent.VK_ESCAPE);
                                    robot.delay(50);

                                    // Get text from clipboard and compare
                                    String currentClipboardText = ClipboardUtils.getClipboardText().trim();
                                    if (currentClipboardText.equals(previousClipboardText)) {
                                        break;
                                    }

                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    else {//System.out.println("subImage is null or contains no black pixels.");
                        }
                        
                    }
                    
                    
                    case 3 -> {
                        ScreenManager.clearConsole(); //"clean" the console
                        FileUtils.reloadGraylist(); // Reload the graylist
                        FileUtils.reloadWarnlist(); // Reload the warnlist
                        overlayManager.getCurrentLists(); //Reload new lists on steam manager
                        System.out.print("\nLists reloaded\n");
                    }
                    
                    case 4 -> {
                        ScreenManager.clearConsole(); //"clean" the console
                        soloTargeting = !soloTargeting;
                    }
                    
                    case 5 -> {
                        ScreenManager.clearConsole(); //"clean" the console
                        FileUtils.downloadWarnlist(); // Try downloading the official warnlist from the GitHub of the "GEB" project
                    }
                    
                    default -> {
                        end = true;
                    }
                }
                    
                } else {
                    choose = in.intValue("\nSOLO_TARGETING: " + status + "\n\n1)Analize a player from a server (from server list)\n2)Analize a player from the server you are connected to\n3)Reload all lists\n4)Switch solotargeting mode\n5)Download official warnlist\nOther)Quit\n\n");
                    
                    switch (choose) {
                        
                    case 1 -> {
                         
                        ScreenManager.clearConsole();
                        robot.delay(3000);
                        // Get the current mouse position
                        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
        
                        // Extract X and Y coordinates
                        int x = (int) mouseLocation.getX();
                        int y = (int) mouseLocation.getY();
                            
                        ScreenManager.click(robot, x, y);
                        overlayManager.clickIcon();
                    }


                    case 2 -> {
                        ScreenManager.clearConsole();
                        robot.delay(3000);
                        robot.keyPress(KeyEvent.VK_TAB);
                        robot.delay(100);
                        // Get the current mouse position
                        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
                        // Extract X and Y coordinates
                        int x = (int) mouseLocation.getX();
                        int y = (int) mouseLocation.getY();
                        //Navigate through the pages to reach the steam icon by clicking the character icon
                        ScreenManager.click(robot, x, y);
                        robot.delay(300);
                        robot.keyRelease(KeyEvent.VK_TAB);
                        
                        BufferedImage outfitButtonCheck = robot.createScreenCapture(new Rectangle(847, 204, 883, 343));
                        File outfitButtonCheckFile = new File("screenshots/outfit_button_check.png");
                        ImageIO.write(outfitButtonCheck, "png", outfitButtonCheckFile.getAbsoluteFile());
                            
                        File outfitButtonFile = new File("screenshots/icons/outfit_button.png");
                        BufferedImage outfitButton = ImageIO.read(outfitButtonFile.getAbsoluteFile());
                            
                        List<int[]> buttonMatches = ImageUtils.imageCompareSimilar(outfitButtonCheck, outfitButton, 20);
                        // If we find the outfit button, let's center it and press it before pressing the steam icon
                        if (buttonMatches.size() == 1) {
                            //System.out.print("\nButton found at: (" + match[0] + ", " + match[1] + ")");
                            //We click on the outfit button
                            ScreenManager.click(robot, 1044, 259);
                            robot.delay(150);
                        }
                        //If we don't find the outfit button, we can already press the steam icon
                        //We click on the 2 possible points where the steam icon can be (depending on whether the character is already selected or not)
                        ScreenManager.click(robot, 1300, 363);                                  
                        ScreenManager.click(robot, 1300, 570);
                        robot.delay(400);
                        //Then we check if the button to restore the icon is visible, if so the steam window is hidden and you have to click, otherwise it is already visible
                        BufferedImage restoreWindowIconCheck = robot.createScreenCapture(new Rectangle(0, 960, 1920, 120));
                        File restoreWindowCheckFile = new File("screenshots/steam_restore_window_check.png");
                        ImageIO.write(restoreWindowIconCheck, "png", restoreWindowCheckFile.getAbsoluteFile());
                            
                        File restoreWindowFile = new File("screenshots/icons/steam_restore_window_icon.png");
                        BufferedImage restoreWindowIcon = ImageIO.read(restoreWindowFile.getAbsoluteFile());
                            
                        List<int[]> iconMatches = ImageUtils.imageCompareEqual(restoreWindowIconCheck, restoreWindowIcon);
                            
                        File restoreWindowFile2 = new File("screenshots/icons/steam_restore_window_icon_2.png");
                        BufferedImage restoreWindowIcon2 = ImageIO.read(restoreWindowFile2.getAbsoluteFile());
                            
                        List<int[]> iconMatches2 = ImageUtils.imageCompareEqual(restoreWindowIconCheck, restoreWindowIcon2);
                            
                        if (iconMatches.size() == 1 || iconMatches2.size() == 1) {
                            ScreenManager.click(robot, 1317, 1003);
                        }
                            
                        overlayManager.clickIcon();
                                    
                        robot.keyPress(KeyEvent.VK_ESCAPE);
                        robot.delay(200);
                        robot.keyRelease(KeyEvent.VK_ESCAPE);
                        robot.delay(50);
                    }
                    
                    
                    case 3 -> {
                        ScreenManager.clearConsole(); //"clean" the console
                        FileUtils.reloadGraylist(); // Reload the graylist
                        FileUtils.reloadWarnlist(); // Reload the warnlist
                        overlayManager.getCurrentLists(); //Reload new lists on steam manager
                        System.out.print("\nLists reloaded\n");
                    }
                    
                    case 4 -> {
                        ScreenManager.clearConsole(); //"clean" the console
                        soloTargeting = !soloTargeting;//Reverses the current state of the Targeting only
                    }
                    
                    case 5 -> {
                        ScreenManager.clearConsole(); //"clean" the console
                        FileUtils.downloadWarnlist(); // Try downloading the official warnlist from the GitHub of the "GEB" project
                    }
                    
                    default -> {
                        end = true;
                    }
                    }
                }
               
               
            } while (!end);
        }
    }
}
