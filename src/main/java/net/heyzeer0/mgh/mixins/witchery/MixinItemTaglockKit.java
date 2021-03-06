package net.heyzeer0.mgh.mixins.witchery;

import com.emoniph.witchery.Witchery;
import net.heyzeer0.mgh.mixins.MixinManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created by HeyZeer0 on 13/05/2017.
 * Copyright © HeyZeer0 - 2016
 */
@Pseudo
@Mixin(targets = "com/emoniph/witchery/item/ItemTaglockKit", remap = false)
public abstract class MixinItemTaglockKit {

    @Inject(method = "onEntityInteract", at = @At(value = "HEAD"), cancellable = true)
    private void injectInteract(World world, EntityPlayer player, ItemStack stack, EntityInteractEvent event, CallbackInfo ci) {
        if(stack.getItem() == Witchery.Items.TAGLOCK_KIT && event.target != null) {
            if(!MixinManager.canAttack(player, event.target)) {
                ci.cancel();
            }else{
                if(event.target instanceof EntityPlayer) {
                    if(!MixinManager.canAttack((EntityPlayer)event.target, player)) {
                        ci.cancel();
                    }
                }
            }
        }
    }

}
