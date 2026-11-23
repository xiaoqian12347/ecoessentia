package com.xiaoqian.ecoessentia.mixin;

import com.xiaoqian.ecoessentia.item.EssentiaCell;
import github.kasuminova.ecoaeextension.common.block.ecotech.estorage.prop.DriveStorageType;
import github.kasuminova.ecoaeextension.common.container.data.EStorageCellData;
import github.kasuminova.ecoaeextension.common.tile.ecotech.estorage.EStorageCellDrive;
import mcjty.theoneprobe.api.IProbeInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "github.kasuminova.ecoaeextension.common.integration.theoneprobe.EStorageInfoProvider", remap = false)
public abstract class ProbeInfoMixin {
    @Redirect(method = "processCellDriveInfo", at = @At(value = "INVOKE",
            target = "Lgithub/kasuminova/ecoaeextension/common/container/data/EStorageCellData;type()Lgithub/kasuminova/ecoaeextension/common/block/ecotech/estorage/prop/DriveStorageType;"))
    private static DriveStorageType xiaoqian$type(EStorageCellData data) {
        return "essentia".equals(data.type().getName()) ? DriveStorageType.FLUID : data.type();
    }

    @Redirect(method = "processCellDriveInfo", at = @At(value = "INVOKE",
            target = "Ljava/lang/String;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"))
    private static String xiaoqian$label(String format, Object[] args, IProbeInfo info, EStorageCellDrive drive) {
        if (drive.getDriveInv().getStackInSlot(0).getItem() instanceof EssentiaCell
                && args.length > 0 && "gui.estorage_controller.cell_info.fluid".equals(args[0])) {
            args[0] = "gui.ecoessentia.cell_info.essentia";
        }
        return String.format(format, args);
    }
}
