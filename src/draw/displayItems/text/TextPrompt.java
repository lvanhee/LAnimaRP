package draw.displayItems.text;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;
import java.util.Optional;

import draw.displayItems.DisplayableItem;
import draw.displayItems.text.textprinter.PassiveAppendTextAreaDrawer;
import draw.displayItems.text.textprinter.PreSetPassiveAppendTextAreaDrawer;
import draw.displayItems.text.textprinter.PreSetPassiveAppendTextAreaDrawer.AppendTypes;
import draw.displayItems.text.textprinter.PreSetPassiveAppendTextAreaDrawer.RepetitionMode;
import input.configuration.TextParameters;
import logic.data.fileLocators.URLLocator;
import logic.data.fileLocators.StaticURLPathLocator;

public class TextPrompt implements DisplayableItem {
	
	private final PreSetPassiveAppendTextAreaDrawer tp;
	private boolean keepTyping = true;

	private TextPrompt(Rectangle rectangle,
			URLLocator localFileFor, int millisBetweenActions, 
			TextParameters textP,
			PreSetPassiveAppendTextAreaDrawer.AppendTypes te, 
			RepetitionMode repetitionMode, Optional<URLLocator> soundWhenTyping) {
		tp = PreSetPassiveAppendTextAreaDrawer.newInstance(rectangle, localFileFor, textP,te, repetitionMode, soundWhenTyping);
		
		new Thread(
				new Runnable() {
					
					@Override
					public void run() {
						while(keepTyping &&! tp.isOver())
						{
							try {
							if(tp.hasJustEndedALine())
								Thread.sleep(500);
							tp.append(te);
							
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
			URLLocator localFileFor, 
			int millisBetweenActions,
			PreSetPassiveAppendTextAreaDrawer.AppendTypes te, RepetitionMode repetitionMode, TextParameters yp, Optional<URLLocator> soundWhenTyping) {
		return new TextPrompt(rectangle, localFileFor, millisBetweenActions, yp,te, repetitionMode, soundWhenTyping);
	}

	public static DisplayableItem newInstance(Rectangle displayZone, File x, int millisBetweenActions,
			AppendTypes oneChar, RepetitionMode repetitionMode, TextParameters tp, 
			Optional<URLLocator> soundWhenTyping) {
		return newInstance(displayZone, StaticURLPathLocator.newInstance(x), millisBetweenActions, oneChar, repetitionMode, tp, soundWhenTyping);
	}

}
