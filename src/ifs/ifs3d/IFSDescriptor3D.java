package ifs.ifs3d;

import ifs.IFSDescriptor;
import utils.math.parser.MathParser2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class IFSDescriptor3D extends IFSDescriptor {
	
	static final int Z = 5;
	
	double zmin, zmax;
	
	public IFSDescriptor3D(List<String> source) throws Exception {
		functions = new ArrayList<>();
		globals = new HashMap<>();
		parseTree = new ParseTree(source);
		if (!parseTree.values.get("VERSION").trim().equals(VERSION)) {
			System.err.println("IFS Descriptor file version mismatch: expected '" +
					VERSION + "', got '" + parseTree.values.get("VERSION") + "'");
			System.exit(1);
		}
		super.name = parseTree.values.get("NAME");
		super.xmin = Double.parseDouble(parseTree.values.get("XMIN"));
		super.xmax = Double.parseDouble(parseTree.values.get("XMAX"));
		super.ymin = Double.parseDouble(parseTree.values.get("YMIN"));
		super.ymax = Double.parseDouble(parseTree.values.get("YMAX"));
		this.zmin = Double.parseDouble(parseTree.values.get("ZMIN"));
		this.zmax = Double.parseDouble(parseTree.values.get("ZMAX"));
		for (ParseTree c : parseTree.children) {
			functions.add(new Function3D(c.values.get("X"), c.values.get("Y"),
					c.values.get("Z"), c.values.get("R"), c.values.get("G"),
					c.values.get("B"), c.values.get("PROB")));
			c.values.remove("X");
			c.values.remove("Y");
			c.values.remove("Z");
			c.values.remove("R");
			c.values.remove("G");
			c.values.remove("B");
			c.values.remove("PROB");
			// attempt to store the remaining values as global variables
			c.values.forEach((s, v) -> {
				try {
					double val = MathParser2.parse(v);
					globals.put(s, val);
				} catch(Exception e) {/* skip if parse fails */}
			});
		}
		rand = new Random();
	}
	
	double[] runFunc(double[] p, int i) throws Exception {
		Map<String, Double> vars = new HashMap<>();
		vars.put("_x", p[X]);
		vars.put("_y", p[Y]);
		vars.put("_z", p[Z]);
		vars.put("_r", p[R]);
		vars.put("_g", p[G]);
		vars.put("_b", p[B]);
		vars.put("_i", (double) i);
		double r2 = p[X] * p[X] + p[Y] * p[Y] + p[Z] * p[Z];
		double r = Math.sqrt(r2);
		vars.put("_R2", r2);
		vars.put("_R", r);
		vars.put("_theta", Math.atan2(p[Y], p[X])); // really there are many angles
		vars.put("_alpha", Math.acos(p[X] / r)); // angle with x axis
		vars.put("_beta", Math.acos(p[Y] / r)); // angle with y axis
		vars.put("_gamma", Math.acos(p[Z] / r)); // angle with z axis
		vars.putAll(globals);
		double rn = rand.nextDouble();
		double a = 0.0;
		for (Function f : functions) {
			if (rn < (a += f.prob)) {
				return f.run(vars);
			}
		}
		throw new Exception("Probabilities did not add up");
	}
	
	private class Function3D extends Function {
		List<String> zrpn;
		
		Function3D(String x, String y, String z, String r, String g, String b, String prob) throws Exception {
			super(x, y, r, g, b, prob);
			zrpn = MathParser2.toRPN(MathParser2.tokenizeInput(z));
		}
		
		@Override
		public double[] run(Map<String, Double> vars) throws Exception {
			double[] res = super.run(vars);
			double z = MathParser2.parseRPN(zrpn);
			double[] p = new double[6];
			p[X] = res[X];
			p[Y] = res[Y];
			p[Z] = z;
			p[R] = res[R];
			p[G] = res[G];
			p[B] = res[B];
			return p;
		}
	}
}
