package io.github.Tors_0.raesbetterfarming.model;

import io.github.Tors_0.raesbetterfarming.registry.ModCustomRenderedItems;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.item.Item;
import net.minecraft.resource.Resource;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ModelSwapper {
    /*public void onModelBake(BakedModelManager manager, Map<Resource, BakedModel> modelRegistry, ModelLoader loader) {
        ModCustomRenderedItems.forEach(item -> swapModels(modelRegistry, getItemModelLocation(item), CustomRenderedItemModel::new));
    }
    public void registerListeners() {
        ModelsBakedCallback.EVENT.register(this::onModelBake);

    }

    public static <T extends BakedModel> void swapModels(Map<Resource, BakedModel> modelRegistry,
                                                         Resource location, Function<BakedModel, T> factory) {
        modelRegistry.put(location, factory.apply(modelRegistry.get(location)));
    }
    public static Resource getItemModelLocation(Item item) {
        return new Resource(Registry.ITEM.getKey(item), "inventory");
    }*/
}
