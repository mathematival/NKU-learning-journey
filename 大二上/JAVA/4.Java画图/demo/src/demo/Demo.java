package demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Demo {

	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		java.awt.Color[] colors = {Color.red,Color.green,Color.blue};
		JComboBox<Color> petList = new JComboBox(colors);
		f.getContentPane().add(petList,BorderLayout.NORTH);
		petList.setRenderer(new ColorRender());
		petList.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JComboBox<String> ccc = (JComboBox<String>) e.getSource();
				JOptionPane.showMessageDialog(null, ccc.getSelectedItem());
			}
		});
//		String[] names = {"Tom","Jerry","Cuihua"};
//		JComboBox<String> box = new JComboBox<>(names);
//		box.setPreferredSize(new Dimension(200,30));
//		box.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				JComboBox<String> ccc = (JComboBox<String>) e.getSource();
//				String txt = (String) ccc.getSelectedItem();
//				JOptionPane.showMessageDialog(null, txt);
//			}
//		});
//		box.setRenderer(new ComboBoxRender());
		
//		f.getContentPane().add(box,BorderLayout.NORTH);
		f.setSize(400, 300);
		f.setVisible(true);
	}

}
