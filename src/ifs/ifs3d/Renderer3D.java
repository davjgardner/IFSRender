package ifs.ifs3d;

import utils.Color3f;
import utils.Utils;
import utils.math.geom.Vector3d;
import utils.math.geom.Vector3i;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static ifs.ifs3d.IFSDescriptor3D.X;
import static ifs.ifs3d.IFSDescriptor3D.Y;
import static ifs.ifs3d.IFSDescriptor3D.Z;
import static ifs.ifs3d.IFSDescriptor3D.R;
import static ifs.ifs3d.IFSDescriptor3D.G;
import static ifs.ifs3d.IFSDescriptor3D.B;
import static ifs.ifs3d.IFSDescriptor3D.FREQ;

public class Renderer3D {
	
	IFSDescriptor3D descriptor;
	
	double voxelScale;
	
	Random rand = new Random();
	
	Renderer3D(IFSDescriptor3D descriptor, double voxelScale) {
		this.descriptor = descriptor;
		this.voxelScale = voxelScale;
	}
	
	/**
	 * Plots the fractal
	 * @param iterations number of iterations to run
	 * @param iterFloor first iteration on which data is to be recorded
	 * @return the plotted fractal in a HashMap
	 */
	Map<Vector3i, double[]> plot(int iterations, int iterFloor) {
		System.out.println("Plotting...");
		Vector3i size = getSize();
		Map<Vector3i, double[]> histogram = new HashMap<>();
		// x, y, z, r, g, b
		double[] p = new double[6];
		p[X] = descriptor.xmin + rand.nextDouble() * (descriptor.xmax - descriptor.xmin);
		p[Y] = descriptor.ymin + rand.nextDouble() * (descriptor.ymax - descriptor.ymin);
		p[Z] = descriptor.zmin + rand.nextDouble() * (descriptor.zmax - descriptor.zmin);
		for (int i = 0; i < iterations; i++) {
			try {
				p = descriptor.runFunc(p, i);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
			if ((i+1) % 1_000_000 == 0) System.out.println(i + 1);
			if (i > iterFloor) {
				Vector3i pos = scalePoint(p);
				if (pos.x < size.x && pos.x >= 0 &&
						pos.y < size.y && pos.y >= 0 &&
						pos.z < size.z && pos.z >= 0) {
					double[] prev = histogram.get(pos);
					if (prev == null) prev = new double[6];
					prev[R] = (prev[R] + p[R]) / 2;
					prev[G] = (prev[G] + p[G]) / 2;
					prev[B] = (prev[B] + p[B]) / 2;
					prev[FREQ]++;
					histogram.put(pos, prev);
				}
			}
		}
		return histogram;
	}
	
	/**
	 * Do post-processing to the plotted fractal
	 * @param histogram plotting data
	 * @return processed data
	 */
	Map<Vector3i, double[]> process(Map<Vector3i, double[]> histogram) {
		System.out.println("Doing alpha scales...");
		double maxFreq = 0;
		for (double[] att : histogram.values()) {
			if (att[FREQ] > maxFreq) maxFreq = att[FREQ];
		}
		double logMaxFreq = Math.log(maxFreq);
		histogram.values().forEach(att -> {
			// we know FREQ is not zero because this value exists in the table
			double alpha = Math.log(att[FREQ]) / logMaxFreq;
			att[R] *= alpha;
			att[G] *= alpha;
			att[B] *= alpha;
		});
		return histogram;
	}
	
	/**
	 * Renders a 2d image of the scene using raycasting
	 * @param voxelData plotted fractal
	 * @param width image width
	 * @param height image height
	 * @param fov image field of view
	 * @param camera camera position
	 * @return the scene rendered to a BufferedImage
	 */
	BufferedImage render2d(Map<Vector3i, double[]> voxelData, BoundingBox sceneBox, int width, int height, double fov, Vector3d camera) {
		System.out.println("Tracing some rays...");
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		/*BoundingBox sceneBox = new BoundingBox(descriptor.xmin, descriptor.xmax, descriptor.ymin,
				descriptor.ymax, descriptor.zmin, descriptor.zmax);*/
		for (int x = 0; x < width; x++) {
			OUTER:
			for (int y = 0; y < height; y++) {
				Ray r = cast(x, y, width, height, fov, camera);
				double t = 0;
				// move the origin of the ray up to the box to avoid checking a bunch of dead space
				// this is ok as long as rays can't start inside the box
				if ((t = sceneBox.collides(r)) > 0) {
					r.origin.add(r.direction.mul(t - 0.1));
//					System.out.println("This ray hits the box!");
				} else continue;
				while (sceneBox.collides(r) > 0) {
					Vector3i pos = r.origin.toVector3i();
					double[] att = voxelData.get(pos);
					if (att != null) {
						// for now, ignore lighting
						img.setRGB(x, y, new Color3f((float) att[R], (float) att[G], (float) att[B]).getRGB());
						// hit something, so go on to next pixel
						continue OUTER;
					}
					r.origin = r.origin.add(r.direction.mul(0.5));
				}
			}
		}
		System.out.println("Done tracing them rays!");
		return img;
	}
	
	BufferedImage[] renderFaceMaps(Map<Vector3i, double[]> voxelData) {
		Vector3i size = getSize();
		BufferedImage xymap = new BufferedImage(size.x, size.y, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < size.x; x ++) {
			for (int y = 0; y < size.y; y++) {
				for (int z = 0; z < size.z; z++) {
					double[] att = voxelData.get(new Vector3i(x, y, z));
					if (att != null) {
						xymap.setRGB(x, y, new Color3f((float) att[R], (float) att[G], (float) att[B]).getRGB());
						break;
					}
				}
			}
		}
		BufferedImage xzmap = new BufferedImage(size.x, size.z, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < size.x; x ++) {
			for (int z = 0; z < size.z; z++) {
				for (int y = 0; y < size.y; y++) {
					double[] att = voxelData.get(new Vector3i(x, y, z));
					if (att != null) {
						xzmap.setRGB(x, z, new Color3f((float) att[R], (float) att[G], (float) att[B]).getRGB());
						break;
					}
				}
			}
		}
		BufferedImage yzmap = new BufferedImage(size.y, size.z, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < size.y; y++) {
			for (int z = 0; z < size.z; z++) {
				for (int x = 0; x < size.x; x ++) {
					double[] att = voxelData.get(new Vector3i(x, y, z));
					if (att != null) {
						yzmap.setRGB(y, z, new Color3f((float) att[R], (float) att[G], (float) att[B]).getRGB());
						break;
					}
				}
			}
		}
		return new BufferedImage[] {xymap, xzmap, yzmap};
	}
	
	/**
	 *
	 * @param sx screen x coord
	 * @param sy screen y cord
	 * @param width image width
	 * @param height image height
	 * @param fov field of view angle
	 * @param camera just position for now, add rotation later
	 * @return a ray for this pixel
	 */
	private Ray cast(double sx, double sy, int width, int height, double fov, Vector3d camera) {
		sy = height - sy - 1;
		sx -= 0.5;
		sy -= 0.5;
		double x = sx/(width) * 2 - 1;
		double y = sy/(height) * 2 - 1;
		double z = -1 / Math.tan(fov / 2);
		Vector3d dir = new Vector3d(x, y, z).normalize();
		return new Ray(camera, dir);
	}
	
	private class Ray {
		Vector3d origin, direction;
		Ray(Vector3d origin, Vector3d direction) {
			this.origin = new Vector3d(origin);
			this.direction = direction.normalize();
		}
	}
	
	private static class BoundingBox {
		double xmin, xmax, ymin, ymax, zmin, zmax;
		
		BoundingBox(double xmin, double xmax, double ymin, double ymax, double zmin, double zmax) {
			this.xmin = xmin;
			this.xmax = xmax;
			this.ymin = ymin;
			this.ymax = ymax;
			this.zmin = zmin;
			this.zmax = zmax;
		}
		
		double collides(Ray ray) {
			Vector3d p1 = new Vector3d(xmin, ymin, zmin);
			Vector3d p2 = new Vector3d(xmax, ymax, zmax);
			// from: https://tavianator.com/fast-branchless-raybounding-box-intersections/
			double tx1 = (p1.x - ray.origin.x) / ray.direction.x;
			double tx2 = (p2.x - ray.origin.x) / ray.direction.x;
			
			double tmin = Math.min(tx1, tx2);
			double tmax = Math.max(tx1, tx2);
			
			double ty1 = (p1.y - ray.origin.y) / ray.direction.y;
			double ty2 = (p2.y - ray.origin.y) / ray.direction.y;
			
			tmin = Math.max(tmin, Math.min(ty1, ty2));
			tmax = Math.min(tmax, Math.max(ty1, ty2));
			
			double tz1 = (p1.z - ray.origin.z) / ray.direction.z;
			double tz2 = (p2.z - ray.origin.z) / ray.direction.z;
			
			tmin = Math.max(tmin, Math.min(tz1, tz2));
			tmax = Math.min(tmax, Math.max(tz1, tz2));
			
			if (tmax < tmin) return -1;
			
			if (tmin < 0.0f) return tmax;
			else return tmin;
		}
	}
	
	private Vector3i getSize() {
		return new Vector3i((int) ((descriptor.xmax - descriptor.xmin) * voxelScale),
				(int) ((descriptor.ymax - descriptor.ymin) * voxelScale),
				(int) ((descriptor.zmax - descriptor.zmin) * voxelScale));
	}
	
	private Vector3i scalePoint(double[] p) {
		int x = (int) ((p[X] - descriptor.xmin) * voxelScale);
		int y = (int) ((p[Y] - descriptor.ymin) * voxelScale);
		int z = (int) ((p[Z] - descriptor.zmin) * voxelScale);
		return new Vector3i(x, y, z);
	}
	
	void display(BufferedImage img) {
		JFrame f = new JFrame("IFS: " + descriptor.name) {
			public void paint(Graphics g) {
				g.drawImage(img, 0, 0, null);
			}
		};
		f.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == 's')
					Utils.saveImageTimestamped(img, "res/", descriptor.name);
			}
		});
		f.setSize(img.getWidth(), img.getHeight());
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	
	public static void main(String[] args) throws Exception {
		String fileName = "ifs/serp3d.ifs";
		IFSDescriptor3D d = new IFSDescriptor3D(Utils.readFile(fileName));
		Renderer3D r = new Renderer3D(d, 200);
		Map<Vector3i, double[]> voxelData = r.process(r.plot(4_000_000, 20));
		/*BufferedImage[] faceMaps = r.renderFaceMaps(voxelData);
		
		JFrame f = new JFrame("IFS: " + d.name) {
			public void paint(Graphics g) {
				g.drawImage(faceMaps[0],    0, 75, 800, 800, null);
				g.drawImage(faceMaps[1],  800, 75, 800, 800, null);
				g.drawImage(faceMaps[2], 1600, 75, 800, 800, null);
			}
		};
		f.setSize(2400, 875);
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);*/
		r.display(r.render2d(voxelData, new BoundingBox(0, 100, 0, 100, 0, 100),1000, 1000, 60, new Vector3d(50, 0, -20)));
	}
	
}
