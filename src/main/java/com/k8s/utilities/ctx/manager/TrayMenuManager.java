package com.k8s.utilities.ctx.manager;

import com.k8s.utilities.ctx.listeners.MenuItemListener;
import com.k8s.utilities.ctx.listeners.SettingsMenuActionListener;
import com.k8s.utilities.ctx.util.ConfigFileUtils;
import com.k8s.utilities.ctx.util.Constants;
import com.k8s.utilities.ctx.components.LinkLabel;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * System Tray Icon context menu manager class.
 * @author Sangramsing
 */
@Slf4j
public class TrayMenuManager {

    private  Menu kubernetesMenu;
    private MenuItemListener menuItemListener;
    public TrayMenuManager() throws IOException {
        this.kubernetesMenu = new Menu(Constants.KUBERNETES);
        ConfigFileUtils.startWatcher(this);
    }

    public void prepareKubernetesMenu() throws FileNotFoundException {
        // Clean up existing menus and menu listener on reload.
        cleanup();

        //Load .Kub/config yaml contexts
        Map<String, Object> data =  ConfigFileUtils.getContextsData();
        String currentCtx = (String)data.get("current-context");
        List<String> k8sContextList = ((ArrayList<Map<String, String>>) data.get("contexts")).stream().map(obj -> obj.get("name")).collect(Collectors.toList());
        for (String context : k8sContextList) {
            MenuItem menuItem = new MenuItem(context);
            if (currentCtx.equalsIgnoreCase(context)) {
                Font font = new Font(Constants.FONT_NAME_ITALIC, Font.BOLD, 14);
                menuItem.setFont(font);
            }
            kubernetesMenu.add(menuItem);
        }
        registerMenuListener(currentCtx, k8sContextList);
    }

    private void registerMenuListener(String currentCtx, List<String> k8sContextList) {
        OptionalInt indexOpt = IntStream.range(0, k8sContextList.size())
                .filter(i -> currentCtx.equals(k8sContextList.get(i)))
                .findFirst();
        this.menuItemListener = new MenuItemListener(indexOpt.getAsInt(), k8sContextList);
        //Register Menu Item Action Listener
        kubernetesMenu.addActionListener(menuItemListener);
        log.info("Registering System Tray Menu. Listener activated.");
    }

    public PopupMenu buildPopupMenu() throws FileNotFoundException {
        prepareKubernetesMenu();
        PopupMenu trayPopupMenu = new PopupMenu();
        trayPopupMenu.add(kubernetesMenu);

        trayPopupMenu.addSeparator();

        MenuItem settingsMenu = new MenuItem(Constants.SETTINGS);
        settingsMenu.addActionListener(new SettingsMenuActionListener(this));
        trayPopupMenu.add(settingsMenu);

        trayPopupMenu.addSeparator();

        //Register aboutMenu Listener
        MenuItem aboutMenu = new MenuItem(Constants.ABOUT);
        aboutMenu.addActionListener(e -> JOptionPane.showMessageDialog(null, new LinkLabel(Constants.ABOUT_MSG), "About", JOptionPane.INFORMATION_MESSAGE));
        trayPopupMenu.add(aboutMenu);

        trayPopupMenu.addSeparator();

        //Register closeMenu Listener
        MenuItem closeMenu = new MenuItem(Constants.CLOSE);
        closeMenu.addActionListener(e -> System.exit(0));
        trayPopupMenu.add(closeMenu);
        log.info("KubeTray system tray menu component created successfully.");
        return trayPopupMenu;
    }

    private void cleanup() {
        kubernetesMenu.removeAll();
        kubernetesMenu.removeActionListener(menuItemListener);
    }

}
