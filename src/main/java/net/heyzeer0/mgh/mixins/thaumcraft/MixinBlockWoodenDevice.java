package net.heyzeer0.mgh.mixins.thaumcraft;

import net.heyzeer0.mgh.MagiHandlers;
import net.heyzeer0.mgh.api.forge.ForgeStack;
import net.heyzeer0.mgh.api.forge.IForgeTileEntity;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created by Frani on 11/08/2017.
 */

@Pseudo
@Mixin(targets = "thaumcraft/common/blocks/BlockWoodenDevice", remap = false)
public abstract class MixinBlockWoodenDevice extends BlockContainer {

    public MixinBlockWoodenDevice(Material m) {
        super(m);
    }

    public EntityPlayer owner;

    @Inject(method = "func_149689_a", at = @At("HEAD"))
    public void onBPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack s, CallbackInfo ci) {
        if (entityliving instanceof EntityPlayer) {
            EntityPlayer $owner = (EntityPlayer) entityliving;
            TileEntity $te = world.getTileEntity(x, y, z);
            if ($te != null) {
                if (!((IForgeTileEntity) $te).hasMHPlayer() && !MagiHandlers.isFakePlayer($owner.getCommandSenderName())) {
                    ((IForgeTileEntity) $te).setMHPlayer($owner);
                } else {
                    ForgeStack.getStack().getCurrentEntityPlayer().ifPresent(((IForgeTileEntity) $te)::setMHPlayer);
                }
            }
        }
    }
}
