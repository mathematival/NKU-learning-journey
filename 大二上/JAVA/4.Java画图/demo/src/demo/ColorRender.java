package demo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ColorRender extends JLabel implements ListCellRenderer<Color>{
	//准备 一个map，用于保存提前准备好的图片
	public HashMap<Color,Icon>  icons = new HashMap<>();

	
	public ColorRender() {
		setOpaque(true); //设置透明
		setPreferredSize(new Dimension(200, 37));
		icons.put(Color.green,new ImageIcon("green.png")   );//在map中放入提前准备好图片
        icons.put(Color.red,new ImageIcon("red.png"));     //用Color对象作为索引
        icons.put(Color.blue,new ImageIcon("blue.png"));     //用Color对象作为索引

	}
	@Override
	public Component getListCellRendererComponent(JList<? extends Color> list,
			Color value, int index, boolean isSelected, boolean cellHasFocus) {
		setIcon(icons.get(value));
		return this;
	}

}
