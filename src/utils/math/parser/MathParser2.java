package utils.math.parser;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import javax.swing.JOptionPane;

/**
 * Mathematical Expressions Parser using the Shunting-yard Algorithm and Reverse-Polish Notation
 * @author david
 *
 */
public class MathParser2 {

	private static Map<String, Integer> operatorMap = new HashMap<String, Integer>(); //map operator to precedence
	private static Map<String, Double> constants = new HashMap<String, Double>();
	
	/**
	 * Evaluate the given mathematical expression. Supported functions: sin, cos, tan, log, ln, sqrt. Includes built-in constants _pi and _e
	 * @param exp expression to evaluate
	 * @return decimal value of evaluated expression
	 * @throws Exception if a symbol is not recognized
	 */
	public static double parse(String exp) throws Exception {
		return parse(exp, new HashMap<String, Double>());
	}
	
	public static void initConstants(Map<String, Double> constants) {
		MathParser2.constants = constants;
		operatorMap.put("+", 2);
		operatorMap.put("-", 2);
		operatorMap.put("*", 3);
		operatorMap.put("/", 3);
		operatorMap.put("~", 3); //mostly for internal use: negatory operator (multiply the thing by -1)
		operatorMap.put("^", 4);
		operatorMap.put("%", 3);
		operatorMap.put("sin", 5);
		operatorMap.put("cos", 5);
		operatorMap.put("tan", 5);
		operatorMap.put("log", 5);
		operatorMap.put("ln", 5);
		operatorMap.put("sqrt", 5);
		operatorMap.put("min", 5);
		operatorMap.put("max", 5);
		operatorMap.put("pow", 5);
		operatorMap.put("(", 1); //parentheses are in here so they will be recognized as not numbers
		operatorMap.put(")", 1);
		operatorMap.put(",", 1); //as are commas
		constants.put("_pi", Math.PI);
		constants.put("_e", Math.E);
	}
	
	/**
	 * Evaluate the given mathematical expression using the given variables. Supported functions: sin, cos, tan, log, ln, sqrt, min, max. Includes built-in constants _pi and _e
	 * @param exp expression to evaluate
	 * @param variables mapping of variable name to value. Variable names are replaced with their corresponding values before the expression is evaluated
	 * @return decimal value of evaluated expression
	 * @throws Exception if a symbol is not recognized
	 */
	public static double parse(String exp, Map<String, Double> variables) throws Exception {
		initConstants(variables);
		return parseRPN(toRPN(tokenizeInput(exp)));
	}
	
	public static List<String> tokenizeInput(String exp) throws Exception {
		String numbers = "0123456789.";
		String letters = "abcedfghijklmnopqrstuvwxyz_ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		String symbols = "+-*/~^%(),";
//		System.out.println(exp);
		List<String> input = new ArrayList<String>();
		for(int i=0; i<exp.length(); i++) {
			if(exp.charAt(i) == ' ' || exp.charAt(i) == '\t' || exp.charAt(i) == '\n') continue;
			else if(numbers.contains(Character.toString(exp.charAt(i)))) {
				String n = Character.toString(exp.charAt(i));
				String s = "";
				int j;
				for(j=i+1; j<exp.length() && numbers.contains((s=Character.toString(exp.charAt(j)))); j++) {
					n += s;
				}
				i = j-1;
				input.add(n);
			}
			else if(letters.contains(Character.toString(exp.charAt(i)))) {
				String n = Character.toString(exp.charAt(i));
				String s = "";
				int j;
				for(j=i+1; j<exp.length() && letters.contains((s=Character.toString(exp.charAt(j)))); j++) {
					n += s;
				}
				i = j-1;
				input.add(n);
			}
			else if(symbols.contains(Character.toString(exp.charAt(i)))) {
				input.add(Character.toString(exp.charAt(i)));
			}
			else throw new Exception("Unrecognized Symbol: " + Character.toString(exp.charAt(i)));
		}
//		print(input);
		return input;
	}
	
