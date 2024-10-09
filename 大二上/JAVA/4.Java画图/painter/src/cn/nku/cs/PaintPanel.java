package cn.nku.cs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.JPanel;

public class PaintPanel extends JPanel{

	private int x1,y1;
	private int x2,y2;
	private Point oldDragPoint,newDragPoint;
	private int currentShapeType;
	private Point p1;

	public PaintPanel() {
		x1=100;y1=100;
		x2=200;y2=200;
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Vars.control.setPoint1(e.getPoint());
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				Vars.control.setPoint2(e.getPoint());
			}
		});
		
		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				currentShapeType = Vars.control.getCurrentShapeType();
				p1 = Vars.control.getP1();
//				oldDragPoint=newDragPoint;
				newDragPoint = e.getPoint();
				repaint();
			}
		});
	}
	
	
	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
		System.out.println("hehe");
		g.drawLine(x1, y1, x2, y2);
		
		drawTempDragShape(g);
		
		
		LinkedList<Shape> data = Vars.model.getData();
		for (Shape shape : data) {
			switch(shape.shapeType){
			case Shape.CIRCLE:
				drawCircle(shape,g);
				break;
			case Shape.RECT:
				drawRect(shape,g);
				break;
			case Shape.TEXT:
				drawText(shape,g);
				break;
			}
		}
	}


	private void drawTempDragShape(Graphics g) {
		if(p1==null||newDragPoint==null){
			return;
		}
		g.setColor(Vars.control.getForeColor());
		int minx = Math.min(p1.x, newDragPoint.x);
		int miny = Math.min(p1.y, newDragPoint.y);
		int width3 = Math.abs(p1.x-newDragPoint.x);
		int height3 = Math.abs(p1.y-newDragPoint.y);
		switch(currentShapeType){
		case Shape.CIRCLE:
			
//			g.drawOval(minx, miny, width2, height2);
			g.drawOval(minx, miny, width3, height3);
			break;
		case Shape.RECT:
//			g.drawRect(minx, miny, width2, height2);
			g.drawRect(minx, miny, width3, height3);
			break;
		case Shape.TEXT:
			break;
		}
	}


	private void drawText(Shape shape, Graphics g) {
		g.setColor(shape.color);
		g.setFont(new Font("ËÎÌו",Font.PLAIN,shape.size));
		g.drawString(shape.text, shape.p2.x, shape.p2.y);
	}


	private void drawRect(Shape shape, Graphics g) {
		g.setColor(shape.color);
		g.drawRect(Math.min(shape.p1.x, shape.p2.x), Math.min(shape.p1.y, shape.p2.y), Math.abs(shape.p1.x-shape.p2.x), Math.abs(shape.p1.y-shape.p2.y));
	}


	private void drawCircle(Shape shape, Graphics g) {
		g.setColor(shape.color);
		g.drawOval(Math.min(shape.p1.x, shape.p2.x), Math.min(shape.p1.y, shape.p2.y), Math.abs(shape.p1.x-shape.p2.x), Math.abs(shape.p1.y-shape.p2.y));
	}
}
