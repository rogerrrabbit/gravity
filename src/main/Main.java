package main;

import java.io.File;

import game.Input;
import graphics.GravityFrame;
import graphics.SimpleSplash;

public class Main {
	public static File quickSaveState;
	
	public static void main(String[] args) {
		quickSaveState = new File("save/quicksave.dat");
		
		new SimpleSplash();
		new GravityFrame();
		new Input();
	}
}