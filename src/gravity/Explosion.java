package gravity;

import graphics.Animation;
import graphics.Animation.AnimationEvents;
import graphics.AnimationObserver;
import graphics.ImageCache.Sprites;

public class Explosion extends Mass implements AnimationObserver {

	private static final long serialVersionUID = -2685225955698064807L;
	private Animation explosion;
	
	public Explosion() {
		this(0);
	}
	
	public Explosion(float force) {
		super(-force);
		explosion = new Animation(Sprites.ANIMATION_EXPLOSION, 0);
		explosion.setAnimation(Animation.AnimationStatus.PLAY_ONCE);
		explosion.addObserver(this);
		this.addVisibleObject(explosion);
	}

	public void onAnimationEvent(Animation a, AnimationEvents e) {
		if (e == Animation.AnimationEvents.ANIMATION_STOPS) {
			Space.getInstance().removeMass(this);
		}
	}
}
