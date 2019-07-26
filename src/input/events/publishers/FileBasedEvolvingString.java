package input.events.publishers;

import java.util.List;

import org.jdom2.Element;

import input.configuration.LAnimaRPContext;
import input.configuration.XMLParser;
import input.events.eventTypes.StringEvolvedEvent;
import input.events.eventTypes.StringEvolvedEventImpl;
import input.events.listeners.LAnimaRPEventListener;
import logic.data.PeriodicRefreshInfo;
import logic.data.fileLocators.URLLocator;
import logic.data.fileLocators.URLManagerUtils;
import logic.data.string.EvolvingString;

public class FileBasedEvolvingString implements EvolvingString, LAnimaRPEventPublisher<StringEvolvedEvent>
{

	private final PeriodicRefreshInfo pri;
	private final URLLocator fl;
	private String lastString;
	private FileBasedEvolvingString(PeriodicRefreshInfo pri, URLLocator fl) {
		this.pri = pri;
		this.fl = fl;
		lastString = URLManagerUtils.getContentsAsStringFrom(fl);

		
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
						String temp = URLManagerUtils.getContentsAsStringFrom(fl);
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

	public static FileBasedEvolvingString newInstance(Element e, LAnimaRPContext context) {
		PeriodicRefreshInfo pri = PeriodicRefreshInfo.NEVER;
		if(XMLParser.hasPeriodicRefreshInfos(e))
			pri = XMLParser.parsePeriodicRefresh(e);
		URLLocator fl = XMLParser.parseFileLocator(e, context);
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

	public static FileBasedEvolvingString newInstance(URLLocator fileToObserve, PeriodicRefreshInfo pr) {
		return new FileBasedEvolvingString(pr, fileToObserve);
	}

	public URLLocator getFile() {
		return fl;
	}

}
