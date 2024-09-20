package com.yocxhell.robottest;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class ClipboardUtils {

    /**
     * Ottiene il testo copiato dagli appunti.
     *
     * @return Il testo copiato, oppure una stringa vuota se non c'è testo.
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
    
    // Metodo per pulire la clipboard
    public static void clearClipboard() {
        // Crea una stringa vuota
        StringSelection emptySelection = new StringSelection("");
        
        // Ottieni la clipboard di sistema
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        
        // Imposta la clipboard con il contenuto vuoto
        clipboard.setContents(emptySelection, null);
    }
}
