package com.k8s.utilities.ctx.util;

import com.k8s.utilities.ctx.listeners.CustomFileListener;
import com.k8s.utilities.ctx.manager.TrayMenuManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

/**
 * Config file manager will continuously monitor configured .kube/config file for any changes.
 * @author Sangramsing
 */
@Slf4j
public class ConfigFileUtils {

    static  String DEFAULT_CONFIG_FILE_PATH = System.getProperty("user.home") + File.separator + ".kube" + File.separator+ "config";
    static String TEMP_FILE = System.getProperty("java.io.tmpdir") + File.separator + "kubetray.tmp";

    static String KUBE_CONFIG_FILE_PATH = initializePath();

    public static void startWatcher(TrayMenuManager trayMenuManager) throws FileSystemException {
        //Monitor .kube/config file for any changes.
        FileSystemManager fsManager = VFS.getManager();
        FileObject kubeConfigFile = fsManager.resolveFile(KUBE_CONFIG_FILE_PATH);
        DefaultFileMonitor fm = new DefaultFileMonitor(new CustomFileListener(trayMenuManager));
        fm.setRecursive(true);
        fm.addFile(kubeConfigFile);
        fm.start();
        log.info("Started file watcher for file:{}",KUBE_CONFIG_FILE_PATH);
    }

    public static String initializePath() {
        try {
            // Load saved .kube/config file location
            FileObject fileObject = VFS.getManager().resolveFile(TEMP_FILE);
            if (fileObject.exists() && fileObject.isFile()) {
                BufferedInputStream inputStreamReader = null;
                try {
                    inputStreamReader = new BufferedInputStream(fileObject.getContent().getInputStream());
                    String kubeConfigPath = "";
                    while (inputStreamReader.available() > 0) {
                        char c = (char) inputStreamReader.read();
                        kubeConfigPath = kubeConfigPath.concat(String.valueOf(c));
                    }
                    KUBE_CONFIG_FILE_PATH = kubeConfigPath;
                } finally {
                    inputStreamReader.close();
                }
            } else {
                OutputStream outputStream = null;
                try {
                    outputStream = fileObject.getContent().getOutputStream();
                    outputStream.write(DEFAULT_CONFIG_FILE_PATH.getBytes());
                } finally {
                    outputStream.close();
                    VFS.close();
                }
                KUBE_CONFIG_FILE_PATH = DEFAULT_CONFIG_FILE_PATH;
            }
            log.info("Initialized .kube/config file path as {}", KUBE_CONFIG_FILE_PATH);
            return KUBE_CONFIG_FILE_PATH;
        } catch (IOException ex) {
            return DEFAULT_CONFIG_FILE_PATH;
        }
    }

    public static void updateConfigPath(String newPath) throws IOException {
        FileObject fileObject = null;
        OutputStream out = null;
        try{
            fileObject = VFS.getManager().resolveFile(TEMP_FILE);
            fileObject.delete();
            fileObject.createFile();
            out = fileObject.getContent().getOutputStream();
            out.write(newPath.getBytes());
            KUBE_CONFIG_FILE_PATH = newPath;
            log.info("New .kube/config file path update successfully with new path:{}", KUBE_CONFIG_FILE_PATH);
        } finally {
            fileObject.close();
            out.close();
            VFS.close();
        }
    }

    public static Map<String, Object>  getContextsData() throws FileNotFoundException {
        log.info("Loading Kubernetes Contexts Details from {}", KUBE_CONFIG_FILE_PATH);
        return new Yaml().load(new FileInputStream(KUBE_CONFIG_FILE_PATH));
    }

    public static String getConfigFilePath() {
        return KUBE_CONFIG_FILE_PATH;
    }

}
