package com.yocxhell.robottest;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class ClipboardUtils {

    /**
     * Gets the text copied from the clipboard.
     *
     * @return The copied text, or an empty string if there is no text.
     */
    public static String getClipboardText() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);

        try {
            if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) contents.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    // Method to clear the clipboard
    public static void clearClipboard() {
        // Create an empty string
        StringSelection emptySelection = new StringSelection("");
        
        // Get the system clipboard
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        
        // Set the clipboard with empty content
        clipboard.setContents(emptySelection, null);
    }
}
