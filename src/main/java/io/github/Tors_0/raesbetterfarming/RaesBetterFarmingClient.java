package io.github.Tors_0.raesbetterfarming;

import io.github.Tors_0.raesbetterfarming.networking.RBFNetworking;
import io.github.Tors_0.raesbetterfarming.registry.ModCustomRenderedItems;
import io.github.Tors_0.raesbetterfarming.registry.ModItems;
import io.github.Tors_0.raesbetterfarming.renderer.SeedPouchItemRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.impl.client.rendering.BuiltinItemRendererRegistryImpl;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

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
        RaesBetterFarming.LOGGER.info(BuiltinItemRendererRegistry.INSTANCE.get(ModItems.SEED_POUCH_).toString());


        //ModCustomRenderedItems.register(ModItems.SEED_POUCH_);
	}
}
