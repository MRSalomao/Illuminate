package illuminate;

import static org.lwjgl.opengl.EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_COLOR_ATTACHMENT1_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_COLOR_ATTACHMENT2_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glDeleteFramebuffersEXT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;

import java.nio.FloatBuffer;

import illuminate.engine.App;
import illuminate.engine.Camera;
import illuminate.engine.Mesh;
import illuminate.engine.Node;
import illuminate.engine.OffscreenFBO;
import illuminate.engine.Shader;
import illuminate.engine.Texture;
import illuminate.engine.Utils;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL42;
import org.lwjgl.util.vector.Vector3f;

public class LightEmitter 
{
	Camera offscreenCamera;
	
	FloatBuffer lightSamplesBuffer, lightPositionsBuffer, lightNormalsBuffer;
	
	int normalPassTextureID, positionPassTextureID, uv2PassTextureID;
	
	public Node lightNode; Mesh lightsMesh; Texture lightTexture;
	
	public Node targetNode; Mesh targetMesh; public Texture targetDiffuse, targetLightmap;
	
	Mesh quadMesh;
	
	Shader firstPass, emitLight, lightConfigurer, edgeNormalizer;
	
	OffscreenFBO offscreenFbo1, offscreenFbo2;
	
	public int samplerWidth, samplerHeight;
	public int emissionWidth, emissionHeight;
	public int currentSample, totalTexels;
	
	public LightEmitter()
	{
		offscreenCamera = new Camera(new Vector3f(0,0,0));
		
		lightsMesh = new Mesh("lights.mrs");
		lightTexture = new Texture("white.png", Texture.DIFFUSE);
		lightNode = new Node(lightsMesh, lightTexture);
		
		lightConfigurer = new Shader("lightConfigurer");
		edgeNormalizer = new Shader("edgeNormalizer");
		
		normalPassTextureID = glGenTextures();	
		positionPassTextureID = glGenTextures();	
		uv2PassTextureID = glGenTextures();	
	}
	
	public void setupLightSampler(int samplerWidth, int samplerHeight)
	{
		this.samplerWidth  = samplerWidth;
		this.samplerHeight = samplerHeight;
		
		totalTexels = samplerWidth * samplerHeight;
		
		OffscreenFBO tmpOffscreenFbo = new OffscreenFBO(samplerWidth, samplerHeight, false);
		
		int lightNormalID = glGenTextures();	
		int lightPositionID = glGenTextures();	
		int lightSamplerID = glGenTextures();	
		
		tmpOffscreenFbo.attachTexture(lightSamplerID,  GL_RGBA32F, GL_NEAREST, GL_COLOR_ATTACHMENT0_EXT);
		tmpOffscreenFbo.attachTexture(lightPositionID, GL_RGBA32F, GL_NEAREST, GL_COLOR_ATTACHMENT1_EXT);
		tmpOffscreenFbo.attachTexture(lightNormalID,   GL_RGBA32F, GL_NEAREST, GL_COLOR_ATTACHMENT2_EXT);
		
		tmpOffscreenFbo.bind();
		tmpOffscreenFbo.setMultTarget();
		
		offscreenCamera.setActive();
		offscreenCamera.clearScreen();
		
		lightConfigurer.setActive();
		lightNode.render();
		
		int width  = tmpOffscreenFbo.width;
		int height = tmpOffscreenFbo.height;
		int bpp = 16;
		
		GL11.glReadBuffer(GL_COLOR_ATTACHMENT0_EXT);
		lightSamplesBuffer = BufferUtils.createByteBuffer(width * height * bpp).asFloatBuffer();
		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_FLOAT, lightSamplesBuffer);
		
