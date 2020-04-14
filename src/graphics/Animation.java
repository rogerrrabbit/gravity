package graphics;

import graphics.ImageCache.Sprites;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

public class Animation extends VisibleObject implements ImageObserver {

	private static final long serialVersionUID = 1624626621580505488L;

	/* Visitor list which are notified when the animation
	 * reaches certain states (loop, stop, etc.).
	 */
	private ArrayList<AnimationObserver> observers;

	public enum AnimationEvents {
		ANIMATION_STOPS
	};
	
	/* Current animation status, which can be:
	 * IDLE: displaying a given still image (default: nothing),
	 * playing ONCE: will stop after the last image when reaching the last sprite,
	 * playing LOOP: play the full set of sprites 'loopStep' times
	 */
	private AnimationStatus status;

	public enum AnimationStatus {
		IDLE,
		PLAY_ONCE,
		PLAY_LOOP
	};

	private float orientation = 0;	/* picture heading */
	private int animationLoops = 0;	/* 0 means infinite loop */
	private int animationStep;	/* how many frames to draw the same image before stepping forward animation */

	/* Sprite list identifier */
	private Sprites animationSprites;

	/* Local references to buffered images */
	private transient BufferedImage[] pictures;
	private int pictureIterator;

	/* Sprites dimensions */
	private float width;
	private float height;
	
	private int drawCount = 0;
	private int loopCount = 0;
	private float orientation_offset;

	private void notifyObservers(AnimationEvents e) {
		if (observers == null) return;
		
		for (AnimationObserver o : observers) {
			o.onAnimationEvent(this, e);
		}
	}

	public boolean addObserver(AnimationObserver e) {
		return observers.add(e);
	}
	
	/**
	 *  Set the image to a specific index in the sprite list
	 *  in IDLE mode
	 *  @param stillImageIndex sprite index
	 */
	public void setImageIndex(int stillImageIndex) {
		if (stillImageIndex < pictures.length) {
			pictureIterator = stillImageIndex;
		}
	}
	
	/** Go to the next image in the sprite list
	 *  in IDLE mode
	 */
	public void next() {
		pictureIterator++;
	}
	
	/**
	 * Set how many draw calls (frames) are spent before displaying the
	 * next image in the sprite list
	 * in LOOP or ONCE modes
	 * @param animationStep frame count
	 */
	public void setAnimationStep(int animationStep) {
		this.animationStep = animationStep;
	}
	
	/**
	 * Set how many complete animation loops are done
	 * in LOOP mode before stopping in IDLE mode
	 * @param loopStep
	 */
	public void setLoopStep(int loopStep) {
		this.animationLoops = loopStep;
	}

	public Animation(Sprites id) {
		this(id, 0);
	}

	/**
	 * Loading sprites.
	 */
	public boolean init() {
		pictures = animationSprites.images();
		if (pictures == null) {
			System.err.println("Oops, sprite loading failed!!");
			return false;
		}
		
		pictureIterator = 0;
	    return true;
	}

	/**
	 * Resetting image iterator to the first sprite.
	 */
	private void rewind() {
		pictureIterator = 0;
	}

	public Animation(Sprites sp, float theta) {
		this(sp, theta, 1);
	}

	public Animation(Sprites sp, float theta, int step) {
	    animationSprites = sp;
		orientation_offset = theta;
		animationStep = step;
		
		/* default status is displaying first sprite */
		status = AnimationStatus.IDLE;

		observers = new ArrayList<AnimationObserver>();
		
		if (init()) {
			width = pictures[0].getWidth();
			height = pictures[0].getHeight();	
		} else {
			System.err.println(String.format("Animation loading failed:%s", animationSprites.name()));
		}
		init();
	}

	private void stop() {
		status = AnimationStatus.IDLE;
		notifyObservers(AnimationEvents.ANIMATION_STOPS);
		setVisible(false);
	}

	public boolean draw(Graphics g, int x, int y) {
		/* if status is not idle then move the iterator to the right sprite */
		if (status != AnimationStatus.IDLE) {
			if (pictureIterator == pictures.length - 1) {
				rewind();
				if (status != AnimationStatus.PLAY_LOOP) {
					stop();
					return false;
				} else if (animationLoops != 0) {
					if (++loopCount>animationLoops) {
						loopCount = 0;
						stop();
						return false;
					}
				}
			} else if (drawCount%animationStep == 0) {
				next();
			}
		}

		if (pictures[pictureIterator] == null) {
			return false;
		}
		
		Graphics2D g2d = (Graphics2D)g;
		AffineTransform old = g2d.getTransform();

		g2d.rotate(orientation + orientation_offset, x, y);
		g2d.drawImage(pictures[pictureIterator], x - (int)(width/2), y - (int)(height/2), (int)width, (int)height, this);
		g2d.setTransform(old);
		
		drawCount++;
		return true;
	}
	
	public float getOrientation() {
		return orientation;
	}

	public void setOrientation(float orientation) {
		this.orientation = orientation;
	}
	
	public void setAnimation(AnimationStatus s) {
		setVisible(true);
		this.status = s;
	}

	public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void setDiameter(float radius) {
		float aspectRatio = width/height;
		if (width>height) {
			width = radius;
			height = radius/aspectRatio;
		} else {
			height = radius;
			width = radius/aspectRatio;
		}
	}
}
