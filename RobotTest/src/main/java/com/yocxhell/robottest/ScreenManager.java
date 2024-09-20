package com.yocxhell.robottest;

import java.awt.Robot;
import java.awt.event.InputEvent;

/**
 * Classe che gestisce il confronto delle immagini.
 */
public class ScreenManager {


    public static String getSoloTargetingStatus(boolean soloTargeting) {
        
        String statusText = soloTargeting ? "TRUE" : "FALSE";
        return statusText ;
    }
    /**
     * Pulisce la console stampando righe vuote.
     */
    public static void clearConsole() {
        for (int i = 0; i < 100; i++) {
            System.out.println();
        }
    }

    /**
     * Esegue un clic del mouse in una posizione specificata.
     *
     * @param robot L'istanza di Robot per eseguire le azioni
     * @param x La coordinata x del clic
     * @param y La coordinata y del clic
     */
    public static void click(Robot robot, int x, int y) {
        robot.mouseMove(x, y);
        robot.delay(50); // Aggiunge un delay per consentire all'app di registrare l'hover
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(50);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
   
}
