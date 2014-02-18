package illuminate;

import static org.lwjgl.opengl.EXTFramebufferObject.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import illuminate.engine.App;
import illuminate.engine.Camera;
import illuminate.engine.Mesh;
import illuminate.engine.Node;
import illuminate.engine.OffscreenFBO;
import illuminate.engine.Shader;
import illuminate.engine.Texture;
import illuminate.engine.Utils;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.GL_READ_WRITE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL42.*;

import org.lwjgl.opengl.GL42;
import org.lwjgl.util.vector.Vector3f;

public class Lightmapper 
{
	Camera offscreenCamera;
	
	FloatBuffer lightSamplesBuffer, lightPositionsBuffer, lightNormalsBuffer;
	public FloatBuffer modelSamplesBuffer, modelPositionsBuffer, modelNormalsBuffer; //TODO
	IntBuffer modelLightingBuffer;
	
	int normalPassTextureID, positionPassTextureID, uv2PassTextureID;
	
	public Node lightNode; Mesh lightsMesh; Texture lightTexture;
	
	public Node targetNode; Mesh targetMesh; public Texture targetDiffuse, targetLightmap;
	
	Mesh quadMesh;
	
	Shader firstPassShader, emitLightShader, lightConfigurer, modelConfigurer, edgeNormalizer, edgeNormalizer2;
	
	OffscreenFBO offscreenFbo;
	
	public int lightSamplerWidth, lightSamplerHeight;
	public int modelSamplerWidth, modelSamplerHeight;
	public int emissionWidth, emissionHeight;
	public int lightCurrentSample, totalLightTexels;
	public int modelCurrentSample, totalModelTexels;
	
	int lightToShootLoc, lightToShoot;
	
	public Lightmapper()
	{
		offscreenCamera = new Camera(new Vector3f(0,0,0));
		
		lightsMesh = new Mesh("lights.mrs");
		lightTexture = new Texture("white.png", Texture.DIFFUSE);
		lightNode = new Node(lightsMesh, lightTexture);
//		lightNode.rotation.set(0, 0, 2.9f);
		
		lightConfigurer = new Shader("lightConfigurer");
		modelConfigurer = new Shader("modelConfigurer");
		edgeNormalizer = new Shader("edgeNormalizer");
		edgeNormalizer2 = new Shader("edgeNormalizer2");
		
		normalPassTextureID = glGenTextures();	
		positionPassTextureID = glGenTextures();	
		uv2PassTextureID = glGenTextures();	
	}
	
	public void setupLightSampler(int samplerWidth, int samplerHeight)
	{
		this.lightSamplerWidth  = samplerWidth;
		this.lightSamplerHeight = samplerHeight;
		
		totalLightTexels = samplerWidth * samplerHeight;
		
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
		
		glDisable(GL_CULL_FACE);
		lightNode.render();
		glEnable(GL_CULL_FACE);
		
		int width  = tmpOffscreenFbo.width;
		int height = tmpOffscreenFbo.height;
		int bpp = 16;
		
		glReadBuffer(GL_COLOR_ATTACHMENT0_EXT);
		lightSamplesBuffer = BufferUtils.createByteBuffer(width * height * bpp).asFloatBuffer();
		glReadPixels(0, 0, width, height, GL_RGBA, GL_FLOAT, lightSamplesBuffer);
		
		glReadBuffer(GL_COLOR_ATTACHMENT1_EXT);
		lightPositionsBuffer = BufferUtils.createByteBuffer(width * height * bpp).asFloatBuffer();
		glReadPixels(0, 0, width, height, GL_RGBA, GL_FLOAT, lightPositionsBuffer);
		
		glReadBuffer(GL_COLOR_ATTACHMENT2_EXT);
		lightNormalsBuffer = BufferUtils.createByteBuffer(width * height * bpp).asFloatBuffer();
		glReadPixels(0, 0, width, height, GL_RGBA, GL_FLOAT, lightNormalsBuffer);
		
		tmpOffscreenFbo.setSingleTarget();
		tmpOffscreenFbo.unbind();
		
		glDeleteTextures(lightNormalID);
		glDeleteTextures(lightPositionID);
		glDeleteTextures(lightSamplerID);
		tmpOffscreenFbo.destroy();
	}
	
