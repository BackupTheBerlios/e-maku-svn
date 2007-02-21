package common.gui.forms;

import java.util.EventListener;

public interface ExternalValueChangeListener extends EventListener {
	public void changeExternalValue(ExternalValueChangeEvent e);
}
