package noppes.npcs.mixin;

import com.google.common.base.MoreObjects;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.controllers.ClientSkinController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "org.tlauncher.TLSkinCape")
public class TLSkinCapeMixin {


    @Inject(at = @At("RETURN"), method = "getLocationSkin", cancellable = true, remap=false)
    private static void registerSkinTexture(GameProfile profile, CallbackInfoReturnable<ResourceLocation> cir) {
        cir.setReturnValue(MoreObjects.firstNonNull(ClientSkinController.getSkinForPlayer(profile.getName()), cir.getReturnValue()));
    }
}
