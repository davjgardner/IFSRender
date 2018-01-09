package ifs;

import static ifs.IFSDescriptor.X;
import static ifs.IFSDescriptor.Y;
import static ifs.IFSDescriptor.R;
import static ifs.IFSDescriptor.G;
import static ifs.IFSDescriptor.B;
import static ifs.IFSDescriptor.FREQ;

import utils.math.geom.*;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

/**
 * Provides methods for rendering an image from an ifs.IFSDescriptor
 */
public class Renderer {
	
	/*final int FREQ = 3, R = 0, G = 1, B = 2;
	final int X = 4, Y = 5;*/
	
	private IFSDescriptor descriptor;
	private double pixelScale;
	
	Random rand;
	
	public Renderer(IFSDescriptor descriptor, double pixelScale) {
		this.descriptor = descriptor;
		this.pixelScale = pixelScale;
		rand = new Random();
	}
	
	public Renderer(IFSDescriptor descriptor, double pixelScale, Random rand) {
		this.descriptor = descriptor;
		this.pixelScale = pixelScale;
		this.rand = rand;
	}
	
	public BufferedImage render(int iterations, Vector3f bgColor) {
		return drawHistogram(plot(iterations, 20), bgColor);
	}
	
	public BufferedImage render(int iterations) {
		return render(iterations, new Vector3f());
	}
	
	public double[][][] plot(int iterations, int iterFloor) {
		int w = getWidth(), h = getHeight();
		double[][][] histogram = new double[w][h][4];
		double[] p = new double[5];
		p[X] = descriptor.xmin + rand.nextDouble() * (descriptor.xmax - descriptor.xmin);
		p[Y] = descriptor.ymin + rand.nextDouble() * (descriptor.ymax - descriptor.ymin);
		for (int i = 0; i < iterations; i++) {
			try {
				p = descriptor.runFunc(p, i);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
			if (i % 1_000_000 == 0) System.out.println(i);
			if (i > iterFloor) {
				Vector2i pos = scalePoint(p);
				if (pos.x < w && pos.x >= 0 && pos.y < h && pos.y >= 0) {
					histogram[pos.x][pos.y][FREQ]++;
					histogram[pos.x][pos.y][R] = (histogram[pos.x][pos.y][R] + p[R])/2;
					histogram[pos.x][pos.y][G] = (histogram[pos.x][pos.y][G] + p[G])/2;
					histogram[pos.x][pos.y][B] = (histogram[pos.x][pos.y][B] + p[B])/2;
				}
			}
		}
		return histogram;
	}
	
	public BufferedImage drawHistogram(double[][][] histogram, Vector3f bgColor) {
		int w = getWidth();
		int h = getHeight();
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);;
		
		// find max frequency
		double maxFreq = 0;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				if (histogram[i][j][FREQ] > maxFreq) maxFreq = histogram[i][j][FREQ];
			}
		}
		double logMaxFreq = Math.log(maxFreq);
		
		// attenuate and build image
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				// calculate brightness
				double alpha = (histogram[i][j][FREQ] == 0)? 0 : Math.log(histogram[i][j][FREQ]) / logMaxFreq;
				float r = (float) (bgColor.x - (bgColor.x - histogram[i][j][R]) * alpha);
				float g = (float) (bgColor.y - (bgColor.y - histogram[i][j][G]) * alpha);
				float b = (float) (bgColor.z - (bgColor.z - histogram[i][j][B]) * alpha);
				img.setRGB(i, h - 1 - j, new Color(r, g, b).getRGB());
			}
		}
		return img;
	}
	
	public int getWidth() {
		return (int) (pixelScale * (descriptor.xmax - descriptor.xmin));
	}
	
	public int getHeight() {
		return (int) (pixelScale * (descriptor.ymax - descriptor.ymin));
	}
	
	private Vector2i scalePoint(double[] p) {
		int x = (int) ((p[IFSDescriptor.X] - descriptor.xmin) * pixelScale);
		int y = (int) ((p[Y] - descriptor.ymin) * pixelScale);
		return new Vector2i(x, y);
	}
	
	public static void main(String[] args) {
		String file;
		if (args.length > 1) {
			file = args[1];
		} else {
			System.out.println("Usage: java ifs.Renderer [IFS descriptor file]");
			file = "ifs/2020.ifs";
		}
		int pixelScale = 350;
		try {
			List<String> source = Utils.readFile(file);
			IFSDescriptor d = new IFSDescriptor(source);
			Renderer renderer = new Renderer(d, pixelScale);
			BufferedImage img = renderer.render(10_000_000);
			JFrame f = new JFrame("IFS: " + file) {
				public void paint(Graphics g) {
					g.drawImage(img, 0, 0, null);
				}
			};
			int w = (int) (d.xmax - d.xmin) * pixelScale;
			int h = (int) (d.ymax - d.ymin) * pixelScale;
			f.setSize(w, h);
			f.setLocationRelativeTo(null);
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}