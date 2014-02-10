package illuminate.engine;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class Utils 
{
	static final double PI = 3.14159265358979323846;
	static final double PI2 = PI/2;
	static final double twoPI = PI*2;

	static float coTangent(float angle) 
	{
		return (float)(1f / Math.tan(angle));
	}
	
	static float degreesToRadians(float degrees) 
	{
		return degrees * (float)(PI / 180d);
	}
	
	static void exitOnGLError(String errorMessage) 
	{
		int errorValue = GL11.glGetError();
		
		if (errorValue != GL11.GL_NO_ERROR) {
			String errorString = GLU.gluErrorString(errorValue);
			System.err.println("ERROR - " + errorMessage + ": " + errorString);
			
			if (Display.isCreated()) Display.destroy();
			System.exit(-1);
		}
	}
}
