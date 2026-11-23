package com.xiaoqian.ecoessentia.mixin;

import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import com.xiaoqian.ecoessentia.item.EssentiaCell;
import com.xiaoqian.ecoessentia.storage.EssentiaCellHandler;
import github.kasuminova.ecoaeextension.common.estorage.EStorageCellHandler;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EStorageCellHandler.class, remap = false)
public abstract class StorageCellHandlerMixin {
    @Inject(method = "getHandler", at = @At("HEAD"), cancellable = true)
    private static void xiaoqian$handler(ItemStack stack, CallbackInfoReturnable<EStorageCellHandler> cir) {
        if (stack.getItem() instanceof EssentiaCell) {
            cir.setReturnValue(EssentiaCellHandler.INSTANCE);
        }
    }

    // The AE cell registry can select ECO's handler directly, bypassing getHandler().
    @Inject(method = "getCellInventory", at = @At("HEAD"), cancellable = true)
    private <T extends IAEStack<T>> void xiaoqian$inventory(ItemStack stack, ISaveProvider host,
            IStorageChannel<T> channel, CallbackInfoReturnable<ICellInventoryHandler<T>> cir) {
        if (stack.getItem() instanceof EssentiaCell) {
            cir.setReturnValue(EssentiaCellHandler.INSTANCE.getCellInventory(stack, host, channel));
        }
    }
}
