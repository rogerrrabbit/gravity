package tools;

import java.io.Serializable;

public class Pair implements Serializable {

	private static final long serialVersionUID = 6625973380875586272L;

	public float fx;
	public float fy;
	
	public Pair(float x, float y) {
		fx = x;
		fy = y;
	}
	
	public Pair() {
		zero();
	}
	
	public Pair(Pair speed) {
		fx = speed.fx;
		fy = speed.fy;
	}

	public void zero() {
		fx = 0;
		fy = 0;
	}
	
	public float getNorm() {
		return (float) Math.sqrt(fx*fx + fy*fy);
	}
	
	public float getAngle() {
		return (float) Math.atan2(fy, fx);
	}
	
	/**
	 * Transform a force on the current object into a speed modification,
	 * @param i the input force
	 */
	public Pair add(Pair i) {
		fx += i.fx;
		fy += i.fy;
		return this;
	}
	
	/**
	 * Transform a force on the current object into a speed modification,
	 * @param i the input force
	 */
	public Pair add(float x,  float y) {
		fx += x;
		fy += y;
		return this;
	}

	/**
	 * Apply a factor to the coordinates
	 * @param factor the factor
	 */
	public Pair factor(float factor) {
		fx *= factor;
		fy *= factor;
		return this;
	}
	
	/**
	 * Vector normalization
	 * @param factor the factor
	 */
	public Pair normalized() {
		return factor(1/getNorm());
	}
	
	/**
	 * Transform a force on the current object into a speed modification,
	 * taking the game speed into account.
	 * @param i the input force
	 */
	public Pair addFactor(Pair i, float factor) {
		fx += i.fx*factor;
		fy += i.fy*factor;
		return this;
	}
	
	/**
	 * Transform a force on the current object into a speed modification,
	 * taking its mass into account.
	 * @param i the input force
	 */
	public Pair rem(Pair i) {
		fx -= i.fx;
		fy -= i.fy;
		return this;
	}
	
	/**
	 * Transform a force on the current object into a speed modification,
	 * taking its mass into account.
	 * @param i the input force
	 */
	public Pair rem(float x,  float y) {
		fx -= x;
		fy -= y;
		return this;
	}
	
	/**
	 * Transform a force on the current object into a speed modification,
	 * taking its mass into account.
	 * @param i the input force
	 */
	public Pair set(float x, float y) {
		fx = x;
		fy = y;
		return this;
	}
	
	/**
	 * Transform a force on the current object into a speed modification,
	 * taking its mass into account.
	 * @param i the input force
	 */
	public Pair set(Pair i) {
		fx = i.fx;
		fy = i.fy;
		return this;
	}
	
	public Pair rotateCoordinates(float angle) {
		return new Pair((float)(fx*Math.cos(angle) - fy*Math.sin(angle)),
						(float)(fx*Math.sin(angle) + fy*Math.cos(angle)));
	}
}
