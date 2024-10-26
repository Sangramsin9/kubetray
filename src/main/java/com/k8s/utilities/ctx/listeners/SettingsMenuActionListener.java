package com.k8s.utilities.ctx.listeners;

import com.k8s.utilities.ctx.util.ConfigFileUtils;
import com.k8s.utilities.ctx.util.Constants;
import com.k8s.utilities.ctx.manager.TrayMenuManager;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Settings menu action listener class to update .kube/config file.
 * @author Sangramsing
 */
@Slf4j
public class SettingsMenuActionListener implements ActionListener {

    private TrayMenuManager trayMenuManager;

    private boolean isAlreadyOpened;

    private JDialog settingsDialog;

    private JButton okButton;

    private JLabel fileLabel;

    private JFileChooser jc;

    public SettingsMenuActionListener(TrayMenuManager trayMenuManager) {
        this.trayMenuManager = trayMenuManager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
            if (isAlreadyOpened) {
                if (settingsDialog != null) {
                    settingsDialog.requestFocus();
                }
                return;
            }
            settingsDialog = new JDialog();
            settingsDialog.setIconImage(Toolkit.getDefaultToolkit().getImage(Thread.currentThread().getContextClassLoader().getResource("images/gear.png")));
            settingsDialog.setTitle(Constants.FILE_CHOOSER_TITLE);
            settingsDialog.setSize(new Dimension(400,150));
            settingsDialog.setLayout(new BorderLayout());
            settingsDialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width  - settingsDialog.getSize().width) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - settingsDialog.getSize().height) / 2);

            JPanel dialogPanel = new JPanel(new BorderLayout());
            JPanel fileChoosePanel = new JPanel();
            fileChoosePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            JPanel okButtonPanel = new JPanel();
            okButtonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            okButton = new JButton();
            okButton.setText("OK");
            okButton.setSize(20, 20);
            okButton.setEnabled(false);

            fileLabel = new JLabel(ConfigFileUtils.getConfigFilePath());
            fileLabel.setSize(60,60);
            JButton fileChooser = new JButton();
            fileChooser.setText("Choose .kube\\config file");
            fileChooser.setSize(20, 20);
            jc = new JFileChooser();
            jc.setCurrentDirectory(new File(ConfigFileUtils.getConfigFilePath()));
            fileChooser.addActionListener(e1 -> processFileSelection());
            okButton.addActionListener(ae -> updateConfigToTmpLocation());
            fileChoosePanel.add(fileChooser);
            fileChoosePanel.add(fileLabel);
            okButtonPanel.add(okButton);
            dialogPanel.add(fileChoosePanel, BorderLayout.NORTH);
            dialogPanel.add(okButtonPanel, BorderLayout.CENTER);
            settingsDialog.add(dialogPanel);
            settingsDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    isAlreadyOpened = false;
                }
            });
            settingsDialog.setVisible(true);
            isAlreadyOpened = true;
    }

    private void processFileSelection() {
        jc.showOpenDialog(settingsDialog);
        if (jc.getSelectedFile() != null) {
            fileLabel.setText(jc.getSelectedFile().getAbsolutePath());
            fileLabel.setForeground(Color.BLACK);
            okButton.setEnabled(true);
        }
    }

    private void updateConfigToTmpLocation() {
        try {
            Yaml yaml = new Yaml();
            yaml.load(new FileInputStream(jc.getSelectedFile().getAbsolutePath()));
            //Update latest .kube/config path
            ConfigFileUtils.updateConfigPath(jc.getSelectedFile().getAbsolutePath());
            settingsDialog.dispose();
            trayMenuManager.prepareKubernetesMenu();
            isAlreadyOpened = false;
        } catch (YAMLException | IOException ex) {
            if (ex instanceof  YAMLException) {
                fileLabel.setText(Constants.INVALID_CONFIG_MSG);
                fileLabel.setForeground(Color.RED);
                okButton.setEnabled(false);
                return;
            }
            throw new RuntimeException(ex);
        }
    }
}
