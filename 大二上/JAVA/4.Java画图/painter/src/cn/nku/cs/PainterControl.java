package cn.nku.cs;

import java.awt.Color;
import java.awt.Point;

public class PainterControl {
	private int currentShapeType;
	private Color foreColor;
	private Point p1,p2;
	
	public void setPoint1(Point p){
		p1 = p;
		System.out.println(p1);
	}
	public void setPoint2(Point p){
		p2 = p;
		System.out.println(p2);
		if(currentShapeType == Shape.TEXT){
			Shape shape = new Shape(Shape.TEXT,p2,p2,Vars.toolPanel.getTextTF().getText(),null,20,this.foreColor,false);
			Vars.model.addShape(shape);
		}else{
			Shape shape = new Shape(currentShapeType, p1, p2, null, null, 20,this.foreColor, false);
			Vars.model.addShape(shape);
		}
		Vars.paintPanel.repaint();
	}
	
	
	public void reportCurrentShapeType(int shapeType){
		this.currentShapeType = shapeType;
	}

	public void reportForeColor(Color color) {
		this.foreColor = color;
	}
	public Color getForeColor() {
		return foreColor;
	}
	public int getCurrentShapeType() {
		return currentShapeType;
	}
	public Point getP1() {
		return p1;
	}
}
