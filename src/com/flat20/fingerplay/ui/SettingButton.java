package com.flat20.fingerplay.ui;



import java.awt.Color;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
 
public class SettingButton extends JButton {
 
    private static final long serialVersionUID = 1L;
 
    public SettingButton(String txt, String icon, String iconHover) {
        super(txt);
        setForeground(Color.WHITE);
         
        setOpaque(false);
        setContentAreaFilled(false); // On met à false pour empêcher le composant de peindre l'intérieur du JButton.
        setBorderPainted(false); // De même, on ne veut pas afficher les bordures.
        setFocusPainted(false); // On n'affiche pas l'effet de focus.
         
        setHorizontalAlignment(SwingConstants.CENTER);
        setHorizontalTextPosition(SwingConstants.CENTER);
      
   
        InputStream is = getClass().getClassLoader().getResourceAsStream(icon);
        BufferedImage img = null;
        try {
			 img =ImageIO.read(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        setIcon(new ImageIcon(img));
        
        is = getClass().getClassLoader().getResourceAsStream(iconHover);
        try {
			 img =ImageIO.read(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
       setRolloverIcon(new ImageIcon(img));
    }
}