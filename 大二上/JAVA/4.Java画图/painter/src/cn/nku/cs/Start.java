package cn.nku.cs;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class Start {
	public static void main(String[] args) {
		JFrame frame = new JFrame("painter");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(Vars.shapeBar,BorderLayout.WEST);
		frame.getContentPane().add(Vars.toolPanel,BorderLayout.NORTH);
		frame.getContentPane().add(Vars.paintPanel,BorderLayout.CENTER);
		frame.setSize(800, 300);
		frame.setVisible(true);
	}
}
