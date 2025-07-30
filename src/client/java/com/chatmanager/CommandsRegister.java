package com.chatmanager;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CommandsRegister {
    private static final String modVersion = FabricLoader.getInstance()
            .getModContainer("chatmanager")
            .map(c -> c.getMetadata().getVersion().getFriendlyString())
            .orElse("unknown");

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    LiteralArgumentBuilder.<FabricClientCommandSource>literal("chatmanager")
                            .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("help")
                                    .executes(context -> {
                                        // リンク付きテキストの作成
                                        Text clickableHelp = Text.literal("(§l§nClick!§r)\n")
                                                .setStyle(Style.EMPTY
                                                        .withColor(Formatting.AQUA)
                                                        .withUnderline(false)
                                                        .withClickEvent(
                                                                new ClickEvent.OpenUrl(java.net.URI.create("https://github.com/radolemon/ChatManager/wiki"))
                                                        )
                                                );

                                        // メッセージとリンクを結合して送信
                                        Text message = Text.literal("\n§6 ====== ChatManager ====== \n\n§dversion: " + modVersion + "\n\n§cNeed More help? ")
                                                .append(clickableHelp);

                                        context.getSource().sendFeedback(message);
                                        return 1;
                                    })

                            )

                            .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("chatLog")
                                    .executes(context -> {
                                        boolean newState = !ConfigLoader.isChatLogEnabled();
                                        ConfigLoader.setChatLogEnabled(newState);
                                        ConfigLoader.save();

                                        // プレイヤーへのフィードバック
                                        context.getSource().sendFeedback(Text.literal("§6ChatLog is now " + (newState ? "§2ENABLED" : "§4DISABLED")));

                                        // コンソールへのログ出力
                                        ChatManagerClient.LOGGER.info("[ChatManager] ChatLog is now " + (newState ? "ENABLED" : "DISABLED"));

                                        return 1;
                                    })
                            )

                            .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("autoLogCleanup")
                                    .executes(context -> {
                                        boolean newState = !ConfigLoader.isAutoLogCleanup();
                                        ConfigLoader.setAutoLogCleanup(newState);
                                        ConfigLoader.save();

                                        context.getSource().sendFeedback(Text.literal("§6AutoLogCleanup is now " + (newState ? "§2ENABLED" : "§4DISABLED")));
                                        // コンソールへのログ出力
                                        ChatManagerClient.LOGGER.info("[ChatManager] ChatLog is now " + (newState ? "ENABLED" : "DISABLED"));

                                        return 1;
                                    })

                                    .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("maxLogFile")
                                            .executes(ctx -> {
                                                int currentValue = ConfigLoader.getMaxLogFilesPerServer();
                                                ctx.getSource().sendFeedback(Text.literal("§6Current maxLogFilesPerServer: §7" + currentValue));
                                                return 1;
                                            })

                                            .then(ClientCommandManager.argument("count", IntegerArgumentType.integer(1))
                                                    .executes(ctx -> {
                                                        int value = IntegerArgumentType.getInteger(ctx, "count");
                                                        ConfigLoader.setMaxLogFilesPerServer(value);
                                                        ConfigLoader.save();

                                                        ctx.getSource().sendFeedback(Text.literal("§6maxLogFilesPerServer is now §2" + value));
                                                        ChatManagerClient.LOGGER.info("[ChatManager] maxLogFilesPerServer set to {}", value);
                                                        return 1;
                                                    })
                                            )
                                    )
                            )

            );

            dispatcher.register(
                    LiteralArgumentBuilder.<FabricClientCommandSource>literal("cm")
                            .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("help")
                                    .executes(context -> {
                                        // リンク付きテキストの作成
                                        Text clickableHelp = Text.literal("(§l§nClick!§r)\n")
                                                .setStyle(Style.EMPTY
                                                        .withColor(Formatting.AQUA)
                                                        .withUnderline(false)
                                                        .withClickEvent(
                                                                new ClickEvent.OpenUrl(java.net.URI.create("https://github.com/radolemon/ChatManager/wiki"))
                                                        )
                                                );

                                        // メッセージとリンクを結合して送信
                                        Text message = Text.literal("\n§6 ====== ChatManager ====== \n\n§dversion: " + modVersion + "\n\n§cNeed More help? ")
                                                .append(clickableHelp);

                                        context.getSource().sendFeedback(message);
                                        return 1;
                                    })
                            )

                            .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("chatlog")
                                    .executes(context -> {
                                        boolean newState = !ConfigLoader.isChatLogEnabled();
                                        ConfigLoader.setChatLogEnabled(newState);
                                        ConfigLoader.save();

                                        context.getSource().sendFeedback(Text.literal("§6ChatLog is now " + (newState ? "§2ENABLED" : "§4DISABLED")));

                                        ChatManagerClient.LOGGER.info("[ChatManager] ChatLog is now " + (newState ? "ENABLED" : "DISABLED"));

                                        return 1;
                                    })
                            )

                            .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("autoLogCleanup")
                                    .executes(context -> {
                                        boolean newState = !ConfigLoader.isAutoLogCleanup();
                                        ConfigLoader.setAutoLogCleanup(newState);
                                        ConfigLoader.save();

                                        context.getSource().sendFeedback(Text.literal("§6AutoLogCleanup is now " + (newState ? "§2ENABLED" : "§4DISABLED")));
                                        // コンソールへのログ出力
                                        ChatManagerClient.LOGGER.info("[ChatManager] ChatLog is now " + (newState ? "ENABLED" : "DISABLED"));

                                        return 1;
                                    })

                                    .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("maxLogFile")
                                            .executes(ctx -> {
                                                int currentValue = ConfigLoader.getMaxLogFilesPerServer();
                                                ctx.getSource().sendFeedback(Text.literal("§6Current maxLogFilesPerServer: §7" + currentValue));
                                                return 1;
                                            })

                                            .then(ClientCommandManager.argument("count", IntegerArgumentType.integer(1))
                                                    .executes(ctx -> {
                                                        int value = IntegerArgumentType.getInteger(ctx, "count");
                                                        ConfigLoader.setMaxLogFilesPerServer(value);
                                                        ConfigLoader.save();

                                                        ctx.getSource().sendFeedback(Text.literal("§6maxLogFilesPerServer is now §2" + value));
                                                        ChatManagerClient.LOGGER.info("[ChatManager] maxLogFilesPerServer set to {}", value);
                                                        return 1;
                                                    })
                                            )
                                    )
                            )
            );
        });
    }
}
