package com.chatmanager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

public class ChatLog {
    public static final File BASE_DIR = new File("ChatLog");

    public static void logMessage(String serverName, String rawMessage) {
        if (!ConfigLoader.isChatLogEnabled()) return;

        // ディレクトリ作成
        File serverDir = new File(BASE_DIR, serverName);
        if (!serverDir.exists()) {
            if (serverDir.mkdirs()) {
                ChatManagerClient.LOGGER.info("[ChatManager] Created chat log directory for server: {}", serverName);
            }
        }

        // 日時を使ってファイル名生成
        LocalDateTime now = LocalDateTime.now();

        // タイムスタンプ + メッセージを追記
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ChatManagerClient.LOG_FILE, true))) {
            String timestamp = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            writer.write("[" + timestamp + "] " + rawMessage);
            writer.newLine();
        } catch (IOException e) {
            ChatManagerClient.LOGGER.error("[ChatManager/ERROR] Failed to write chat log to {}", ChatManagerClient.LOG_FILE.getName(), e);
        }
    }

    public static void cleanupOldLogs() {
        if (!ConfigLoader.isAutoLogCleanup()) return;

        File[] logFiles = ChatManagerClient.SERVER_DIR.listFiles((dir, name) -> name.endsWith(".log"));
        if (logFiles == null || logFiles.length <= ConfigLoader.getMaxLogFilesPerServer()) return;

        Arrays.sort(logFiles, Comparator.comparingLong(File::lastModified));

        int filesToDelete = logFiles.length - ConfigLoader.getMaxLogFilesPerServer();
        for (int i = 0; i < filesToDelete; i++) {
            File file = logFiles[i];
            if (file.delete()) {
                ChatManagerClient.LOGGER.info("[ChatManager] Deleted old chat log: {}", file.getName());
            } else {
                ChatManagerClient.LOGGER.warn("[ChatManager/WARN] Failed to delete chat log: {}", file.getName());
            }
        }
    }
}
