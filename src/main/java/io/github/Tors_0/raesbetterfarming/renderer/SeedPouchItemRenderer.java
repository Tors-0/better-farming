package io.github.Tors_0.raesbetterfarming.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.Tors_0.raesbetterfarming.item.SeedPouchItem;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.DynamicItemRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public class SeedPouchItemRenderer implements DynamicItemRenderer {
    /**
     * Renders an item stack.
     *
     * @param stack           the rendered item stack
     * @param mode            the model transformation mode
     * @param matrices        the matrix stack
     * @param vertexConsumers the vertex consumer provider
     * @param light           packed lightmap coordinates
     * @param overlay         the overlay UV passed to {@link VertexConsumer#overlay(int)}
     */
    @ClientOnly
    @Override
    public void render(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        //itemRenderer.renderItem(stack, ModelTransformation.Mode.GUI, light, OverlayTexture.NO_WHITE_U, matrices, vertexConsumers, 0);
        if (mode == ModelTransformation.Mode.GUI) {
            SeedPouchItem.getAmmoForPreview(stack)
                    .ifPresent(ammo -> {
                        itemRenderer.renderItem(ammo, ModelTransformation.Mode.GUI, light, OverlayTexture.NO_WHITE_U, matrices, vertexConsumers, 0);
                    });
        }

    }
}
