package objects;

import tools.Pair;
import graphics.ImageCache.Sprites;
import graphics.Sprite;
import gravity.*;

public class Heart extends Body{

	private static final long serialVersionUID = -4427894404095486155L;

	private Sprite body;

	private static final float heartDiameter = 20;
	private static final float heartDensity = .00001f;

	public Heart(Pair location) {
		this(location, heartDiameter, 0, 0);
	}
	
	public Heart(Pair location, float radius) {
		this(location, radius, 0, 0);
	}

	public Heart(float x, float y) {
		this(x, y, heartDiameter);
	}

	public Heart(float x, float y, float diameter) {
		this(x, y, diameter, 0, 0);
	}

	public Heart(Pair location, float radius, float initSpeedX, float initSpeedY) {
		this(location.fx, location.fy, radius, initSpeedX, initSpeedY);
	}
	
	public Heart(float x, float y, float diameter, float initSpeedX, float initSpeedY) {
		super(heartDensity*diameter*diameter, diameter, 0, initSpeedX, initSpeedY);
		setLocation(x, y);

		addVisibleObject(body = new Sprite(Sprites.SPRITE_HEART));
		body.setDiameter(heartDiameter);
		body.setVisible(true);
	}

	public boolean absorbEnergy(Body m) {
		return false;
	}

	public void run() {
	}
}