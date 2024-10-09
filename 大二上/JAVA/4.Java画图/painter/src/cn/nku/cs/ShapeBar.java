package cn.nku.cs;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

public class ShapeBar extends JToolBar{

	private JToggleButton circleBtn;
	private JToggleButton rectBtn;
	private JToggleButton textBtn;
	
	public ShapeBar() {
		setLayout(new GridLayout(5, 1));
		add(getCircleBtn());
		add(getRectBtn());
		add(getTextBtn());
		ButtonGroup group = new ButtonGroup();
		group.add(getCircleBtn());
		group.add(getRectBtn());
		group.add(getTextBtn());
	}
	
	public JToggleButton getRectBtn() {
		if (rectBtn==null) {
			rectBtn = new JToggleButton("Rect");
			rectBtn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					Vars.control.reportCurrentShapeType(Shape.RECT);
				}
			});
		}
		return rectBtn;
	}
	public JToggleButton getTextBtn() {
		if (textBtn==null) {
			textBtn = new JToggleButton("A");
			textBtn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					Vars.control.reportCurrentShapeType(Shape.TEXT);
				}
			});
		}
		return textBtn;
	}
	public JToggleButton getCircleBtn() {
		if(circleBtn==null){
			circleBtn  = new JToggleButton("Circle");
			circleBtn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					Vars.control.reportCurrentShapeType(Shape.CIRCLE);
				}
			});
		}
		return circleBtn;
	}
	
}
