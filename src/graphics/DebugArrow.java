package graphics;

import java.awt.Color;
import java.awt.Graphics;
import tools.Pair;

public class DebugArrow extends VisibleObject {

	private static final long serialVersionUID = 7267044541328183542L;

	private Pair coordinates;
	private float scale;
	private Color color;

	/**
	 * Updating arrow direction and length, vector as an input
	 * @param coordinates
	 */
	public void setCoordinates(float fx, float fy) {
		this.coordinates.fx = fx;
		this.coordinates.fy = fy;
	}

	/**
	 * Updating arrow direction and length, angle as an input (degrees)
	 * @param coordinates
	 */
	public void setCoordinates(float angle) {
		setCoordinates((float) Math.cos(angle), (float) Math.sin(angle));
	}

	public DebugArrow(Color color) {
		this(color, 1);
	}
	
	public DebugArrow(Color color, float scale) {
		coordinates = new Pair();
		this.scale=scale;
		this.color=color;
		this.setVisible(true);
	}

	public boolean draw(Graphics g, int x, int y) {
		g.setColor(color);
		g.fillRect(x-1, y-1, 2, 2);
		g.drawLine(x, y, x + (int)(scale*coordinates.fx), y + (int)(scale*coordinates.fy));
		return true;
	}
}