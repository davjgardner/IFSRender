import utils.math.parser.MathParser2;

import java.util.*;

public class IFSDescriptor {

	static final String VERSION = "2.0";
	
	double xmin, xmax, ymin, ymax;
	
	Random rand;
	
	List<Function> functions;
	
	public IFSDescriptor(String source) throws Exception{
		ParseTree p = new ParseTree(source.split("\n"));
		if (!p.values.get("VERSION").equals(VERSION)) {
			System.err.println("IFS Descriptor file version mismatch: incompatible input");
			System.exit(1);
		}
		this.xmin = Double.parseDouble(p.values.get("XMIN"));
		this.xmax = Double.parseDouble(p.values.get("XMAX"));
		this.ymin = Double.parseDouble(p.values.get("YMIN"));
		this.ymax = Double.parseDouble(p.values.get("YMAX"));
		for (ParseTree c : p.children) {
			functions.add(new Function(c.values.get("X"), c.values.get("Y"),
					c.values.get("R"), c.values.get("G"), c.values.get("B"),
					c.values.get("PROB")));
		}
	}
	
	public IFSDescriptor(String source, int randomSeed) throws Exception{
		this(source);
		rand = new Random(randomSeed);
	}
	
	/**
	 * Randomly picks a function from the list and runs it with the given variable mappings
	 * @param vars variable mappings
	 * @return {x, y, r, g, b}
	 * @throws Exception if the evaluation fails
	 */
	double[] runFunc(Map<String, Double> vars) throws Exception {
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
			return new double[] {x, y, r, g, b};
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
		ParseTree(String[] data) {
			values = new HashMap<>();
			children = new ArrayList<>();
			for (int i = 0; i < data.length; i++) {
				String l = data[i];
				String[] tokens = l.split(" ");
				switch(tokens[0]) {
					case "FUNCTION":
						children.add(new ParseTree(Arrays.copyOfRange(data, i + 1, data.length)));
						do i++; while (!data[i].equals("END")); // fast forward to the end of the subtree
						break;
					case "END":
						return; // return from recursive call at the end of the subtree
					default: // the line is a variable assignment
						values.put(tokens[0], l.substring(l.indexOf('=') + 1)); // grab everything after the '='
						break;
				}
			}
		}
	}
	
}
