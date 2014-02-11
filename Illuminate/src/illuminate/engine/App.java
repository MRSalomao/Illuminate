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
	
	public int canvasWidth = 1024, canvasHeight = 728;
	
	public Camera mainCamera;
	FpsCameraHandler fpsCameraHandler;
	
	Shader diffuse, lightDiffuse;
	
	LightEmitter lightEmitter;
	
	int edgeNormalizerTexture;
	
	public void init()
	{
		mainCamera = new Camera(new Vector3f(-10,0,0));
		mainCamera.clearScreenColor(0.0f);
		fpsCameraHandler = new FpsCameraHandler(mainCamera);
		
		diffuse = new Shader("diffuse");
		lightDiffuse = new Shader("lightDiffuse");
		
		lightEmitter = new LightEmitter();
		lightEmitter.setupLightSampler(16, 16);
		lightEmitter.setupEmissionRender(1024, 1024);
		
		edgeNormalizerTexture = lightEmitter.genEdgeNormalizer();
		
//		while ( lightEmitter.emitLightFromSample() ) if(lightEmitter.currentSample%100==0) System.out.println(lightEmitter.currentSample);
	}
	
	public void render(float dt)
	{
		if(!lightEmitter.emitLightFromSample())
		{
			mainCamera.setActive();
			fpsCameraHandler.update(dt);
			Camera.activeCamera.clearScreen();
			
			diffuse.setActive();
			
			glActiveTexture(GL_TEXTURE0 + 4);
//			glBindTexture(GL_TEXTURE_2D, edgeNormalizerTexture);
			
			lightEmitter.targetNode.render();
			
			lightDiffuse.setActive();
			
			glDisable(GL_CULL_FACE);
			lightEmitter.lightNode.render();
			glEnable(GL_CULL_FACE);
		}
		
		System.out.println(lightEmitter.currentSample);
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		mainCamera.setActive();
//		mainCamera.clearScreen();
//		
//		fpsCameraHandler.update(dt);
//		
//		diffuse.setActive();

//		lightEmitter.targetLightmap.type = Texture.LIGHTMAP;
//		lightEmitter.targetNode.setLightmap(lightEmitter.targetNode.lightmap);
		
//		lightEmitter.targetNode.render();
		
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
