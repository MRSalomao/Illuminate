package illuminate;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.GL_READ_WRITE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL42;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class Texture 
{
	public static int DIFFUSE=0, LIGHTMAP=1, IMAGE_BUFFER=3;
	
	int type;
	
	public int texId;
	
	public Texture(String textureName, int type) 
	{
		this.type = type;
		
		loadPNGTexture("assets/images/" + textureName, GL_TEXTURE0);
		
		Utils.exitOnGLError("setupTexture");
	}
	
	
	public Texture(int width, int height, int type) 
	{
		this.type = type;
		
		FloatBuffer buf = BufferUtils.createByteBuffer(width*height*4*4).asFloatBuffer();
		for (int i = 0; i < width*height*4; i++) buf.put(0f);
		buf.flip();
		
		texId = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texId);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, width, height, 0, GL_RGBA, GL_FLOAT, buf);
		glBindTexture(GL_TEXTURE_2D, 0);
		
//		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
//		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		
		GL42.glBindImageTexture(3, texId, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
	}
	
	
	private void loadPNGTexture(String filename, int textureUnit) 
	{
		ByteBuffer buf = null;
		int tWidth = 0;
		int tHeight = 0;
		
		try 
		{
			// Open the PNG file as an InputStream
			InputStream in = new FileInputStream(filename);
			// Link the PNG decoder to this stream
			PNGDecoder decoder = new PNGDecoder(in);
			
			// Get the width and height of the texture
			tWidth = decoder.getWidth();
			tHeight = decoder.getHeight();
			
			
			// Decode the PNG file in a ByteBuffer
			buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
			decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
			buf.flip();
			
			in.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			System.exit(-1);
		}
		
		// Create a new texture object in memory and bind it
		texId = glGenTextures();
		glActiveTexture(textureUnit);
		glBindTexture(GL_TEXTURE_2D, texId);
		
		// All RGB bytes are aligned to each other and each component is 1 byte
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		
		// Upload the texture data and generate mip maps (for scaling)
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, tWidth, tHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
		glGenerateMipmap(GL_TEXTURE_2D);
		
		// Setup the ST coordinate system
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		
		// Setup what to do when the texture has to be scaled
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		
		Utils.exitOnGLError("loadPNGTexture");
	}
	
	
	public void setActive()
	{
		glActiveTexture(GL_TEXTURE0 + type);
		
		if (type == 3)
		{
			glBindTexture(GL_TEXTURE_2D, 0);
			
			GL42.glBindImageTexture(3, texId, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		}
		else
		{
			glBindTexture(GL_TEXTURE_2D, texId);
		}
		
		Utils.exitOnGLError("Texture-setActive");
	}
	
	public void dispose()
	{
		
	}
}
