package ifs;

import utils.math.parser.MathParser2;

import java.util.*;

public class IFSDescriptor {
	
	/**
	 * IFS Descriptor file parser version
	 */
	private static final String VERSION = "2.0";
	
	static final int R = 0, G = 1, B = 2, FREQ = 3, X = 3, Y = 4;
	
	double xmin, xmax, ymin, ymax;
	
	String name;
	
	private Random rand;
	
	private List<Function> functions;
	
	Map<String, Double> globals;
	
	IFSDescriptor(List<String> source) throws Exception {
		functions = new ArrayList<>();
		globals = new HashMap<>();
		ParseTree p = new ParseTree(source);
		if (!p.values.get("VERSION").trim().equals(VERSION)) {
			System.err.println("IFS Descriptor file version mismatch: expected '" +
					VERSION + "', got '" + p.values.get("VERSION") + "'");
			System.exit(1);
		}
		this.name = p.values.get("NAME");
		this.xmin = Double.parseDouble(p.values.get("XMIN"));
		this.xmax = Double.parseDouble(p.values.get("XMAX"));
		this.ymin = Double.parseDouble(p.values.get("YMIN"));
		this.ymax = Double.parseDouble(p.values.get("YMAX"));
		for (ParseTree c : p.children) {
			functions.add(new Function(c.values.get("X"), c.values.get("Y"),
					c.values.get("R"), c.values.get("G"), c.values.get("B"),
					c.values.get("PROB")));
			c.values.remove("X");
			c.values.remove("Y");
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
	
	public IFSDescriptor(List<String> source, int randomSeed) throws Exception{
		this(source);
		rand = new Random(randomSeed);
	}
	
	/**
	 * Randomly picks a function from the list and runs it with the given variable mappings
	 * @param p input point
	 * @param i iteration
	 * @return {x, y, r, g, b}
	 * @throws Exception if the evaluation fails
	 */
	double[] runFunc(double[] p, int i) throws Exception {
		Map<String, Double> vars = new HashMap<>();
		vars.put("_x", p[X]);
		vars.put("_y", p[Y]);
		vars.put("_r", p[R]);
		vars.put("_g", p[G]);
		vars.put("_b", p[B]);
		vars.put("_i", (double) i);
		vars.put("_R2", p[X] * p[X] + p[Y] * p[Y]);
		vars.put("_R", Math.sqrt(p[X] * p[X] + p[Y] * p[Y]));
		vars.put("_theta", Math.atan2(p[Y], p[X]));
		vars.putAll(globals);
		double r = rand.nextDouble();
		double a = 0.0;
		for (Function f : functions) {
			if (r < (a += f.prob)) {
				return f.run(vars);
			}
		}
		throw new Exception("Probabilities did not add up");
	}
	
	/**
	 * Represents a single IFS function
	 */
	private class Function {
		List<String> xrpn, yrpn, rrpn, grpn, brpn;
		double prob;
		
		/**
		 * Parses the input expression strings into Reverse Polish Notation to simplify later calls
		 * @param x x expression
		 * @param y y expression
		 * @param r r expression
		 * @param g g expression
		 * @param b b expression
		 * @param prob function probability
		 * @throws Exception if parsing fails
		 */
		Function(String x, String y, String r, String g, String b, String prob) throws Exception {
			this.prob = Double.parseDouble(prob);
			MathParser2.initConstants(new HashMap<>()); // this is necessary here - bug in MathParser2
			xrpn = MathParser2.toRPN(MathParser2.tokenizeInput(x));
			yrpn = MathParser2.toRPN(MathParser2.tokenizeInput(y));
			rrpn = MathParser2.toRPN(MathParser2.tokenizeInput(r));
			grpn = MathParser2.toRPN(MathParser2.tokenizeInput(g));
			brpn = MathParser2.toRPN(MathParser2.tokenizeInput(b));
		}
		
		/**
		 * Runs the function with the given variable mappings
		 * @param vars variable mappings
		 * @return {x, y, r, g, b}
		 * @throws Exception if the evaluation fails
		 */
		double[] run(Map<String, Double> vars) throws Exception {
			MathParser2.initConstants(vars);
			double x = MathParser2.parseRPN(xrpn);
			double y = MathParser2.parseRPN(yrpn);
			double r = MathParser2.parseRPN(rrpn);
			double g = MathParser2.parseRPN(grpn);
			double b = MathParser2.parseRPN(brpn);
			double[] p = new double[5];
			p[X] = x;
			p[Y] = y;
			p[R] = r;
			p[G] = g;
			p[B] = b;
			return p;
		}
	}
	
	/**
	 * Intermediate state for parsing descriptor files
	 */
	private class ParseTree {
		Map<String, String> values;
		List<ParseTree> children;
		
		ParseTree(Map<String, String> values, List<ParseTree> children) {
			this.values = values;
			this.children = children;
		}
		
		/**
		 * Creates a <code>ParseTree</code> from the given source code
		 * @param data line-separated source code
		 */
		ParseTree(List<String> data) {
			values = new HashMap<>();
			children = new ArrayList<>();
			for (int i = 0; i < data.size(); i++) {
				String l = data.get(i).trim();
				if (l.isEmpty() || l.startsWith("#")) continue;
				String[] tokens = l.split(" ");
				switch(tokens[0]) {
					case "FUNCTION":
						children.add(new ParseTree(data.subList(i+1, data.size())));
						do i++; while (!data.get(i).equals("END")); // fast forward to the end of the subtree
						break;
					case "END":
						return; // return from recursive call at the end of the subtree
					default: // the line is a variable assignment
						values.put(tokens[0], l.substring(l.indexOf('=') + 1)); // grab everything after the '='
						break;
				}
			}
			print();
		}
		
		void print() {
			values.forEach((k, v) -> System.out.println(k + ": " + v));
			System.out.println();
			children.forEach(ParseTree::print);
		}
	}
	
}
