package noppes.npcs.client.model;

import net.minecraft.util.ResourceLocation;
import noppes.npcs.entity.EntityCustomModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ModelCustom extends AnimatedGeoModel<EntityCustomModel> {
    @Override
    public ResourceLocation getAnimationFileLocation(EntityCustomModel entity) {
        return entity.animResLoc;
    }

    @Override
    public ResourceLocation getModelLocation(EntityCustomModel entity) {
        return entity.modelResLoc;
    }

    @Override
    public ResourceLocation getTextureLocation(EntityCustomModel entity) {
        return entity.textureResLoc;
    }
}