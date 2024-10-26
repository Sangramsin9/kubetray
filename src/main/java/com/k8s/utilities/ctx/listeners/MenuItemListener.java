package com.k8s.utilities.ctx.listeners;

import com.k8s.utilities.ctx.util.Constants;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

/**
 * System Tray Context MenuItem listener class.
 * @author Sangramsing
 */
@Slf4j
public class MenuItemListener implements ActionListener {

    private int prevSelected;

    private List<String> items = null;

    public MenuItemListener(int prevSelected, List<String> items) {
        this.prevSelected = prevSelected;
        this.items = items;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ((Menu) e.getSource()).getItem(prevSelected).setFont(new Font(Constants.FONT_NAME_DEFAULT, Font.PLAIN, 12));
        OptionalInt indexOpt1 = IntStream.range(0, items.size())
                .filter(i -> e.getActionCommand().equals(items.get(i)))
                .findFirst();
        prevSelected = indexOpt1.getAsInt();
        ((Menu) e.getSource()).getItem(indexOpt1.getAsInt()).setFont(new Font(Constants.FONT_NAME_ITALIC, Font.BOLD, 14));
        try {
            Runtime.getRuntime().exec(String.format(Constants.KUBECTL_USE_CONTEXTS_COMMAND, e.getActionCommand()));
            log.info("Kubernetes context successfully changed to {}", e.getActionCommand());
        } catch (IOException ex) {
            log.error("Error while executing {} command exception : {}", Constants.KUBECTL_USE_CONTEXTS_COMMAND, ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
}
