package input.events.publishers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private boolean isActive=false;
	
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
						if(! isActive) continue;
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
		return newInstance(fl,pri);
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
		isActive = subscriber.hasListeners();
	}

	@Override
	public void subscribe(LAnimaRPEventListener<StringEvolvedEvent> el) {
		subscriber.subscribe(el);
		isActive = true;
	}

	@Override
	public void publish(StringEvolvedEvent newInstance) {
		subscriber.publish(newInstance);
	}

	private static final Map<URLLocator, FileBasedEvolvingString> cache = new HashMap<>();
	public static FileBasedEvolvingString newInstance(URLLocator fileToObserve, PeriodicRefreshInfo pr) {
		if(cache.containsKey(fileToObserve))
			return cache.get(fileToObserve);
		FileBasedEvolvingString res = new FileBasedEvolvingString(pr, fileToObserve);
		cache.put(fileToObserve, res);
		return res;
	}

	public URLLocator getFile() {
		return fl;
	}

}
