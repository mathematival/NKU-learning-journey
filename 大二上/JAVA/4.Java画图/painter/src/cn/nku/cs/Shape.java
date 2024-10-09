package cn.nku.cs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;

public class Shape {
	public static final int CIRCLE = 1;
	public static final int RECT = 2;
	public static final int TEXT =3 ;
	public int shapeType;
	public Point p1;
	public Point p2;
	public String text;
	public Font font ;
	public int size;
	public Color color;
	public boolean isFill;
	public Shape(int shapeType, Point p1, Point p2, String text, Font font,
			int size, Color color, boolean isFill) {
		super();
		this.shapeType = shapeType;
		this.p1 = p1;
		this.p2 = p2;
		this.text = text;
		this.font = font;
		this.size = size;
		this.color = color;
		this.isFill = isFill;
	}
	
	
	
}
