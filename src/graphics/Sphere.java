package graphics;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;

public class Sphere extends VisibleObject {

	private static final long serialVersionUID = 7799557487499484845L;

	private Color borderColor;
	private Color fillColor;
	private float radius;
	private boolean fill;
	private boolean border;

	public Sphere setFill(boolean fill) {
		this.fill = fill;
		return this;
	}

	public Sphere setBorder(boolean border) {
		this.border = border;
		return this;
	}
	
	public Sphere(float r) {
		borderColor = Color.PINK;
		fillColor = Color.WHITE;
		radius = r;	
	}

	public boolean draw(Graphics g, int x, int y) {
		Graphics2D g2d = (Graphics2D)g;	
		Shape oval = new java.awt.geom.Ellipse2D.Float(x-(radius/2), y-(radius/2), radius, radius);
	
		if (fill) {
			g.setColor(fillColor);
			g.fillOval((int)(x-(radius/2)), (int)(y-(radius/2)), (int)radius, (int)radius);
		}
		
		if (border) {
			g.setColor(borderColor);
			g2d.draw(oval);
		}

		return true;
	}

	public Color getColor() {
		return borderColor;
	}
	public void setBorderColor(Color color) {
		this.borderColor = color;
	}
	public Color getFillColor() {
		return fillColor;
	}
	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}
	public float getRadius() {
		return radius;
	}
	public void setDiameter(float radius) {
		this.radius = radius;
	}
}
