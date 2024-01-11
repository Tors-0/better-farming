package io.github.Tors_0.raesbetterfarming.command;

import net.minecraft.network.Packet;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

import java.util.function.Function;

public class CustomTitleCommand {
    public static void executeTitle(ServerCommandSource source, ServerPlayerEntity target, Text title, String titleType, Function<Text, Packet<?>> constructor) {
        try {
            target.networkHandler.sendPacket((Packet) constructor.apply(Texts.parse(source, title, target, 0)));
        } catch (Exception ignored) {}
//        source.sendFeedback(Text.literal("i did it"),true);
    }
}
