package cn.nku.cs;

import java.util.LinkedList;

public class ShapeModel {
	private LinkedList<Shape> data = new LinkedList<>();
	
	public void addShape(Shape shape){
		data.addLast(shape);
	}
	public LinkedList<Shape> getData() {
		return data;
	}
}
