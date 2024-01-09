package io.github.Tors_0.raesbetterfarming.networking;

import io.github.Tors_0.raesbetterfarming.RaesBetterFarming;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

public class RBFNetworking {
	public static final Identifier HARVEST_PACKET_ID = new Identifier(RaesBetterFarming.ID,"harvest_packet_id");
    @ClientOnly
	public static void init() { // called in onInitializeClient in my client class
		ClientPlayNetworking.registerGlobalReceiver(HARVEST_PACKET_ID, (client, handler, buf, responseSender) -> {
			BlockPos pos = buf.readBlockPos();
			client.execute(() -> {
                assert client.world != null;
                client.world.addBlockBreakParticles(pos,client.world.getBlockState(pos));
                client.world.playSound(pos,SoundEvents.BLOCK_CROP_BREAK,SoundCategory.BLOCKS,1f,1f,false);
			});
		});
	}
}
