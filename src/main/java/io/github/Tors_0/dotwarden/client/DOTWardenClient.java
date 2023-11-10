package io.github.Tors_0.dotwarden.client;

import io.github.Tors_0.dotwarden.client.render.ScytheItemRenderer;
import io.github.Tors_0.dotwarden.common.networking.DOTWNetworking;
import io.github.Tors_0.dotwarden.common.registry.ModItems;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;

import java.util.Objects;

public class DOTWardenClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     *
     * @param mod the mod which is initialized
     */
    @Override
    public void onInitializeClient(ModContainer mod) {
        DOTWNetworking.init();

        // code from RealRTTV/malum-quilt
        Item item = ModItems.HARMONIC_STAFF;
        Identifier scytheId = Registry.ITEM.getId(item);
        ScytheItemRenderer scytheItemRenderer = new ScytheItemRenderer(scytheId);
        ResourceLoader.get(ResourceType.CLIENT_RESOURCES).registerReloader(scytheItemRenderer);
        BuiltinItemRendererRegistry.INSTANCE.register(item, scytheItemRenderer);
        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
            out.accept(new ModelIdentifier(Objects.requireNonNull(Identifier.method_43902(scytheId.getNamespace(), scytheId.getPath() + "_gui")), "inventory"));
            out.accept(new ModelIdentifier(Objects.requireNonNull(Identifier.method_43902(scytheId.getNamespace(), scytheId.getPath() + "_handheld")), "inventory"));
        });

    }
}
