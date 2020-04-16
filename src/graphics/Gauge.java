package graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;

public class Gauge extends VisibleObject {

	private static final long serialVersionUID = 6613449132172825299L;

	private float maxValue;
	private float curValue;
	private String label;
	
	private static final int gaugeHeight = 100;
	private static final int gaugeWidth = 10;
	
	private Color color;

	public Gauge(Color color) {
		this(color, 1, "");
	}
		
	public Gauge(Color color, float maxValue, String label) {
		this.maxValue=maxValue;
		this.color=color;
		this.label=label;
		this.setVisible(true);
	}

	public boolean draw(Graphics g, int x, int y) {
		Graphics2D g2 = (Graphics2D) g;
		Stroke prevStroke = g2.getStroke();

		Stroke aspect = new BasicStroke(gaugeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0f);
		Shape background = new java.awt.geom.Line2D.Float(x, y, x, y - gaugeHeight);
		Shape front = new java.awt.geom.Line2D.Float(x,	y, x , y - ((curValue/maxValue)*gaugeHeight));

		g2.setStroke(aspect); 
		g2.setColor(Color.WHITE);
		g2.draw(background);
		g.setColor(color);
		g2.draw(front);
		g2.setStroke(prevStroke);
		g.drawString(label, x, y-10);
				
		return true;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public float getCurValue() {
		return curValue;
	}
	public float getMaxValue() {
		return maxValue;
	}
	
	public void setCurValue(float curValue) {
		this.curValue = curValue;
	}
	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
	}

}