package game;

import game.Game.Controls;
import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Rumbler;
import net.java.games.input.Version;

public class Input implements Runnable {

	private static final long  inputStep = 32;
	private static final float simulationSpeedStep = .1f;
	private static final float axisDeadZone = .5f;
	private static final int   keyboardCount = 1;
	private static final int   gamepadCount = 2;
	
	public static enum Buttons {
		/* iBUFFALO CLASSIC USB GAMEPAD (SNES) */
		BUFFALO_BUTTON_A(Identifier.Button._0),
		BUFFALO_BUTTON_B(Identifier.Button._1),
		BUFFALO_BUTTON_X(Identifier.Button._2),
		BUFFALO_BUTTON_Y(Identifier.Button._3),
		BUFFALO_BUTTON_LT(Identifier.Button._4),
		BUFFALO_BUTTON_RT(Identifier.Button._5),
		BUFFALO_BUTTON_ALT_SELECT(Identifier.Button._6),
		BUFFALO_BUTTON_ALT_START(Identifier.Button._7),
		BUFFALO_BUTTON_START(Identifier.Button.START),
		BUFFALO_BUTTON_SELECT(Identifier.Button.SELECT);

		public Identifier id;
		Buttons (Identifier id) {
			this.id = id;
		}
	};

	private Thread input;
	
	public Input() {
		input = new Thread(this);
		//getAllControllersInfo();
		input.run();
	}
	
	/**
     * Prints all the controllers and its components.
     */
    public void getAllControllersInfo()
    {
        System.out.println("JInput version: " + Version.getVersion());
        System.out.println("");
        
        // Get a list of the controllers JInput knows about and can interact with.
        Controller[] controllersList = ControllerEnvironment.getDefaultEnvironment().getControllers();
        
        // First print all controllers names.
        for(int i =0;i<controllersList.length;i++){
            System.out.println(controllersList[i].getName());
        }

        // Print all components of controllers.
        for(int i = 0; i < controllersList.length; i++){            
            System.out.println("\n");
            System.out.println("-----------------------------------------------------------------");
            
            // Get the name of the controller
            System.out.println(controllersList[i].getName());
            // Get the type of the controller, e.g. GAMEPAD, MOUSE, KEYBOARD, 
            // see http://www.newdawnsoftware.com/resources/jinput/apidocs/net/java/games/input/Controller.Type.html
            System.out.println("Type: "+controllersList[i].getType().toString());

            // Get this controllers components (buttons and axis)
            Component[] components = controllersList[i].getComponents();
            System.out.print("Component count: "+components.length);
            
            Rumbler[] rumblers = controllersList[i].getRumblers();
            System.out.print("Controler rumbler count: "+rumblers.length);
            for (Rumbler r : rumblers) {
            	r.rumble(1.f);
            	r.rumble(2.f);
            	r.rumble(1.f);
            	r.rumble(0.f);
            }
            
            for(int j=0; j<components.length; j++){
                System.out.println("");
                
                // Get the components name
                System.out.println("Component "+j+": "+components[j].getName());
                // Get it's identifier, E.g. BUTTON.PINKIE, AXIS.POV and KEY.Z, 
                // see http://www.newdawnsoftware.com/resources/jinput/apidocs/net/java/games/input/Component.Identifier.html
                System.out.println("    Identifier: "+ components[j].getIdentifier().getName());
                System.out.print("    ComponentType: ");
                if (components[j].isRelative())
                    System.out.print("Relative");
                else
                    System.out.print("Absolute");
                
                if (components[j].isAnalog()) 
                    System.out.print(" Analog");
                else
                    System.out.print(" Digital");
            }
            
            System.out.println("\n");
            System.out.println("-----------------------------------------------------------------");
        }
    }
    
