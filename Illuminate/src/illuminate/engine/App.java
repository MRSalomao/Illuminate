package illuminate.engine;

import illuminate.LightEmitter;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.ARBTextureStorage.glTexStorage2D;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.EXTFramebufferBlit;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL41;
import org.lwjgl.opengl.GL42;

import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.GL_READ_WRITE;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL31.GL_TEXTURE_BUFFER;

import org.lwjgl.util.vector.Vector3f;

public class App 
{
	public static App singleton;
	
	public int canvasWidth = 1024, canvasHeight = 1024;
	
	public Camera mainCamera;
	FpsCameraHandler fpsCameraHandler;
	
	Shader diffuse;
	
	LightEmitter lightEmitter;
	
	public void init()
	{
		mainCamera = new Camera(new Vector3f(-10,0,0));
		mainCamera.clearScreenColor(0.0f);
		mainCamera.setActive();
		fpsCameraHandler = new FpsCameraHandler(mainCamera);
		
		diffuse = new Shader("diffuse");
		
		lightEmitter = new LightEmitter();
		lightEmitter.setupLightSampler(64, 64);
		lightEmitter.setupEmissionRender(256, 256);
		
		while ( lightEmitter.emitLightFromSample() ) if(lightEmitter.currentSample%100==0) System.out.println(lightEmitter.currentSample);
	}
	
	public void render(float dt)
	{
		glViewport(0, 0, 1024, 1024); //TODO needed?
		
		mainCamera.setActive();
		mainCamera.clearScreen();
		
		fpsCameraHandler.update(dt);
		
		diffuse.setActive();

		lightEmitter.targetLightmap.type = Texture.LIGHTMAP;
		lightEmitter.targetNode.setLightmap(lightEmitter.targetNode.lightmap);
		
		lightEmitter.targetNode.render();
		
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
		{
			System.exit(0);
		}
	}
	

	public App()
	{
		singleton = this;
	}
}
