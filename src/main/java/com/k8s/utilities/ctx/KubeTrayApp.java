package com.k8s.utilities.ctx;

import com.k8s.utilities.ctx.manager.TrayMenuManager;
import com.k8s.utilities.ctx.util.Constants;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

/**
 * Simple Java Swing utility app, easy to use System Tray menu for selecting the default Kubernetes context (cluster).
 * @author Sangramsing
 * https://github.com/sangramsin9/kubetray
 */
@Slf4j
public class KubeTrayApp {

    public static void main(String []args) throws Exception {
        if(!SystemTray.isSupported()){
            log.warn("System tray is not supported !!! ");
            return;
        }
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(Thread.currentThread().getContextClassLoader()
                .getResource("images/wheel7.png")),
                Constants.KUBERNETES_CONTEXT_SELECT, new TrayMenuManager().buildPopupMenu());
        trayIcon.setImageAutoSize(true);
        SystemTray.getSystemTray().add(trayIcon);
    }


}
