package noppes.npcs.mixin;

import com.google.common.base.MoreObjects;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.texture.DownloadingTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noppes.npcs.client.controllers.ClientSkinController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity {

    public AbstractClientPlayerEntityMixin(World p_i241920_1_, BlockPos p_i241920_2_, float p_i241920_3_, GameProfile p_i241920_4_) {
        super(p_i241920_1_, p_i241920_2_, p_i241920_3_, p_i241920_4_);
    }

    @Inject(at = @At("RETURN"), method = "getSkinTextureLocation", cancellable = true)
    public void getSkinLocation(CallbackInfoReturnable<ResourceLocation> cir) {
        cir.setReturnValue(MoreObjects.firstNonNull(ClientSkinController.getSkinForPlayer(getDisplayName().getString()), cir.getReturnValue()));
    }
}
