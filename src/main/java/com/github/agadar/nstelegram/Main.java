package com.github.agadar.nstelegram;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author martin
 */
public class Main 
{
    public static void main(String args[])
    {
        try
        {
            // Set cross-platform look&feel.
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

            // Create and display the form.
            java.awt.EventQueue.invokeLater(() -> 
            {
                NSTelegramForm form = new NSTelegramForm();
                form.setLocationRelativeTo(null);
                form.setVisible(true);
            });
        }
        catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException |
                UnsupportedLookAndFeelException ex)
        {
            Logger.getLogger(NSTelegramForm.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
}
