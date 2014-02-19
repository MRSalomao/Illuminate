package illuminate.engine;

import illuminate.Lightmapper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

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
	
	static public int canvasWidth = 1024, canvasHeight = 1024;
	
	public Camera mainCamera;
	FpsCameraHandler fpsCameraHandler;
	
	Shader diffuseShader, lightDiffuseShader;
	
	Lightmapper lightmapper;
	
	//int edgeNormalizerTexture;
	
	public void init()
	{
		mainCamera = new Camera(new Vector3f(-10,0,0));
		mainCamera.clearScreenColor(0.0f);
		fpsCameraHandler = new FpsCameraHandler(mainCamera);
		
		diffuseShader = new Shader("diffuse");
		lightDiffuseShader = new Shader("lightDiffuse");
		
		lightmapper = new Lightmapper();
		lightmapper.setupLightSampler(64, 64);
		lightmapper.setupEmissionRender(512, 512);
		lightmapper.genEdgeNormalizer2();
		
		startTime = System.currentTimeMillis();
		
		while ( lightmapper.emitFromLightSample() ) if(lightmapper.lightCurrentSample%100==0) System.out.println(lightmapper.lightCurrentSample);
		
		lightmapper.setupModelSampler(512, 512); 
		
		int size = 512;
		File file = new File("test.png"); // The file to save to.
		String format = "PNG"; // Example: "PNG" or "JPG"
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		  
		int width = size, height = size, bpp = 4;
		
		for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < height; y++)
			{
				int i = (x + (width * y)) * bpp;
				int r = ((int) (lightmapper.modelSamplesBuffer.get(i+0) * 255)) & 0xFF;
				int g = ((int) (lightmapper.modelSamplesBuffer.get(i+1) * 255)) & 0xFF;
				int b = ((int) (lightmapper.modelSamplesBuffer.get(i+2) * 255)) & 0xFF;
				
				image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
			}
		}
		  
		try {
			ImageIO.write(image, format, file);
		} catch (IOException e) { e.printStackTrace(); }
		
//		while ( lightmapper.emitFromModelSample() ) if(lightmapper.modelCurrentSample%100==0) System.out.println(lightmapper.modelCurrentSample);
	}
	long startTime; boolean done;//TODO, 
	public void render(float dt)
	{
//		if(!lightmapper.emitFromLightSample())
//		{
//			if (!done)
//			{
//				System.out.println("DONE: " + (System.currentTimeMillis() - startTime) );
//				done = true;
//				
//				lightmapper.setupModelSampler(64, 64); 
//			}
//			lightmapper.setupModelSampler(64, 64); 
//			if(!lightmapper.emitFromModelSample())
//			{
			
				mainCamera.setActive();
				fpsCameraHandler.update(dt);
				Camera.activeCamera.clearScreen();
				
				diffuseShader.setActive();
				
				glActiveTexture(GL_TEXTURE0 + 4);
		//			glBindTexture(GL_TEXTURE_2D, edgeNormalizerTexture);
				
				lightmapper.targetNode.render();
				
				lightDiffuseShader.setActive();
				
				glDisable(GL_CULL_FACE);
				lightmapper.lightNode.render();
				glEnable(GL_CULL_FACE);
//			}
//		}
		
//		System.out.println(lightmapper.currentSample);
//		try {
//			Thread.sleep(50);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
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
