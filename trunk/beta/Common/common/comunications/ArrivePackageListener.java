package common.comunications;

import java.util.EventListener;

public interface ArrivePackageListener extends EventListener {
    public void validPackage(ArrivePackageEvent e);

}
