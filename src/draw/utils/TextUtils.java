package draw.utils;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

public class TextUtils {

	public static int getWidthOf(String text, Font font) {
		return (int)(1.1*font.getStringBounds(text,
				new FontRenderContext(new AffineTransform(), true, true)
				).getWidth());
	}

	public static double getRequiredHeightForPrinting(String s, int width, Font myFont) {
		if(s.isEmpty())return 0;

		String toBePrinted = s;

		String currentLine = "";

		double requiredHeight = 0;

		while (!toBePrinted.isEmpty()) {
			char nextChar = toBePrinted.charAt(0);
			toBePrinted = toBePrinted.substring(1);
			String nextLine = currentLine+nextChar;
			double widthToBePrinted =getWidthOf(nextLine, myFont); 
			if(widthToBePrinted>width||nextChar=='\n')
			{
				nextLine = ""+nextChar;
				requiredHeight+= getHeightOfStringWhenPrinted(s, myFont);
			}
			else currentLine = nextLine;
		}
		return requiredHeight;
	}
	
	private static double getHeightOfStringWhenPrinted(String string, Font f) {
		AffineTransform affinetransform = new AffineTransform();
		FontRenderContext frc = new FontRenderContext(affinetransform,true,true);
		return (int)((f.getStringBounds(string , frc).getHeight()));
	}

}
