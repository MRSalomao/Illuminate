package illuminate;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Node 
{
	Matrix4f modelMatrix;
	Vector3f position;
	Vector3f rotation;
	Vector3f scale;
	
	FloatBuffer matrix44Buffer;
	
	Mesh mesh;
//	Shader shader;
	Texture diffuse, lightmap;
	
	public Node(Mesh mesh, Texture diffuse)
	{
		this.mesh = mesh;
//		this.shader = shader;
		this.diffuse = diffuse;
		
		
		position = new Vector3f(0, 0, 0);
		rotation = new Vector3f(0, 0, 0);
		scale = new Vector3f(1, 1, 1);

		// Setup node matrix
		modelMatrix = new Matrix4f();
		
		// Create a FloatBuffer with the proper size to store our matrices later
		matrix44Buffer = BufferUtils.createFloatBuffer(16);
	}
	
	public void setLightmap(Texture lightmap)
	{
		this.lightmap = lightmap; 
	}
	
	
	public void render()
	{
		Matrix4f.setIdentity(modelMatrix);
		
		// Scale, translate and rotate model
		Matrix4f.translate(position, modelMatrix, modelMatrix);
		Matrix4f.scale(scale, modelMatrix, modelMatrix);
		Matrix4f.rotate(rotation.z, new Vector3f(0, 0, 1), modelMatrix, modelMatrix);
		Matrix4f.rotate(rotation.y, new Vector3f(0, 1, 0), modelMatrix, modelMatrix);
		Matrix4f.rotate(rotation.x, new Vector3f(1, 0, 0), modelMatrix, modelMatrix);
		
		// Upload modelMatrix
		modelMatrix.store(matrix44Buffer); matrix44Buffer.flip();
		glUniformMatrix4(Shader.activeProgram.modelMatrixLocation, false, matrix44Buffer);

		diffuse.setActive();
		if (lightmap != null) lightmap.setActive();
		
		mesh.render();
	}
	
	public void dispose()
	{
		
	}
}
