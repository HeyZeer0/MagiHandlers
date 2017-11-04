package net.heyzeer0.mgh.mixins.forge;

import net.heyzeer0.mgh.MagiHandlers;
import net.heyzeer0.mgh.hacks.IEntity;
import net.heyzeer0.mgh.hacks.IMixinChunk;
import net.heyzeer0.mgh.hacks.ITileEntityOwnable;
import net.heyzeer0.mgh.mixins.MixinManager;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.FakePlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created by Frani on 15/10/2017.
 */

@Mixin(World.class)
public abstract class MixinWorld {

    @Shadow public abstract Chunk getChunkFromBlockCoords(int p_72938_1_, int p_72938_2_);

    @Redirect(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntity;updateEntity()V"))
    public void redirectTileEntityUpdate(TileEntity te) {
        if (te.getBlockType().getUnlocalizedName().toLowerCase().contains("ic2") ||
            te.getBlockType().getUnlocalizedName().toLowerCase().contains("appliedenergistics2")) {
            MagiHandlers.getStack().push(te);
            te.updateEntity();
            MagiHandlers.getStack().remove(te);
        } else if (!((IMixinChunk)this.getChunkFromBlockCoords(te.xCoord, te.zCoord)).isMarkedToUnload()) {
            MagiHandlers.getStack().push(te);
            te.updateEntity();
            MagiHandlers.getStack().remove(te);
        }
    }

    @Redirect(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;updateEntity(Lnet/minecraft/entity/Entity;)V"))
    public void redirectEntityUpdate(World world, Entity entity) {
        if (!((IMixinChunk)world.getChunkFromChunkCoords(entity.chunkCoordX, entity.chunkCoordZ)).isMarkedToUnload()) {
            MagiHandlers.getStack().push(entity);
            world.updateEntity(entity);
            MagiHandlers.getStack().remove(entity);
        }
    }

    @Inject(method = "onEntityAdded", at = @At(value = "HEAD"))
    public void onEntitySpawn1(Entity e, CallbackInfo cir) {
        if (MagiHandlers.getStack().getFirst(TileEntity.class).isPresent()) {
            ((IEntity)e).setOwner(((ITileEntityOwnable)MagiHandlers.getStack().getFirst(TileEntity.class).get()).getFakePlayer());
        } else if (MagiHandlers.getStack().getFirst(EntityPlayer.class).isPresent()) {
            MagiHandlers.getStack().getFirst(EntityPlayer.class).ifPresent(((IEntity)e)::setOwner);
        } else if (MagiHandlers.getStack().getFirst(Entity.class).isPresent()) {
            MagiHandlers.getStack().getFirst(Entity.class).ifPresent(entity -> {
                if (((IEntity) entity).hasOwner()) {
                    ((IEntity) e).setOwner(((IEntity) entity).getOwner());
                }
            });
        } else if (MagiHandlers.getStack().ignorePhase || e instanceof EntityItem || e instanceof EntityLiving || e instanceof EntityFallingBlock) {
            // ignore, for now
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        for (Runnable r : MagiHandlers.instance.tasks) {
            r.run();
            MagiHandlers.instance.tasks.remove(r);
        }
    }

    @Inject(method = "setBlock(IIILnet/minecraft/block/Block;II)Z", at = @At("HEAD"), cancellable = true)
    public void onSetBlock(int x, int y, int z, Block block, int meta, int flag, CallbackInfoReturnable cir) {
        if (MagiHandlers.getStack().getFirst(EntityPlayer.class).isPresent()) {
            if (MagiHandlers.getStack().ignorePhase) return;
            EntityPlayer player = MagiHandlers.getStack().getFirst(EntityPlayer.class).get();
            if (!(player instanceof FakePlayer) && !MixinManager.canBuild(x, y, z, (World)(Object)this, player)) {
                cir.setReturnValue(false);
            }
        } else if (MagiHandlers.getStack().getFirst(Entity.class).isPresent()) {
            if (MagiHandlers.getStack().ignorePhase) return;
            IEntity entity = (IEntity) MagiHandlers.getStack().getFirst(Entity.class).get();
            if (entity.hasOwner()) {
                if (!MixinManager.canBuild(x, y, z, (World)(Object)this, entity.getOwner())) {
                    cir.setReturnValue(false);
                }
            }
        } else if (MagiHandlers.getStack().getFirst(TileEntity.class).isPresent()) {
            if (MagiHandlers.getStack().ignorePhase) return;
            ITileEntityOwnable tile = (ITileEntityOwnable) MagiHandlers.getStack().getFirst(TileEntity.class).get();
            if (tile.hasTrackedPlayer()) {
                if (!MixinManager.canBuild(x, y, z, (World)(Object)this, tile.getFakePlayer())) {
                    cir.setReturnValue(false);
                }
            }
        } else {
            //MagiHandlers.log("Could not capture the cause of a setBlock, ignoring. Stacktrace: ");
            //Thread.dumpStack();
        }
    }

}
