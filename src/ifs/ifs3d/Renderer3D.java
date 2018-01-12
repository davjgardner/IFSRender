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
	
	double[][][][] plot(int iterations, int iterFloor) {
		Vector3i size = getSize();
		// 3 axes, RGB + FREQ (4 attributes) per voxel
		double[][][][] histogram = new double[size.x][size.y][size.z][4];
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
					histogram[pos.x][pos.y][pos.z][FREQ]++;
					histogram[pos.x][pos.y][pos.z][R] = (histogram[pos.x][pos.y][pos.z][R] + p[R]) / 2;
					histogram[pos.x][pos.y][pos.z][G] = (histogram[pos.x][pos.y][pos.z][G] + p[G]) / 2;
					histogram[pos.x][pos.y][pos.z][B] = (histogram[pos.x][pos.y][pos.z][B] + p[B]) / 2;
				}
			}
		}
		return histogram;
	}
	
	double[][][][] process(double[][][][] histogram) {
		return histogram;
	}
	
	BufferedImage render2d(double[][][][] voxelData) {
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
