package net.heyzeer0.mgh;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by HeyZeer0 on 08/03/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class MagiHandlers extends DummyModContainer {
    
    public MagiHandlers()
    {
        super(new ModMetadata());
        ModMetadata metadata = getMetadata();
        metadata.authorList.add("HeyZeer0");
        metadata.name="MagiHandlers";
        metadata.modId="MagiHandlers";
        metadata.version="1.0";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
        bus.register(this);
        return true;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){

    }

}
