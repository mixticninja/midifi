package com.flat20.fingerplay.ui;



import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
 
public class BackgroundPanel extends JPanel {
 
    private static final long serialVersionUID = 1L;
    private ImageIcon background;
   
    public BackgroundPanel(String fileName) {
        super();
        this.setLayout(new GridBagLayout());
        
        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        BufferedImage img = null;
        try {
			 img =ImageIO.read(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        this.background = new ImageIcon(img);
    }
 
    public void setBackground(ImageIcon background) {
        this.background = background;
    }
 
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background.getImage(), 0, 0, this);
    }
}