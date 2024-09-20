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
 * Classe principale per l'esecuzione del test del Robot.
 */
public class RobotTest {

    /**
     * Unisce due liste di coordinate, inserendo i primi elementi di match2 dopo i primi di match e i secondi di match2 dopo i secondi di match.
     *
     * @param matches La prima lista di coordinate
     * @param matches2 La seconda lista di coordinate
     * @return Una nuova lista con gli elementi di match e match2 uniti come specificato
     */
    private static List<int[]> mergeMatches(List<int[]> matches, List<int[]> matches2) {
        // Crea una lista per contenere tutti gli elementi delle due liste di input
        List<int[]> mergedMatches = new ArrayList<>();
        
        // Aggiungi tutti gli elementi delle due liste alla lista unificata
        mergedMatches.addAll(matches);
        mergedMatches.addAll(matches2);

        // Ordina la lista unificata in base al secondo elemento di ogni array
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
                // Usa il metodo di ScreenManager per ottenere il testo colorato
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
                        
                        // Se non si trovano icone steam nello screenshot non sono visibili i player e si interrompe l'azione
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
                        
                        
                        // Se l'inizio della barra viene trovata, significa che ci sono altri match nascosti e necessitano di scrolling per essere cliccati
                        if (savedImage != null && ImageUtils.imageCompareEqual(imgA, savedImage).size() == 1) {
                            try {
                                // Carica l'immagine di conferma (per capire quando terminare lo scroll)
                                File endImageFile = new File("screenshots/icons/scrollbar_end.png");
                                BufferedImage endImage = ImageIO.read(endImageFile.getAbsoluteFile());

                                // Ottieni la dimensione dello schermo
                                Toolkit toolkit = Toolkit.getDefaultToolkit();
                                Rectangle screenRect = new Rectangle(toolkit.getScreenSize());

                                BufferedImage previousCapture = imgA; // Cattura iniziale

                                // Finché l'immagine di fine scrollbar non viene trovata, continua a scrollare e cliccare l'ultima icona trovata
                                while (true) {
                                    imgA = robot.createScreenCapture(screenRect);
                                    robot.mouseWheel(1); // Scrolla in basso
                                    robot.delay(100);

                                    // Verifica se l'immagine di fine scrollbar è visibile
                                    if (!ImageUtils.imageCompareEqual(imgA, endImage).isEmpty()) {
                                        //System.out.println("Fine della scrollbar trovata.");
                                        break; // Esce dal ciclo se l'immagine di fine scrollbar è trovata
                                    }

                                    // Clicca l'ultima icona trovata
                                    overlayManager.clickIcon(lastIconX, lastIconY);

                                    // Verifica se l'immagine catturata è cambiata (per evitare loop infiniti)
                                    if (ImageUtils.imagesAreEqual(previousCapture, imgA)) {
                                        //System.out.println("Nessun cambiamento nella schermata, terminando lo scroll.");
                                        break; // Esce dal ciclo se la schermata non cambia
                                    }
                                    previousCapture = imgA; // Aggiorna la cattura precedente
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
                        
                        // Cattura lo screenshot
                        BufferedImage imgA = robot.createScreenCapture(new Rectangle(offsetX, offsetY, 577, 577));

                        // Carica immagini icona (principale e variante)
                        File characterIconFile = new File("screenshots/icons/view_players_menu_character_icon.png");
                        BufferedImage characterIcon = ImageIO.read(characterIconFile.getAbsoluteFile());

                        File characterIcon2File = new File("screenshots/icons/view_players_menu_character_icon_2.png");
                        BufferedImage characterIcon2 = ImageIO.read(characterIcon2File.getAbsoluteFile());
                        
                        // Converti solo imgA per sostituire i pixel trasparenti con il bianco
                        BufferedImage imgAConverted = ImageUtils.convertNonBlackOrReferenceColorsToWhite(imgA, characterIcon, characterIcon2);
                        
                        robot.delay(100);
                        robot.keyRelease(KeyEvent.VK_TAB);
                        
                        File ingameCharacterListFile = new File("screenshots/ingame_character_list.png");
                        ImageIO.write(imgA, "png", ingameCharacterListFile.getAbsoluteFile());

                        File ingameCharacterListConvertedFile = new File("screenshots/ingame_character_list_converted.png");
                        ImageIO.write(imgAConverted, "png", ingameCharacterListConvertedFile.getAbsoluteFile());

                        // Confronta imgA convertita con i due character button
                        List<int[]> matches = ImageUtils.imageCompareSimilar(imgAConverted, characterIcon, 20);
                        List<int[]> matches2 = ImageUtils.imageCompareSimilar(imgAConverted, characterIcon2, 20);
                        // Unisci la lista di risultati in modo alternato per ripristinare l'ordine
                        List<int[]> mergedMatches = mergeMatches(matches, matches2);

                        // Se non si trovano icone carattere, non sono visibili i player e si interrompe l'azione
                        if (mergedMatches.isEmpty()) {
                            System.out.print("\nNo players found, action stopped\n");
                            break;
                        }

                        List<int[]> currentMatches = new ArrayList<>(mergedMatches);
                        
                        for (int[] match : mergedMatches) {
                            //System.out.println("Icona carattere trovata a: (" + match[0] + ", " + match[1] + ")");
                        }

                        // Creare una nuova immagine dalla sezione desiderata dell'immagine convertita
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
                            //Naviga attraverso le pagine per raggiungere l'icona steam, cliccando il character icon
                            ScreenManager.click(robot, clickX, clickY);
                            robot.delay(300);
                            robot.keyRelease(KeyEvent.VK_TAB);
                            //Dopodichè controlliamo se è presente il bottone outfit
                            BufferedImage outfitButtonCheck = robot.createScreenCapture(new Rectangle(847, 204, 883, 343));
                            File outfitButtonCheckFile = new File("screenshots/outfit_button_check.png");
                            ImageIO.write(outfitButtonCheck, "png", outfitButtonCheckFile.getAbsoluteFile());
                            
                            File outfitButtonFile = new File("screenshots/icons/outfit_button.png");
                            BufferedImage outfitButton = ImageIO.read(outfitButtonFile.getAbsoluteFile());
                            
                            List<int[]> buttonMatches = ImageUtils.imageCompareSimilar(outfitButtonCheck, outfitButton, 20);
                            // Se troviamo il bottone outfit, centriamolo e premiamolo prima di premere l'icona steam
                            if (buttonMatches.size() == 1) {
                                //System.out.print("\nBottone trovato a: (" + match[0] + ", " + match[1] + ")");
                                //Clicchiamo sul bottone outfit
                                ScreenManager.click(robot, 1044, 259);
                                robot.delay(150);
                            }
                            //Se non troviamo il bottone outfit, possiamo già premere l'icona di steam
                            //Clicchiamo sui 2 punti possibili dove può essere l'icona di steam (a causa del personaggio già selezionato o meno)
                            ScreenManager.click(robot, 1300, 363);                                  
                            ScreenManager.click(robot, 1300, 570);
                            robot.delay(400);
                            //Poi controlliamo se il tasto per ripristinare l'icona è visibile, se si la finestra di steam è nascosta e bisogna cliccare, altrimenti è già visibile
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
                            
                            //Se ci sono tra 5 e 6 icone, salva le coordinate dell'ultima icona
                            if (currentMatches.size() > 4 && currentMatches.size() < 7) {
                                lastIconX = clickX;
                                lastIconY = clickY;
                            }
                            
                        }
                        
                        // Pulizia della clipboard all'inizio
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

                                    //Ottenere il testo dalla clipboard
                                    previousClipboardText = ClipboardUtils.getClipboardText();
                                    
                                    //Se non troviamo il bottone outfit, possiamo già premere l'icona di steam
                                    //Clicchiamo sui 2 punti possibili dove può essere l'icona di steam (a causa del personaggio già selezionato o meno)
                                    ScreenManager.click(robot, 1300, 363);                                  
                                    ScreenManager.click(robot, 1300, 570);
                                    robot.delay(400);
                                    //Poi controlliamo se il tasto per ripristinare l'icona è visibile, se si la finestra di steam è nascosta e bisogna cliccare, altrimenti è già visibile
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

                                    // Ottenere il testo dalla clipboard e confrontare
                                    String currentClipboardText = ClipboardUtils.getClipboardText().trim();
                                    if (currentClipboardText.equals(previousClipboardText)) {
                                        break;
                                    }

                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    else {//System.out.println("subImage è nullo o non contiene pixel neri.");
                        }
                        
                    }
                    
                    
                    case 3 -> {
                        ScreenManager.clearConsole(); //"pulisci" la console
                        FileUtils.reloadGraylist(); // Ricarica la graylist
                        FileUtils.reloadWarnlist(); // Ricarica la warnlist
                        overlayManager.getCurrentLists(); //Ricarica le nuove liste sul manager steam
                        System.out.print("\nLists reloaded\n");
                    }
                    
                    case 4 -> {
                        ScreenManager.clearConsole(); //"pulisci" la console
                        soloTargeting = !soloTargeting;
                    }
                    
                    case 5 -> {
                        ScreenManager.clearConsole(); //"pulisci" la console
                        FileUtils.downloadWarnlist(); // Prova a scaricare la warnlist ufficiale dal GitHub del progetto "GEB"
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
                        // Ottieni la posizione attuale del mouse
                        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
        
                        // Estrai le coordinate X e Y
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
                        // Ottieni la posizione attuale del mouse
                        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
                        // Estrai le coordinate X e Y
                        int x = (int) mouseLocation.getX();
                        int y = (int) mouseLocation.getY();
                        //Naviga attraverso le pagine per raggiungere l'icona steam, cliccando il character icon
                        ScreenManager.click(robot, x, y);
                        robot.delay(300);
                        robot.keyRelease(KeyEvent.VK_TAB);
                        
                        BufferedImage outfitButtonCheck = robot.createScreenCapture(new Rectangle(847, 204, 883, 343));
                        File outfitButtonCheckFile = new File("screenshots/outfit_button_check.png");
                        ImageIO.write(outfitButtonCheck, "png", outfitButtonCheckFile.getAbsoluteFile());
                            
                        File outfitButtonFile = new File("screenshots/icons/outfit_button.png");
                        BufferedImage outfitButton = ImageIO.read(outfitButtonFile.getAbsoluteFile());
                            
                        List<int[]> buttonMatches = ImageUtils.imageCompareSimilar(outfitButtonCheck, outfitButton, 20);
                        // Se troviamo il bottone outfit, centriamolo e premiamolo prima di premere l'icona steam
                        if (buttonMatches.size() == 1) {
                            //System.out.print("\nBottone trovato a: (" + match[0] + ", " + match[1] + ")");
                            //Clicchiamo sul bottone outfit
                            ScreenManager.click(robot, 1044, 259);
                            robot.delay(150);
                        }
                        //Se non troviamo il bottone outfit, possiamo già premere l'icona di steam
                        //Clicchiamo sui 2 punti possibili dove può essere l'icona di steam (a causa del personaggio già selezionato o meno)
                        ScreenManager.click(robot, 1300, 363);                                  
                        ScreenManager.click(robot, 1300, 570);
                        robot.delay(400);
                        //Poi controlliamo se il tasto per ripristinare l'icona è visibile, se si la finestra di steam è nascosta e bisogna cliccare, altrimenti è già visibile
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
                        ScreenManager.clearConsole(); //"pulisci" la console
                        FileUtils.reloadGraylist(); // Ricarica la graylist
                        FileUtils.reloadWarnlist(); // Ricarica la warnlist
                        overlayManager.getCurrentLists(); //Ricarica le nuove liste sul manager steam
                        System.out.print("\nLists reloaded\n");
                    }
                    
                    case 4 -> {
                        ScreenManager.clearConsole(); //"pulisci" la console
                        soloTargeting = !soloTargeting;//Inverte lo stato corrente del soloTargeting
                    }
                    
                    case 5 -> {
                        ScreenManager.clearConsole(); //"pulisci" la console
                        FileUtils.downloadWarnlist(); // Prova a scaricare la warnlist ufficiale dal GitHub del progetto "GEB"
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
