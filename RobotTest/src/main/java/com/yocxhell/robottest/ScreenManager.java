package com.yocxhell.robottest;

import java.awt.Robot;
import java.awt.event.InputEvent;

/**
 * Class that manages image comparison.
 */
public class ScreenManager {

    public static String getSoloTargetingStatus(boolean soloTargeting) {
        String statusText = soloTargeting ? "TRUE" : "FALSE";
        return statusText;
    }

    /**
     * Clears the console by printing empty lines.
     */
    public static void clearConsole() {
        for (int i = 0; i < 100; i++) {
            System.out.println();
        }
    }

    /**
     * Executes a mouse click at a specified position.
     *
     * @param robot The Robot instance to perform actions
     * @param x The x coordinate of the click
     * @param y The y coordinate of the click
     */
    public static void click(Robot robot, int x, int y) {
        robot.mouseMove(x, y);
        robot.delay(50); // Adds a delay to allow the app to register the hover
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(50);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

}

