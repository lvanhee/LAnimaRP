package input.configuration;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.swing.ImageIcon;

import org.jdom2.Element;

import com.sun.jna.platform.FileUtils;

import draw.displayItems.DisplayableItem;
import draw.displayItems.images.ImageDisplayer;
import draw.displayItems.text.textprinter.PassiveAppendTextAreaDrawer;
import input.events.eventTypes.LAnimaRPKeyEventImpl;
import input.events.listeners.GenericLAnimaRPEventListener;
import input.events.publishers.KeyMonitorer;
import logic.data.fileLocators.FileLocator;
import logic.data.fileLocators.FileManagerUtils;
import logic.data.fileLocators.StaticFileLocator;
import logic.data.fileLocators.FileManagerUtils.FileType;

public class FileSystem implements DisplayableItem{
	private enum Status{
		WAITING_FOR_FILENAME, 
		DISPLAYING_FILE, 
		SHOWING_FAILURE_MESSAGE}
	
	
	private List<GenericMediumDisplayer> gmd=new LinkedList<>();
	private final Rectangle displayBox;
	private final LAnimaRPContext context;
	
	private final FileLocator folderOnHardDrive;
	
	private Status currentStatus = Status.WAITING_FOR_FILENAME;
	private String currentString = "";
	private final Optional<FileLocator>soundWhenTyping;
	
	private final Optional<ImageDisplayer> backgroundWhenWaitingforFilename;
	private final PassiveAppendTextAreaDrawer queryDrawer;
	private final PassiveAppendTextAreaDrawer errorMessageDrawer;
	
	private final TextParameters printingTextParameters=TextParameters.DEFAULT;
	
	private final String FAILURE_MESSAGE = "Failed to find the file:";
	
	
	private FileSystem(Rectangle displayBox,
			FileLocator folderOnHardDrive, 
			Optional<ImageDisplayer> backgroundWhenWaitingforFilename2,
			PassiveAppendTextAreaDrawer queryDrawer, 
			TextParameters failureTextParameters, 
			LAnimaRPContext context,
			Optional<FileLocator> soundWhenTyping) {
		this.displayBox = displayBox;
		this.context = context;
		this.folderOnHardDrive = folderOnHardDrive;
		this.backgroundWhenWaitingforFilename = backgroundWhenWaitingforFilename2;
		this.queryDrawer = queryDrawer;
		errorMessageDrawer= PassiveAppendTextAreaDrawer.newInstance(this.queryDrawer.getDrawingRectangle(), failureTextParameters);
		this.soundWhenTyping = soundWhenTyping;
		
		
		
		GenericLAnimaRPEventListener.newInstance(KeyMonitorer.INSTANCE,
				x->
		{
			LAnimaRPKeyEventImpl event = (LAnimaRPKeyEventImpl)x;
			if(!event.isKeyPressed())return;
			
			if(event.getKeyEvent().getKeyCode()==KeyEvent.VK_ENTER)
			{
				switchDisplayMode();
			}
			else
				{ if(currentStatus != Status.WAITING_FOR_FILENAME)return;
				char c = event.getKeyEvent().getKeyChar();
				if((c>='A'&& c <= 'z') || c>='0' && c<='9')
				{
					currentString += c;
					queryDrawer.setString(currentString);
				}
				if(event.getKeyEvent().getKeyCode()==8)
				{
					if(currentString.isEmpty()) return;
					currentString = currentString.substring(0, currentString.length()-1);
					queryDrawer.setString(currentString);

				}
				
				}
		});
	}

