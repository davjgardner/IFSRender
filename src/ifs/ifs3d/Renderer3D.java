package ifs.ifs3d;

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
	
	Map<Vector3i, double[]> plot(int iterations, int iterFloor) {
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
			if (i % 1_000_000 == 0) System.out.println(i);
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
	
	Map<Vector3i, double[]> process(Map<Vector3i, double[]> histogram) {
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
	
	BufferedImage render2d(Map<Vector3i, double[]> voxelData) {
		return null;
	}
	
	Vector3i getSize() {
		return new Vector3i((int) ((descriptor.xmax - descriptor.xmin) * voxelScale),
				(int) ((descriptor.ymax - descriptor.ymin) * voxelScale),
				(int) ((descriptor.zmax - descriptor.zmin) * voxelScale));
	}
	
	Vector3i scalePoint(double[] p) {
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
					saveDefault(img);
			}
		});
		f.setSize(img.getWidth(), img.getHeight());
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	
	void save(String fileName, BufferedImage img) {
		try {
			ImageIO.write(img, "PNG", new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void saveDefault(BufferedImage img) {
		DateFormat df = new SimpleDateFormat("yyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		String s = "res/" + descriptor.name + '_' + df.format(date) + ".png";
		save(s, img);
	}
	
}
