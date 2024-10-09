package hhh;

import java.awt.BorderLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;


public class Gobang {
	static Image logo = new ImageIcon("logo.png").getImage();
	
	public Gobang() {
		
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Îå×ÓÆå");
		frame.setIconImage(logo);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
		frame.getContentPane().add(Vars.toolPanel,BorderLayout.NORTH);
		frame.getContentPane().add(Vars.eastPanel,BorderLayout.EAST);
		frame.getContentPane().add(Vars.westPanel,BorderLayout.WEST);
		frame.getContentPane().add(Vars.messagePanel,BorderLayout.SOUTH);
		frame.getContentPane().add(Vars.paintPanel,BorderLayout.CENTER);
		
		frame.setSize(1200, 900);
		frame.setVisible(true);
	}

}
