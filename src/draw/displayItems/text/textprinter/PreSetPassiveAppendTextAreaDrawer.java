package draw.displayItems.text.textprinter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.ReadPendingException;

import com.sun.jna.platform.FileUtils;

import draw.displayItems.DisplayableItem;
import draw.displayItems.text.textprinter.PreSetPassiveAppendTextAreaDrawer.AppendTypes;
import logic.data.fileLocators.FileLocator;

public class PreSetPassiveAppendTextAreaDrawer implements DisplayableItem {
	
	public enum AppendTypes
	{
		ONE_CHAR, 
		ONE_WORD_PER_PRESS
	}
	
	public enum RepetitionMode
	{
		REPEAT_FOREVER
	}
	private final PassiveAppendTextAreaDrawer drawer;
	private String text="";	
	private final FileLocator resetter;
	private final RepetitionMode repetition=RepetitionMode.REPEAT_FOREVER;
	private final AppendTypes at;
	

	
	private PreSetPassiveAppendTextAreaDrawer(Rectangle r, FileLocator fl, Color c, AppendTypes at)
	{
		drawer = PassiveAppendTextAreaDrawer.newInstance(r, c);
		resetter = fl;
		this.at = at;
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

	public static PreSetPassiveAppendTextAreaDrawer newInstance(Rectangle r, FileLocator textFile, Color c, AppendTypes at) {
		return new PreSetPassiveAppendTextAreaDrawer(r,textFile, c, at);
	}

	public void append(AppendTypes type) {
		if(text.isEmpty()&&repetition==RepetitionMode.REPEAT_FOREVER)
			text = readText(resetter.getFile());
		switch (type) {
		case ONE_CHAR:
			drawer.append(""+text.substring(0,1));
			text = text.substring(1);
			break;
			
		case ONE_WORD_PER_PRESS:
			int nextIndex = text.indexOf(" ")+1;
			drawer.append(text.substring(0,nextIndex));
			text = text.substring(nextIndex);
		break;

		default: throw new Error();
		}
		
	}
	

}
