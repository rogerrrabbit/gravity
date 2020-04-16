package objects;

import tools.Pair;
import graphics.ImageCache.Sprites;
import graphics.Sprite;
import gravity.*;

public class LightBulb extends Body{

	private static final long serialVersionUID = -4427894404095486155L;

	private Sprite body;

	private static final float lightBulbRadius = 10;
	private static final float energyLevel = 120;

	public LightBulb(Pair location) {
		this(location, 0, 0);
	}
	
	public LightBulb(float x, float y) {
		this(x, y, 0, 0);
	}

	public LightBulb(Pair location, float initSpeedX, float initSpeedY) {
		this(location.fx, location.fy, initSpeedX, initSpeedY);
	}
	
	public LightBulb(float x, float y, float initSpeedX, float initSpeedY) {
		super(0, lightBulbRadius, 0, initSpeedX, initSpeedY);
		setLocation(x, y);

		addVisibleObject(body = new Sprite(Sprites.SPRITE_BULLET));
		body.setVisible(true);
	}

	public float getEnergy() {
		return energyLevel;
	}
	
	/**
	 * @param m absorbed Mass
	 * @return true so the smaller object is destroyed
	 */
	public boolean absorbEnergy(Body m) {
		if (m instanceof LightBulb) { return false; }
		return true;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}