package gravity;

import graphics.Animation;
//import graphics.VisibleObject;
import graphics.ImageCache.Sprites;
import graphics.SpecialEffect;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;

import tools.Pair;

public final class Space implements Serializable {

	private static final long serialVersionUID = -8220212906786980764L;
	private static volatile Space instance = null;
	public static boolean debug = false;
	
	private Dimension spaceDimensions;
	private boolean enableCollisions;
	private boolean enableGravity;
	
	private ArrayList<Mass> outMasses;
	private ArrayList<Mass> inMasses;
	
	private ArrayList<Mass> gGrid;
	private ArrayList<Body> cGrid;
	
	private long frameCounter = 0;
	
	public ArrayList<SpecialEffect> artifacts;
	public ArrayList<SpecialEffect> garbage;

	private static final float G=0.01f;
	private static final float GxOffset=0f;
	private static final float GyOffset=0f;
	private static final float minDistance=1.f;
	
	private static final int maxObjectCount=2000;
	private static final int computeStep=2;

	/*private transient Thread gravity;
	private transient Thread collisions;*/
	
	private float gameSpeed = 1f;
	private float inertia = 1f;
	
	public void setGameSpeed(float gameSpeed) {
		this.gameSpeed = gameSpeed;
	}

	public void setFriction(float friction) {
		this.inertia = friction;
	}

	private Space() {
		gGrid = new ArrayList<Mass>();
		cGrid = new ArrayList<Body>();
		
		outMasses = new ArrayList<Mass>();
		inMasses = new ArrayList<Mass>();
		
		spaceDimensions = new Dimension();
		
		artifacts = new ArrayList<SpecialEffect>();
		garbage = new ArrayList<SpecialEffect>();
		
		enableCollisions = true;
		enableGravity = true;
		
		/*gravity = new Thread(new GravityThread());
		collisions = new Thread(new CollisionsThread());
		gravity.start();
		collisions.start();*/
	}

	public void attachVisibleObject(SpecialEffect v) {
		artifacts.add(v);
	}
	
	public boolean toggleGravity() {
		return (enableGravity = !enableGravity);
	}
	
	public boolean toggleCollisions() {
		return (enableCollisions = !enableCollisions);
	}
	
	public final static Space getInstance() {
		if (Space.instance == null) {
			synchronized(Space.class) {
				if (Space.instance == null) {
					Space.instance = new Space();
				}
			}
		}
		return Space.instance;
	}

	public final static void swapSpace(Space s) {
		synchronized(Space.class) {
			Space.instance = s;
			s.initGraphics();
		}
	}
	
	public Dimension getDimensions() {
		return spaceDimensions;
	}
	
	public int getObjectCount() {
		return gGrid.size();
	}
	
	public int getSpecialEffectsCount() {
		return artifacts.size();
	}
	
	public void setDimensions(Dimension d) {
		spaceDimensions = d;
	}
	
	public void setDimensions(int i, int j) {
		spaceDimensions = new Dimension(i, j);
		
	}
	
	public Mass insertMass(Mass m) {
		synchronized(inMasses) {
			if (m != null && getObjectCount()<maxObjectCount) {
				return inMasses.add(m)?m:null;
			}
		}
		return null;
	}

	public Mass removeMass(Mass m) {
		synchronized(outMasses) {
			return outMasses.add(m)?m:null;
		}
	}

	public synchronized void removeAll() {
		for (Mass m : gGrid) removeMass(m);
	}
	
	public synchronized void removeAllNow() {
		for (Mass m : gGrid) removeMass(m);
		removeThings();
	}
	
	private synchronized void addThings() {
		synchronized(inMasses) {
			for (Mass m : inMasses) {
				if (gGrid.add(m) && (m instanceof Body)) {
					if (!cGrid.contains(m)) {
						cGrid.add((Body)m);
					}
				}
			}
			inMasses.clear();
		}
	}
	
	private synchronized void removeThings() {
		synchronized(outMasses) {
			for (Mass m : outMasses) {
				m.onRemove();
				if (!m.isDocked()) {
					gGrid.remove(m);
				}
				cGrid.remove(m);
			}
			outMasses.clear();
		}
	}

	/**
	 * Gives the Force to be applied to a Mass
	 * that results from the global gravity field generated by all the registered objects.
	 * @param m the mass we want to get the resulting gravity force from
	 * @return the force
	 */
	public Pair calculateGForce(Mass m) { 
		Pair f = new Pair(0,0);	
		for (Mass sister : gGrid) {
			if(m != sister) {
				float distance = Mass.calculateDistance(m, sister);
				if (distance >= minDistance) {
					float mass = sister.getMass();
					float normX = (sister.getLocationX() - m.getLocationX())/distance;
					float normY = (sister.getLocationY() - m.getLocationY())/distance;
					/* note: object inertia is compensated here */
					f.fx += (G*mass*normX)/(distance*distance);
					f.fy += (G*mass*normY)/(distance*distance);
				}
			}
		}
		f.add(GxOffset, GyOffset);
		return f;
	}
	
