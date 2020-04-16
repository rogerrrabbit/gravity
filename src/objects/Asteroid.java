package objects;

import tools.Pair;
import graphics.ImageCache.Sprites;
import graphics.Sprite;
import gravity.*;

public class Asteroid extends Body{

	private static final long serialVersionUID = -4427894404095486155L;

	private Sprite layer1;

	private static final float asteroidRadius = 5;
	private static final float asteroidDensity = 0.05f;

	public Asteroid(float x, float y) {
		this(x, y, asteroidRadius);
	}

	public Asteroid(float x, float y, float radius) {
		this(x, y, radius, 0, 0);
	}

	public Asteroid(Pair location, float radius, float initSpeedX, float initSpeedY) {
		this(location.fx, location.fy, radius, initSpeedX, initSpeedY);
	}
	
	public Asteroid(float x, float y, float radius, float initSpeedX, float initSpeedY) {
		super(asteroidDensity*radius*radius, radius, 0.1f, initSpeedX, initSpeedY);
		setLocation(x, y);
		
		switch ((int)(Math.random()*5.f)) {
			case 0: layer1 = new Sprite(Sprites.SPRITE_ASTEROID_1); break;
			case 1: layer1 = new Sprite(Sprites.SPRITE_ASTEROID_2); break;
			case 2: layer1 = new Sprite(Sprites.SPRITE_ASTEROID_3); break;
			case 3: layer1 = new Sprite(Sprites.SPRITE_ASTEROID_4); break;
			case 4: layer1 = new Sprite(Sprites.SPRITE_ASTEROID_5); break;
		}

		addVisibleObject(layer1);
		layer1.setVisible(true);
		updateAsteroidRadius();
	}

	private void updateAsteroidRadius() {
		setDiameter((float)Math.sqrt(getMass()/asteroidDensity));
		layer1.setDiameter(getDiameter());
	}

	/**
	 * When hit by a smaller object, the asteroid increases its own mass and speed
	 * @param m absorbed Mass
	 * @return true so the smaller object is destroyed
	 */
	public boolean absorbEnergy(Body m) {
		incSpeed(	(m.getSpeedX() - this.getSpeedX()) * m.getMass(),
					(m.getSpeedY() - this.getSpeedY()) * m.getMass());
		setMass(getMass() + m.getMass());
		updateAsteroidRadius();
		return true;
	}

	public void run() {
	}
}