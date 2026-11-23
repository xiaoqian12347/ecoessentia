package com.xiaoqian.ecoessentia.item;

import appeng.api.AEApi;
import appeng.api.storage.IStorageChannel;
import com.xiaoqian.ecoessentia.EcoEssentia;
import github.kasuminova.ecoaeextension.common.block.ecotech.estorage.prop.DriveStorageLevel;
import github.kasuminova.ecoaeextension.common.item.estorage.EStorageCell;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import thaumicenergistics.api.storage.IAEEssentiaStack;
import thaumicenergistics.api.storage.IEssentiaStorageChannel;
import thaumicenergistics.util.inventory.EssentiaCellConfig;

public final class EssentiaCell extends EStorageCell<IAEEssentiaStack> {
    public static final EssentiaCell[] CELLS = {
            new EssentiaCell(DriveStorageLevel.A, 16, 4),
            new EssentiaCell(DriveStorageLevel.B, 64, 16),
            new EssentiaCell(DriveStorageLevel.C, 256, 64)
    };

    private EssentiaCell(DriveStorageLevel level, int millionBytes, int multiplier) {
        super(level, millionBytes, multiplier);
        String name = "estorage_cell_essentia_" + millionBytes + "m";
        setRegistryName(EcoEssentia.MOD_ID, name);
        setTranslationKey(EcoEssentia.MOD_ID + "." + name);
    }

    @Override
    public int getTotalTypes(ItemStack stack) {
        return 25;
    }

    @Override
    public int getBytesPerType(ItemStack stack) {
        return getByteMultiplier() * 1024;
    }

    @Override
    public IStorageChannel<IAEEssentiaStack> getChannel() {
        return AEApi.instance().storage().getStorageChannel(IEssentiaStorageChannel.class);
    }

    @Override
    public IItemHandler getConfigInventory(ItemStack stack) {
        return new EssentiaCellConfig(stack);
    }
}
