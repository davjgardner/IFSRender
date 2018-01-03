package utils.math.graphing;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

import utils.math.parser.MathParser2;

public class Grapher extends JPanel {
	
	//private DataSet data;
	private String function;
	private int f = 20;
	private ArrayList<String> functions = new ArrayList<String>();
	private int sx = 800, sy = 800;
	private double xmin=-10.0, xmax=10.0, ymin=-10.0, ymax=10.0;
	private double delta = .1;
	private Point mousePoint = new Point();
	
	
	public Grapher(String function) {
		this.function = function;
		this.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				/*xmin += map(-sx, sx, xmin, xmax, e.getPoint().x-mousePoint.x);
				ymin += map(-sy, sy, ymin, ymax, e.getPoint().y-mousePoint.y);
				xmax += map(-sx, sx, xmin, xmax, e.getPoint().x-mousePoint.x);
				ymax += map(-sy, sy, ymin, ymax, e.getPoint().y-mousePoint.y);*/
				//System.out.println(e.getPoint().toString());
				//System.out.println(mousePoint.toString());
				xmin += (e.getPoint().x-mousePoint.x) * .025;
				xmax += (e.getPoint().x-mousePoint.x) * .025;
				ymin += (e.getPoint().y-mousePoint.y) * .025;
				ymax += (e.getPoint().y-mousePoint.y) * .025;
				//System.out.println("dx=" + (e.getPoint().x-mousePoint.x));
				//System.out.println("dy=" + (e.getPoint().y-mousePoint.y));
				mousePoint = e.getPoint();
				repaint();
				//System.out.println("x: (" + xmin + ", " + xmax + ") y: (" + ymin + ", " + ymax + ")" );
			}
			@Override
			public void mouseMoved(MouseEvent e) {}
			
		});
		this.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(e.getWheelRotation()<0) {
					xmin *= .9;
					xmax *= .9;
					ymin *= .9;
					ymax *= .9;
				}
				else {
					xmin *= (1/.9);
					xmax *= (1/.9);
					ymin *= (1/.9);
					ymax *= (1/.9);
				}
				repaint();
				//System.out.println("x: (" + xmin + ", " + xmax + ") y: (" + ymin + ", " + ymax + ")" );
			}
		});
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				mousePoint = e.getPoint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {}
			
		});
	}
	
	public void setFunction(String f) {function = f; this.repaint();}
	public String getFunction() {return function;}
	public void clearFunctions() {functions.clear();}
	public void addFunction(String f) {functions.add(f);}
	
	
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.black);
		g.drawLine(0, sy - (int) map(ymin, ymax, 0, sy, 0), sx, sy - (int) map(ymin, ymax, 0, sy, 0));
		g.drawLine(sx -(int) map(xmin, xmax, 0, sx, 0), 0, sx- (int) map(xmin, xmax, 0, sx, 0), sy);
		for (int i=0; i<functions.size(); i++) {
			double j = 0.0;
			Point prevPoint = null;
			for (double i1 = xmin; i1 < xmax; i1 += delta) {
				try {
					j = MathParser2.parse(functions.get(i).replace(
							"x", Double.toString(i1)));
				} catch (Exception e) {
					e.printStackTrace();
					prevPoint = null;
				}
				Point p = new Point(
						(int) Math.round(map(xmin, xmax, 0, sx, i1)), sy
								- (int) Math.round(map(ymin, ymax, 0, sy, j)));

				if (prevPoint != null)
					g.drawLine(prevPoint.x, prevPoint.y, p.x, p.y);
				else
					g.drawLine(p.x, p.y, p.x, p.y);
				prevPoint = new Point(p.x, p.y);
				//System.out.println(j);
			}
		}
		for(double i=xmin; i<xmax; i++) {
			int xloc = (int) map(xmin, xmax, 0, sx, i);
			int yloc = (int) map(ymin, ymax, 0, sy, 0);
			g.drawLine(xloc, yloc-3, xloc, yloc+3);
		}
		for(double i=ymin; i<ymax; i++) {
			//g.setColor(Color.red);
			int xloc = (int) map(xmin, xmax, 0, sx, 0);
			int yloc = (int) map(ymin, ymax, 0, sy, i);
			g.drawLine(xloc-3, yloc, xloc+3,yloc);
		}
	}
	
	public double map(double imin, double imax, double omin, double omax, double x) {
		return (x - imin) * (omax - omin) / (imax - imin) + omin;
	}
	
	
	public static void main(String args[]) {
		JFrame frame = new JFrame("Graphing Test");
		frame.setSize(960, 875);
		frame.setLayout(new BorderLayout());
		final Grapher grapher = new Grapher("x");
		frame.add(grapher, BorderLayout.CENTER);
		JPanel controlPanel = new JPanel();
		controlPanel.add(new JLabel("x Min:"));
		final JTextField xminBox = new JTextField(4);
		xminBox.setText(Double.toString(grapher.xmin));
		controlPanel.add(xminBox);
		controlPanel.add(new JLabel("x Max:"));
		final JTextField xmaxBox = new JTextField(4);
		xmaxBox.setText(Double.toString(grapher.xmax));
		controlPanel.add(xmaxBox);
		controlPanel.add(new JLabel("y Min:"));
		final JTextField yminBox = new JTextField(4);
		yminBox.setText(Double.toString(grapher.ymin));
		controlPanel.add(yminBox);
		controlPanel.add(new JLabel("y Max:"));
		final JTextField ymaxBox = new JTextField(4);
		ymaxBox.setText(Double.toString(grapher.ymax));
		controlPanel.add(ymaxBox);
		controlPanel.add(new JLabel("Delta:"));
		final JTextField dBox = new JTextField(4);
		dBox.setText(Double.toString(grapher.delta));
		controlPanel.add(dBox);
		JPanel fPanel = new JPanel();
		fPanel.setLayout(new GridLayout(grapher.f, 2));
		final JTextField[] fFields = new JTextField[grapher.f];
		for(int i=0; i<grapher.f; i++) {
			fPanel.add(new JLabel("y" + (i+1)+ " ="));
			fFields[i] = new JTextField(8);
			fPanel.add(fFields[i]);
		}
		JButton gButton = new JButton("Graph!");
		gButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				grapher.xmin = Double.parseDouble(xminBox.getText());
				grapher.xmax = Double.parseDouble(xmaxBox.getText());
				grapher.ymin = Double.parseDouble(yminBox.getText());
				grapher.ymax = Double.parseDouble(ymaxBox.getText());
				grapher.delta = Double.parseDouble(dBox.getText());
				//grapher.setFunction(fBox.getText());
				grapher.clearFunctions();
				for(int i=0; i<grapher.f; i++) {
					grapher.addFunction(fFields[i].getText());
				}
				grapher.repaint();
			}
		});
		controlPanel.add(gButton);
		frame.add(fPanel, BorderLayout.EAST);
		frame.add(controlPanel, BorderLayout.SOUTH);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
