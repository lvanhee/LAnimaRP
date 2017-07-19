package logic.data;

public class PeriodicRefreshInfo {

	private final boolean refresh; 
	private final long period;
	private PeriodicRefreshInfo(boolean b) {
		refresh = b;
		period = -1;
	}

	public PeriodicRefreshInfo(long parseLong) {
		refresh = true;
		this.period = parseLong;
	}

	public static final PeriodicRefreshInfo NEVER = new PeriodicRefreshInfo(false);
	public static PeriodicRefreshInfo newInstance(long parseLong) {
		return new PeriodicRefreshInfo(parseLong);
	}

	public boolean isRefreshActive() {
		return refresh;
	}
	
	public long getRefreshPeriod()
	{
		return period;
	}
	
	public String toString()
	{
		return "Refresh:"+period;
	}

}
