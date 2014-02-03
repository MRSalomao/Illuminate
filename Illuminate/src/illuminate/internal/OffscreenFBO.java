package illuminate.internal;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;

public class OffscreenFBO 
{
	public int framebufferID, depthRenderBufferID;
	
	int width, height;
	
	public OffscreenFBO(int width, int height, boolean hasDepthbuffer)
	{
		this.width = width;
		this.height = height;
		
		// Create an extra FBO for offscreen rendering
		framebufferID = glGenFramebuffersEXT();	
		depthRenderBufferID = glGenRenderbuffersEXT();			
		
		if (hasDepthbuffer) attachDepthbuffer();
	}
	
	public void attachTexture(int colorTextureID)
	{
		attachTexture(colorTextureID, GL_RGBA8, GL_NEAREST, GL_COLOR_ATTACHMENT0_EXT);
	}
	
	public void attachTexture(int colorTextureID, int internalFormat, int filterMode, int colorAttachment)
	{
		bind();
		
		glBindTexture(GL_TEXTURE_2D, colorTextureID);									

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filterMode);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filterMode);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		
		glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, GL_RGBA, GL_INT, (java.nio.ByteBuffer) null);	
		
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, colorAttachment, GL_TEXTURE_2D, colorTextureID, 0);

		unbind();
	}
	
	public void setMultTarget()
	{
		IntBuffer draw_bufs = BufferUtils.createIntBuffer(2);
		draw_bufs.put(GL30.GL_COLOR_ATTACHMENT0);
		draw_bufs.put(GL30.GL_COLOR_ATTACHMENT1);
		GL20.glDrawBuffers(draw_bufs);
	}
	
	private void attachDepthbuffer()
	{
		bind();

		glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, depthRenderBufferID);				
		glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL14.GL_DEPTH_COMPONENT24, width, height);	
		glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT,GL_DEPTH_ATTACHMENT_EXT,GL_RENDERBUFFER_EXT, depthRenderBufferID);

		unbind();
	}
	
	public void resize(int width, int height)
	{
		this.width = width;
		this.height = height;
		
		glDeleteRenderbuffersEXT(depthRenderBufferID);
		
		attachDepthbuffer();
	}
	
	
	public void removeTexture(int texture)
	{
		glDeleteTextures(texture);
	}
	
	public void bind()
	{
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, framebufferID); 	
	}
	
	public void unbind()
	{
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
	}
}
