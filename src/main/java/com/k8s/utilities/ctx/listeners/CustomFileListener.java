package com.k8s.utilities.ctx.listeners;

import com.k8s.utilities.ctx.manager.TrayMenuManager;
import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;

/**
 * Custom file listener class to listen external changes for configured .kube/config file.
 * @author Sangramsing
 */
public class CustomFileListener implements FileListener {

    private TrayMenuManager trayMenuManager = null;
    public CustomFileListener(TrayMenuManager trayMenuManager) {

        this.trayMenuManager = trayMenuManager;
    }

    @Override
    public void fileCreated(FileChangeEvent fileChangeEvent) {}

    @Override
    public void fileDeleted(FileChangeEvent fileChangeEvent) {}

    @Override
    public void fileChanged(FileChangeEvent fileChangeEvent) throws Exception {
        trayMenuManager.prepareKubernetesMenu();
    }
}
