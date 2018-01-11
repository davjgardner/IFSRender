package ifs;

import static ifs.IFSDescriptor.X;
import static ifs.IFSDescriptor.Y;
import static ifs.IFSDescriptor.R;
import static ifs.IFSDescriptor.G;
import static ifs.IFSDescriptor.B;
import static ifs.IFSDescriptor.FREQ;

import utils.GifSequenceWriter;
import utils.math.geom.*;
import utils.Utils;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
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
import java.util.List;
import java.util.Map;
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
	
	public void save(String fileName, BufferedImage img) {
		try {
			ImageIO.write(img, "PNG", new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveDefault(BufferedImage img) {
		DateFormat df = new SimpleDateFormat("yyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		String s = "res/" + descriptor.name + '_' + df.format(date) + ".png";
		save(s, img);
	}
	
	public void renderSequence(Map<String, double[]> vars, int iterations, int frames, int fps, String fileName)
			throws IOException {
		BufferedImage[] imgs = new BufferedImage[frames];
		for (int i=0; i < frames; i++) {
			double iter = i;
			vars.forEach((s, range) -> {
				// lerp
				double val = range[0] + iter * (range[1] - range[0]) / frames;
				System.out.println("val = " + val);
				descriptor.globals.put(s, val);
			});
			imgs[i] = render(iterations);
		}
		ImageOutputStream out = new FileImageOutputStream(new File(fileName));
		
		GifSequenceWriter writer = new GifSequenceWriter(out, imgs[0].getType(), 1000 / fps, false);
		for (int i=0; i < frames; i++) {
			writer.writeToSequence(imgs[i]);
		}
		
		writer.close();
		out.close();
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
					saveDefault(img);
			}
		});
		int w = (int) ((descriptor.xmax - descriptor.xmin) * pixelScale);
		int h = (int) ((descriptor.ymax - descriptor.ymin) * pixelScale);
		f.setSize(w, h);
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	
	public static void main(String[] args) {
		String file;
		if (args.length > 1) {
			file = args[1];
		} else {
			System.out.println("Usage: java ifs.Renderer [IFS descriptor file]");
			file = "ifs/random/src/4.ifs";
		}
		int pixelScale = 400;
		try {
			List<String> source = Utils.readFile(file);
			IFSDescriptor d = new IFSDescriptor(source);
			Renderer renderer = new Renderer(d, pixelScale);
			BufferedImage img = renderer.render(10_000_000);
			renderer.display(img);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
