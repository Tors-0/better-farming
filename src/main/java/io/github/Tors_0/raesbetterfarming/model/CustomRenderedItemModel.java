package io.github.Tors_0.raesbetterfarming.model;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.client.render.model.BakedModel;

public class CustomRenderedItemModel extends ForwardingBakedModel {
    public CustomRenderedItemModel(BakedModel originalModel) {
        this.wrapped = originalModel;
    }

    @Override
    public BakedModel getWrappedModel() {
        return wrapped;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }
}
