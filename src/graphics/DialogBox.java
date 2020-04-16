package graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.image.ImageObserver;

public class DialogBox extends VisibleObject implements ImageObserver {

	private static final long serialVersionUID = 2508215373328405809L;

	private static final int selectorHeight = 50;
	private static final int selectorWidth = 100;
	private static final int selectorThickness = 2;
	
	private int timeOut = 60;

	public DialogBox setTimeOut(int timeOut) {
		this.timeOut = timeOut;
		return this;
	}

	DebugInfo text;
	
	private Color color;

	public DialogBox(Color color, String text) {
		this.color = color;
		this.text = new DebugInfo(text, color);
		setVisible(true);
	}

	public boolean draw(Graphics g, int x, int y) {
		Graphics2D g2 = (Graphics2D) g;
		Stroke prevStroke = g2.getStroke();

		Stroke aspect = new BasicStroke(selectorThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0f);
		Shape box = new java.awt.geom.Rectangle2D.Float(x - (selectorWidth/2), y - (selectorHeight/2), selectorWidth, selectorHeight);

		g2.setStroke(aspect); 
		g2.setColor(color);
		g2.draw(box);
		g2.setStroke(prevStroke);
		text.draw(g2, x - (selectorWidth/2) + 10, y);

		return (timeOut-- > 0);
	}

	public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5) {
		return false;
	}
}