	public void setupModelSampler(int samplerWidth, int samplerHeight)
	{
		this.modelSamplerWidth  = samplerWidth;
		this.modelSamplerHeight = samplerHeight;
		
		totalModelTexels = samplerWidth * samplerHeight;
		
		OffscreenFBO tmpOffscreenFbo = new OffscreenFBO(samplerWidth, samplerHeight, false);
		
		int lightNormalID = glGenTextures();	
		int lightPositionID = glGenTextures();	
		int lightSamplerID = glGenTextures();	
		
		tmpOffscreenFbo.attachTexture(lightSamplerID,  GL_RGBA32F, GL_NEAREST, GL_COLOR_ATTACHMENT0_EXT);
		tmpOffscreenFbo.attachTexture(lightPositionID, GL_RGBA32F, GL_NEAREST, GL_COLOR_ATTACHMENT1_EXT);
		tmpOffscreenFbo.attachTexture(lightNormalID,   GL_RGBA32F, GL_NEAREST, GL_COLOR_ATTACHMENT2_EXT);
		
		tmpOffscreenFbo.bind();
		tmpOffscreenFbo.setMultTarget();
		glViewport(0, 0, samplerWidth, samplerHeight);
		
		offscreenCamera.setActive();
		offscreenCamera.clearScreen();
		
		glDisable(GL_CULL_FACE);
		modelConfigurer.setActive();
		targetNode.render();
		glEnable(GL_CULL_FACE);
		
		int width  = tmpOffscreenFbo.width;
		int height = tmpOffscreenFbo.height;
		int bpp = 16;
		
		glReadBuffer(GL_COLOR_ATTACHMENT0_EXT);
		modelSamplesBuffer = BufferUtils.createByteBuffer(width * height * bpp).asFloatBuffer();
		glReadPixels(0, 0, width, height, GL_RGBA, GL_FLOAT, modelSamplesBuffer);
		
		glReadBuffer(GL_COLOR_ATTACHMENT1_EXT);
		modelPositionsBuffer = BufferUtils.createByteBuffer(width * height * bpp).asFloatBuffer();
		glReadPixels(0, 0, width, height, GL_RGBA, GL_FLOAT, modelPositionsBuffer);
		
		glReadBuffer(GL_COLOR_ATTACHMENT2_EXT);
		modelNormalsBuffer = BufferUtils.createByteBuffer(width * height * bpp).asFloatBuffer();
		glReadPixels(0, 0, width, height, GL_RGBA, GL_FLOAT, modelNormalsBuffer);
		
//		modelLightingBuffer = BufferUtils.createByteBuffer(lightSamplerWidth * lightSamplerHeight * 4).asIntBuffer();
//		glBindImageTexture(3, 0, 0, false, 0, GL_READ_WRITE, GL_R32I);
//		glBindTexture(GL_TEXTURE_2D, targetLightmap.texId);
//		glGetTexImage(GL_TEXTURE_2D, 0, GL_RED_INTEGER, GL_INT, modelLightingBuffer);
//		glBindTexture(GL_TEXTURE_2D, 0);
//		glBindImageTexture(3, targetLightmap.texId, 0, false, 0, GL_READ_WRITE, GL_R32I);
		
		tmpOffscreenFbo.setSingleTarget();
		tmpOffscreenFbo.unbind();
		
		glDeleteTextures(lightNormalID);
		glDeleteTextures(lightPositionID);
		glDeleteTextures(lightSamplerID);
		tmpOffscreenFbo.destroy();
	}
	
