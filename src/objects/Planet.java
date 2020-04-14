package objects;
import graphics.ImageCache.Sprites;
import graphics.Sprite;
import gravity.Body;

public class Planet extends Body {

	private static final long serialVersionUID = 8926479396144149553L;

	private static final int planetRadius = 620;
	private static final float planetAtmosphereRatio = .9090f;
	private static final float planetDensity = 40f;
	
	private Sprites[] spriteList = {
			Sprites.SPRITE_PLANET01,
			Sprites.SPRITE_PLANET02,
			Sprites.SPRITE_PLANET03,
			Sprites.SPRITE_PLANET04,
			Sprites.SPRITE_PLANET05,
			Sprites.SPRITE_PLANET06,
	};
	
	private Sprite sprite;
	private static int spriteIndex = 0;

	public Planet(int x, int y) {
		super(planetRadius*planetDensity, planetRadius);
		setLocation(x, y);
		
		sprite = new Sprite(spriteList[spriteIndex]);
		addVisibleObject(sprite).setVisible(true);

		setDiameter(planetRadius);
		spriteIndex = (spriteIndex + 1) % spriteList.length;
	}
	
	public boolean absorbEnergy(Body m) {
		incSpeed(m.getSpeedX()*m.getMass()/(getMass()*2), m.getSpeedY()*m.getMass()/(getMass()*2));
		return true;
	}
	
	public void setDiameter(float diameter) {
		this.diameter = diameter * planetAtmosphereRatio;
		setMass(diameter*planetDensity);
		sprite.setDiameter(diameter);
	}

	public void run() {
		
	}
	
	protected void updateLocation(float gameSpeed, float inertia) {
		
	}
}
