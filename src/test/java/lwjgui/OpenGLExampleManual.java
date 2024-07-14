package lwjgui;

import static org.lwjgl.system.MemoryStack.stackMallocFloat;
import static org.lwjgl.system.MemoryStack.stackPop;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.Buffer;
import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.gl.GenericShader;
import lwjgui.paint.Color;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.WindowManager;
import lwjgui.scene.control.CheckBox;
import lwjgui.scene.control.Label;
import lwjgui.scene.control.Slider;
import lwjgui.scene.layout.BorderPane;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.VBox;

public class OpenGLExampleManual {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;
	
	private static CheckBox spinBox;
	private static Slider slider;
	private static double rotation;
	
	private static GenericShader shader;
	private static int vao;
	private static int vbo;

	public static void main(String[] args) {
		// Restarts the JVM if necessary on the first thread to ensure Mac compatibility 
		if (LWJGUIUtil.restartJVMOnFirstThread(true, args))
			return;
		
		// Initialize GLFW
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Create a standard opengl 3.2 window. You can do this yourself.
		long handle = LWJGUIUtil.createOpenGLCoreWindow("Hello World", WIDTH, HEIGHT, true, false);
		
		// Initialize lwjgui for this window
		Window window = WindowManager.generateWindow(handle);
		window.setWindowAutoClear(false); // We must call glClear ourselves.
		window.show(); // Display window if it's invisible.
		
		// Add some components
		addComponents(window.getScene());
		
		// Initialize OpenGL information
		initializeOpenGL();
		
		// Game Loop
		while (!GLFW.glfwWindowShouldClose(handle)) {
			// Tick window manager for any input or windowing commands
			WindowManager.update();

			// Clear back buffer
			glClearColor(0,0,0,1);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
			// Render OpenGL
			renderOpenGL();
			
			// Render GUI
			window.render();
			
			// Swap buffers
			GLFW.glfwSwapBuffers(handle);
		}

		// Clear global window resources
		WindowManager.dispose();
		// Stop GLFW
		glfwTerminate();
	}

	private static void addComponents(Scene scene) {
		// Create a simple pane
		BorderPane root = new BorderPane();
		root.setPadding(new Insets(24));
		root.setBackgroundLegacy(null); // See through, so we don't block the opengl drawn underneath
		scene.setRoot(root);

		// Put labels in pane
		{
			VBox vbox = new VBox();
			vbox.setAlignment(Pos.CENTER);
			vbox.setBackgroundLegacy(Color.BLUE.alpha(0.4f));
			root.setCenter(vbox);

			Label label1 = new Label("Hello World!");
			label1.setTextFill(Color.AQUA);
			vbox.getChildren().add(label1);
			
			Label label2 = new Label("OpenGL drawn straight to window.");
			label2.setTextFill(Color.WHITE);
			vbox.getChildren().add(label2);
			
			Label label3 = new Label("LWJGUI ontop!");
			label3.setTextFill(Color.CORAL);
			vbox.getChildren().add(label3);
		}
		
		// Bottom hbox
		{
			HBox hbox = new HBox();
			hbox.setSpacing(8);
			hbox.setBackgroundLegacy(null);
			root.setBottom(hbox);

			// Add a checkbox
			spinBox = new CheckBox("Spin");
			hbox.getChildren().add(spinBox);
			
			slider = new Slider(-180, 180, 0);
			slider.setPrefWidth(200);
			hbox.getChildren().add(slider);
			
			// slider can change rotation
			slider.setOnValueChangedEvent((event)->{
				rotation = Math.toRadians(slider.getValue());
			});
		}
	}
	
	private static void initializeOpenGL() {
		// Test shader
		shader = new GenericShader(); // Will load a testing vert/frag quad shader
		
		// Setup geometry
		int vertSize = 3; // vec3 in shader
		int texSize = 2; // vec2 in shader
		int colorSize = 4; // vec4 in shader
		int size = vertSize + texSize + colorSize; // Stride length
		int verts = 3; // Number of vertices
		int bytes = Float.BYTES; // Bytes per element (float)
		
		stackPush();
		{
			// Initial vertex data
			FloatBuffer buffer = stackMallocFloat(verts * size);
			buffer.put(-0.5f).put(+0.5f).put(0.0f);		// Vert 1 position
			buffer.put(new float[] {0.0f, 0.0f});		// Vert 1 texture
			buffer.put(new float[] {1.0f,0.0f,0.0f,1.0f}); // Vert 1 color
			
			buffer.put(+0.5f).put(+0.5f).put(0.0f);		// Vert 2 position
			buffer.put(new float[] {0.0f, 0.0f});		// Vert 2 texture
			buffer.put(new float[] {0.0f,1.0f,0.0f,1.0f}); // Vert 2 color
			
			buffer.put(+0.0f).put(-0.5f).put(0.0f);		// Vert 3 position
			buffer.put(new float[] {0.0f, 0.0f});		// Vert 3 texture
			buffer.put(new float[] {0.0f,0.0f,1.0f,1.0f}); // Vert 3 color
			((Buffer) buffer).flip();

			// Generate buffers
			vbo = glGenBuffers();
			vao = glGenVertexArrays();

			// Upload Vertex Buffer
			glBindBuffer(GL_ARRAY_BUFFER, vbo);
			glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

			// Set attributes (automatically stored to currently bound VAO)
			glBindVertexArray(vao);
			glEnableVertexAttribArray(0); // layout 0 shader
			glEnableVertexAttribArray(1); // layout 1 shader
			glEnableVertexAttribArray(2); // layout 2 shader
			int vertOffset = 0;
			glVertexAttribPointer( 0, vertSize,  GL_FLOAT, false, size*bytes, vertOffset );
			int texOffset = vertSize*bytes;
			glVertexAttribPointer( 1, texSize,   GL_FLOAT, false, size*bytes, texOffset );
			int colorOffset = texOffset + texSize*bytes;
			glVertexAttribPointer( 2, colorSize, GL_FLOAT, false, size*bytes, colorOffset );

			// Unbind
			glBindBuffer(GL_ARRAY_BUFFER, 0);
			glBindVertexArray(0);
		}
		stackPop();
	}
	
	private static void renderOpenGL() {
		if ( spinBox != null && spinBox.isChecked() ) {
			rotation += 1.0e-3d;
			if ( rotation > Math.PI )
				rotation = -Math.PI;
			
			slider.setValue(Math.toDegrees(rotation));
		}
		
		// Bind shader for drawing
		shader.bind();
		shader.projectOrtho( -0.6f, -0.6f, 1.2f, 1.2f );
		shader.setWorldMatrix(new Matrix4f().rotateY((float) rotation));

		// Disable culling (just in case)
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		// Render geometry
		glBindVertexArray(vao);
		glDrawArrays(GL_TRIANGLES, 0, 3);
	}
}