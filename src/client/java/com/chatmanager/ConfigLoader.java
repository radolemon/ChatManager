package com.chatmanager;

import java.io.*;
import java.util.Properties;

public class ConfigLoader {
    private static final File CONFIG_FILE = new File("config/ChatManager/config.properties");

    private static boolean ChatLogEnabled = true;
    private static boolean AutoLogCleanup = true;
    private static int MaxLogFilesPerServer = 10;

    public static void load() {
        Properties props = new Properties();

        if (!CONFIG_FILE.exists()) {
            save(); // 初回にファイル作成
            ChatManagerClient.LOGGER.info("[ChatManager] Config file not found, created default at: {}", CONFIG_FILE.getAbsolutePath());
        }

        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            props.load(input);
            ChatManagerClient.LOGGER.info("[ChatManager] Loaded config.properties from {}", CONFIG_FILE.getAbsolutePath());
        } catch (IOException e) {
            ChatManagerClient.LOGGER.error("[ChatManager/ERROR] Failed to load config.properties", e);
        }

        // 読み込み
        ChatLogEnabled = Boolean.parseBoolean(props.getProperty("ChatLogEnabled", Boolean.toString(ChatLogEnabled)));
        AutoLogCleanup = Boolean.parseBoolean(props.getProperty("AutoLogCleanup", Boolean.toString(AutoLogCleanup)));
        try {
            MaxLogFilesPerServer = Integer.parseInt(props.getProperty("MaxLogFilesPerServer", Integer.toString(MaxLogFilesPerServer)));
        } catch (NumberFormatException e) {
            ChatManagerClient.LOGGER.warn("[ChatManager/WARN] Invalid number for maxLogFilesPerServer, using default: {}", MaxLogFilesPerServer);
        }
    }

    public static void save() {
        Properties props = new Properties();
        props.setProperty("ChatLogEnabled", Boolean.toString(ChatLogEnabled));
        props.setProperty("AutoLogCleanup", Boolean.toString(AutoLogCleanup));
        props.setProperty("MaxLogFilesPerServer", Integer.toString(MaxLogFilesPerServer));

        try {
            CONFIG_FILE.getParentFile().mkdirs();
            try (OutputStream out = new FileOutputStream(CONFIG_FILE)) {
                props.store(out, "ChatManager Config");
            }
            ChatManagerClient.LOGGER.info("[ChatManager] Saved config.properties");
        } catch (IOException e) {
            ChatManagerClient.LOGGER.error("[ChatManager/ERROR] Failed to save config.properties", e);
        }
    }

    // Getter
    public static boolean isChatLogEnabled() { return ChatLogEnabled; }
    public static boolean isAutoLogCleanup() { return AutoLogCleanup; }
    public static int getMaxLogFilesPerServer() { return MaxLogFilesPerServer; }

    // Setter（必要なら）
    public static void setChatLogEnabled(boolean value) { ChatLogEnabled = value; }
    public static void setAutoLogCleanup(boolean value) { AutoLogCleanup = value; }
    public static void setMaxLogFilesPerServer(int value) { MaxLogFilesPerServer = value; }
}
