package net.heyzeer0.mgh.mixins.forge;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created by HeyZeer0 on 24/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
@Mixin(ForgeChunkManager.class)
public abstract class MixinForgeChunkManager {

    @Inject(method = "forceChunk", at = @At("HEAD"), remap = false)
    private static void injectForge(ForgeChunkManager.Ticket ticket, ChunkCoordIntPair chunk, CallbackInfo ci) {
        if(ticket != null && chunk != null) {
            try {
                System.out.println(ticket.world.getProviderName() + " | " + ticket.getModId() + " | " + (ticket.getPlayerName() == null ? "Server" : ticket.getPlayerName()) + " | " + ticket.getType().toString());
            } catch (Exception e) {}
        }
    }

}
