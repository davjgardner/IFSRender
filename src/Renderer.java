import utils.math.geom.*;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/**
 * Provides methods for rendering an image from an IFSDescriptor
 */
public class Renderer {
	
	/*final int FREQ = 3, R = 0, G = 1, B = 2;
	final int X = 4, Y = 5;*/
	
	private static final int X = 0, Y = 1, R = 2, G = 3, B = 4, FREQ = 5;
	
	public static BufferedImage render(IFSDescriptor descriptor, int iterations, double pixelScale) {
		return null;
	}
	
	private static Vector2i scalePoint(double[] p, IFSDescriptor descriptor, double pixelScale) {
		int x = (int) ((p[X] - descriptor.xmin) * pixelScale);
		int y = (int) ((p[Y] - descriptor.ymin) * pixelScale);
		return new Vector2i(x, y);
	}
	
	public static void main(String[] args) {
		if (args.length > 1) {
			int pixelScale = 350;
			try {
				List<String> source = Utils.readFile(args[1]);
				IFSDescriptor d = new IFSDescriptor(source);
				BufferedImage img = render(d, 10000000, pixelScale);
				JFrame f = new JFrame("IFS: " + args[1]) {
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
			}
		} else {
			System.out.println("Usage: java Renderer [IFS descriptor file]");
		}
	}
}
