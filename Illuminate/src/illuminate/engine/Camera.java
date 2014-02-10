package illuminate.engine;

import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Camera 
{
	Matrix4f projectionMatrix;
	Matrix4f viewMatrix;
	
	FloatBuffer matrix44Buffer;
	
	public Vector3f position, rotation;
	
	float fieldOfView, aspectRatio, near_plane, far_plane;

	public static Camera activeCamera;
	
	public Camera(Vector3f cameraPos)
	{
		this(cameraPos, new Vector3f(0,0,0), 60f, (float) App.singleton.canvasWidth / (float) App.singleton.canvasHeight, 0.1f, 100f);
	}
	
	public Camera(Vector3f cameraPos, Vector3f cameraAngle)
	{
		this(cameraPos, cameraAngle, 60f, (float) App.singleton.canvasWidth / (float) App.singleton.canvasHeight, 0.1f, 100f);
	}
	
	public Camera(Vector3f cameraPos, Vector3f cameraAngle, float aspectRatio, float near_plane, float far_plane)
	{
		this(cameraPos, cameraAngle, 60f, (float) App.singleton.canvasWidth / (float) App.singleton.canvasHeight, near_plane, far_plane);
	}
	
	public Camera(Vector3f cameraPos, Vector3f cameraAngle, float fieldOfView, float aspectRatio, float near_plane, float far_plane)
	{
		this.position   = cameraPos;
		this.rotation = cameraAngle;
		
		this.fieldOfView = fieldOfView; 
		this.aspectRatio = aspectRatio;
		this.near_plane  = near_plane;
		this.far_plane   = far_plane;

		
		// Setup projection matrix
		projectionMatrix = new Matrix4f();
		setProjection();
		
		// Setup view matrix
		viewMatrix = new Matrix4f();
		
		// Create a FloatBuffer with the proper size to store our matrices later
		matrix44Buffer = BufferUtils.createFloatBuffer(16);
	}
	
	public void setActive()
	{
		activeCamera = this;
	}
	
	public void update()
	{
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		Matrix4f.setIdentity(viewMatrix);
		
		Matrix4f.rotate(-rotation.x - (float)Utils.PI/2, new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
		Matrix4f.rotate(-rotation.y                    , new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Matrix4f.rotate(-rotation.z + (float)Utils.PI/2, new Vector3f(0, 0, 1), viewMatrix, viewMatrix);
		
		Matrix4f.translate(new Vector3f(-position.x, -position.y, -position.z), viewMatrix, viewMatrix);
	}
	
	
	private void setProjection()
	{
		float y_scale = Utils.coTangent(Utils.degreesToRadians(fieldOfView / 2f));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = far_plane - near_plane;
		
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((far_plane + near_plane) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * near_plane * far_plane) / frustum_length);
        projectionMatrix.m33 = 0;
	}
	
	 public void lookAt(Vector3f eye, Vector3f center, Vector3f up) 
	 {
	 	Vector3f f = new Vector3f();
	 	Vector3f.sub(center, eye, f);
	 	f.normalise();
	 	
	 	Vector3f s = new Vector3f();
	 	Vector3f.cross(f, up, s);
	    s.normalise();
	    
	    Vector3f u = new Vector3f();
	    Vector3f.cross(s, f, u);

	    Matrix4f result = new Matrix4f();
	    
	    result.m00 =  s.x;
	    result.m01 =  u.x;
	    result.m02 = -f.x;
	    result.m03 = 0.0f;
	    
	    result.m10 =  s.y;
	    result.m11 =  u.y;
	    result.m12 = -f.y;
	    result.m13 = 0.0f;
	    
	    result.m20 =  s.z;
	    result.m21 =  u.z;
	    result.m22 = -f.z;
	    result.m23 = 0.0f;
	    
	    result.m30 = 0.0f;
	    result.m31 = 0.0f;
	    result.m32 = 0.0f;
	    result.m33 = 1.0f;
	    
	    result.translate(new Vector3f(-eye.x,-eye.y,-eye.z));
	    viewMatrix = result;
	}
	 
	 public void lookAtDirection(Vector3f eye, Vector3f dir, Vector3f up) 
	 {
	 	Vector3f s = new Vector3f();
	 	Vector3f.cross(dir, up, s);
	    s.normalise();
	    
	    Vector3f u = new Vector3f();
	    Vector3f.cross(s, dir, u);

	    Matrix4f result = new Matrix4f();
	    
	    result.m00 =  s.x;
	    result.m01 =  u.x;
	    result.m02 = -dir.x;
	    result.m03 = 0.0f;
	    
	    result.m10 =  s.y;
	    result.m11 =  u.y;
	    result.m12 = -dir.y;
	    result.m13 = 0.0f;
	    
	    result.m20 =  s.z;
	    result.m21 =  u.z;
	    result.m22 = -dir.z;
	    result.m23 = 0.0f;
	    
	    result.m30 = 0.0f;
	    result.m31 = 0.0f;
	    result.m32 = 0.0f;
	    result.m33 = 1.0f;
	    
	    result.translate(new Vector3f(-eye.x,-eye.y,-eye.z));
	    viewMatrix = result;
	}
	 
	 public Vector3f getDirection()
	 {
		 float cosY = (float) Math.cos(rotation.x);
		 
//		 System.out.print(new Vector3f( (float) Math.cos(rotation.z)*cosY, (float) Math.sin(rotation.z)*cosY, (float) Math.sin(rotation.x) ));
		 
		 return new Vector3f( (float) Math.cos(rotation.z)*cosY, (float) Math.sin(rotation.z)*cosY, (float) Math.sin(rotation.x) );
	 }
	 
	 public void clearScreen()
	 {
		 glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	 }
	 
	 public void clearScreenColor(float r, float g, float b, float a)
	 {
			glClearColor(r, g, b, a);
	 }
	 
	 public void clearScreenColor(float gray)
	 {
			glClearColor(gray, gray, gray, 1f);
	 }
}
