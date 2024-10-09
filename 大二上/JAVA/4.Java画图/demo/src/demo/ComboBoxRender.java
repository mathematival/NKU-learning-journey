package demo;

import java.awt.Component;
import java.util.LinkedHashMap;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ComboBoxRender extends JLabel implements ListCellRenderer<String>{
	ImageIcon img1 = new ImageIcon("tom.png");
	ImageIcon img2 = new ImageIcon("jerry.png");
	ImageIcon img3 = new ImageIcon("cuihua.png");
	LinkedHashMap<String, ImageIcon> map = new LinkedHashMap<>();
	public ComboBoxRender() {
		map.put("Tom", img1);
		map.put("Jerry", img2);
		map.put("Cuihua", img3);
		setOpaque(true); //设置透明
        setHorizontalAlignment(CENTER);//设置本标签水平居中对齐
        setVerticalAlignment(CENTER);//设置本标签垂直居中对齐

	}
	@Override
	public Component getListCellRendererComponent(JList list, String value,
			int index, boolean isSelected, boolean cellHasFocus) {
		
		System.out.println(value);
		if (isSelected) {//重点，这里是根据用户选中与否，给本标签设置背景色和前景色
            setBackground(list.getSelectionBackground());//想让自己的标签显示什么背景色，可以直接指定，不一定非要采集List
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
		ImageIcon icon = map.get(value);
		setIcon(icon);
		if (icon !=null){
			setText(value);
		}else{
			System.out.println(value + "null");
		}
		

		return this;
	}
	

}
