package draw.displayItems.text.textprinter;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;
import draw.SmartScroller;
import draw.displayItems.DisplayableItem;
import main.DisplayWindow;

public class PassiveAppendTextAreaDrawer implements DisplayableItem{


	private Rectangle drawingRectangle;
	private final Font myFont = new Font("Courier New", Font.BOLD, 20);
	
	private List<String>stringPerLine = new LinkedList<>();	
	
	private final Color c;

	private PassiveAppendTextAreaDrawer(Rectangle drawingRectangle, Color c) {
		this.drawingRectangle = drawingRectangle;
		stringPerLine.add("");
		this.c = c;
	}

	@Override
	public synchronized void drawMe(Graphics2D g) {
		g.setFont(myFont);
		int i = 0;
		g.setColor(c);
		for(String s: stringPerLine)
		{
			g.drawString(s, drawingRectangle.x, drawingRectangle.y+getHeightPerLine()*i);
			i++;
		}
	}

	public synchronized void append(String s)
	{
		if(s.isEmpty())return;
		if(s.startsWith("\n"))
		{
			toNewLine();
			append(s.substring(1));
			return;
		}
		if(getLineWidth(getCurrentLine()+s.charAt(0))>drawingRectangle.getWidth())
		{
			if(isSplittableCharacter(s.charAt(0)))
			{
				toNewLine();
				append(s);
				return;
			}
			else
			{
				String current = getCurrentLine();
				String lastSplit = getLargestSplittablePartOf(current);				
				if(lastSplit.equals("")||(lastSplit.length()==1 && isSplittableCharacter(lastSplit.charAt(0))))
				{
					setCurrentLine(current+lastSplit);
					toNewLine();
					append(s);
					return;
				}
				setCurrentLine(lastSplit);
				toNewLine();
				if(lastSplit.equals(current))
					append(s);
				else
					append(current.substring(lastSplit.length())+s);
				return;
			}
		}
		addToCurrentLine(s.charAt(0));
		append(s.substring(1));
		return;
		
	}

	private void setCurrentLine(String lastSplit) {
		stringPerLine.set(stringPerLine.size()-1, lastSplit);
	}

	private String getLargestSplittablePartOf(String s) {
		String res = s;
		while(!res.isEmpty())
		{
			if(isSplittableCharacter(res.charAt(res.length()-1)))
				return res;
			res = res.substring(0, res.length()-1);
		}
		return s;
			
	}

	private boolean isSplittableCharacter(char charAt) {
		return charAt==' ';
	}

	private double getLineWidth(String string) {
		AffineTransform affinetransform = new AffineTransform();
		FontRenderContext frc = new FontRenderContext(affinetransform,true,true);
		return (int)((myFont.getStringBounds(getCurrentLine(), frc).getWidth()));
	}

	private String getCurrentLine() {
		return stringPerLine.get(stringPerLine.size()-1);
	}

	private synchronized void addToCurrentLine(char indexOf) {
		if(stringPerLine.isEmpty())
			stringPerLine.add("");
		stringPerLine.set(stringPerLine.size()-1, 
				stringPerLine.get(stringPerLine.size()-1)+indexOf);
	}

	private void toNewLine() {
		stringPerLine.add("");
		while(isListTooLarge())
			stringPerLine.remove(0);
	}

	private boolean isListTooLarge() {
		return getHeightPerLine()*(stringPerLine.size()+1) > drawingRectangle.getHeight();
	}

	private int getHeightPerLine() {
		AffineTransform affinetransform = new AffineTransform();
		FontRenderContext frc = new FontRenderContext(affinetransform,true,true);
		return (int)(myFont.getStringBounds(stringPerLine.get(stringPerLine.size()-1), frc).getHeight());
	}

	@Override
	public void terminate() {
	}
	
    

	public static PassiveAppendTextAreaDrawer newInstance(Rectangle r, Color c) {
		return new PassiveAppendTextAreaDrawer(r,c);
	}

}