	public static Pair calculateGCenter(ArrayList<Mass> masses, Mass center) { 
		Pair f = new Pair(0,0);	
		for (Mass m : masses) {
			float distance = Mass.calculateDistance(m, center);
			float mass = m.getMass();
			float normX = (m.getLocationX() - center.getLocationX())/distance;
			float normY = (m.getLocationY() - center.getLocationY())/distance;
			f.fx += (G*mass*normX)/(distance*distance);
			f.fy += (G*mass*normY)/(distance*distance);
		}
		f.add(GxOffset, GyOffset);
		return f;
	}

	/**
	 * Apply gravity laws on registered moving objects and update their locations
	 */
	private synchronized void applyForces(float gameSpeed) {
		if (frameCounter%computeStep == 1) return;
		
		for (Mass object : gGrid) {
			object.updateForces(gameSpeed);
		}
	}
	
	private synchronized void updateLocations(float gameSpeed) {
		for (Body object : cGrid) {
			if (object.isDocked() == false) {
				object.updateLocation(gameSpeed, inertia);
			}
		}
	}
		
	/**
	 * Detect and solve collisions between bodies
	 */
	private synchronized void applyCollisions() {
		if (frameCounter%computeStep == 0) return;

		for (Body object : cGrid) {
			if (isOutOfBounds(object)) {
				removeMass(object);
			}

			else if (detectFatalCollisions(object)) {
				removeMass(object);
						
				Animation explosion = new Animation(Sprites.ANIMATION_EXPLOSION, 0);
				explosion.setAnimation(Animation.AnimationStatus.PLAY_ONCE);
				SpecialEffect se = new SpecialEffect(explosion, object.getLocation());
				this.attachVisibleObject(se);
			}
		}
	}

	/** 
	 * Checks whether a body is having collisions with other ones
	 * TODO implement shapes in Body objects and use advanced collision detection from JAVA
	 * @param b the Body to check for collisions
	 * @return the result of the impact, if any (returns false else)
	 */
	private boolean detectFatalCollisions(Body b) {
		for (Body m : cGrid) {	
			if (m != b && Mass.calculateDistance(m, b) <= b.getDiameter()/2 + m.getDiameter()/2) {
				return b.onImpact(m);
			}
		}
		return false;
	}

	public synchronized ArrayList<Mass> detectBodiesCircle(Body center, float radius) {
		ArrayList<Mass> bodies = new ArrayList<Mass>();
		for (Body m : cGrid) {
			if (m != center && Mass.calculateDistance(m, center) <= radius) {
				bodies.add(m);
			}
		}	
		
		return bodies;
	}
	
	public synchronized ArrayList<Mass> detectBodiesCircleExternal(Body center, float radius) {
		ArrayList<Mass> bodies = new ArrayList<Mass>();
		for (Body m : cGrid) {
			if (m != center &&
				m.getParent() != center &&
				(m.getParent() == null || m.getParent() != center.getParent()) &&
				Mass.calculateDistance(m, center) <= radius) {
				bodies.add(m);
			}
		}

		return bodies;
	}


	private boolean isOutOfBounds(Mass m) {
		int x = (int)m.getLocationX();
		int y = (int)m.getLocationY();
		
		if (x<0 || y<0) {
			return true;
		}
		if (x>getDimensions().width || y>getDimensions().height) {
			return true;
		}
		
		return false;
	}

	public synchronized void drawSpace(Graphics g) {
		for (Mass object : gGrid) {
			object.paintGraphics(g);
		}
			
		for (SpecialEffect object : artifacts) {
			if (!object.paint(g)) {
				garbage.add(object);
			}
		}
		for (SpecialEffect effect : garbage) {
			artifacts.remove(effect);
		}
		garbage.clear();
	}
	
	public synchronized void initGraphics() {
		for (Mass object : gGrid) {
			object.initGraphics();
		}
		for (SpecialEffect effect : artifacts) {
			effect.initGraphics();
		}
	}

	/*private class GravityThread implements Runnable {
		
		public void run() {
			for(;;) {
				try {
					if (enableGravity) {
						applyForces(gameSpeed, friction);
					}
					Thread.currentThread().wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
	
	private class CollisionsThread implements Runnable {
		
		public void run() {
			for(;;) {
				try {
					if (enableCollisions) {
						applyCollisions();	
					}
					Thread.currentThread().wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}*/
	
	public void run() {
		if (enableGravity) {
			applyForces(gameSpeed);
		}
		if (enableCollisions) {
			applyCollisions();	
		}
		
		/* waiting for threads to complete current frame calculations */
		/*try {
			gravity.join();
			collisions.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		removeThings();
		addThings();

		updateLocations(gameSpeed);

		/*gravity.notify();
		collisions.notify();*/
		
		frameCounter++;
	}
}
