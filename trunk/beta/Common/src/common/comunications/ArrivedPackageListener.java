package common.comunications;

import java.util.EventListener;

public interface ArrivedPackageListener extends EventListener {
    public void validPackage(ArrivedPackageEvent e);

}
