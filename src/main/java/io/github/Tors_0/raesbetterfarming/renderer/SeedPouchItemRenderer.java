package io.github.Tors_0.raesbetterfarming.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.Tors_0.raesbetterfarming.item.SeedPouchItem;
import io.github.Tors_0.raesbetterfarming.registry.ModItems;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.DynamicItemRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public class SeedPouchItemRenderer implements DynamicItemRenderer {

    private static final ItemStack POUCH_OPEN_STACK = new ItemStack(ModItems.DUMMY_SEED_POUCH_EMPTY);
    private static final ItemStack POUCH_CLOSED_STACK = new ItemStack(ModItems.DUMMY_SEED_POUCH_FULL);
    private static final ItemStack ENABLED = new ItemStack(Items.GREEN_WOOL);
    private static final ItemStack DISABLED = new ItemStack(Items.RED_WOOL);

    public SeedPouchItemRenderer() {}

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
    public void render(
            ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay
    ) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        boolean hasItems = SeedPouchItem.getAmountFilled(stack) > 0;

        if (mode == ModelTransformation.Mode.GUI) {
            MatrixStack matrices2 = new MatrixStack();
            matrices2.translate(0,0,0);
            SeedPouchItem.getAmmoForPreview(stack)
                    .ifPresent(ammo -> {
                        itemRenderer.renderItem(hasItems ? POUCH_CLOSED_STACK : POUCH_OPEN_STACK,
                                ModelTransformation.Mode.NONE, light, OverlayTexture.DEFAULT_UV,
                                matrices2, vertexConsumers, 42);

                        matrices.translate((1/32f)+(2/3f),(1/3f), 1);
                        matrices.scale(2/3f,2/3f,2/3f);

                        itemRenderer.renderItem(ammo, ModelTransformation.Mode.NONE, light,
                                OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 0);
                    });
//            MatrixStack matrices3 = new MatrixStack();
//            matrices3.translate(-1/4f,-1/4f, 1);
//            matrices3.scale(.5f,.5f,.5f);
//            itemRenderer.renderItem(stack.getItem().hasGlint(stack) ? ENABLED : DISABLED, mode, light, overlay, matrices3, vertexConsumers, 0);

        } else {
            matrices.translate(.5f,.5f,.5f);

            itemRenderer.renderItem(hasItems ? POUCH_CLOSED_STACK : POUCH_OPEN_STACK,
                    mode, light, overlay, matrices, vertexConsumers, 0);
        }
    }
}
