package objects;
import tools.Pair;
import graphics.ImageCache.Sprites;
import graphics.Sprite;
import gravity.Body;
import gravity.Space;

public class Satellite extends Body {

	private static final long serialVersionUID = -7537204760781615313L;

	private static final int planetRadius = 30;
	private static final float planetDensity = 0.1f;
	private Sprite satelliteAnimation;

	public Satellite(Pair p) {
		this((int)p.fx, (int)p.fy);
	}
	
	public Satellite(int x, int y) {
		super(planetRadius*planetDensity, planetRadius);
		setLocation(x, y);
		satelliteAnimation = new Sprite(Sprites.SPRITE_SATELLITE, planetRadius);
		satelliteAnimation.setOrientation((float)(Math.random()*2*Math.PI));
		addVisibleObject(satelliteAnimation).setVisible(true);
	}
	
	protected boolean absorbEnergy(Body m) {
		Space.getInstance().removeMass(this);
		return true;
	}
	
	public void run() {

	}
}