    /**
     * Prints controllers components and its values.
     * 
     * @param controllerType Desired type of the controller.
     */
    public void pollControllerAndItsComponents()
    {
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
        
        Controller[] listOfGamepads = new Controller[gamepadCount];
        Controller[] listOfKeyboards = new Controller[keyboardCount];
        int gamepadFound = 0;
        int keyboardFound = 0;
        
        for(int i=0; i < controllers.length; i++) {
            if(controllers[i].getType() == Controller.Type.STICK ||
               controllers[i].getType() == Controller.Type.GAMEPAD) {
            	if (gamepadFound < gamepadCount) {
            		listOfGamepads[gamepadFound++] = controllers[i];
            	}
            } else if(controllers[i].getType() == Controller.Type.KEYBOARD) {
            	if (keyboardFound < keyboardCount) {
            		listOfKeyboards[keyboardFound++] = controllers[i];
            	}
            }
        }
        
        if(listOfGamepads[0] == null) {
            System.out.println("Found no gamepad.");
        }

        for (int i=0; i < gamepadFound; i++) {
        	System.out.println("Player " + (i+1) +" controller: " + listOfGamepads[i].getName());
        }

        while(true) {
        	/* gamepads */
        	for (int gamepadIndex=0; gamepadIndex < gamepadFound; gamepadIndex++) {
	            listOfGamepads[gamepadIndex].poll();
	            Component[] components = listOfGamepads[gamepadIndex].getComponents();
	            for(int componentsIndex=0; componentsIndex<components.length; componentsIndex++) {
	            	Identifier componentId = components[componentsIndex].getIdentifier();
	                float componentData = components[componentsIndex].getPollData();
	                
	                /* axes */
	                if(components[componentsIndex].isAnalog()) {
	                    if (componentId == Identifier.Axis.Y) {
                    		if(componentData <= -axisDeadZone) {
                    			Game.getInstance().handleButton(gamepadIndex, Controls.CONTROL_FORWARD);
                    		} else if(componentData >= axisDeadZone) {
                    			Game.getInstance().handleButton(gamepadIndex, Controls.CONTROL_BACKWARD);
                    		}
	                    } else if (componentId == Identifier.Axis.X) {
                    		if (componentData >= axisDeadZone) {
                    			Game.getInstance().handleButton(gamepadIndex, Controls.CONTROL_RIGHT);
                    		} else if (componentData <= -axisDeadZone) {
                    			Game.getInstance().handleButton(gamepadIndex, Controls.CONTROL_LEFT);
                    		}
	                    }
	                    
	                /* buttons */
	                } else if (componentData == 1.0f) {
	                	/*if (componentData == 1.0f) {
	                		Game.getInstance().setPauseState(true);
	                	}*/
	                	
	                	if (componentId == Buttons.BUFFALO_BUTTON_A.id) {
							Game.getInstance().handleButton(gamepadIndex, Controls.CONTROL_FIRE0);
	                	} else if (componentId == Buttons.BUFFALO_BUTTON_B.id) {
							Game.getInstance().handleButton(gamepadIndex, Controls.CONTROL_FIRE1);
	                	} else if (componentId == Buttons.BUFFALO_BUTTON_X.id) {
							Game.getInstance().handleButton(gamepadIndex, Controls.CONTROL_SWITCH);
	                	} else if (componentId == Buttons.BUFFALO_BUTTON_ALT_START.id) {
	                		Game.getInstance().handleButton(gamepadIndex, Controls.CONTROL_START);
	                	} else if (componentId == Buttons.BUFFALO_BUTTON_ALT_SELECT.id) {
	                		Game.getInstance().handleButton(gamepadIndex, Controls.CONTROL_SELECT);
	                	} else if (componentId == Buttons.BUFFALO_BUTTON_START.id) {
	                		Game.getInstance().handleButton(gamepadIndex, Controls.CONTROL_START);
	                	} else if (componentId == Buttons.BUFFALO_BUTTON_SELECT.id) {
	                		Game.getInstance().handleButton(gamepadIndex, Controls.CONTROL_SELECT);
	                	} else if (componentId == Buttons.BUFFALO_BUTTON_LT.id) {
	                		Game.getInstance().setScale(Game.getInstance().getScale()-0.1f);
	                	} else if (componentId == Buttons.BUFFALO_BUTTON_RT.id) {
	                		Game.getInstance().setScale(Game.getInstance().getScale()+0.1f);
	                	}
	                }
	            }
        	}
        	
        	/* keyboards */
        	for (int keyboardIndex=0; keyboardIndex < keyboardFound; keyboardIndex++) {
	            listOfKeyboards[keyboardIndex].poll();
	            Component[] components = listOfKeyboards[keyboardIndex].getComponents();
	            for(int componentsIndex=0; componentsIndex<components.length; componentsIndex++) {
	            	Identifier componentId = components[componentsIndex].getIdentifier();
	                float componentData = components[componentsIndex].getPollData();
	                if (componentData != 1.0f) {
	                	continue;
	                }
	                
	                /* player 1 */
	                if(componentId == Identifier.Key.LEFT) {
            			Game.getInstance().handleButton(0, Controls.CONTROL_LEFT);
	                } else if(componentId == Identifier.Key.RIGHT) {
            			Game.getInstance().handleButton(0, Controls.CONTROL_RIGHT);
	                } else if(componentId == Identifier.Key.UP) {
            			Game.getInstance().handleButton(0, Controls.CONTROL_FORWARD);
	                } else if(componentId == Identifier.Key.DOWN) {
            			Game.getInstance().handleButton(0, Controls.CONTROL_BACKWARD);
	                } else if(componentId == Identifier.Key.MULTIPLY) {
            			Game.getInstance().handleButton(0, Controls.CONTROL_FIRE0);
	                } else if(componentId == Identifier.Key.DIVIDE) {
            			Game.getInstance().handleButton(0, Controls.CONTROL_FIRE1);
            			
    	            /* player 2 */
	                } else if(componentId == Identifier.Key.Q) {
             			Game.getInstance().handleButton(1, Controls.CONTROL_LEFT);
 	                } else if(componentId == Identifier.Key.D) {
             			Game.getInstance().handleButton(1, Controls.CONTROL_RIGHT);
 	                } else if(componentId == Identifier.Key.Z) {
             			Game.getInstance().handleButton(1, Controls.CONTROL_FORWARD);
 	                } else if(componentId == Identifier.Key.S) {
             			Game.getInstance().handleButton(1, Controls.CONTROL_BACKWARD);
 	                } else if(componentId == Identifier.Key.E) {
             			Game.getInstance().handleButton(1, Controls.CONTROL_FIRE0);
 	                } else if(componentId == Identifier.Key.R) {
             			Game.getInstance().handleButton(1, Controls.CONTROL_FIRE1);
 	                } else if(componentId == Identifier.Key.A) {
             			Game.getInstance().handleButton(1, Controls.CONTROL_SWITCH);
             			
             		/* other */
	                } else if(componentId == Identifier.Key.PAGEDOWN) {
            			Game.getInstance().incSimulationSpeed(-simulationSpeedStep);
	                } else if(componentId == Identifier.Key.PAGEUP) {
            			Game.getInstance().incSimulationSpeed(simulationSpeedStep);
	                } else if(componentId == Identifier.Key.ADD) {
            			Game.getInstance().setScale(Game.getInstance().getScale()+0.01f);
	                } else if(componentId == Identifier.Key.SUBTRACT) {
            			Game.getInstance().setScale(Game.getInstance().getScale()-0.01f);
		            } else if(componentId == Identifier.Key.NUMPAD4) {
	        			Game.getInstance().incCameraOffsetX(-1);
	                } else if(componentId == Identifier.Key.NUMPAD6) {
	        			Game.getInstance().incCameraOffsetX(1);
	                } else if(componentId == Identifier.Key.NUMPAD2) {
	        			Game.getInstance().incCameraOffsetY(1);
	                } else if(componentId == Identifier.Key.NUMPAD8) {
	        			Game.getInstance().incCameraOffsetY(-1);
	                }
	                
	            }
        	}
            try {
                Thread.sleep(inputStep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
	
	public void run() {
		getAllControllersInfo();
		pollControllerAndItsComponents();
	}
}
