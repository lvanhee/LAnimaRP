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

}
