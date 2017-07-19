package draw.displayItems.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import draw.displayItems.DisplayableItem;
import main.DisplayWindow;

public class PassiveTextAreaDrawer implements DisplayableItem{
	
	private JTextArea jtf = new JTextArea(10,20);
	private Rectangle drawingRectangle;
	

	private PassiveTextAreaDrawer(Rectangle drawingRectangle) {
		this.drawingRectangle = drawingRectangle;
		jtf.setOpaque(false);
		jtf.setForeground(Color.GREEN);
		jtf.setFont(new Font("Courier New", Font.PLAIN, 10));
	}

	public static PassiveTextAreaDrawer newInstance(
			Rectangle drawingRectangle)
	{
		return new PassiveTextAreaDrawer(drawingRectangle);
	}

	@Override
	public void drawMe(Graphics2D g) {
		SwingUtilities.paintComponent(g, jtf, DisplayWindow.getFrame(), drawingRectangle);
	}
	
	public void append(String s)
	{
		jtf.append(s);
		while(jtf.getText().split("\n").length>40)
		{	
			jtf.setText(jtf.getText().substring(jtf.getText().indexOf("\n")+1));
		}
	}

	@Override
	public void terminate() {
	}

}
