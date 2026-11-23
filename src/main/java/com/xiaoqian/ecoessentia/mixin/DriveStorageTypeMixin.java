package com.xiaoqian.ecoessentia.mixin;

import github.kasuminova.ecoaeextension.common.block.ecotech.estorage.prop.DriveStorageType;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

/** Append before PropertyEnum is created; preserve all existing enum ordinals. */
@Mixin(value = DriveStorageType.class, remap = false)
public abstract class DriveStorageTypeMixin {
    @Shadow @Final @Mutable private static DriveStorageType[] $VALUES;

    @Invoker("<init>")
    private static DriveStorageType xiaoqian$construct(String name, int ordinal, String serializedName) {
        throw new AssertionError();
    }

    @Inject(method = "<clinit>", at = @At(value = "FIELD", opcode = Opcodes.PUTSTATIC,
            target = "Lgithub/kasuminova/ecoaeextension/common/block/ecotech/estorage/prop/DriveStorageType;$VALUES:[Lgithub/kasuminova/ecoaeextension/common/block/ecotech/estorage/prop/DriveStorageType;",
            shift = At.Shift.AFTER))
    private static void xiaoqian$appendEssentia(CallbackInfo ci) {
        int ordinal = $VALUES.length;
        DriveStorageType essentia = xiaoqian$construct("ESSENTIA", ordinal, "essentia");
        $VALUES = Arrays.copyOf($VALUES, ordinal + 1);
        $VALUES[ordinal] = essentia;
    }
}
