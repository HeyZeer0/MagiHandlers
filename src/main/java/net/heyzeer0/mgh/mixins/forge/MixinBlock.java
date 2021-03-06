package net.heyzeer0.mgh.mixins.forge;

import net.heyzeer0.mgh.MagiHandlers;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created by Frani on 15/10/2017.
 */
@Mixin(Block.class)
public abstract class MixinBlock {

    @Shadow public abstract boolean hasTileEntity(int metadata);

    @Shadow @Deprecated public abstract boolean hasTileEntity();

    @Inject(method = "onBlockPlacedBy", at = @At("HEAD"))
    public void onBlockPlacedByOwner(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack itemstack, CallbackInfo ci) {
        if (itemstack != null && this.hasTileEntity(itemstack.getItemDamage()) || this.hasTileEntity()) {
            if (entityliving instanceof EntityPlayer) {
                boolean isFake = MagiHandlers.isFakePlayer(entityliving.getCommandSenderName());
                if (isFake) {
                    MagiHandlers.getStack().getCurrentEntityPlayer().ifPresent(p -> MagiHandlers.track(p.getCommandSenderName(), p.getUniqueID().toString(), world, x, y, z));
                } else {
                    EntityPlayer p = (EntityPlayer) entityliving;
                    MagiHandlers.track(p.getCommandSenderName(), p.getUniqueID().toString(), world, x, y, z);
                }
            }
        }
    }

}
