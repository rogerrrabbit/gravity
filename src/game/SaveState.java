package game;

import java.io.Serializable;
import gravity.Space;

public class SaveState implements Serializable {

	private static final long serialVersionUID = 3773011968166296862L;
	private Game gameState;
	private Space spaceState;

	public Game getGameState() {
		return gameState;
	}
	public Space getSpaceState() {
		return spaceState;
	}
	
	public SaveState(Game g, Space s) {
		gameState = g;
		spaceState = s;
	}
}