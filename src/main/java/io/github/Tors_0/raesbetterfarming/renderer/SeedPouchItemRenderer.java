package io.github.Tors_0.raesbetterfarming.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.Tors_0.raesbetterfarming.item.SeedPouchItem;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.DynamicItemRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtElement;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public class SeedPouchItemRenderer implements DynamicItemRenderer {

    private static final ItemStack POUCH_OPEN_STACK = new ItemStack(Items.BUNDLE);
    private static final ItemStack POUCH_CLOSED_STACK = new ItemStack(Items.BUNDLE);
    static {
        POUCH_CLOSED_STACK.getOrCreateNbt().getList("Items", NbtElement.COMPOUND_TYPE);
    }

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
    public void render(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        ClientWorld world = MinecraftClient.getInstance().world;

        if (mode == ModelTransformation.Mode.GUI) {
            MatrixStack matrices2 = new MatrixStack();
            SeedPouchItem.getAmmoForPreview(stack)
                    .ifPresentOrElse(ammo -> {
                        itemRenderer.renderItem(POUCH_OPEN_STACK,ModelTransformation.Mode.GUI,255,OverlayTexture.NO_WHITE_U,matrices2,vertexConsumers,42);
                        matrices.translate(1 /4f,1 /4f, 1);
                        matrices.scale(.5f,.5f,.5f);
                        itemRenderer.renderItem(ammo, ModelTransformation.Mode.GUI, 255, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 0);
                    }, () -> {

                    });
        } else {
            matrices.translate(.5f,.5f,.5f);
            itemRenderer.renderItem(POUCH_OPEN_STACK,mode,light,overlay,matrices,vertexConsumers,0);
        }
    }
}
