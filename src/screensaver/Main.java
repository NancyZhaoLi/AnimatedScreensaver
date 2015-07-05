package screensaver;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

/**
 * The main class to run the program.
 */
public class Main {

	public static final Shape star = new Polygon(new int[] { 0, 4, 20, 4, 0,
			-4, -20, -4, 0 }, new int[] { -20, -4, 0, 4, 40, 4, 0, -4, -20 }, 9);
	public static final GeneralPath heart = Main.makeHeart();
	public static final Rectangle2D rectangle = new Rectangle2D.Double(-10, -10, 20, 20);



	public static void main(String[] args) {

		Canvas canvas = new Canvas();

		// Create the nodes that will be part of the screen saver.
		SSNode root = new SSNode(heart, Color.RED);
		SSNode child0 = new SSNode(heart, Color.GREEN);
		SSNode child1 = new SSNode(star, Color.YELLOW);
		SSNode child0_0 = new SSNode(heart, Color.WHITE);
		SSNode child0_1 = new SSNode(heart, Color.ORANGE);
		root.transform(AffineTransform.getTranslateInstance(500 / 2, 500 / 2));

		child0.transform(AffineTransform.getTranslateInstance(150, 0));

		child1.transform(AffineTransform.getTranslateInstance(0, 100));
		//
		child1.transform(AffineTransform.getRotateInstance(Math.PI));
		child1.transform(AffineTransform.getScaleInstance(2, 2));
		child1.transform(AffineTransform.getScaleInstance(0.5, 0.5));
		
		
		child0_0.transform(AffineTransform.getTranslateInstance(75, 0));
		child0_0.transform(AffineTransform.getRotateInstance(Math.PI / 2));
		child0_0.transform(AffineTransform.getScaleInstance(0.5, 0.5));

		child0_1.transform(AffineTransform.getTranslateInstance(-75, 0));
		child0_1.transform(AffineTransform.getScaleInstance(0.5, 0.5));
		
		// Make a ring of 6 nodes around child_0_0.
		for(int i=0; i<6; i++) {
			double d = 60;
			SSNode c = new SSNode(heart, Color.GREEN);
			double rot = i*Math.PI/6;
			c.transform(AffineTransform.getRotateInstance(rot));
			c.transform(AffineTransform.getTranslateInstance(d*Math.cos(rot), d*Math.sin(rot)));
			c.transform(AffineTransform.getScaleInstance(0.75, 0.75));		
			child0_0.addChild(c);
		}

		// Make the scene graph.
		root.addChild(child0);
		root.addChild(child1);
		child0.addChild(child0_0);
		child0.addChild(child0_1);

		canvas.addNode(root);

		JFrame frame = new JFrame("Screensaver");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 522);
		frame.setContentPane(canvas);
		frame.setVisible(true);
	}

	/**
	 * Make a heart-shaped shape at the origin.
	 */
	private static GeneralPath makeHeart() {
		GeneralPath heart = new GeneralPath();
		heart.moveTo(0, 0);
		heart.curveTo(0, -5, 5, -10, 10, -10);
		heart.curveTo(15, -10, 20, -5, 20, 0);
		heart.curveTo(20, 10, 5, 25, 0, 30);
		heart.curveTo(-5, 25, -20, 10, -20, 0);
		heart.curveTo(-20, -5, -15, -10, -10, -10);
		heart.curveTo(-5, -10, 0, -5, 0, 0);
		return heart;
	}
}
