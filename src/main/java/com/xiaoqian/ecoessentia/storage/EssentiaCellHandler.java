package com.xiaoqian.ecoessentia.storage;

import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.me.storage.BasicCellInventoryHandler;
import com.xiaoqian.ecoessentia.item.EssentiaCell;
import github.kasuminova.ecoaeextension.common.estorage.EStorageCellHandler;
import net.minecraft.item.ItemStack;

public final class EssentiaCellHandler extends EStorageCellHandler {
    public static final EssentiaCellHandler INSTANCE = new EssentiaCellHandler();

    @Override
    public boolean isCell(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof EssentiaCell;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T extends IAEStack<T>> ICellInventoryHandler<T> getCellInventory(
            ItemStack stack, ISaveProvider host, IStorageChannel<T> channel) {
        if (!isCell(stack) || ((EssentiaCell) stack.getItem()).getChannel() != channel) {
            return null;
        }
        return new BasicCellInventoryHandler(new EssentiaCellInventory(stack, host), channel);
    }
}
