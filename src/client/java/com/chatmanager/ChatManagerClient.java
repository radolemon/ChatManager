package com.chatmanager;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatManagerClient implements ClientModInitializer {
	public static final String MOD_ID = "ChatManager";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static File SERVER_DIR = new File("");
	public static File LOG_FILE = new File("");

	private static String currentServerName = "Unknown";

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		LOGGER.info("> ⚙️️ ChatManager 🗯️ <\n");

		LOGGER.info(">🔎 Check File...");
		FileChecker.CheckFile();
		LOGGER.info("✔ Done.");

		LOGGER.info(">💾 Load Config...");
		ConfigLoader.load();
		LOGGER.info("✔ Done.");

		LOGGER.info(">🔧 Register Commands...");
		CommandsRegister.register();
		LOGGER.info("✔ Commands registered.");

		LOGGER.info(">🔧 Register Events...");
		registerEvents();
		LOGGER.info("✔ Events registered.\n");
	}

	private void registerEvents() {
		ClientPlayConnectionEvents.JOIN.register((handler, senderm, client) -> {
			currentServerName = getCurrentServerName();
			LOGGER.info("🛰️ Joined server: " + currentServerName);
			SERVER_DIR = new File(ChatLog.BASE_DIR, currentServerName);

			LocalDateTime now = LocalDateTime.now();
			String fileName = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".log";
			LOG_FILE = new File(ChatManagerClient.SERVER_DIR, fileName);

			LOGGER.info("Created log file: " + fileName);

			ChatLog.cleanupOldLogs();
		});

		ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, timestamp) -> {
			String msgText = message.getString();

			ChatLog.logMessage(currentServerName, msgText);
		});
	}

	private String getCurrentServerName() {
		ServerInfo info = MinecraftClient.getInstance().getCurrentServerEntry();
		if (info != null) {
			// サーバーアドレスをサニタイズして識別子に使う
			return sanitize(info.address);
		} else {
			return "SinglePlayer";
		}
	}

	private String sanitize(String input) {
		return input.replaceAll("[^a-zA-Z0-9-_.]", "_");
	}
}