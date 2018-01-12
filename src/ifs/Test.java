package ifs;

import utils.Color3f;
import utils.Utils;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Test {
	public static void main(String[] args) {
		//renderList(1801, 1828, 1766, 1735, 1690, 1583, 1537, 1531, 1370, 1307, 874, 824, 776, 764, 736, 709, 653, 587, 564, 526, 469, 451, 428, 88, 80, 632, 526, 90, 73);
		renderList(80);
	}
	
	private static void testBuilder() {
		try {
			IFSBuilder builder = new IFSBuilder("Test", -2, 2, -2, 2);
			builder.addFunction(1, "0.5 * _x", "0.5 * _y", Color3f.red);
			builder.addFunction(1, "0.5 * _x + 0.5", "0.5 * _y", Color3f.green);
			builder.addFunction(1, "0.5 * _x + 0.25", "0.5 * _y + sqrt(3) / 4", Color3f.blue);
			String s[] = builder.print().split("\n");
			IFSDescriptor descriptor = new IFSDescriptor(Arrays.asList(s));
			Renderer renderer = new Renderer(descriptor, 350);
			renderer.display(renderer.render(10_000_000));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void testRandom() {
		try {
			IFSBuilder builder = new IFSBuilder("Random2", -2, 2, -2, 2);
			Random rand = new Random();
			for (int i=0; i < 4; i++)
				builder.addTemplate(builder.affine, rand.nextDouble(), builder.randomColor(rand));
			builder.genRandom(2, rand);
			IFSDescriptor descriptor = new IFSDescriptor(Arrays.asList(builder.randomizeAndPrint().split("\n")));
			Renderer renderer = new Renderer(descriptor, 350);
			renderer.display(renderer.render(10_000_000));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void testAnim() {
		try {
			IFSDescriptor d = new IFSDescriptor(Utils.readFile("ifs/2020-anim.ifs"));
			Renderer renderer = new Renderer(d, 350);
			Map<String, double[]> vars = new HashMap<>();
			vars.put("a1", new double[] {0.5, 3.5});
			vars.put("a2", new double[] {0.05, 0.6});
			renderer.renderSequence(vars, 10_000_000, 100, 20, "res/2020-anim-1.gif");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void genRandom() {
		try {
			Random rand = new Random();
			for (int i = 1000; i < 2000; i++) {
				IFSBuilder builder = new IFSBuilder(Integer.toString(i), -2, 2, -2, 2);
				int numFuncs = rand.nextInt(10) + 3; // [3, 12] functions
				int numAffine = rand.nextInt(numFuncs);
				int symmetry = rand.nextInt(5) + 2;
				// add affine functions
				for (int a = 0; a < numAffine; a++) {
					builder.addTemplate(builder.affine, rand.nextDouble(), builder.randomColor(rand));
				}
				// add other functions
				builder.genRandom(numFuncs - numAffine, rand);
				// add symmetry
				if (rand.nextBoolean()) {
					builder.addSymmetry(symmetry);
				}
				String source = builder.randomizeAndPrint();
				BufferedWriter writer = new BufferedWriter(
						new FileWriter("ifs/random/src/" + builder.getName() + ".ifs"));
				writer.write(source);
				writer.close();
				IFSDescriptor d = new IFSDescriptor(Arrays.asList(source.split("\n")));
				Renderer r = new Renderer(d, 100);
				BufferedImage img = r.render(1_000_000);
				r.save("ifs/random/img/" + builder.getName() + ".png", img);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static void renderList(int... l) {
		try {
			for (int i = 0; i < l.length; i++) {
				IFSDescriptor d = new IFSDescriptor(Utils.readFile("ifs/random/src/" + l[i] + ".ifs"));
				Renderer r = new Renderer(d, 400);
				r.save("res/good/" + l[i] + ".png", r.render(12_250_000));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