	/**
	 * Convert an expression in infix notation to Reverse-Polish Notation
	 * @param input expression in infix notation
	 * @return list of tokens in RPN
	 * @throws Exception 
	 */
	public static List<String> toRPN(List<String> input) throws Exception {
		List<String> output = new ArrayList<String>();
		Stack<String> stack = new Stack<String>();
		for(int i=0; i<input.size(); i++) {
			String token = input.get(i);
			if(!operatorMap.containsKey(token)) {output.add(token);} //it must be a number
			else if(operatorMap.get(token) == 5) stack.push(token); //push functions (sin, cos, etc)
			else if(token.equals("(")) stack.push(token);
			else if(token.equals(")")) { //pop operators into output until open paren is reached
				String s;
				try {
					while (!(s = stack.pop()).equals("("))
						output.add(s);
				} catch (Exception e) {
					throw new Exception("Mismatched Parentheses");
				}
//				if(operatorMap.get(stack.peek()) == 5) output.add(stack.pop()); //this may not be needed : after popping operators into output, check for a function, like if the situation was sin(...)
			}
			else if(token.equals(",")) { //act like this was a close paren
				String s;
				try {
					while (!stack.peek().equals("(")) {
						s = stack.pop();
						output.add(s);
					}
				} catch (Exception e) {
					throw new Exception("Mismatched Parentheses");
				}
			}
			else if (token.equals("-") && (i==0 || (operatorMap.containsKey(input.get(i-1)) && !input.get(i-1).equals(")")))) {
				if(input.get(i+1).equals("("))
						stack.push("~"); //I'll call this the negatory operator, same precedence as */
				else input.set(i+1, "-" + input.get(i+1));
			}
			else {
				while(!stack.empty() && operatorMap.get(token) <= operatorMap.get(stack.peek())) output.add(stack.pop());
				stack.push(token);
			}
//			print(output);
		}
		String s;
		while(true) {
			try {
				s = stack.pop();
				if(s.equals("(") || s.equals(")")) throw new Exception("Mismatched Parentheses");
				output.add(s);
			} catch(EmptyStackException e) {
				break;
			}
		}
//		print(output);
		return output;
	}
	
	/**
	 * Parse an expression in Reverse-Polish Notation
	 * @param input list of tokens in RPN
	 * @return value of evaluated expression
	 * @throws Exception if a symbol is not recognized
	 */
	public static double parseRPN(List<String> input) throws Exception {
//		print(input);
		Stack<Double> stack = new Stack<Double>();
		for(int i=0; i<input.size(); i++) {
//			print(stack);
			String s = input.get(i);
			if(!operatorMap.containsKey(s)) stack.push(getVal(s));
			else {
				switch(s) {
				case "+":
					double a1 = stack.pop();
					double a2 = stack.pop();
					stack.push(a2+a1);
					break;
				case "-":
					a1 = stack.pop();
					a2 = stack.pop();
					stack.push(a2-a1);
					break;
				case "*":
					a1 = stack.pop();
					a2 = stack.pop();
					stack.push(a2*a1);
					break;
				case "/":
					a1 = stack.pop();
					a2 = stack.pop();
					stack.push(a2/a1);
					break;
				case "~":
					a1 = stack.pop();
					stack.push(-a1);
					break;
				case "^":
					a1 = stack.pop();
					a2 = stack.pop();
					stack.push(Math.pow(a2, a1));
					break;
				case "%":
					a1 = stack.pop();
					a2 = stack.pop();
					stack.push(a2%a1);
					break;
				case "sin":
					a1 = stack.pop();
					stack.push(Math.sin(a1));
					break;
				case "cos":
					a1 = stack.pop();
					stack.push(Math.cos(a1));
					break;
				case "tan":
					a1 = stack.pop();
					stack.push(Math.tan(a1));
					break;
				case "log":
					a1 = stack.pop();
					stack.push(Math.log10(a1));
					break;
				case "ln":
					a1 = stack.pop();;
					stack.push(Math.log(a1));
					break;
				case "sqrt":
					a1 = stack.pop();
					stack.push(Math.sqrt(a1));
					break;
				case "min":
					a1 = stack.pop();
					a2 = stack.pop();
					stack.push(Math.min(a1, a2));
					break;
				case "max":
					a1 = stack.pop();
					a2 = stack.pop();
					stack.push(Math.max(a1, a2));
					break;
				case "pow":
					a1 = stack.pop();
					a2 = stack.pop();
					stack.push(Math.pow(a2, a1));
					break;
				default:
					throw new Exception("unknown symbol: " + s);
				}
			}
		}
		return stack.pop();
	}
	
	private static double getVal(String s) {
		if(constants.containsKey(s)) return constants.get(s);
		if(constants.containsKey(s.replace("-", ""))) return -constants.get(s.replace("-", ""));
		else return Double.parseDouble(s);
	}
	
	private static String getString(double d) {
		NumberFormat nf = DecimalFormat.getInstance();
		
		return Double.toString(d);
	}
	
	private static void print(List<String> list) {
		for(String s : list) {
			System.out.print(s + " ");
		}
		System.out.println();
	}
	
	private static void print(Stack<Double> s) {
		System.out.println(s);
	}
	
	public static void main(String[] args) {
		String expression = "2*-3*5";
		expression = JOptionPane.showInputDialog(null, "Enter an expression");
		try {
			System.out.println(parse(expression));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
