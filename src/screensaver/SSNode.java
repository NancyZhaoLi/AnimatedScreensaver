package screensaver;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * Represent one Screen Save Node.
 */
public class SSNode implements MouseMotionListener, MouseListener {

	private AffineTransform trans = new AffineTransform();
	private Shape shape;
	private ArrayList<SSNode> children = new ArrayList<SSNode>();
	private SSNode parent = null;
	private String id; // for debugging printf statements
	private Point2D lastPoint = null;
	private Color color = Color.RED;
	private double velocity = Math.PI/180;
	private boolean heartBeatMode = false;
	private int heartBeatTimer = 0;

	/**
	 * Create a new SSNode, given a shape and a colour.
	 */
	public SSNode(Shape s, Color color) {
		this.id = "id";
		this.shape = s;
		this.color = color;
	}

	/**
	 * Set this node's shape to a new shape.
	 */
	public void setShape(Shape s) {
		this.shape = s;
	}
	
	/**
	 * Get this node's shape.
	 */
	public Shape getShape() {
		return this.shape;
	}

	/**
	 * Set this node's color to a new color.
	 */
	public void setColor(Color c) {
		this.color = c;
	}	
	
	/**
	 * Get this node's color.
	 */
	public Color getColor() {
		return this.color;
	}
	
	/**
	 * Add a child node to this node.
	 */
	public void addChild(SSNode child) {
		child.id = this.id + "." + (this.children.size());
		this.children.add(child);
		child.parent = this;
	}

	/**
	 * Remove a child node to this node.
	 */
	public void removeChild(SSNode child) {
		this.children.remove(child);
	}
	
	/**
	 * Is this node the root node? The root node doesn't have a parent.
	 */
	private boolean isRoot() {
		return this.parent == null;
	}

	/**
	 * Get this node's parent node; null if there is
	 *  no such parent.
	 */
	public SSNode getParent() {
		return this.parent;
	}

	/**
	 * One tick of the animation timer. What should this node do when a unit of
	 * time has passed?
	 */
	public void tick() {
		// don't rotate for root
		if (!this.isRoot()){
			pretransform(AffineTransform.getRotateInstance(this.velocity));
		}
		
		//beat heart
		if (this.heartBeatMode){
			if (this.heartBeatTimer < 10){
				this.enlarge();
			}
			else if (this.heartBeatTimer < 20){
				this.reduce();
			}
			else {
				this.heartBeatTimer = 0;
				if (this.isRoot()){
					SSNode child = new SSNode(this.shape,this.color);
					Random rand = new Random();
					child.transform(AffineTransform.getTranslateInstance(rand.nextInt(200), rand.nextInt(200)));
					this.addChild(child);
				}
				this.heartBeat();
			}
			this.heartBeatTimer++;
		}
		
		//rotate children 
		for (SSNode n : this.children) n.tick();
	}

	/**
	 * Does this node contain the given point (which is in window coordinates)?
	 */
	public boolean containsPoint(Point2D p) {
		AffineTransform inverseTransform = this.getFullInverseTransform();
		Point2D pPrime = inverseTransform.transform(p, null);

		return this.shape.contains(pPrime);
	}

	/**
	 * Return the node containing the point. If nodes overlap, child nodes take
	 * precedence over parent nodes.
	 */
	public SSNode hitNode(Point2D p) {
		for (SSNode c : this.children) {
			SSNode hit = c.hitNode(p);
			if (hit != null)
				return hit;
		}
		if (this.containsPoint(p)) {
			return this;
		} else {
			return null;
		}
	}

	/**
	 * Transform this node's transformation matrix by concatenating t to it.
	 */
	public void transform(AffineTransform t) {
		this.trans.concatenate(t);
	}

	/**
	 * Transform this node's transformation matrix by preconcatenating t to it.
	 */
	public void pretransform(AffineTransform t) {
		this.trans.preConcatenate(t);
	}
	
	/**
	 * Convert p to a Point2D.
	 */
	private Point2D.Double p2D(Point p) {
		return new Point2D.Double(p.getX(), p.getY());
	}

	/**
	 * Gives the point relative to its parent
	 */
	private Point2D relativePoint(Point2D p) {
		AffineTransform inverseTransform = this.getFullInverseTransform();
		return inverseTransform.transform(p, null);
	}
	
	private void enlarge(){
		AffineTransform scale = AffineTransform.getScaleInstance(1.05,1.05);
		transform(scale);
		double ratio = 1/1.05;
		scale = AffineTransform.getScaleInstance(ratio,ratio);
		for (SSNode n: this.children){
			n.transform(scale);
		}
	}
	
	private void reduce(){
		AffineTransform scale = AffineTransform.getScaleInstance(0.95,0.95);
		transform(scale);
		double ratio = 1/0.95;
		scale = AffineTransform.getScaleInstance(ratio,ratio);
		for (SSNode n: this.children){
			n.transform(scale);
		}
	}
	
	private void switchShape(){
		if(this.shape.equals(Main.heart)){
			this.shape = Main.rectangle;
		}
		else if(this.shape.equals(Main.rectangle)){
			this.shape = Main.star;
		}
		else if(this.shape.equals(Main.star)){
			this.shape = Main.heart;
		}
	}
	
