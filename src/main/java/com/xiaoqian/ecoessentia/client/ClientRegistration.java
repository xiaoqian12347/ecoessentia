package com.xiaoqian.ecoessentia.client;

import com.xiaoqian.ecoessentia.EcoEssentia;
import com.xiaoqian.ecoessentia.item.EssentiaCell;
import com.xiaoqian.ecoessentia.storage.EssentiaCellInventory;
import github.kasuminova.ecoaeextension.common.block.ecotech.estorage.BlockEStorageCellDrive;
import github.kasuminova.ecoaeextension.common.block.ecotech.estorage.prop.DriveStorageType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = EcoEssentia.MOD_ID, value = Side.CLIENT)
public final class ClientRegistration {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void models(ModelRegistryEvent event) {
        for (EssentiaCell cell : EssentiaCell.CELLS) {
            ModelLoader.setCustomModelResourceLocation(cell, 0, new ModelResourceLocation(cell.getRegistryName(), "inventory"));
            cell.addPropertyOverride(new ResourceLocation(EcoEssentia.MOD_ID, "cell_status"),
                    (stack, world, entity) -> new EssentiaCellInventory(stack, null).getStatusForCell());
        }
        // Only essentia states use our models; all other channels retain ECO's states.
        ModelLoader.setCustomStateMapper(BlockEStorageCellDrive.INSTANCE, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                if ("essentia".equals(state.getValue(DriveStorageType.STORAGE_TYPE).getName())) {
                    return new ModelResourceLocation(new ResourceLocation(EcoEssentia.MOD_ID, "estorage_cell_drive"),
                            getPropertyString(state.getProperties()));
                }
                return new ModelResourceLocation(BlockEStorageCellDrive.INSTANCE.getRegistryName(),
                        getPropertyString(state.getProperties()));
            }
        });
    }
}
