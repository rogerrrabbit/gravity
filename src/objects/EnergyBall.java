package objects;

import tools.Pair;
import graphics.ImageCache.Sprites;
import graphics.Sprite;
import gravity.*;

public class EnergyBall extends Body{

	private static final long serialVersionUID = -4427894404095486155L;

	private Sprite body;

	private static final float ballRadius = 3;
	private static final float ballDensity = .5f;

	public EnergyBall(Pair location) {
		this(location, ballRadius, 0, 0);
	}
	
	public EnergyBall(float x, float y) {
		this(x, y, ballRadius);
	}

	public EnergyBall(float x, float y, float radius) {
		this(x, y, radius, 0, 0);
	}

	public EnergyBall(Pair location, float radius, float initSpeedX, float initSpeedY) {
		this(location.fx, location.fy, radius, initSpeedX, initSpeedY);
	}
	
	public EnergyBall(float x, float y, float radius, float initSpeedX, float initSpeedY) {
		super(ballDensity*radius*radius, radius, 0, initSpeedX, initSpeedY);
		setLocation(x, y);

		addVisibleObject(body = new Sprite(Sprites.SPRITE_BLOB));
		body.setVisible(true);

		updateRadius();
	}

	private void updateRadius() {
		setDiameter((int)Math.sqrt(getMass()/ballDensity));
		body.setDiameter(getDiameter());
	}

	/**
	 * When hit by a smaller object, the object increases its own mass and speed
	 * @param m absorbed Mass
	 * @return true so the smaller object is destroyed
	 */
	public boolean absorbEnergy(Body m) {
		incSpeed(	(m.getSpeedX() - this.getSpeedX()) * m.getMass(),
					(m.getSpeedY() - this.getSpeedY()) * m.getMass());
		setMass(getMass() + m.getMass());
		updateRadius();
		return true;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}