	public void setupEmissionRender(int emissionWidth, int emissionHeight)
	{
		this.emissionWidth  = emissionWidth;
		this.emissionHeight = emissionHeight;
		
		offscreenFbo = new OffscreenFBO(emissionWidth, emissionHeight, true);

		offscreenFbo.attachTexture(positionPassTextureID, GL_RGBA32F, GL_LINEAR, GL_COLOR_ATTACHMENT0_EXT);
		offscreenFbo.attachTexture(normalPassTextureID,   GL_RGBA32F, GL_LINEAR, GL_COLOR_ATTACHMENT1_EXT);
		offscreenFbo.attachTexture(uv2PassTextureID,      GL_RGBA32F, GL_LINEAR, GL_COLOR_ATTACHMENT2_EXT);
		
		
		firstPassShader = new Shader("firstPass");
		emitLightShader = new Shader("emitLight");
		
		lightToShootLoc = glGetUniformLocation(emitLightShader.programId, "lightToShoot");
		
		targetDiffuse = new Texture("house.png", Texture.DIFFUSE);
		targetLightmap = new Texture(emissionWidth, emissionHeight, Texture.IMAGE_BUFFER);
		
		targetLightmap.setActive();
		
		targetMesh = new Mesh("house.mrs");
		quadMesh = new Mesh("quad.mrs");

		targetNode = new Node(targetMesh, targetDiffuse);
	}
	
	public boolean emitFromLightSample()
	{
		if (lightCurrentSample == totalLightTexels)
		{
			return false;
		}
		
		if (lightSamplesBuffer.get(lightCurrentSample*4) == 1f)
		{
			lightToShoot = 50;
			
			calibrateLightCamera();
			
			executeFirstPass();
			executeFinalPass();
		}
		
		lightCurrentSample++;
		
		return true;
	}
	
	public boolean emitFromModelSample()
	{
		if (modelCurrentSample == totalModelTexels)
		{
			return false;
		}
		
		System.out.println("sample: " + modelSamplesBuffer.get(modelCurrentSample*4));
		if (modelSamplesBuffer.get(modelCurrentSample*4) > 0.0f)
		{
			lightToShoot = 1;
			
			calibrateBounceCamera();
			
			executeFirstPass();
			executeFinalPass();
		}
		
		modelCurrentSample++;
		
		return true;
	}
	
	float perturbation = .5f;
	void calibrateLightCamera()
	{
		offscreenCamera.setActive();
		
		Vector3f eye = new Vector3f(lightPositionsBuffer.get(lightCurrentSample * 4 + 0), 
									lightPositionsBuffer.get(lightCurrentSample * 4 + 1), 
									lightPositionsBuffer.get(lightCurrentSample * 4 + 2));
		
		Vector3f dir = new Vector3f(lightNormalsBuffer.get(lightCurrentSample * 4 + 0), 
									lightNormalsBuffer.get(lightCurrentSample * 4 + 1), 
									lightNormalsBuffer.get(lightCurrentSample * 4 + 2)); //System.out.println(eye + " " + dir);
									
		Vector3f.add(dir, new Vector3f( ( (float) Math.random() - 0.5f ) * perturbation,
										( (float) Math.random() - 0.5f ) * perturbation,
										( (float) Math.random() - 0.5f ) * perturbation), dir);
		
		dir.normalise();
		
		offscreenCamera.lookAtDirection(eye, dir, new Vector3f(0,0,1));
	}
	
	void calibrateBounceCamera()
	{
		offscreenCamera.setActive();
		
		Vector3f eye = new Vector3f(modelPositionsBuffer.get(modelCurrentSample * 4 + 0), 
									modelPositionsBuffer.get(modelCurrentSample * 4 + 1), 
									modelPositionsBuffer.get(modelCurrentSample * 4 + 2));
		
		Vector3f dir = new Vector3f(modelNormalsBuffer.get(modelCurrentSample * 4 + 0), 
									modelNormalsBuffer.get(modelCurrentSample * 4 + 1), 
									modelNormalsBuffer.get(modelCurrentSample * 4 + 2)); System.out.println(eye + " " + dir);
									
		Vector3f.add(dir, new Vector3f( ( (float) Math.random() - 0.5f ) * perturbation,
										( (float) Math.random() - 0.5f ) * perturbation,
										( (float) Math.random() - 0.5f ) * perturbation), dir);
		
		dir.normalise();
		
		offscreenCamera.lookAtDirection(eye, dir, new Vector3f(0,0,1)); //TODO
	}
	
	void executeFirstPass()
	{
		offscreenFbo.bind();
		offscreenFbo.setMultTarget();
		
		offscreenCamera.clearScreen();
		
		firstPassShader.setActive();
		
		targetNode.render();

		offscreenFbo.setSingleTarget();
		offscreenFbo.unbind();
	}
	
