package draw.displayItems.videos;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.jdom2.Element;

import draw.displayItems.DisplayableItem;
import input.configuration.LAnimaRPContext;
import input.configuration.XMLParser;
import input.events.triggers.PauseTrigger;
import logic.data.fileLocators.URLLocator;
import logic.data.fileLocators.FileLocatorInputFileProvider;
import logic.data.fileLocators.InputFileProvider;

public class VideoDisplayer implements DisplayableItem {

	//do not de-allocate this variable, else the program dies
	private final PassiveVideoDisplayer vd;
	private boolean isTerminating = false;
	private VideoDisplayer(
			Rectangle localisation, 
			final InputFileProvider ifp,
			final PauseTrigger trigger)
	{
		vd = PassiveVideoDisplayer.newInstance(localisation);
		
		trigger.setDisplayer(vd);

		new Thread(new Runnable() {

			@Override
			public void run() {
				Thread.currentThread().setName("Loop video slideshow");
				while(!isTerminating && ifp.hasNext())
				{
					if(!vd.isPlaying() && !trigger.isPauseRequested())
						playNextPlayableVideo();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			private void playNextPlayableVideo() {
				while(!vd.isPlaying())
				{
					vd.playNewVideoVideo(ifp.next());
				}
			}
		}
				).start();
	}

	public static DisplayableItem newInstance(Element e, LAnimaRPContext context) {
			URLLocator fl = XMLParser.parsePathLocator(e);
			Rectangle r = XMLParser.parseRectangle(e);
			
			PauseTrigger trigger = XMLParser.parsePauseTrigger(e);
			
			return new VideoDisplayer(r,
					FileLocatorInputFileProvider.newInstance(
							fl,
							FileLocatorInputFileProvider.Repetition.ENDLESS,
							FileLocatorInputFileProvider.OrderingForFolders.RANDOM, 
							x ->
							!x.isDirectory() &&
							x.canRead() &&
							(x.getName().endsWith(".avi")||
									x.getName().endsWith(".mp4"))
							),
					trigger
					);
		}

		@Override
		public void drawMe(Graphics2D g) {
			vd.drawMe(g);
		}

		@Override
		public void terminate() {
			vd.terminate();
			isTerminating = true;
		}

	}
