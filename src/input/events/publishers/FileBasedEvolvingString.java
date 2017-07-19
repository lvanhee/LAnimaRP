package input.events.publishers;

import org.jdom2.Element;

import input.configuration.XMLParser;
import input.events.eventTypes.StringEvolvedEvent;
import input.events.eventTypes.StringEvolvedEventImpl;
import input.events.listeners.LAnimaRPEventListener;
import logic.data.PeriodicRefreshInfo;
import logic.data.fileLocators.FileLocator;
import logic.data.fileLocators.FileManagerUtils;
import logic.data.string.EvolvingString;

public class FileBasedEvolvingString implements EvolvingString, LAnimaRPEventPublisher<StringEvolvedEvent>
{

	private final PeriodicRefreshInfo pri;
	private final FileLocator fl;
	String lastString;
	private FileBasedEvolvingString(PeriodicRefreshInfo pri, FileLocator fl) {
		this.pri = pri;
		this.fl = fl;
		lastString = FileManagerUtils.getContentsAsStringFrom(fl);
		subscriber.publish(StringEvolvedEventImpl.newInstance(lastString));

		
		if(pri != PeriodicRefreshInfo.NEVER)
		{
			
			new Thread(new Runnable() {
				
				
				@Override
				public void run() {
					Thread.currentThread().setName("FileBasedEvolvingString"+fl+":"+pri);
					while(true)
					{
						try {
							Thread.sleep(pri.getRefreshPeriod());
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						String temp = FileManagerUtils.getContentsAsStringFrom(fl);
						if(!temp.equals(lastString))
							{
							 lastString = temp;
							 subscriber.publish(StringEvolvedEventImpl.newInstance(lastString));
							}
						
					}
				}
			}).start();
		}
	}

	public static FileBasedEvolvingString newInstance(Element e) {
		PeriodicRefreshInfo pri = PeriodicRefreshInfo.NEVER;
		if(XMLParser.hasPeriodicRefreshInfos(e))
			pri = XMLParser.parsePeriodicRefresh(e);
		FileLocator fl = XMLParser.parseFileLocator(e);
		return new FileBasedEvolvingString(pri,fl);
	}
	
	private final GenericPublisher<StringEvolvedEvent> subscriber = 
			GenericPublisher.newInstance();
	


	@Override
	public String getString() {
		return lastString;
	}
	
	public String toString()
	{
		return lastString;
	}

	@Override
	public void unsubscribe(LAnimaRPEventListener<StringEvolvedEvent> listener) {
		subscriber.unsubscribe(listener);
	}

	@Override
	public void subscribe(LAnimaRPEventListener<StringEvolvedEvent> el) {
		subscriber.subscribe(el);
	}

	@Override
	public void publish(StringEvolvedEvent newInstance) {
		subscriber.publish(newInstance);
	}

	public static FileBasedEvolvingString newInstance(FileLocator fileToObserve, PeriodicRefreshInfo pr) {
		return new FileBasedEvolvingString(pr, fileToObserve);
	}

	public FileLocator getFile() {
		return fl;
	}

}
