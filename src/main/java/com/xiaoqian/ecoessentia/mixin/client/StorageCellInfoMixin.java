package com.xiaoqian.ecoessentia.mixin.client;

import com.xiaoqian.ecoessentia.EcoEssentia;
import github.kasuminova.mmce.client.gui.util.TextureProperties;
import github.kasuminova.ecoaeextension.common.block.ecotech.estorage.prop.DriveStorageType;
import github.kasuminova.ecoaeextension.common.container.data.EStorageCellData;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "github.kasuminova.ecoaeextension.client.gui.widget.estorage.EStorageCellInfo", remap = false)
public abstract class StorageCellInfoMixin {
    @Shadow @Final protected EStorageCellData data;
    @Shadow protected TextureProperties cellTypeBackground;

    @Unique
    private static final TextureProperties xiaoqian$essentiaBackground = new TextureProperties(
            new ResourceLocation(EcoEssentia.MOD_ID, "textures/gui/estorage_controller_elements.png"),
            10, 82, 8, 26);

    // Reuse the fluid switch branch while keeping ESSENTIA in network and server data.
    @Redirect(method = {"initBackground", "initInfo"}, at = @At(value = "INVOKE",
            target = "Lgithub/kasuminova/ecoaeextension/common/container/data/EStorageCellData;type()Lgithub/kasuminova/ecoaeextension/common/block/ecotech/estorage/prop/DriveStorageType;"))
    private DriveStorageType xiaoqian$displayType(EStorageCellData data) {
        return "essentia".equals(data.type().getName()) ? DriveStorageType.FLUID : data.type();
    }

    @Inject(method = "initBackground", at = @At("RETURN"))
    private void xiaoqian$essentiaBackground(CallbackInfo ci) {
        if ("essentia".equals(data.type().getName())) {
            cellTypeBackground = xiaoqian$essentiaBackground;
        }
    }

    @Redirect(method = "initInfo", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resources/I18n;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", remap = true))
    private String xiaoqian$label(String key, Object[] args) {
        if ("essentia".equals(data.type().getName()) && "gui.estorage_controller.cell_info.fluid".equals(key)) {
            key = "gui.ecoessentia.cell_info.essentia";
        }
        return I18n.format(key, args);
    }
}