	public void switchColor(){
		Random rand = new Random();
		int r = rand.nextInt(255);
		int g = rand.nextInt(255);
		int b = rand.nextInt(255);
		this.color = new Color(r,g,b);
	}

	public void heartBeat(){
		this.heartBeatMode = true;
		this.shape = Main.heart;
		Random rand = new Random();
		int g = rand.nextInt(255);
		int b = rand.nextInt(255);
		this.color = new Color(255,g,b);
		for (SSNode n : children){
			n.heartBeat();
		}
	}
	
	public void stopHeartBeat(){
		this.heartBeatMode = false;
		this.heartBeatTimer = 0;
		for (SSNode n : children){
			n.stopHeartBeat();
		}
	}
	/*************************************************************
	 * 
	 * Handle mouse events directed to this node.
	 * 
	 *************************************************************/

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.lastPoint = p2D(e.getPoint());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		this.lastPoint = null;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.stopHeartBeat();
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

    /**
     * Handle mouse drag event, with the assumption that we have already
     * been "selected" as the sprite to interact with.
     * This is a very simple method that only works because we
     * assume that the coordinate system has not been modified
     * by scales or rotations. You will need to modify this method
     * appropriately so it can handle arbitrary transformations.
     */
	@Override
	public void mouseDragged(MouseEvent e) {
		Point2D mouse = p2D(e.getPoint());		

		double dx = mouse.getX() - this.lastPoint.getX();
		double dy = mouse.getY() - this.lastPoint.getY();

		double originalX = this.trans.getTranslateX();
		double originalY = this.trans.getTranslateY();
		
		dx += originalX;
		dy += originalY;
		
		double originalAngle = Math.atan2(originalX, originalY);
		double currentAngle = Math.atan2(dx,dy);

    	pretransform(AffineTransform.getRotateInstance(originalAngle - currentAngle));
    	
		originalX = this.trans.getTranslateX();
		originalY = this.trans.getTranslateY();
		try{
			AffineTransform inverseTrans = AffineTransform.getTranslateInstance(originalX, originalY).createInverse();
			pretransform(inverseTrans);
		
			AffineTransform newTrans = AffineTransform.getTranslateInstance(dx,dy);
			pretransform(newTrans);
		}
		catch(NoninvertibleTransformException excep){}
		lastPoint = mouse;
	}


	public void mouseMoved(MouseEvent e) {
	}


	public void keyTyped(KeyEvent e) {
	}
	
 
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()){
		case KeyEvent.VK_UP:
			this.enlarge();
			break;
		case KeyEvent.VK_DOWN:
			this.reduce();
			break;
		case KeyEvent.VK_LEFT:
			this.color = this.color.brighter();
			break;
		case KeyEvent.VK_RIGHT:
			this.color = this.color.darker();
			break;
		case KeyEvent.VK_N:
			SSNode child = new SSNode(this.shape,this.color);
			Random rand = new Random();
			child.transform(AffineTransform.getTranslateInstance(rand.nextInt(100), rand.nextInt(100)));
			this.addChild(child);
			break;		
		case KeyEvent.VK_S: 
			switchShape();
			break;
		case KeyEvent.VK_C:
			switchColor();
			break;
		case KeyEvent.VK_D:
			if (!this.isRoot()){
				this.parent.removeChild(this);
			}
			break;
		}
	}
	

	public void keyReleased(KeyEvent e) {
	}
	
	
	
	
	
	/**
	 * Paint this node and its children.
	 */
	public void paintNode(Graphics2D g2) {
		/*
		 * You can change this code if you wish. Based on an in-class example
		 * it's going to be really tempting. You are advised, however, not to
		 * change it. Doing so will likely bring you hours of grief and much
		 * frustration.
		 */

		// Remember the transform being used when called
		AffineTransform t = g2.getTransform();

		g2.transform(this.getFullTransform());
		g2.setColor(this.color);
		g2.fill(this.shape);
		// Restore the transform.
		g2.setTransform(t);

		// Paint each child
		for (SSNode c : this.children) {
			c.paintNode(g2);
		}

		// Restore the transform.
		g2.setTransform(t);
	}

	/*
	 * There are a number of ways in which the handling of the transforms could
	 * be optimized. That said, don't bother. It's not the point of the
	 * assignment.
	 */

	/**
	 * Returns our local transform. Copy it just to make sure it doesn't get
	 * messed up.
	 */
	public AffineTransform getLocalTransform() {
		return new AffineTransform(this.trans);
	}

	/**
	 * Returns the full transform to this node from the root.
	 */
	public AffineTransform getFullTransform() {
		// Start with an identity matrix. Concatenate on the left
		// every local transformation matrix from here to the root.
		AffineTransform at = new AffineTransform();
		SSNode curNode = this;
		while (curNode != null) {
			at.preConcatenate(curNode.getLocalTransform());
			curNode = curNode.getParent();
		}
		return at;
	}

	/**
	 * Return the full inverse transform, starting with the root. That is, get
	 * the full transform from here to the root and then invert it, catching
	 * exceptions (there shouldn't be any).
	 */
	private AffineTransform getFullInverseTransform() {
		try {
			AffineTransform t = this.getFullTransform();
			AffineTransform tp = t.createInverse();
			return tp;
		} catch (NoninvertibleTransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new AffineTransform();
		}
	}

}
