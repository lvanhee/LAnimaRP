package draw.displayItems.text;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;

import draw.displayItems.DisplayableItem;
import draw.displayItems.text.textprinter.PassiveAppendTextAreaDrawer;
import draw.displayItems.text.textprinter.PreSetPassiveAppendTextAreaDrawer;
import logic.data.fileLocators.FileLocator;

public class TextPrompt implements DisplayableItem {
	
	private final PreSetPassiveAppendTextAreaDrawer tp;
	private boolean keepTyping = true;

	private TextPrompt(Rectangle rectangle, FileLocator localFileFor, int millisBetweenActions, 
			PreSetPassiveAppendTextAreaDrawer.AppendTypes te) {
		tp = PreSetPassiveAppendTextAreaDrawer.newInstance(rectangle, localFileFor, Color.green);
		
		new Thread(
				new Runnable() {
					
					@Override
					public void run() {
						while(keepTyping)
						{
							tp.append(te);
							try {
								Thread.sleep(millisBetweenActions);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}).start();
	}

	@Override
	public void drawMe(Graphics2D g) {
		tp.drawMe(g);
	}

	@Override
	public void terminate() {
		keepTyping = false;
	}

	public static TextPrompt newInstance(
			Rectangle rectangle, 
			FileLocator localFileFor, 
			int millisBetweenActions,
			PreSetPassiveAppendTextAreaDrawer.AppendTypes te) {
		return new TextPrompt(rectangle, localFileFor, millisBetweenActions, te);
	}

}
