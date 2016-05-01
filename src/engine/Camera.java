package engine;


import utils.Matrix;
import utils.Vector3;

public class Camera {
	private Vector3 position, target;
	
	public Matrix viewMatrix;
	
	public Camera(Vector3 position, Vector3 target) {
		this.position = position;
		this.target = target;
		
		viewMatrix = Matrix.lookAtLH(position, target, Vector3.up);
	}
	
	public Vector3 getPosition() {
		return position.Clone();
	}
	
	public void setPosition(Vector3 pos) {
		position = pos;
		viewMatrix = Matrix.lookAtLH(position, target, Vector3.up);
	}
	
	public void setTarget(Vector3 pos) {
		target = pos;
		viewMatrix = Matrix.lookAtLH(position, target, Vector3.up);
	}
	
	public Camera Clone() {
		return new Camera(position, target);
	}
}