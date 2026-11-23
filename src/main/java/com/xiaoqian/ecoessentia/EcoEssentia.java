package com.xiaoqian.ecoessentia;

import appeng.api.AEApi;
import com.xiaoqian.ecoessentia.item.EssentiaCell;
import com.xiaoqian.ecoessentia.storage.EssentiaCellHandler;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = EcoEssentia.MOD_ID, name = "ECO Essentia Storage", version = "0.2.0",
        acceptedMinecraftVersions = "[1.12.2]",
        dependencies = "required-after:ecoaeextension@[1.2.0,1.3);required-after:appliedenergistics2;"
                + "required-after:thaumicenergistics;required-after:thaumcraft")
@Mod.EventBusSubscriber(modid = EcoEssentia.MOD_ID)
public final class EcoEssentia {
    public static final String MOD_ID = "ecoessentia";

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(EssentiaCell.CELLS);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        AEApi.instance().registries().cell().addCellHandler(EssentiaCellHandler.INSTANCE);
    }
}
