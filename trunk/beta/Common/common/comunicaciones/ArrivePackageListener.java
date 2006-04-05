package common.comunicaciones;

import java.util.EventListener;

public interface ArrivePackageListener extends EventListener {
    public void validPackage(ArrivePackageEvent e);

}
