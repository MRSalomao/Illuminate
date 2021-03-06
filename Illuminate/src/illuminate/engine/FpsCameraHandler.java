package illuminate.engine;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class FpsCameraHandler
{
	Camera camera;
	
	boolean FPSModeOn = false;
	
	public FpsCameraHandler(Camera camera)
	{
		this.camera = camera;
		
		camera.update();
	}
	
	public void update(float dt)
	{
		while (Keyboard.next()) 
		{
			if (Keyboard.getEventKey() == Keyboard.KEY_SPACE) 
			{
			    if (!Keyboard.getEventKeyState()) 
			    {
			    	FPSModeOn = !FPSModeOn;
			    	
					 Mouse.setGrabbed(FPSModeOn);
			    }
			}
		}
		
		if (FPSModeOn)
		{
			if (Keyboard.isKeyDown(Keyboard.KEY_W))
			{
				Vector3f direction = camera.getDirection();
				Vector3f.add(camera.position, (Vector3f) direction.scale(dt*+0.01f), camera.position);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_S))
			{
				Vector3f direction = camera.getDirection();
				Vector3f.add(camera.position, (Vector3f) direction.scale(dt*-0.01f), camera.position);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_A))
			{
				Vector3f direction = camera.getDirection();
				Vector3f.cross(direction, new Vector3f(0,0,1), direction);
				direction.normalise();
				Vector3f.add(camera.position, (Vector3f) direction.scale(dt*-0.01f), camera.position);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_D))
			{
				Vector3f direction = camera.getDirection();
				Vector3f.cross(direction, new Vector3f(0,0,1), direction);
				direction.normalise();
				Vector3f.add(camera.position, (Vector3f) direction.scale(dt*+0.01f), camera.position);
			}
			
			if (Keyboard.isKeyDown(Keyboard.KEY_E))
			{
				camera.position.z += dt*0.01f;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_Q))
			{
				camera.position.z -= dt*0.01f;
			}
			
			if (Keyboard.isKeyDown(Keyboard.KEY_UP))
			{
				camera.rotation.x += dt*0.001f;
				
				if (camera.rotation.x > Utils.PI2-0.01)
				{
					camera.rotation.x = (float) Utils.PI2-0.01f;
				}
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
			{
				camera.rotation.x -= dt*0.001f;
				
				if (camera.rotation.x < -Utils.PI2+0.01)
				{
					camera.rotation.x = (float) -Utils.PI2+0.01f;
				}
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
			{
				camera.rotation.z += dt*0.001f;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
			{
				camera.rotation.z -= dt*0.001f;
			}
			
			camera.rotation.z -= Mouse.getDX()*0.0015;
			camera.rotation.x += Mouse.getDY()*0.0015;
			
			if (camera.rotation.x < -Utils.PI2+0.01)
			{
				camera.rotation.x = (float) -Utils.PI2+0.01f;
			}
			if (camera.rotation.x > Utils.PI2-0.01)
			{
				camera.rotation.x = (float) Utils.PI2-0.01f;
			}
			
			camera.update();
		}
	}

}
