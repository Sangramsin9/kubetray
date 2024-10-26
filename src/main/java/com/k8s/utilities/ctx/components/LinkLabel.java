package com.k8s.utilities.ctx.components;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Custom Label component created for adding clickable link with default system browser.
 * @author Sangramsing
 */
@Slf4j
public class LinkLabel extends JEditorPane {
    private static final long serialVersionUID = 1L;

    public LinkLabel(String htmlBody) {
        super("text/html", "<html><body style=\"" + getStyle() + "\">" + htmlBody + "</body></html>");
        addHyperlinkListener(e -> {
            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                openLink(e.getURL());
            }
        });
        setEditable(false);
        setBorder(null);
    }

    private static StringBuffer getStyle() {
        // for copying style
        JLabel label = new JLabel();
        Font font = label.getFont();
        Color color = label.getBackground();

        StringBuffer style = new StringBuffer("font-family:" + font.getFamily() + ";");
        style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
        style.append("font-size:" + font.getSize() + "pt;");
        style.append("background-color: rgb("+color.getRed()+","+color.getGreen()+","+color.getBlue()+");");
        return style;
    }
    private boolean openLink(URL url) {
        try {
            return openLink(url.toURI());
        } catch (URISyntaxException e) {
            log.error("Exception while Opening URL ", e);
        }
        return false;
    }

    private boolean openLink(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}

