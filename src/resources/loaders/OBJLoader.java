package resources.loaders;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import engine.math.Color;
import engine.math.Vector3;
import engine.models.Face;
import engine.models.Mesh;
import engine.models.Texture;
import engine.models.UVSet;
import engine.models.Vertex;
import engine.models.Materials.ColorTexture;
import engine.models.Materials.ImageTexture;
import engine.models.Materials.UnsupportedDimensionException;
import utils.Log;

public class OBJLoader {
	public static Mesh load(String path) throws IOException, MalformException, IndexOutOfBoundsException, UnsupportedDimensionException {
		Texture texture = ColorTexture.error;
		ArrayList<Face> facelist = new ArrayList<Face>(100);
		ArrayList<Vertex> vertlist = new ArrayList<Vertex>(100);
		ArrayList<UVSet> uvlist = new ArrayList<UVSet>(100);
		
		BufferedReader objfilereader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(path))));
		boolean skip = false;
		int count = 0;
		while(!objfilereader.ready()){}
		
		String line;
		int linenumber=0;
		while ((line = objfilereader.readLine()) != null)  {
			if (count > 2) {
				skip = true;
				count = 0;
			}
			linenumber++;
			if (line.startsWith("v ")) {
				String[] values = line.substring(2).split(" ");
				Vertex newvertex = null;
					if (values.length >= 3) {
						newvertex = new Vertex(new Vector3(
								Float.valueOf(values[0]),
								Float.valueOf(values[1]),
								Float.valueOf(values[2])
							));
//						if (skip) {
							newvertex.position.z = (new Random()).nextFloat()*1;
							skip = false;
//						}
					}
					if (values.length >= 6) {
						newvertex.color = new Color(
								Float.valueOf(values[3]),
								Float.valueOf(values[4]),
								Float.valueOf(values[5])
								);
					} 
					
					if (newvertex != null) {
						vertlist.add(newvertex);						
					} else {
						Log.writeLine("Error parsing vertex in obj file '" + path + "' on line " + linenumber);
						throw new MalformException("Error parsing vertex on line " + linenumber);
					}
			
			} else if (line.startsWith("vt ")) {
				String[] values = line.substring(3).split(" ");
				uvlist.add(new UVSet(
							Float.valueOf(values[0]),
							Float.valueOf(values[1])
						));
					
			} else if (line.startsWith("f ")) {
				String[] components = line.substring(2).split(" ");
				if (components.length != 3) {
					Log.writeLine("Non-triangle face detected. Unsuported face type");
					throw new MalformException("Unsuported face type (" + components.length + ")");
				}
				
				int[] indicies = new int[6]; {
					int i=0;
					for (String component : components) {
						String[] subcomponents = component.split("/"); 
						indicies[i] = Integer.valueOf(subcomponents[0])-1;// Add vertex indexes
						if (subcomponents.length > 1) {
							if (subcomponents[1].length() != 0)
								indicies[i+3] = Integer.valueOf(subcomponents[1])-1; // Add U, V indexes
						}
						i++;
					}
				}
				
				if (indicies[0] > vertlist.size())
					throw new IndexOutOfBoundsException("Pointer to undefined Vertex (" + indicies[3] + ") on line " + line + ".");
				if (indicies[1] > vertlist.size())
					throw new IndexOutOfBoundsException("Pointer to undefined Vertex (" + indicies[4] + ") on line " + line + ".");
				if (indicies[2] > vertlist.size())
					throw new IndexOutOfBoundsException("Pointer to undefined Vertex (" + indicies[5] + ") on line " + line + ".");
				
				Vertex 
				v1 = vertlist.get(indicies[0]),
				v2 = vertlist.get(indicies[1]),
				v3 = vertlist.get(indicies[2]);
				Face newface = new Face(
						indicies[0],
						indicies[1],
						indicies[2]
						);

				newface.normal = calculateFaceNormal(
						v1.position,
						v2.position,
						v3.position
						).normalize();
				
				facelist.add(newface);
				
				if (!uvlist.isEmpty()) {
					if (indicies[3] > uvlist.size())
						throw new IndexOutOfBoundsException("Pointer to undefined UV coordinate (" + indicies[3] + ") on line " + line + ".");
					if (indicies[4] > uvlist.size())
						throw new IndexOutOfBoundsException("Pointer to undefined UV coordinate (" + indicies[4] + ") on line " + line + ".");
					if (indicies[5] > uvlist.size())
						throw new IndexOutOfBoundsException("Pointer to undefined UV coordinate (" + indicies[5] + ") on line " + line + ".");
					
					if (v1.textureCoordinates != UVSet.zero)
						Log.write("Redefinition of UV for vertex " +indicies[0] + ".");
					if (v2.textureCoordinates != UVSet.zero)
						Log.write("Redefinition of UV for vertex " +indicies[1] + ".");
					if (v3.textureCoordinates != UVSet.zero)
						Log.write("Redefinition of UV for vertex " +indicies[2] + ".");
					
					v1.textureCoordinates = uvlist.get(indicies[3]);
					v2.textureCoordinates = uvlist.get(indicies[4]);
					v3.textureCoordinates = uvlist.get(indicies[5]);
				}
			} else if (line.startsWith("tex")) { // Just for testing, not actually part of the spec
				String folder = path.substring(0, path.lastIndexOf("/") + 1);
				texture = new ImageTexture(folder + line.substring(4));
			}
			count++;
		}
		
		objfilereader.close();
		
		// Convert to lists
		Vertex[] vertcies = new Vertex[vertlist.size()];
		for (int i=0; i<vertlist.size(); i++)
			vertcies[i] = vertlist.get(i);
		
		Face[] faces = new Face[facelist.size()];
		for (int i=0; i<facelist.size(); i++)
			faces[i] = facelist.get(i);
		
		//System.out.println("Loaded model with " + vertcies.length + " vertcies and " + faces.length + " faces.");
		return new Mesh(vertcies, faces, texture);
	}
	
	// Reference: https://www.opengl.org/wiki/Calculating_a_Surface_Normal
	private static Vector3 calculateFaceNormal(Vector3 v1, Vector3 v2, Vector3 v3) {
		Vector3 normal = new Vector3(0);
		Vector3 u = Vector3.subtract(v2, v1);
		Vector3 v = Vector3.subtract(v3, v1);
		normal.x = (u.y * v.z) - (u.z * v.y);
		normal.y = (u.z * v.x) - (u.x * v.z);
		normal.z = (u.x * v.y) - (u.y * v.x);
		return normal;
	}
	
	//TOOD: Method to create triangular faces out of list of verts and UVs
}
