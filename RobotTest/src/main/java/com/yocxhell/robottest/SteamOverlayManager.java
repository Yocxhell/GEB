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
 * Gestisce le interazioni con l'overlay di Steam.
 */
public class SteamOverlayManager {

    private Robot robot;
    private Map<String, String> warnlist;


    public SteamOverlayManager(Robot robot) throws IOException {
        this.robot = robot;
    }

    /**
     * Clicca sull'icona e esegue le azioni dell'overlay di Steam.
     *
     * @param x La coordinata x dell'icona
     * @param y La coordinata y dell'icona
     */
    public void clickIcon(int x, int y) {
        ScreenManager.click(robot, x, y);
        // Esegui azioni dopo il clic, attendendo che l'overlay si apra fino al tempo massimo fornito, altrimenti prosegue al prossimo
        performActions("screenshots/icons/steam_overlay_option_bar.png", 10000, 500);
    }
    
    public void clickIcon() {
        // Esegui azioni dopo il clic, attendendo che l'overlay si apra fino al tempo massimo fornito, altrimenti prosegue al prossimo
        performActions("screenshots/icons/steam_overlay_option_bar.png", 10000, 500);
    }

    /**
     * Esegue le azioni necessarie sull'overlay di Steam e attende fino a quando l'immagine di conferma non viene trovata.
     *
     * @param confirmationImagePath Il percorso dell'immagine di conferma da trovare
     * @param maxWaitTime Il tempo massimo di attesa in millisecondi
     * @param checkInterval L'intervallo di controllo in millisecondi
     */
    public void performActions(String confirmationImagePath, int maxWaitTime, int checkInterval) {
        try {
            // Carica l'immagine di conferma (url contenente lo steamid)
            File confirmationImageFile = new File(confirmationImagePath);
            BufferedImage confirmationImage = ImageIO.read(confirmationImageFile.getAbsoluteFile());

            // Ottieni la dimensione dello schermo
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Rectangle screenRect = new Rectangle(toolkit.getScreenSize());

            long startTime = System.currentTimeMillis();
            // Finché il tempo di attesa per trovare la corrispondenza non finisce
            while (System.currentTimeMillis() - startTime < maxWaitTime) {
                BufferedImage screenCapture = robot.createScreenCapture(screenRect);

                // Trova la posizione dell'immagine di conferma nello screenshot per trovare l'url con steamid da copiare
                Point imagePosition = ImageUtils.findImagePosition(screenCapture, confirmationImage);

                if (imagePosition != null) {
                    //System.out.println("Menu option trovato.");
                    // Esegui le azioni desiderate qui
                    executeActions(imagePosition, confirmationImage);

                    return; // Esce dal metodo dopo aver eseguito le azioni
                }

                // Delay tra i tentativi
                robot.delay(checkInterval);
            }

            //System.out.println("Tempo massimo di attesa superato senza trovare l'immagine di conferma.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Esegue una serie di azioni specifiche una volta trovata l'immagine di conferma.
     *
     * @param position La posizione dell'immagine trovata
     * @param confirmationImage L'immagine di conferma
     */
    private void executeActions(Point position, BufferedImage confirmationImage) {
        int xStart = position.x + confirmationImage.getWidth() / 2 + 95; // Centrato orizzontalmente sull'immagine
        int yStart = position.y + confirmationImage.getHeight() / 2; // Centrato verticalmente sull'immagine

        // Stampa le coordinate per il debug
        //System.out.println("Coordinate calcolate per il clic: (" + xStart + ", " + yStart + ")");

        // Ottieni la dimensione dello schermo
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Rectangle screenRect = new Rectangle(toolkit.getScreenSize());
        int screenWidth = screenRect.width;

        // Esegui il clic e inizia a trascinare
        robot.mouseMove(xStart, yStart);
        robot.delay(100);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

        // Trascina il mouse fino ai limiti dello schermo
        int stepSize = 10; // Dimensione del passo per il movimento
        for (int x = xStart; x <= screenWidth; x += stepSize) {
            robot.mouseMove(x, yStart);
            robot.delay(1);
        }

        // Rilascia il clic
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

        // Copio il testo evidenziato con il trascinamento del mouse negli appunti
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_C);
        robot.delay(50);
        robot.keyRelease(KeyEvent.VK_C);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.delay(50);

        // Ottieni il testo copiato dagli appunti
        String copiedText = ClipboardUtils.getClipboardText().trim();


        // Aggiungi il testo alla graylist se non è già presente
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

            //Provo a cercare la barra di uscita
            Point imagePosition = ImageUtils.findImagePosition(screenCapture, targetImage);

            if (imagePosition != null) {
                //System.out.println("Immagine exit trovata e cliccata.");
                int xClick = imagePosition.x + targetImage.getWidth() / 2; // Centrato orizzontalmente sull'immagine
                int yClick = imagePosition.y + targetImage.getHeight() / 2; // Centrato verticalmente sull'immagine
                robot.mouseMove(xClick, yClick);
                robot.delay(200);
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            } else {
                //System.out.println("Immagine exit non trovata.");
                // Prova con altre azioni di fallback se necessario
                robot.mouseMove(1317, 1003);
                robot.delay(100);
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            }
        } catch (IOException e) {
            //System.err.println("Errore nel leggere l'immagine di exit: " + e.getMessage());
            e.printStackTrace();
        }

        // Premi Shift + Tab
        robot.keyPress(KeyEvent.VK_SHIFT);
        robot.keyPress(KeyEvent.VK_TAB);
        robot.delay(50);
        robot.keyRelease(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_SHIFT);
        // Delay per far svanire l'overlay di Steam per assicurarsi che le icone siano cliccabili
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
