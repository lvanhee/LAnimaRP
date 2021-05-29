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
import draw.displayItems.DisplayableItem;
import draw.utils.TextUtils;
import input.configuration.TextParameters;
import main.DisplayWindow;

public class PassiveAppendTextAreaDrawer implements DisplayableItem{


	private Rectangle drawingRectangle;
	private final Font myFont = new Font("Courier New", Font.BOLD, 20);
	
	private List<String>stringPerLine = new LinkedList<>();	
	
	private final TextParameters tp;
	
	private String originalTextBeingPrinted="";

	private PassiveAppendTextAreaDrawer(Rectangle drawingRectangle, TextParameters textParameters) {
		this.drawingRectangle = drawingRectangle;
		stringPerLine.add("");
		this.tp = textParameters;
	}

	@Override
	public synchronized void drawMe(Graphics2D g) {
		g.setFont(myFont);
		int i = 1;
		g.setColor(tp.getColor());
		for(String s: stringPerLine)
		{
			g.drawString(s, drawingRectangle.x, drawingRectangle.y+getHeightPerLine()*i);
			i++;
		}
	}

	public synchronized void append(String s)
	{
		originalTextBeingPrinted+=s;
		appendInternal(s);
	}

	private void appendInternal(String s) {
		while(!s.isEmpty())
		{
		if(s.startsWith("\n"))
		{
			toNewLine();
			appendInternal(s.substring(1));
			return;
		}
		if(TextUtils.getWidthOf(getCurrentLine()+s.charAt(0),myFont)>drawingRectangle.getWidth())
		{
			if(isSplittableCharacter(s.charAt(0)))
			{
				toNewLine();
				appendInternal(s);
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
					appendInternal(s);
					return;
				}
				setCurrentLine(lastSplit);
				toNewLine();
				if(lastSplit.equals(current))
					appendInternal(s);
				else
					appendInternal(current.substring(lastSplit.length())+s);
				return;
			}
		}
		addToCurrentLine(s.charAt(0));
		s = s.substring(1);
		}
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
	
    

	public static PassiveAppendTextAreaDrawer newInstance(Rectangle r, TextParameters failureTextParameters) {
		return new PassiveAppendTextAreaDrawer(r,failureTextParameters);
	}
	
	public synchronized void setString(String s)
	{
		clear();
		append(s);
		/*for(int i =0; i < s.length();i++)
			append(s.charAt(""+i));*/
	}

	public synchronized void clear() {
		stringPerLine.clear();
		stringPerLine.add("");
		originalTextBeingPrinted = "";
	}

	public Rectangle getDrawingRectangle() {
		return drawingRectangle;
	}

	public Color getColor() {
		return tp.getColor();
	}

	public void setColor(Color color) {
		this.tp.setColor(color);
	}

	public boolean hasHustEndedALine() {
		return stringPerLine.get(stringPerLine.size()-1).equals("");
	}

	public String getDrawnString() {
		return originalTextBeingPrinted;
	}

	public double getRatioStringOverScreenSize(String s) {
		double requiredHeight = TextUtils.getRequiredHeightForPrinting(s,drawingRectangle.width, myFont);
		
		return requiredHeight/drawingRectangle.height;
	}


}
