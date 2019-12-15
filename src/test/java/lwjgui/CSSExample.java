package lwjgui;

import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.layout.StackPane;

public class CSSExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(String[] args, Window window) {
		// Create a simple root pane
		StackPane pane = new StackPane();
		
		// Create pane to be styled! :)
		StackPane styledPane = new StackPane();
		styledPane.getClassList().add("TestStyle");
		pane.getChildren().add(styledPane);
		
		// Apply some style!
		pane.setStyle("stackpane { background-color:rgb(240,240,240) } .TestStyle { width:100px; height:100px; border-style:solid; border-radius:8px; }");
		styledPane.setStyle(".TestStyle { background-color:red; border-color:rgba(200,64,255,0.5); border-width:4px; }");
		
		// Create a new scene
		window.setScene(new Scene(pane, WIDTH, HEIGHT));
		
		// Make window visible
		window.show();
	}
}