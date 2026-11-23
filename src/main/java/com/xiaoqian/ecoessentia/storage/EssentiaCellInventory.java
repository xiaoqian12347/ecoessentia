package com.xiaoqian.ecoessentia.storage;

import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IItemList;
import com.xiaoqian.ecoessentia.item.EssentiaCell;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;
import thaumcraft.api.aspects.Aspect;
import thaumicenergistics.api.storage.IAEEssentiaStack;

import java.util.Map;

public final class EssentiaCellInventory implements ICellInventory<IAEEssentiaStack> {
    // Persistent save-format key, independent of the public registry namespace.
    private static final String DATA_KEY = "xiaoqian_ecoessentia";
    private final ItemStack stack;
    private final EssentiaCell cell;
    private final ISaveProvider host;
    private final IStorageChannel<IAEEssentiaStack> channel;
    private final EssentiaStore store;

    public EssentiaCellInventory(ItemStack stack, ISaveProvider host) {
        this.stack = stack;
        this.cell = (EssentiaCell) stack.getItem();
        this.host = host;
        this.channel = cell.getChannel();
        this.store = new EssentiaStore(cell.getBytes(stack), cell.getBytesPerType(stack),
                Math.multiplyExact(channel.getUnitsPerByte(), cell.getByteMultiplier()), cell.getTotalTypes(stack));
        NBTTagCompound root = stack.getTagCompound();
        if (root != null && root.hasKey(DATA_KEY)) {
            if (!root.hasKey(DATA_KEY, 10)) {
                throw new IllegalStateException("Invalid ECO essentia storage NBT");
            }
            NBTTagCompound data = root.getCompoundTag(DATA_KEY);
            for (String aspect : data.getKeySet()) {
                if (!data.hasKey(aspect, 4)) {
                    throw new IllegalStateException("Invalid saved essentia amount: " + aspect);
                }
                store.load(aspect, data.getLong(aspect));
            }
        }
    }

    @Override
    public IAEEssentiaStack injectItems(IAEEssentiaStack input, Actionable mode, IActionSource source) {
        if (input == null || input.getStackSize() <= 0) {
            return input;
        }
        if (input.getAspect() == null) {
            return input;
        }
        String aspect = input.getAspect().getTag();
        long accepted = store.insert(aspect, input.getStackSize(), mode == Actionable.SIMULATE);
        if (accepted > 0 && mode == Actionable.MODULATE) {
            save(aspect);
        }
        return accepted == input.getStackSize() ? null : input.copy().setStackSize(input.getStackSize() - accepted);
    }

    @Override
    public IAEEssentiaStack extractItems(IAEEssentiaStack request, Actionable mode, IActionSource source) {
        if (request == null || request.getStackSize() <= 0 || request.getAspect() == null) {
            return null;
        }
        String aspect = request.getAspect().getTag();
        long extracted = store.extract(aspect, request.getStackSize(), mode == Actionable.SIMULATE);
        if (extracted == 0) {
            return null;
        }
        if (mode == Actionable.MODULATE) {
            save(aspect);
        }
        return request.copy().setStackSize(extracted);
    }

    private void save(String aspect) {
        NBTTagCompound root = stack.getTagCompound();
        if (root == null) {
            root = new NBTTagCompound();
            stack.setTagCompound(root);
        }
        NBTTagCompound data = root.getCompoundTag(DATA_KEY);
        Long amount = store.amounts().get(aspect);
        if (amount == null) {
            data.removeTag(aspect);
        } else {
            data.setLong(aspect, amount);
        }
        if (data.isEmpty()) {
            root.removeTag(DATA_KEY);
        } else {
            root.setTag(DATA_KEY, data);
        }
        if (host != null) {
            host.saveChanges(this);
        }
    }

    @Override
    public IItemList<IAEEssentiaStack> getAvailableItems(IItemList<IAEEssentiaStack> out) {
        for (Map.Entry<String, Long> entry : store.amounts().entrySet()) {
            Aspect aspect = Aspect.getAspect(entry.getKey());
            if (aspect != null) {
                IAEEssentiaStack available = channel.createStack(aspect);
                if (available != null) {
                    available.setStackSize(entry.getValue());
                    available.setCraftable(false);
                    out.add(available);
                }
            }
        }
        return out;
    }

    @Override public IStorageChannel<IAEEssentiaStack> getChannel() { return channel; }
    @Override public ItemStack getItemStack() { return stack; }
    @Override public double getIdleDrain() { return cell.getIdleDrain(); }
    @Override public FuzzyMode getFuzzyMode() { return cell.getFuzzyMode(stack); }
    @Override public IItemHandler getConfigInventory() { return cell.getConfigInventory(stack); }
    @Override public IItemHandler getUpgradesInventory() { return cell.getUpgradesInventory(stack); }
    @Override public int getBytesPerType() { return cell.getBytesPerType(stack); }
    @Override public boolean canHoldNewItem() { return store.canAddType(); }
    @Override public long getTotalBytes() { return cell.getBytes(stack); }
    @Override public long getFreeBytes() { return store.freeBytes(); }
    @Override public long getUsedBytes() { return store.usedBytes(); }
    @Override public long getTotalItemTypes() { return cell.getTotalTypes(stack); }
    @Override public long getStoredItemCount() { return store.count(); }
    @Override public long getStoredItemTypes() { return store.amounts().size(); }
    @Override public long getRemainingItemTypes() { return Math.max(0, getTotalItemTypes() - getStoredItemTypes()); }
    @Override public long getRemainingItemCount() { return store.remainingUnits(); }
    @Override public int getUnusedItemCount() { return store.unusedUnits(); }
    @Override public int getStatusForCell() { return canHoldNewItem() ? 1 : getRemainingItemCount() > 0 ? 2 : 3; }
    @Override public void persist() { /* Each mutation writes its affected aspect directly to the item NBT. */ }
}
