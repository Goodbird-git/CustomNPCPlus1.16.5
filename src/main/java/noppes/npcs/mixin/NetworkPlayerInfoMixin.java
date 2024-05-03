package noppes.npcs.mixin;

import com.google.common.base.MoreObjects;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.controllers.ClientSkinController;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NetworkPlayerInfo.class)
public abstract class NetworkPlayerInfoMixin {
    @Shadow
    @Final
    private GameProfile profile;

    @Inject(at = @At("RETURN"), method = "getSkinLocation", cancellable = true)
    public void getSkinLocation(CallbackInfoReturnable<ResourceLocation> cir) {
        cir.setReturnValue(MoreObjects.firstNonNull(ClientSkinController.getSkinForPlayer(profile.getName()), cir.getReturnValue()));
    }
}
