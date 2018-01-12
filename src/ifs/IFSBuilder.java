package ifs;

import utils.Color3f;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class IFSBuilder {
	
	private List<Function> functions;
	
	private String name;
	
	private double xmin, xmax, ymin, ymax;
	
	Function flipx = new Function(0, "-_x", "_y");
	Function flipy = new Function(0, "_x", "-_y");
	
	Function affine = new Function(6, "a0 * _x + a1 * _y + a2", "a3 * _x + a4 * _y + a5");
	Function sin = new Function(0, "sin(_x)", "sin(_y)");
	Function sphere = new Function(0, "_x / _R2", "_y / _R2");
	Function swirl = new Function(0, "_x * sin(_R2) - _y * cos(_R2)", "_x * cos(_R2) + _y * sin(_R2)");
	Function horseshoe = new Function(0, "1 / _R * (_x - _y) * (_x + _y)", "1 / _R * 2 * _x * _y");
	Function polar = new Function(0, "_theta / _pi", "_R - 1");
	Function handkerchief = new Function(0, "_R * sin(_theta + _R)", "_R * cos(_theta - _R)");
	Function heart = new Function(0, "_R * sin(_theta * _R)", "-_R * cos(_theta * _R)");
	Function disc = new Function(0, "_theta / _pi * sin(_pi * _R)", "_theta / _pi * cos(_pi * _R)");
	Function spiral = new Function(0, "1 / _R * (cos(_theta) + sin(_R))", "1 / _R * (sin(_theta) - cos(_R))");
	Function hyperbolic = new Function(0, "sin(_theta) / _R", "_R * cos(_theta)");
	Function diamond = new Function(0, "sin(_theta) * cos(_R)", "cos(_theta) * sin(_R)");
	Function ex = new Function(0, "_R * ((sin(_theta + _R))^3 + (cos(_theta - _R))^3)", "_R * ((sin(_theta + _R))^3 - (cos(_theta - _R))^3)");
	Function waves = new Function(4, "_x + a0 * sin(_y / (a1^2))", "_y + a2 * sin(_x / (a3^2))");
	Function fisheye = new Function(0, "2 / (_R + 1) * _y", "2 / (_R + 1) * _x");
	Function popcorn = new Function(2, "_x + a0 * sin(tan(3 * _y))", "_y + a1 * sin(tan(3 * _x))");
	Function eyefish = new Function(0, "2 / (_R + 1) * _x", "2 / (_R + 1) * _y");
	Function bubble = new Function(0, "4 / (_R2 + 4) * _x", "4 / (_R2 + 4) * _y");
	
	List<Function> funcLib = Arrays.asList(affine, sin, sphere, swirl, horseshoe, polar, handkerchief, heart, disc,
			spiral, hyperbolic, diamond, ex, waves, fisheye, popcorn, eyefish, bubble);
	
	IFSBuilder(String name, double xmin, double xmax, double ymin, double ymax) {
		this();
		setName(name);
		setBounds(xmin, xmax, ymin, ymax);
	}
	
	IFSBuilder() {
		functions = new LinkedList<>();
	}
	
	void genRandom(int nFuncs) {
		Random rand = new Random();
		genRandom(nFuncs, rand);
	}
	
	void genRandom(int nFuncs, Random rand) {
		for (int i = 0; i < nFuncs; i++) {
			addTemplate(funcLib.get(rand.nextInt(funcLib.size())), rand.nextDouble(), randomColor(rand));
		}
		normalizeProbs();
	}
	
	Color3f randomColor(Random rand) {
		return new Color3f(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
	}
	
	/**
	 * Adds n-degree rotational symmetry - add as last function
	 * @param n
	 */
	void addSymmetry(int n) {
		assert(n > 1);
		double prob = 1.0 / n;
		functions.forEach(f -> f.prob *= prob);
		for (int i = 0; i < n - 1; i++) {
			functions.add(new Function(0, prob, rot(2 * Math.PI / n)));
		}
	}
	
	IFSBuilder addFunction(Function f) {
		functions.add(f);
		return this;
	}
	
	IFSBuilder addFunction(double prob, String x, String y, Color3f c, double[] params) {
		functions.add(new Function(prob, x, y, c, params));
		return this;
	}
	
	IFSBuilder addFunction(double prob, String x, String y, Color3f c) {
		functions.add(new Function(prob, x, y, c));
		return this;
	}
	
	IFSBuilder addFunction(double prob, String x, String y) {
		functions.add(new Function(prob, x, y));
		return this;
	}
	
	IFSBuilder addTemplate(Function f, double prob, double[] params) {
		f = f.dup();
		f.prob = prob;
		f.setParams(params);
		functions.add(f);
		return this;
	}
	
	IFSBuilder addTemplate(Function f, double prob) {
		addTemplate(f, prob, new double[] {});
		return this;
	}
	
	IFSBuilder addTemplate(Function f, double prob, Color3f c) {
		f = f.dup();
		f.prob = prob;
		f.c = c;
		functions.add(f);
		return this;
	}
	
	IFSBuilder setBounds(double xmin, double xmax, double ymin, double ymax) {
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
		return this;
	}
	
	IFSBuilder setName(String name) {
		this.name = name;
		return this;
	}
	
	String getName() {
		return name;
	}
	
	/**
	 * Generates a rotation function by <code>theta</code>
	 * @param theta angle of rotation
	 * @return
	 */
	private String rot(double theta) {
		return String.format("X = cos(%f) * _x - sin(%f) * _y\n" +
				"Y = sin(%f) * _x + cos(%f) * _y\n", theta, theta, theta, theta);
	}
	
	/**
	 * Builds this IFS into a source string
	 * @return
	 */
	String print() {
		normalizeProbs();
		StringBuilder sb = new StringBuilder();
		sb.append("VERSION = 2.0\n");
		sb.append("NAME = ").append(name).append("\n");
		sb.append(String.format("XMIN = %f\nXMAX = %f\nYMIN = %f\nYMAX = %f\n", xmin, xmax, ymin, ymax));
		functions.forEach(f -> sb.append(f.print(null)));
		return sb.toString();
	}
	
	String randomizeAndPrint() {
		normalizeProbs();
		Random rand = new Random();
		StringBuilder sb = new StringBuilder();
		sb.append("VERSION = 2.0\n");
		sb.append("NAME = ").append(name).append("\n");
		sb.append(String.format("XMIN = %f\nXMAX = %f\nYMIN = %f\nYMAX = %f\n", xmin, xmax, ymin, ymax));
		functions.forEach(f -> sb.append(f.print(rand.doubles(f.nparams).map(d -> d * 2 - 1).toArray())));
		return sb.toString();
	}
	
	private void normalizeProbs() {
		double probTotal = functions.stream().mapToDouble(Function::getProb).sum();
		if (probTotal == 1.0) return;
		functions.forEach(f -> f.prob /= probTotal);
	}
	
	class Function {
		double prob;
		int nparams;
		double params[];
		String f;
		String x, y;
		Color3f c;
		
		Function(int nparams, double prob, String f) {
			this.f = f;
			this.prob = prob;
			this.nparams = nparams;
		}
		
		Function(int nparams, String f) {
			this.f = f;
			this.nparams = nparams;
		}
		
		Function(int nparams, String x, String y, Color3f c) {
			this.nparams = nparams;
			this.x = x;
			this.y = y;
			this.c = c;
		}
		
		Function(int nparams, String x, String y) {
			this.nparams = nparams;
			this.x = x;
			this.y = y;
		}
		
		Function(double prob, String x, String y, Color3f c, double[] params) {
			this.prob = prob;
			this.nparams = params.length;
			this.params = params;
			this.x = x;
			this.y = y;
			this.c = c;
		}
		
		Function(double prob, String x, String y, Color3f c) {
			this.prob = prob;
			this.x = x;
			this.y = y;
			this.c = c;
		}
		
		Function(double prob, String x, String y) {
			this.prob = prob;
			this.x = x;
			this.y = y;
		}
		
		double getProb() {return prob;}
		
		void setParams(double[] params) {
			if (params == null) return;
			this.params = params;
			this.nparams = params.length;
		}
		
		String print(double[] parameters) {
			setParams(parameters);
			return print();
		}
		
		String print() {
			StringBuilder sb = new StringBuilder();
			sb.append("FUNCTION\n");
			sb.append("PROB = ").append(prob).append("\n");
			for (int i = 0; i < nparams; i++) {
				sb.append("a").append(i).append(" = ")
						.append(params[i]).append("\n");
			}
			if (f != null) sb.append(f);
			else {
				sb.append("X = ").append(x).append("\n");
				sb.append("Y = ").append(y).append("\n");
			}
			if (c != null) {
				sb.append(String.format("R = 0.5 * _r + 0.5 * %f\n", c.r));
				sb.append(String.format("G = 0.5 * _g + 0.5 * %f\n", c.g));
				sb.append(String.format("B = 0.5 * _b + 0.5 * %f\n", c.b));
			} else {
				sb.append("R = _r\nG = _g\nB = _b\n");
			}
			sb.append("END\n");
			return sb.toString();
		}
		
		Function dup() {
			Function f = new Function(nparams, x, y, c);
			f.setParams(this.params);
			return f;
		}
	}
	
}
