package ui;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import engine.Camera;
import engine.ImageTexture;
import engine.Mesh;
import engine.Renderer;
import engine.math.Vector3;
import resources.loaders.OBJLoader;
import utils.FrameCounter;
import utils.Log;


public class Viewport extends Canvas implements MouseWheelListener {
	private static final long serialVersionUID = -869334646261735255L;

	private BufferStrategy buffer;

	private FrameCounter fc = new FrameCounter();

	private Renderer renderer;
	private Camera camera = new Camera(new Vector3(0,3,5), new Vector3(0,1,0));


	public Viewport() {
		setIgnoreRepaint(true);
		setSize(Window.width, Window.height);

		addMouseWheelListener(this);
	}

	public void start() {
		createBufferStrategy(2);
		buffer = getBufferStrategy();
		
		try {
			Log.init();
		} catch (IOException e) {
			System.err.println("Failed to open log file for writing.");
			e.printStackTrace();
		}

		renderer = new Renderer();
		try {
			String localdir =  System.getProperty("user.dir").replaceAll("\\\\", "/");
			/*
			*/
			Mesh floor = OBJLoader.load(localdir + "/res/floor.obj");
			ImageTexture floortex = (ImageTexture)floor.texture;
			floortex.repeatX = 10;
			floortex.repeatY = 10;
			renderer.addMesh(floor);
			Mesh cube = OBJLoader.load(localdir + "/res/glados.obj");
			cube.setPosition(new Vector3(0,1,0));
			renderer.addMesh(cube);
			
		} catch (Exception e) {
			System.out.println("An error accured loading resources.");
			e.printStackTrace();
			System.exit(1);
		}
		
		Graphics2D graphics = (Graphics2D)buffer.getDrawGraphics();
		while(true) {
			tick();
			graphics.clearRect(0, 0, getWidth(), getHeight());
			renderer.clearBuffer();
			renderer.clearDepthBuffer();
			renderer.render(camera);
			renderer.swapBuffers();
			graphics.drawImage(renderer.output, 0, 0, getWidth(), getHeight(), this);

			graphics.setColor(Color.green);
			graphics.drawString(String.valueOf(fc.fps) + "FPS", 5, 15);

			if(!buffer.contentsLost())
				buffer.show();

			fc.newFrame();
		} 
	}

	float sincos = 0;
	float stepsize = (float)Math.PI*2/360f;
	float distance = 5;
	private void tick() {
		/*
		Mesh m = renderer.meshes.get(0);
		m.setRotation(m.getRotation().add(0f, -0.01f, 0f));
		*/
		Vector3 pos = camera.getPosition();
		pos.x = (float)Math.cos(sincos) * distance;
		pos.z = (float)Math.sin(sincos) * distance;
		camera.setPosition(pos);
		sincos += stepsize;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWheelRotation() < 0)
			distance -= 0.1f;
		else
			distance += 0.1f;
	}
}
