package draw.displayItems.advanced.dnasca;

import java.awt.Color;
import java.util.Random;

public enum Nucleobase {A,C,T,G;

	public static Nucleobase randomBase() {
		return Nucleobase.values()[new Random().nextInt(4)];
	}

	public int toRGB() {
		return toColor().getRGB();
	}

	public Color toColor() {
		switch(this)
		{
		case A: return Color.green;
		case C: return Color.BLUE;
		case T: return Color.RED;
		case G:return Color.YELLOW;
		}
		throw new Error();
	}

}
