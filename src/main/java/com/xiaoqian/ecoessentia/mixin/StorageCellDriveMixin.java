package com.xiaoqian.ecoessentia.mixin;

import com.xiaoqian.ecoessentia.item.EssentiaCell;
import github.kasuminova.ecoaeextension.common.block.ecotech.estorage.prop.DriveStorageType;
import github.kasuminova.ecoaeextension.common.container.data.EStorageCellData;
import github.kasuminova.ecoaeextension.common.item.estorage.EStorageCell;
import github.kasuminova.ecoaeextension.common.tile.ecotech.estorage.EStorageCellDrive;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EStorageCellDrive.class, remap = false)
public abstract class StorageCellDriveMixin {
    @Inject(method = "getCellType", at = @At("HEAD"), cancellable = true)
    private static void xiaoqian$type(EStorageCell<?> cell, CallbackInfoReturnable<DriveStorageType> cir) {
        if (cell instanceof EssentiaCell) {
            cir.setReturnValue(DriveStorageType.valueOf("ESSENTIA"));
        }
    }

    @Inject(method = "getMaxTypes", at = @At("HEAD"), cancellable = true)
    private static void xiaoqian$types(EStorageCellData data, CallbackInfoReturnable<Integer> cir) {
        if ("essentia".equals(data.type().getName())) {
            cir.setReturnValue(25);
        }
    }

    @Inject(method = "getMaxBytes", at = @At("HEAD"), cancellable = true)
    private static void xiaoqian$bytes(EStorageCellData data, CallbackInfoReturnable<Long> cir) {
        if ("essentia".equals(data.type().getName())) {
            long bytes = 0;
            for (EssentiaCell cell : EssentiaCell.CELLS) {
                if (cell.getLevel() == data.level()) {
                    bytes = cell.getBytes(ItemStack.EMPTY);
                    break;
                }
            }
            cir.setReturnValue(bytes);
        }
    }
}