		GL11.glReadBuffer(GL_COLOR_ATTACHMENT1_EXT);
		lightPositionsBuffer = BufferUtils.createByteBuffer(width * height * bpp).asFloatBuffer();
		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_FLOAT, lightPositionsBuffer);
		
		GL11.glReadBuffer(GL_COLOR_ATTACHMENT2_EXT);
		lightNormalsBuffer = BufferUtils.createByteBuffer(width * height * bpp).asFloatBuffer();
		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_FLOAT, lightNormalsBuffer);
		
		tmpOffscreenFbo.setSingleTarget();
		tmpOffscreenFbo.unbind();
		
		GL11.glDeleteTextures(lightNormalID);
		GL11.glDeleteTextures(lightPositionID);
		GL11.glDeleteTextures(lightSamplerID);
		tmpOffscreenFbo.destroy();
	}
	
	public void setupEmissionRender(int emissionWidth, int emissionHeight)
	{
		this.emissionWidth  = emissionWidth;
		this.emissionHeight = emissionHeight;
		
		offscreenFbo1 = new OffscreenFBO(emissionWidth, emissionHeight, true);
		offscreenFbo2 = new OffscreenFBO(emissionWidth, emissionHeight, false);

		offscreenFbo1.attachTexture(positionPassTextureID, GL_RGBA32F, GL_LINEAR, GL_COLOR_ATTACHMENT0_EXT);
		offscreenFbo1.attachTexture(normalPassTextureID,   GL_RGBA32F, GL_LINEAR, GL_COLOR_ATTACHMENT1_EXT);
		offscreenFbo1.attachTexture(uv2PassTextureID,      GL_RGBA32F, GL_LINEAR, GL_COLOR_ATTACHMENT2_EXT);
		
		
		firstPass = new Shader("firstPass");
		emitLight = new Shader("emitLight");
		
		targetDiffuse = new Texture("house.png", Texture.DIFFUSE);
		targetLightmap = new Texture(emissionWidth, emissionHeight, Texture.IMAGE_BUFFER);
		
		targetLightmap.setActive();
		
		targetMesh = new Mesh("house.mrs");
		quadMesh = new Mesh("quad.mrs");

		targetNode = new Node(targetMesh, targetDiffuse);
	}
	
	public boolean emitLightFromSample()
	{
		if (currentSample == totalTexels)
		{
			return false;
		}
		
		if (lightSamplesBuffer.get(currentSample*4) == 1f)
		{
			calibrateCamera();
			
			executeFirstPass();
			executeFinalPass();
		}
		else
		{
			currentSample++;
			
			return false;
		}
		
		currentSample++;
		
		return true;
	}
	
	float perturbation = .3f;
	void calibrateCamera()
	{
		offscreenCamera.setActive();
		
		Vector3f eye = new Vector3f(lightPositionsBuffer.get(currentSample * 4 + 0), 
									lightPositionsBuffer.get(currentSample * 4 + 1), 
									lightPositionsBuffer.get(currentSample * 4 + 2));
		
		Vector3f dir = new Vector3f(lightNormalsBuffer.get(currentSample * 4 + 0), 
									lightNormalsBuffer.get(currentSample * 4 + 1), 
									lightNormalsBuffer.get(currentSample * 4 + 2)); System.out.println(eye + " " + dir);
									
		Vector3f.add(dir, new Vector3f( ( (float) Math.random() - 0.5f ) * perturbation,
										( (float) Math.random() - 0.5f ) * perturbation,
										( (float) Math.random() - 0.5f ) * perturbation), dir);
		
		offscreenCamera.lookAtDirection(eye, dir, new Vector3f(0,0,1));
	}
	
	void executeFirstPass()
	{
		offscreenFbo1.bind();
		offscreenFbo1.setMultTarget();
		
		offscreenCamera.clearScreen();
		
		firstPass.setActive();
		
		targetNode.render();

		offscreenFbo1.setSingleTarget();
		offscreenFbo1.unbind();
	}
	
	void executeFinalPass()
	{
		offscreenCamera.clearScreen();
		
//		glActiveTexture(GL_TEXTURE0 + 0);
//		glBindTexture(GL_TEXTURE_2D, positionPassTextureID);
//		glActiveTexture(GL_TEXTURE0 + 1);
//		glBindTexture(GL_TEXTURE_2D, normalPassTextureID);
		glActiveTexture(GL_TEXTURE0 + 2);
		glBindTexture(GL_TEXTURE_2D, uv2PassTextureID);
		
		emitLight.setActive();
		
		quadMesh.render();
		
//		glActiveTexture(GL_TEXTURE0 + 0);
//		glBindTexture(GL_TEXTURE_2D, 0);
//		glActiveTexture(GL_TEXTURE0 + 1);
//		glBindTexture(GL_TEXTURE_2D, 0);
//		glActiveTexture(GL_TEXTURE0 + 2);
//		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public int genEdgeNormalizer()
	{
		int fboID = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboID);

		glActiveTexture(GL_TEXTURE0 + 4);
		int cnTextureID = glGenTextures();
		GL11.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, cnTextureID);
		GL32.glTexImage2DMultisample(GL32.GL_TEXTURE_2D_MULTISAMPLE, 8, GL11.GL_RGBA8, emissionWidth, emissionHeight, true);

		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0_EXT, cnTextureID, 0);
		
		edgeNormalizer.setActive();
		
		Camera.activeCamera.clearScreenColor(1.0f);
		Camera.activeCamera.clearScreen();
		
		targetNode.render();
		
		Camera.activeCamera.clearScreenColor(0.0f);
		
		glDeleteFramebuffersEXT(fboID);
		
		return cnTextureID;
	}
	
}
