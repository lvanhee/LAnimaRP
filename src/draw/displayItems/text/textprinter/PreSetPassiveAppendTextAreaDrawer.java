package draw.displayItems.text.textprinter;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import draw.displayItems.DisplayableItem;
import draw.displayItems.text.textprinter.PreSetPassiveAppendTextAreaDrawer.AppendTypes;
import logic.data.fileLocators.FileLocator;

public class PreSetPassiveAppendTextAreaDrawer implements DisplayableItem {
	
	public enum AppendTypes
	{
		ONE_CHAR
	}
	
	
	private final PassiveAppendTextAreaDrawer drawer;
	
	private String text="";	

	
	private PreSetPassiveAppendTextAreaDrawer(Rectangle r, FileLocator fl)
	{
		drawer = PassiveAppendTextAreaDrawer.newInstance(r);
		text = readText(fl.getFile());
	}
	
	@Override
	public void drawMe(Graphics2D g) {
		drawer.drawMe(g);
	}

	@Override
	public void terminate() {
		drawer.terminate();
	}
	
	private String readText(File f) {
		try {
		  BufferedReader br = new BufferedReader(new FileReader(f));
		    
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();

		        while (line != null) {
		            sb.append(line);
		            sb.append(System.lineSeparator());
		            line = br.readLine();
		        }
		        br.close();
		        return sb.toString();
		    } catch (IOException e) {
				e.printStackTrace();
				throw new Error();
			}
	}

	public static PreSetPassiveAppendTextAreaDrawer newInstance(Rectangle r, FileLocator textFile) {
		return new PreSetPassiveAppendTextAreaDrawer(r,textFile);
	}

	public void append(AppendTypes oneChar) {
		switch (oneChar) {
		case ONE_CHAR:
			drawer.append(""+text.substring(0,1));
			text = text.substring(1);
			break;

		default: throw new Error();
		}
		
	}
	

}
