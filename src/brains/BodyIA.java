package brains;

import java.io.Serializable;

import gravity.Body;

public abstract class BodyIA implements Serializable, Runnable {
	
	private static final long serialVersionUID = -1182571064352105032L;
	
	protected transient Thread iaThread = null;
	protected static final long computeStep = 32;
	
	public BodyIA(Body b) {
		this(b, null);
	}

	public BodyIA(Body v, Body t) {
	}
	
	public abstract void performActions();
	
	public void stop() {
		if (iaThread != null) {
			iaThread.interrupt();
			System.out.println("IA stopped");
		}
	}
	
	public void start() {
		if (iaThread == null) {
			iaThread = new Thread(this);
			iaThread.start();
			System.out.println("IA started");
		}
	}
}