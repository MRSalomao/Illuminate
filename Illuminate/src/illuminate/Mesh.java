package illuminate;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL42;
import org.lwjgl.opengl.GL43;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.ARBTextureStorage.*;

public class Mesh 
{
	Shader shader;
	
	int vaoId, vboId, vboIndicesId;

	ByteBuffer verticesByteBuffer;
	
	int indicesSize, verticesSize, numberOfUVmaps;
	ArrayList<VertexData> vertices;
	int[] indices;
	
	
	public Mesh(String meshName) 
	{
		DataInputStream in;
		try 
		{
			in = new DataInputStream(new BufferedInputStream(new FileInputStream("assets/models/" + meshName)));

			// Read mesh header
			verticesSize   = in.readInt(); 
			indicesSize    = in.readInt() * 3;
			numberOfUVmaps = in.readInt();
			
			// Initialize lists
			vertices = new ArrayList<VertexData>(verticesSize); 
			indices = new int[indicesSize];
			
			// Read vertex attributes
			for(int i=0; i<verticesSize; i++)
			{
				VertexData vd = new VertexData();
				vd.pos    = new float[] {in.readFloat(), in.readFloat(), in.readFloat(), 1f};
				vd.normal = new float[] {in.readFloat(), in.readFloat(), in.readFloat()};
				vd.uv1    = new float[] {in.readFloat(), in.readFloat()};
				
				if (numberOfUVmaps == 2)
				{
					vd.uv2 = new float[] {in.readFloat(), in.readFloat()};
				}
				else
				{
					vd.uv2 = new float[] {1,1};
				}
				
				vertices.add(vd);
			}
			
			// Read triangle indices
			for(int i=0; i<indices.length/3; i++)
			{
				indices[i*3+0] = in.readInt();
				indices[i*3+1] = in.readInt();
				indices[i*3+2] = in.readInt();
			}
			
			in.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		// Put each 'Vertex' in one FloatBuffer
		verticesByteBuffer = BufferUtils.createByteBuffer(verticesSize * VertexData.stride);				
		FloatBuffer verticesFloatBuffer = verticesByteBuffer.asFloatBuffer();
		for (int i = 0; i < verticesSize; i++) 
		{
			// Add position, color and texture floats to the buffer
			verticesFloatBuffer.put(vertices.get(i).getElements());
		}
		verticesFloatBuffer.flip();

		IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indicesSize);
		indicesBuffer.put(indices);
		indicesBuffer.flip();
		
		// Create a new Vertex Array Object in memory and select it (bind)
		vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);
		
		// Create a new Vertex Buffer Object in memory and select it (bind)
		vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, verticesFloatBuffer, GL_STREAM_DRAW);
		
		glVertexAttribPointer(Shader.positionAttrib, VertexData.positionElementCount, GL_FLOAT, false, VertexData.stride, VertexData.positionByteOffset);
		glVertexAttribPointer(Shader.normalAttrib,   VertexData.normalElementCount,   GL_FLOAT, false, VertexData.stride, VertexData.normalByteOffset);
		glVertexAttribPointer(Shader.textureAttrib1, VertexData.texture1ElementCount, GL_FLOAT, false, VertexData.stride, VertexData.texture1ByteOffset);
		glVertexAttribPointer(Shader.textureAttrib2, VertexData.texture2ElementCount, GL_FLOAT, false, VertexData.stride, VertexData.texture2ByteOffset);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
		
		// Create a new VBO for the indices and select it (bind) - INDICES
		vboIndicesId = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIndicesId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		
		Utils.exitOnGLError("Creating mesh: " + meshName);
	}
	
	
	void render() 
	{
		glBindVertexArray(vaoId);
		
		glEnableVertexAttribArray(Shader.positionAttrib);
		glEnableVertexAttribArray(Shader.normalAttrib);
		glEnableVertexAttribArray(Shader.textureAttrib1);
		glEnableVertexAttribArray(Shader.textureAttrib2);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIndicesId);
		
		glDrawElements(GL_TRIANGLES, indicesSize, GL_UNSIGNED_INT, 0);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		
		glDisableVertexAttribArray(Shader.positionAttrib);
		glDisableVertexAttribArray(Shader.normalAttrib);
		glDisableVertexAttribArray(Shader.textureAttrib1);
		glDisableVertexAttribArray(Shader.textureAttrib2);
	
		glBindVertexArray(0);
		
		Utils.exitOnGLError("meshRender");
	}
	
	
	public void printMesh()
	{
		System.out.print("verticesSize: "   + verticesSize); 
		System.out.print(" indicesSize: "    + indicesSize);
		System.out.print(" numberOfUVmaps: " + numberOfUVmaps);
		System.out.print("\n");
		
		for(int i=0; i<verticesSize; i++)
		{
			System.out.print(String.format("%d", i) + ": ");
			
			System.out.print("xyz: " + String.format("%.2f" , vertices.get(i).pos[0]) + " " +
										String.format("%.2f" , vertices.get(i).pos[1]) + " " +
										String.format("%.2f" , vertices.get(i).pos[2]) + "  ");
			
			System.out.print("nxnynz: " + String.format("%.2f" , vertices.get(i).normal[0]) + " " +
										   String.format("%.2f" , vertices.get(i).normal[1]) + " " +
										   String.format("%.2f" , vertices.get(i).normal[2]) + "  ");
			
			System.out.print("uv1: "  + String.format("%.2f" , vertices.get(i).uv1[0]) + " " +
										 String.format("%.2f" , vertices.get(i).uv1[1]) + "  ");
			
			System.out.print("uv2: "  + String.format("%.2f" , vertices.get(i).uv1[0]) + " " +
										 String.format("%.2f" , vertices.get(i).uv1[1]) + "\n");
		}
		
		for(int i=0; i<indices.length/3; i++)
		{			
			System.out.print("triangle " + String.format("%d", i) +  ": " + indices[i+0] + " " + indices[i+1] + " " + indices[i+2] + "\n");
		}
	}
	
	public void dispose()
	{
		
	}
}
