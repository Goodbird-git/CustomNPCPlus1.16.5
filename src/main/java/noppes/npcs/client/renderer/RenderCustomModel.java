package noppes.npcs.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.model.ModelCustom;
import noppes.npcs.entity.EntityCustomModel;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RenderCustomModel extends GeoEntityRendererCompat<EntityCustomModel> {

    public RenderCustomModel(EntityRendererManager renderManager){
        super(renderManager, new ModelCustom());
    }

    @Override
    public RenderType getRenderType(EntityCustomModel animatable, float partialTicks, MatrixStack stack,
                                    IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn,
                                    ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
