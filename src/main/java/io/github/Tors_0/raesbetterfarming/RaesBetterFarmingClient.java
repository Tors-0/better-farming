package io.github.Tors_0.raesbetterfarming;

import io.github.Tors_0.raesbetterfarming.networking.RBFNetworking;
import io.github.Tors_0.raesbetterfarming.registry.ModItems;
import io.github.Tors_0.raesbetterfarming.renderer.SeedPouchItemRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.resource.loader.api.QuiltResourcePack;

@ClientOnly
public class RaesBetterFarmingClient implements ClientModInitializer {
	/**
	 * Runs the mod initializer on the client environment.
	 *
	 * @param mod the mod which is initialized
	 */
	@Override
	public void onInitializeClient(ModContainer mod) {
		RBFNetworking.init();

        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.SEED_POUCH_, new SeedPouchItemRenderer());
	}
}
