package engine;

import java.io.FileWriter;
import java.io.IOException;

import engine.math.Matrix;
import engine.math.Vector3;

public class Mesh {
	private Vertex[] vertcies;
	private Face[] faces;
	public Vector3 
	position = Vector3.zero.Clone(),
	rotation = new Vector3(0.00001f, 0.00001f, 0.00001f),
	scale = new Vector3(1);
	public Texture texture;
	
	public Matrix worldmatrix;
	
	public Mesh(Vertex[] verticies, Face[] faces, Texture tex) {
		worldmatrix = Matrix.RotationYawPitchRoll(rotation.y, rotation.x, rotation.z).multiply(Matrix.translation(position.x, position.y, position.z));
		this.vertcies = verticies;
		this.faces = faces;
		this.texture = tex;

	}
	
	public void render(Renderer renderer, Camera cam) {
		Matrix worldview = Matrix.multiply(worldmatrix, cam.viewMatrix);
		Matrix transformmatrix = Matrix.multiply(worldview, renderer.projectionMatrix);
		
		Vertex[] transformedverts = getProjectedVertcies(transformmatrix, renderer.width, renderer.height);
		
		for (Face face : faces) {
			Vector3 transformednormal = transformmatrix.transformNormal(face.normal);
			if (transformednormal.z >= 0)
				continue;
			
			
			renderer.drawTriangle(
					transformedverts[face.vertex1],
					transformedverts[face.vertex2],
					transformedverts[face.vertex3],
					texture
					);
		}
	}
	
	private Vertex[] getProjectedVertcies(Matrix m, int width, int height) {
		Vertex[] transformedverts = new Vertex[vertcies.length];
		Matrix a = Matrix.scaling(width, -height, 1);
		Matrix b = Matrix.translation(width / 2, height / 2, 1);
		m = Matrix.multiply(m, a).multiply(b);
		
		for (int i=0; i<vertcies.length; i++) {
			Vertex vert = vertcies[i];
			Vector3 point2d = m.transformCoordinates(vert.position);
			
			transformedverts[i] = new Vertex(
					point2d,
					vert.textureCoordinates
					);
		}
		return transformedverts;
	}
	
	public Vector3 getPosition() {
		return position.Clone();
	}
	
	public Vector3 getRotation() {
		return rotation.Clone();
	}
	
	public Vector3 getScale() {
		return scale.Clone();
	}
	
	public void setPosition(Vector3 pos) {
		position = pos;
		worldmatrix = Matrix.RotationYawPitchRoll(rotation.y, rotation.x, rotation.z)
				.multiply(Matrix.translation(position.x, position.y, position.z))
				.multiply(Matrix.scaling(scale.x, scale.y, scale.z));
	}
	
	public void setRotation(Vector3 rot) {
		rotation = rot;
		worldmatrix = Matrix.RotationYawPitchRoll(rotation.y, rotation.x, rotation.z)
				.multiply(Matrix.translation(position.x, position.y, position.z))
				.multiply(Matrix.scaling(scale.x, scale.y, scale.z));
	}
	
	public void setScale(Vector3 scale) {
		this.scale = scale;
		worldmatrix = Matrix.RotationYawPitchRoll(rotation.y, rotation.x, rotation.z)
				.multiply(Matrix.translation(position.x, position.y, position.z))
				.multiply(Matrix.scaling(scale.x, scale.y, scale.z));
	}
	
	public void writeOBJ(FileWriter file) throws IOException {
		for (Vertex v : vertcies) {
			String output = "v " + v.position.x + " " + v.position.y + " " + v.position.z +"\n";
			file.write(output);
		}
		for (Face f : faces) {
			String out = "f " + (f.vertex1+1) + " " + (f.vertex2+1) + " " + (f.vertex3+1) + "\n";
			file.write(out);
		}
		for (Face f : faces) {
			String output = "n " + (f.normal.x + 1) + " " + (f.normal.y + 1) + " " + (f.normal.z)+"\n";
			file.write(output);
		}
		file.flush();
		file.close();
	}
		
}