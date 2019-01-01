package draw.displayItems.sound;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Triangle implements Shape {
	
	private final GeneralPath gp;

	private Triangle(Point point, Point point2, Point point3) {
		  gp = new GeneralPath();
		  gp.moveTo(point.x, point.y);
		  gp.lineTo(point2.x, point2.y);
		  gp.lineTo(point3.x, point3.y);
		  gp.lineTo(point.x, point.y);		  
	}

	@Override
	public boolean contains(Point2D p) {
		return gp.contains(p);
	}

	@Override
	public boolean contains(Rectangle2D r) {
		return gp.contains(r);
	}

	@Override
	public boolean contains(double x, double y) {
		return gp.contains(x, y);
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		return gp.contains(x,y,w,h);
	}

	@Override
	public Rectangle getBounds() {
		return gp.getBounds();
	}

	@Override
	public Rectangle2D getBounds2D() {
		return gp.getBounds2D();
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at) {
		return gp.getPathIterator(at);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return gp.getPathIterator(at, flatness);
	}

	@Override
	public boolean intersects(Rectangle2D r) {
		return gp.intersects(r);
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		return gp.intersects(x,y,w,h);
	}

	public static Triangle newInstance(Point point, Point point2, Point point3) {
		return new Triangle(point,point2, point3);
	}

}