	void executeFinalPass()
	{
//		offscreenFbo.bind();
		offscreenCamera.clearScreen();
		
//		glActiveTexture(GL_TEXTURE0 + 0);
//		glBindTexture(GL_TEXTURE_2D, positionPassTextureID);
//		glActiveTexture(GL_TEXTURE0 + 1);
//		glBindTexture(GL_TEXTURE_2D, normalPassTextureID);
		glActiveTexture(GL_TEXTURE0 + 2);
		glBindTexture(GL_TEXTURE_2D, uv2PassTextureID);
		
		emitLightShader.setActive();
		glUniform1i(lightToShootLoc, lightToShoot);
		
		glViewport(0, 0, emissionWidth, emissionHeight);
		quadMesh.render();
		glViewport(0, 0, App.canvasWidth, App.canvasHeight);
		
//		glActiveTexture(GL_TEXTURE0 + 0);
//		glBindTexture(GL_TEXTURE_2D, 0);
//		glActiveTexture(GL_TEXTURE0 + 1);
//		glBindTexture(GL_TEXTURE_2D, 0);
//		glActiveTexture(GL_TEXTURE0 + 2);
//		glBindTexture(GL_TEXTURE_2D, 0);
//		offscreenFbo.unbind();
	}
	
	public int genEdgeNormalizer()
	{
		int fboID = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, fboID);

		glActiveTexture(GL_TEXTURE0 + 4);
		int cnTextureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, cnTextureID);
		glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, 32, GL_RGBA8, emissionWidth, emissionHeight, true);
		
		glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0_EXT, cnTextureID, 0);
		
		edgeNormalizer.setActive();
		
		Camera.activeCamera.clearScreenColor(1.0f);
		Camera.activeCamera.clearScreen();
		
		glViewport(0, 0, emissionWidth, emissionHeight);
		glDisable(GL_CULL_FACE);
		targetNode.render();
		glEnable(GL_CULL_FACE);
		glViewport(0, 0, App.canvasWidth, App.canvasHeight);
		
		Camera.activeCamera.clearScreenColor(0.0f);
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glDeleteFramebuffersEXT(fboID);
		
		return cnTextureID;
	}
	
	public int genEdgeNormalizer2()
	{
		int fboID = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, fboID);

		
		int cnTextureID = glGenTextures();
		glActiveTexture(GL_TEXTURE0 + 4);
		glBindTexture(GL_TEXTURE_2D, cnTextureID);
		ByteBuffer buf = null;
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, emissionWidth, emissionHeight, 0, GL_RGBA, GL_BYTE, buf);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, cnTextureID, 0);
		
		edgeNormalizer2.setActive();
		
		Camera.activeCamera.clearScreenColor(0f,0f,0f,0f);
		Camera.activeCamera.clearScreen();
		
		float samples = 32;
		glUniform1f(edgeNormalizer2.alphaLocation, 1f/(samples*samples) );
		
		glEnable(GL_BLEND);
		glBlendEquationSeparate(GL_FUNC_ADD, GL_FUNC_ADD);
		glBlendFuncSeparate(GL_ONE, GL_ONE, GL_ONE, GL_ONE);
		
		for (float i=0; i<samples; i++)
		{
			for (float j=0; j<samples; j++)
			{
//				float test = i/samples/emissionWidth*0.5f - 1/emissionWidth*0.25f;
				glUniform1f(edgeNormalizer2.iLocation, i/samples/emissionWidth*2f - 1f/emissionWidth);
				glUniform1f(edgeNormalizer2.jLocation, j/samples/emissionWidth*2f - 1f/emissionWidth);
				
				glViewport(0, 0, emissionWidth, emissionHeight);
				glDisable(GL_CULL_FACE);
				targetNode.render();
				glEnable(GL_CULL_FACE);
				glViewport(0, 0, App.canvasWidth, App.canvasHeight);
			}
		}
		glDisable (GL_BLEND);
		
		Camera.activeCamera.clearScreenColor(0);
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glDeleteFramebuffersEXT(fboID);
		
		return cnTextureID;
	}
	
}
