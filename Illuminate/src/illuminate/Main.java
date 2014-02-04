package illuminate;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL11.glViewport;

import java.io.File;

import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class Main 
{
	App app;
	
	long lastFrame;
	int fps;
	long lastFPS;
	
	public Main() 
	{
		// Initialize our application
		app = new App();
		
		// Initialize OpenGL (Display)
		setupOpenGL();
		
		// Called once, before running
		App.singleton.init();
		
		// call once before loop to initialise lastFrame
		getDelta(); 
		
		// call before loop to initialise fps timer
		lastFPS = getTime(); 
		
		while (!Display.isCloseRequested()) 
		{
			// Do a single loop (logic/render)
			App.singleton.render(getDelta());
			
			// Update FPS, if logging it
			updateFPS();
			
			// Let the CPU synchronize with the GPU if GPU is tagging behind
			Display.update();
		}
		
		// Destroy OpenGL (Display)
		this.destroyOpenGL();
	}

	private void setupOpenGL() 
	{
		// Setup an OpenGL context with highest API version available
		try 
		{
			Display.setDisplayMode(new DisplayMode(App.singleton.canvasWidth, App.singleton.canvasHeight));
//			Display.setVSyncEnabled(true);
			Display.setTitle("Illuminate");
			Display.create();
//			Display.setFullscreen(true);
		} 
		catch (LWJGLException e) 
		{
			e.printStackTrace();
			System.exit(-1);
		}
		
		System.out.println("OpenGL version: " + glGetString(GL_VERSION));
		
		// Enable depth testing and backface culling
		glEnable(GL_DEPTH_TEST);
		glCullFace(GL_BACK);
		glEnable(GL_CULL_FACE);
		
		// Map the internal OpenGL coordinate system to the entire screen
		glViewport(0, 0, App.singleton.canvasWidth, App.singleton.canvasHeight);
		
		// default background color: black
		glClearColor(0.2f, 0.2f, 0.5f, 1.0f);
		
		Utils.exitOnGLError("setupOpenGL");
	}
	
	private void destroyOpenGL() 
	{	
//		// Delete the texture
//		glDeleteTextures(texIds[0]);
//		glDeleteTextures(texIds[1]);
//		
//		// Delete the shaders
//		glUseProgram(0);
//		glDeleteProgram(pId);
//		
//		// Select the VAO
//		glBindVertexArray(vaoId);
//		
//		// Disable the VBO index from the VAO attributes list
//		glDisableVertexAttribArray(0);
//		glDisableVertexAttribArray(1);
//		
//		// Delete the vertex VBO
//		glBindBuffer(GL_ARRAY_BUFFER, 0);
//		glDeleteBuffers(vboId);
//		
//		// Delete the index VBO
//		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
//		glDeleteBuffers(vboiId);
//		
//		// Delete the VAO
//		glBindVertexArray(0);
//		glDeleteVertexArrays(vaoId);
//		
//		Utils.exitOnGLError("destroyOpenGL");
//		
//		Display.destroy();
	}
	
	public float getDelta() 
	{
	    long time = getTime();
	    float delta = (float) (time - lastFrame);
	    lastFrame = time;
	 
	    return delta;
	}
	
	public long getTime() 
	{
	    return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	public void updateFPS() 
	{
		if (getTime() - lastFPS > 1000) 
		{
			System.out.println("FPS: " + fps);
//			Display.setTitle("FPS: " + fps);
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
	}
	
	public static void main(String[] args) 
	{
		System.setProperty("org.lwjgl.librarypath", new File(new File(System.getProperty("user.dir"), "native"), LWJGLUtil.getPlatformName()).getAbsolutePath());
		System.setProperty("net.java.games.input.librarypath", System.getProperty("org.lwjgl.librarypath"));
		
		new Main();
	}
}