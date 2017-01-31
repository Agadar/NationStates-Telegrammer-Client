package com.github.agadar.nstelegram;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.JTextField;

/**
 * A JTextField with a gray hint text. Original code from:
 * http://stackoverflow.com/questions/1738966/java-jtextfield-with-input-hint.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class HintTextField extends JTextField {

    /* The hint to show. */
    private String hint;
    
    public HintTextField() {
        super();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (getText().length() == 0 && hint != null) {
            int h = getHeight();
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Insets ins = getInsets();
            FontMetrics fm = g.getFontMetrics();
            int c0 = getBackground().getRGB();
            int c1 = getForeground().getRGB();
            int m = 0xfefefefe;
            int c2 = ((c0 & m) >>> 1) + ((c1 & m) >>> 1);
            g.setColor(new Color(c2, true));
            g.drawString(hint, ins.left, h / 2 + fm.getAscent() / 2 - 2);
        }
    }

    /**
     * Sets the hint text to show.
     *
     * @param hint
     */
    public void setHint(String hint) {
        this.hint = hint;
        repaint();
    }
}
