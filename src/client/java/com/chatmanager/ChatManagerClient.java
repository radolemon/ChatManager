package com.chatmanager;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatManagerClient implements ClientModInitializer {
	public static final String MOD_ID = "ChatManager";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static File SERVER_DIR = new File("");
	public static File LOG_FILE = new File("");

	private static String currentServerName = "Unknown";
	private final Set<String> loggedMessages = ConcurrentHashMap.newKeySet();

	@Override
	public void onInitializeClient() {
		LOGGER.info("[ChatManager]  ⚙️️ ChatManager 🗯️ ");

		LOGGER.info("[ChatManager] >🔎 Check File...");
		FileChecker.CheckFile();
		LOGGER.info("[ChatManager] ✔ Done.");

		LOGGER.info("[ChatManager] >💾 Load Config...");
		ConfigLoader.load();
		LOGGER.info("[ChatManager] ✔ Done.");

		LOGGER.info("[ChatManager] >🔧 Register Commands...");
		CommandsRegister.register();
		LOGGER.info("[ChatManager] ✔ Commands registered.");

		LOGGER.info("[ChatManager] >🔧 Register Events...");
		registerEvents();
		LOGGER.info("[ChatManager] ✔ Events registered.\n");
	}

	private void registerEvents() {
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			currentServerName = getCurrentServerName();
			LOGGER.info("[ChatManager] 🛰️ Joined server: " + currentServerName);
			SERVER_DIR = new File(ChatLog.BASE_DIR, currentServerName);

			LocalDateTime now = LocalDateTime.now();
			String fileName = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".log";
			LOG_FILE = new File(SERVER_DIR, fileName);

			LOGGER.info("[ChatManager] Created log file: " + fileName);
			ChatLog.cleanupOldLogs();

			loggedMessages.clear();
		});

		// プレイヤーのチャットメッセージ（UUIDあり → 確実に記録）
		ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, timestamp) -> {
			String msgText = message.getString();
			if (!loggedMessages.contains(msgText)) {
				ChatLog.logMessage(currentServerName, msgText);
				loggedMessages.add(msgText);
			}
		});

		// システムメッセージ（案内、非UUIDメッセージ → 高度フィルター処理）
		ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
			String raw = message.getString().trim();
			if (raw.isBlank()) return;

			String filtered = extractChatContent(raw);

			// ログ済みならスキップ
			if (loggedMessages.contains(filtered)) return;

			// 「:」がない（= 会話形式でない）ものは除外
			if (!filtered.contains(":")) return;

			// プレイヤー名がタブリストにあるか確認
			if (containsPlayerNameInTabList(filtered)) {
				ChatLog.logMessage(currentServerName, filtered);
				loggedMessages.add(filtered);
			}
		});
	}

	private String getCurrentServerName() {
		ServerInfo info = MinecraftClient.getInstance().getCurrentServerEntry();
		if (info != null) {
			return sanitize(info.address);
		} else {
			return "SinglePlayer";
		}
	}

	private boolean containsPlayerNameInTabList(String message) {
		ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
		if (handler == null) return false;

		for (PlayerListEntry entry : handler.getPlayerList()) {
			if (entry.getProfile() != null) {
				String playerName = entry.getProfile().getName();
				if (playerName != null && message.contains(playerName)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * rawメッセージから会話部分だけ抽出する簡易実装。
	 * 例: "[System] [CHAT] PlayerName: メッセージ" → "PlayerName: メッセージ"
	 */
	private String extractChatContent(String raw) {
		int idx = raw.indexOf("] ");
		if (idx != -1 && idx + 2 < raw.length()) {
			return raw.substring(idx + 2).trim();
		}
		return raw;
	}

	private String sanitize(String input) {
		return input.replaceAll("[^a-zA-Z0-9-_.]", "_");
	}
}
