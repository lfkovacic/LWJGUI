package lwjgui.scene;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import lwjgui.glfw.Cursor;
import lwjgui.glfw.PixelBufferHandle;

public final class WindowHandle {

	protected int width, height;
	protected String title;
	protected List<Icon> icons = new ArrayList<>();
	protected Cursor cursor;
	protected boolean legacyGL;

	protected WindowHandle(int width, int height, String title, boolean legacyGL) {
		System.out.println("Creating WindowHandle for '" + title + "'");
		this.width = width;
		this.height = height;
		this.title = title;
		this.legacyGL = legacyGL;

		// Reset the window hints
		GLFW.glfwDefaultWindowHints();

		if (legacyGL) {
			glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_ANY_PROFILE);
			glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
			glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
		} else {
			// Set the window to use OpenGL 3.3 Core with forward compatibility
			this.setWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
			this.setWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
			this.setWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_OPENGL_API);
			this.setWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
			this.setWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, true);
		}
	}

	public WindowHandle canResize(boolean flag) {
		this.setWindowHint(GLFW.GLFW_RESIZABLE, flag);
		return this;
	}

	public WindowHandle isVisible(boolean flag) {
		this.setWindowHint(GLFW.GLFW_VISIBLE, flag);
		return this;
	}

	public WindowHandle isDecorated(boolean flag) {
		this.setWindowHint(GLFW.GLFW_DECORATED, flag);
		return this;
	}

	public WindowHandle setFocusOnCreation(boolean flag) {
		this.setWindowHint(GLFW.GLFW_FOCUSED, flag);
		return this;
	}

	public WindowHandle alwaysOnTop(boolean flag) {
		this.setWindowHint(GLFW.GLFW_FLOATING, flag);
		return this;
	}

	public WindowHandle isMaximizedOnCreation(boolean flag) {
		this.setWindowHint(GLFW.GLFW_MAXIMIZED, flag);
		return this;
	}

	public WindowHandle useDebugContext(boolean flag) {
		this.setWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, flag);

		return this;
	}

	public WindowHandle setPixelBuffer(PixelBufferHandle pbh) {
		System.out.println(pbh.toString());
		this.setWindowHint(GLFW.GLFW_RED_BITS, pbh.getRedBits());
		this.setWindowHint(GLFW.GLFW_ACCUM_RED_BITS, pbh.getRedBitsAccum());
		this.setWindowHint(GLFW.GLFW_GREEN_BITS, pbh.getGreenBits());
		this.setWindowHint(GLFW.GLFW_ACCUM_GREEN_BITS, pbh.getGreenBitsAccum());
		this.setWindowHint(GLFW.GLFW_BLUE_BITS, pbh.getBlueBits());
		this.setWindowHint(GLFW.GLFW_ACCUM_BLUE_BITS, pbh.getBlueBitsAccum());
		this.setWindowHint(GLFW.GLFW_ALPHA_BITS, pbh.getAlphaBits());
		this.setWindowHint(GLFW.GLFW_ACCUM_ALPHA_BITS, pbh.getAlphaBitsAccum());
		this.setWindowHint(GLFW.GLFW_DEPTH_BITS, pbh.getDepthBits());
		this.setWindowHint(GLFW.GLFW_STENCIL_BITS, pbh.getStencilBits());
		this.setWindowHint(GLFW.GLFW_AUX_BUFFERS, pbh.getAuxBuffers());
		this.setWindowHint(GLFW.GLFW_SAMPLES, pbh.getSamples());
		this.setWindowHint(GLFW.GLFW_REFRESH_RATE, pbh.getRefreshRate());
		this.setWindowHint(GLFW.GLFW_STEREO, pbh.getStereo());
		this.setWindowHint(GLFW.GLFW_SRGB_CAPABLE, pbh.getSRGBCapable());
		this.setWindowHint(GLFW.GLFW_DOUBLEBUFFER, pbh.getDoubleBuffer());
		return this;
	}

	public WindowHandle setWindowHint(int hint, boolean flag) {
		GLFW.glfwWindowHint(hint, (flag ? 1 : 0));
		return this;
	}

	public WindowHandle setIcon(Icon... icons) {
		this.icons.addAll(Arrays.asList(icons));
		return this;
	}

	public WindowHandle setCursor(Cursor cursor) {
		this.cursor = cursor;
		return this;
	}

	public void setWindowHint(int hint, int value) {
		GLFW.glfwWindowHint(hint, value);
	}

}
