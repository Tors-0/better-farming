package io.github.Tors_0.raesbetterfarming.networking;

import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;
import io.github.Tors_0.raesbetterfarming.RaesBetterFarming;

public class RBFNetworking {
	public static final Identifier HARVEST_PACKET_ID = new Identifier(RaesBetterFarming.ID,"harvest_packet_id");
	public static void init() {
		ClientPlayNetworking.registerGlobalReceiver(HARVEST_PACKET_ID, (client, handler, buf, responseSender) -> {
			BlockPos pos = buf.readBlockPos();
			client.execute(() -> {
                assert client.world != null;
                client.world.addBlockBreakParticles(pos,client.world.getBlockState(pos));
                client.world.playSound(client.player,pos,SoundEvents.BLOCK_CROP_BREAK,SoundCategory.BLOCKS,1f,1f);
			});
		});
	}
}
