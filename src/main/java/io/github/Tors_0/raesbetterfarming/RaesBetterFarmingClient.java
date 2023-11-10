package io.github.Tors_0.raesbetterfarming;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import io.github.Tors_0.raesbetterfarming.networking.RBFNetworking;

public class RaesBetterFarmingClient implements ClientModInitializer {
	/**
	 * Runs the mod initializer on the client environment.
	 *
	 * @param mod the mod which is initialized
	 */
	@Override
	public void onInitializeClient(ModContainer mod) {
		RBFNetworking.init();
	}
}
