package com.xiaoqian.ecoessentia.mixin;

import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Arrays;
import java.util.List;

public final class LateMixinLoader implements ILateMixinLoader {
    @Override
    public List<String> getMixinConfigs() {
        return Arrays.asList("mixins.ecoessentia.json", "mixins.ecoessentia_top.json");
    }

    @Override
    public boolean shouldMixinConfigQueue(String config) {
        return Loader.isModLoaded("ecoaeextension") && Loader.isModLoaded("thaumicenergistics")
                && (!config.endsWith("_top.json") || Loader.isModLoaded("theoneprobe"));
    }
}
