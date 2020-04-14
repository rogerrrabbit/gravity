package graphics;

import java.awt.Color;
import java.awt.Graphics;
import tools.Pair;

public class Cross extends VisibleObject {

	private static final long serialVersionUID = 7267044541328183542L;

	private Pair coordinates;
	private float scale;
	private Color color;

	public void setCoordinates(Pair coordinates) {
		this.coordinates.fx = coordinates.fx;
		this.coordinates.fy = coordinates.fy;
	}

	public void setCoordinates(float angle) {
		this.coordinates.fx = (float) Math.cos(angle);
		this.coordinates.fy = (float) Math.sin(angle);
	}

	public Cross(Color color) {
		this(color, 100);
	}
	
	public Cross(Color color, float scale) {
		coordinates = new Pair();
		this.scale=scale;
		this.color=color;
		this.setVisible(true);
	}

	public boolean draw(Graphics g, int x, int y) {
		g.setColor(color);
		g.drawLine(x, (int)(y-(scale/2)), x, (int)(y +(scale/2)));
		g.drawLine((int)(x-(scale/2)), y, (int)(x +(scale/2)), y);
		return true;
	}
}