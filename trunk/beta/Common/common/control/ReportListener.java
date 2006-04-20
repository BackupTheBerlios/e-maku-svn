package common.control;
import java.util.EventListener;

public interface ReportListener extends EventListener {
	public void arriveReport(ReportEvent e); 
}