	private void switchDisplayMode() {
		System.out.println("Switching"+currentStatus);
		switch(currentStatus)
		{
		case DISPLAYING_FILE:
			currentStatus =  Status.WAITING_FOR_FILENAME;
			gmd.stream().forEach(x->x.terminate());
			gmd = null;
			break;

		case WAITING_FOR_FILENAME:
			Set<File> files = FileManagerUtils.getAllFilesMatchingNameWithoutExtension(folderOnHardDrive.getFile(),currentString);
			if(files.size()==0)
			{
				setToFailureMode();
			}
			else if(files.size()>=1)
			{
				int nbDisplayedItems = (int) (files.size() - files.stream().filter(x->FileManagerUtils.getTypeOf(x)==FileType.SOUND).count());
				int widthPerSplit = displayBox.width/nbDisplayedItems;
				int currentX = displayBox.x;
				gmd = new LinkedList<>();
				
				for(File f: files){
					Rectangle displayRectangle = new Rectangle(currentX, displayBox.y, widthPerSplit, displayBox.height);
					if(files.size() > 1 && FileManagerUtils.getTypeOf(f)==FileType.SOUND)
						displayRectangle=new Rectangle(0, 0, 0, 0);
					
					gmd.add(GenericMediumDisplayer.newInstance(
							displayRectangle, f, printingTextParameters, context, soundWhenTyping));
					currentX+=displayRectangle.width;
				}
				currentStatus = Status.DISPLAYING_FILE;
			}
			clearText();
			break;
		case SHOWING_FAILURE_MESSAGE:break;
			default: throw new Error();
		}
	}

	private void setToFailureMode() {
		currentStatus = Status.SHOWING_FAILURE_MESSAGE;
		errorMessageDrawer.setString(FAILURE_MESSAGE+" "+currentString);
		
		Color initialColor = errorMessageDrawer.getColor();
		new Thread(()->
		{
			while(true)
			{
				Color c = errorMessageDrawer.getColor();
				if(c.getAlpha()==0) break;
				errorMessageDrawer.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()-1));
				try {
					Thread.sleep(4);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			errorMessageDrawer.setColor(initialColor);
			currentStatus = Status.WAITING_FOR_FILENAME;
		}
		).start();
	}

	private void clearText() {
		currentString = "";
		queryDrawer.clear();
	}

	public static FileSystem newInstance(Element e, LAnimaRPContext context) {
		Rectangle displayBox = XMLParser.parseRectangle(e);
		
		Optional<FileLocator> soundWhenTyping = Optional.empty();
		if(e.getChild(XMLKeywords.TEXT_DISPLAYER_CONFIGURATION.getName())!=null)
			soundWhenTyping = Optional.of(StaticFileLocator.newInstance(e.getChild(XMLKeywords.TEXT_DISPLAYER_CONFIGURATION.getName())
					.getAttributeValue(XMLKeywords.SOUND_ON_TYPE.getName())));
			
			FileLocator fl = XMLParser.parsePathLocator(e, context);
		
		PassiveAppendTextAreaDrawer queryDrawer= PassiveAppendTextAreaDrawer.newInstance(
				XMLParser.parseRectangle(e.getChild(XMLKeywords.FILENAME_QUERY_AREA.getName()))
				, TextParameters.DEFAULT);
		Optional<ImageDisplayer>backgroundWhenWaitingforFilename = Optional.empty();
		if(e.getChild(XMLKeywords.IMAGE_WHEN_TYPING.getName())!=null)
			backgroundWhenWaitingforFilename= Optional.of(ImageDisplayer.newInstance(
					e.getChild(XMLKeywords.IMAGE_WHEN_TYPING.getName()).getChild(XMLKeywords.IMAGE_ANIMATION.getName()), context));
		
		TextParameters failureTextParameters = TextParameters.DEFAULT;
		if(e.getChild(XMLKeywords.FAILURE_TEXT.getName())!= null)
			failureTextParameters = XMLParser.parseTextParameters(e.getChild(XMLKeywords.FAILURE_TEXT.getName()));
		
		return new FileSystem(displayBox, fl, backgroundWhenWaitingforFilename, queryDrawer, failureTextParameters, context, soundWhenTyping);
	}

	@Override
	public void drawMe(Graphics2D g) {

		switch(currentStatus)
		{
		case WAITING_FOR_FILENAME:
			if(backgroundWhenWaitingforFilename.isPresent())
				backgroundWhenWaitingforFilename.get().drawMe(g);
			queryDrawer.drawMe(g);
			break;
		case SHOWING_FAILURE_MESSAGE:
			if(backgroundWhenWaitingforFilename.isPresent())
				backgroundWhenWaitingforFilename.get().drawMe(g);
			errorMessageDrawer.drawMe(g);
			break;
		case DISPLAYING_FILE:
			gmd.stream().forEach(x->x.drawMe(g));
			break;
		default: throw new Error();
		}
	}

	@Override
	public void terminate() {
		gmd.stream().forEach(x->x.terminate());
	}
}
