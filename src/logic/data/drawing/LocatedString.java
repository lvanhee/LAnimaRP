package logic.data.drawing;

import java.awt.Point;
import java.awt.geom.Point2D;

public class LocatedString {
	
	private String s;
	Point2D.Double p;
	public LocatedString(String string, Point2D.Double point) {
		this.s = string;
		p = point;
	}
	public Point2D.Double getPoint() {
		return p;
	}
	public String getString() {
		return s;
	}
	
	public String toString()
	{
		return p+":"+s;
	}
	public void setPoint(Point2D.Double point) {
		p = point;
	}

}
