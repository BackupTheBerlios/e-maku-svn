package com.kazak.smi.lib.network;

import java.util.EventListener;

public interface PackageComingListener extends EventListener {
    public void validPackage(ArrivedPackageEvent e);

}
