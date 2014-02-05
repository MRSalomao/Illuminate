package illuminate;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import illuminate.internal.OffscreenFBO;
import static org.lwjgl.opengl.ARBTextureStorage.glTexStorage2D;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
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
	static App singleton;
	
	int canvasWidth = 1024, canvasHeight = 1024;
	
	OffscreenFBO offscreenFbo;
	
	static Camera camera;
	FpsCameraHandler fpsCameraHandler;
	
	Node node, node2;
	Mesh mesh, mesh2;
	Shader shader1, shader2;
	Texture texture, texture2, texture3;
	
	int normalTextureID, positionTextureID, blackWhiteTextureID;
	int normalPassTextureID, positionPassTextureID, uv2PassTextureID;
	

	public void init()
	{
		camera = new Camera(new Vector3f(-10,0,0));
		camera.clearScreenColor(0.0f);
		fpsCameraHandler = new FpsCameraHandler(camera);
		
		
		offscreenFbo = new OffscreenFBO(1024, 1024, true);
		
		normalTextureID = glGenTextures();	
		positionTextureID = glGenTextures();	
		blackWhiteTextureID = glGenTextures();	
		
		normalPassTextureID = glGenTextures();	
		positionPassTextureID = glGenTextures();	
		uv2PassTextureID = glGenTextures();	
		
		offscreenFbo.attachTexture(normalPassTextureID, GL_RGBA8, GL_NEAREST, GL_COLOR_ATTACHMENT1_EXT);
		offscreenFbo.attachTexture(positionPassTextureID, GL_RGBA8, GL_NEAREST, GL_COLOR_ATTACHMENT0_EXT);
		offscreenFbo.attachTexture(uv2PassTextureID, GL30.GL_RGBA16F, GL_NEAREST, GL_COLOR_ATTACHMENT2_EXT);
		

		shader1 = new Shader("firstPass");
		shader2 = new Shader("lightEmitter");
		
		texture = new Texture("house.png", Texture.DIFFUSE);
		texture2 = new Texture("colors2.png", Texture.LIGHTMAP);
		texture3 = new Texture(1024, 1024, Texture.IMAGE_BUFFER);
		
		mesh = new Mesh("house.mrs");
		mesh2 = new Mesh("plane.mrs");

		node = new Node(mesh, texture);
		node.setLightmap(texture2);
		node.position.set(0, 0, -3.29f);

		
		node2 = new Node(mesh2, texture);
		node2.setLightmap(texture);
		
		
		offscreenFbo.bind();
//		glViewport(0, 0, 1024, 1024);
		
		camera.clearScreen();
//		shader1.setActive();
//		node.render();
		
//		GL11.glReadBuffer(EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT);
//		int width  = 256;
//		int height = 256;
//		int bpp = 4;
//		buffer = BufferUtils.createByteBuffer(width * height * bpp);
//		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer );
		
		glViewport(0, 0, canvasWidth, canvasHeight);
		offscreenFbo.unbind();
		
		texture3.setActive();
	}
	ByteBuffer buffer;
	
	float a;
	public void render(float dt)
	{
		camera.clearScreen();
		glViewport(0, 0, canvasWidth, canvasHeight);
		
		fpsCameraHandler.update(dt);
		
//		if (Mouse.getX() < 256 && Mouse.getY() < 256) System.out.println(buffer.get(Mouse.getX()*4 + Mouse.getY()*4*256));
		
		draw();
		
		shader2.setActive();
		a+=0.002*dt;
//		camera.position.z = a;
//		camera.lookAt(camera.position, node2.position, new Vector3f(0,0,1));
//		node.position.set((float)Math.cos(a)*2, (float)Math.sin(a)*2, 1);
		
		glActiveTexture(GL_TEXTURE0 + 1);
		glBindTexture(GL_TEXTURE_2D, normalPassTextureID);
		glActiveTexture(GL_TEXTURE0 + 2);
		glBindTexture(GL_TEXTURE_2D, positionPassTextureID);
		glActiveTexture(GL_TEXTURE0 + 0);
		glBindTexture(GL_TEXTURE_2D, uv2PassTextureID);
		
		mesh2.render();
		
		glActiveTexture(GL_TEXTURE0 + 1);
		glBindTexture(GL_TEXTURE_2D, 0);
		glActiveTexture(GL_TEXTURE0 + 0);
		glBindTexture(GL_TEXTURE_2D, 0);
		glActiveTexture(GL_TEXTURE0 + 2);
		glBindTexture(GL_TEXTURE_2D, 0);


		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferBlit.GL_READ_FRAMEBUFFER_EXT, offscreenFbo.framebufferID);
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferBlit.GL_DRAW_FRAMEBUFFER_EXT, 0);
		
		EXTFramebufferBlit.glBlitFramebufferEXT(0, 0, 1024, 1024, 0, 0, 256, 256, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
	}
	
	void draw()
	{
		offscreenFbo.bind();
		offscreenFbo.setMultTarget();
		glViewport(0, 0, 1024, 1024);
		
		camera.clearScreen();
		
		shader1.setActive();
		
		node.render();
		
		glViewport(0, 0, canvasWidth, canvasHeight);
		offscreenFbo.setSingleTarget();
		offscreenFbo.unbind();
	}
	
	public App()
	{
		singleton = this;
	}
}
