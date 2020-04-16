package objects;
import java.awt.Color;

import graphics.ImageCache.Sprites;
import graphics.Sphere;
import graphics.Sprite;
import gravity.Singularity;

public class BlackHole extends Singularity {

	private static final long serialVersionUID = -1304840283372483227L;

	private Sphere body;
	private Sprite hole;
	private static final int holeRadius = 30;
	private static final float holeDensity = 100f;

	public BlackHole() {
		this(0, 0);
	}
	
	public BlackHole(int x, int y) {
		super(holeRadius*holeDensity, holeRadius);
		setLocation(x, y);
		addVisibleObject(body = new Sphere(holeRadius));
		body.setBorderColor(Color.RED);
		body.setFillColor(Color.BLACK);
		body.setVisible(true);
		hole = new Sprite(Sprites.SPRITE_BLACKHOLE);
		hole.setVisible(true);
		addVisibleObject(hole);
	}
}
