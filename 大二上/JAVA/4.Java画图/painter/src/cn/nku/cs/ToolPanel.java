package cn.nku.cs;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ToolPanel extends JPanel{
	
	private JTextField textTF;
	private JComboBox<Color> colorCB;
	
	public ToolPanel() {
		add(getTextTF());
		add(getColorCB());
	}
	
	public JComboBox<Color> getColorCB() {
		if (colorCB == null) {
			colorCB = new JComboBox<Color>();
			colorCB.addItem(Color.red);
			colorCB.addItem(Color.green);
			colorCB.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange() == ItemEvent.SELECTED){
						Vars.control.reportForeColor((Color)colorCB.getSelectedItem());
					}
				}
			});
		}
		return colorCB;
	}
	public JTextField getTextTF() {
		if (textTF == null) {
			textTF = new JTextField(20);
		}
		return textTF;
	}

}
