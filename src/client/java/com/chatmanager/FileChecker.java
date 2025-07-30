package com.chatmanager;

import java.io.*;
import java.nio.file.Files;

public class FileChecker {
    private static final File CONFIG_DIR = new File("config/ChatManager");
    private static final File CONFIG_FILE = new File(CONFIG_DIR, "config.properties");
    private static final File CHAT_LOG = new File("ChatLog");

    public static void CheckFile() {
        // config/chatmanager ディレクトリがなければ作成
        if (!CONFIG_DIR.exists()) {
            boolean created = CONFIG_DIR.mkdirs();
            if (created) {
                ChatManagerClient.LOGGER.info("[ChatManager] Created config directory: {}", CONFIG_DIR.getAbsolutePath());
            } else {
                ChatManagerClient.LOGGER.warn("[ChatManager] Failed to create config directory: {}", CONFIG_DIR.getAbsolutePath());
            }
        }

        if (!CHAT_LOG.exists()) {
            boolean created = CHAT_LOG.mkdirs();
            if (created) {
                ChatManagerClient.LOGGER.info("[ChatManager] Created ChatLog directory: {}", CHAT_LOG.getAbsolutePath());
            } else {
                ChatManagerClient.LOGGER.warn("[ChatManager] Failed to create ChatLog directory: {}", CHAT_LOG.getAbsolutePath());
            }
        }

        // config.properties がなければ、resources/defaultFile/config.properties をコピー
        if (!CONFIG_FILE.exists()) {
            try (InputStream in = FileChecker.class.getClassLoader().getResourceAsStream("DefaultFile/config.properties")) {
                if (in == null) {
                    ChatManagerClient.LOGGER.error("[ChatManager/ERROR] DefaultFile/config.properties not found in resources.");
                    return;
                }

                Files.copy(in, CONFIG_FILE.toPath());
                ChatManagerClient.LOGGER.info("[ChatManager] Copied default config.properties to {}", CONFIG_FILE.getAbsolutePath());

            } catch (IOException e) {
                ChatManagerClient.LOGGER.error("[ChatManager/ERROR] Failed to copy default config.properties", e);
            }
        } else {
            ChatManagerClient.LOGGER.info("[ChatManager] Config file already exists at {}", CONFIG_FILE.getAbsolutePath());
        }
    }
}