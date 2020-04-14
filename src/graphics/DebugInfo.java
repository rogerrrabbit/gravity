package graphics;

import java.awt.Color;
import java.awt.Graphics;

public class DebugInfo extends VisibleObject {

	private static final long serialVersionUID = -1484237114198955253L;

	private String information;
	private Color textColor;
	private int offset;

	private static final int xOffset = 5;
	private static final int yOffset = 5;
	private static final int lineSpacing = 12;
	
	public DebugInfo(int offset, String information, Color textColor) {
		this.offset = offset;
		this.information = information;
		this.textColor = textColor;
		this.setVisible(true);
	}

	public DebugInfo(String information, Color textColor) {
		this(0, information, textColor);
	}

	public DebugInfo(int offset) {
		this(offset, "", Color.GRAY);
	}
	
	public boolean draw(Graphics g, int x, int y) {
		g.setColor(textColor);
		g.drawString(information, x + xOffset, y + yOffset + offset*lineSpacing);
		return true;
	}
	
	public String getInformation() {
		return information;
	}
	public void setInformation(String information) {
		this.information = information;
	}
	public Color getTextColor() {
		return textColor;
	}
	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}
}
