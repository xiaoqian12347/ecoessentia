package com.xiaoqian.ecoessentia.storage;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Capacity accounting uses native essentia units, not millibuckets. */
public final class EssentiaStore {
    private final long totalBytes;
    private final int bytesPerType;
    private final int unitsPerByte;
    private final int maxTypes;
    private final Map<String, Long> amounts = new LinkedHashMap<>();
    private final Map<String, Long> readOnlyAmounts = Collections.unmodifiableMap(amounts);
    private long total;

    public EssentiaStore(long totalBytes, int bytesPerType, int unitsPerByte, int maxTypes) {
        if (totalBytes <= 0 || bytesPerType <= 0 || unitsPerByte <= 0 || maxTypes <= 0) {
            throw new IllegalArgumentException("Storage limits must be positive");
        }
        Math.multiplyExact(totalBytes, unitsPerByte);
        this.totalBytes = totalBytes;
        this.bytesPerType = bytesPerType;
        this.unitsPerByte = unitsPerByte;
        this.maxTypes = maxTypes;
    }

    public long insert(String aspect, long amount, boolean simulate) {
        if (aspect == null || aspect.isEmpty() || amount <= 0) {
            return 0;
        }
        boolean existing = amounts.containsKey(aspect);
        if (!existing && amounts.size() >= maxTypes) {
            return 0;
        }
        long overhead = (long) (amounts.size() + (existing ? 0 : 1)) * bytesPerType;
        long capacity = Math.max(0, totalBytes - overhead) * unitsPerByte;
        long accepted = Math.min(amount, Math.max(0, capacity - total));
        if (accepted > 0 && !simulate) {
            amounts.put(aspect, amounts.getOrDefault(aspect, 0L) + accepted);
            total += accepted;
        }
        return accepted;
    }

    public long extract(String aspect, long amount, boolean simulate) {
        if (amount <= 0) {
            return 0;
        }
        long stored = amounts.getOrDefault(aspect, 0L);
        long extracted = Math.min(stored, amount);
        if (extracted > 0 && !simulate) {
            if (stored == extracted) {
                amounts.remove(aspect);
            } else {
                amounts.put(aspect, stored - extracted);
            }
            total -= extracted;
        }
        return extracted;
    }

    /** Preserve over-capacity or currently unknown aspects so they can still be recovered. */
    public void load(String aspect, long amount) {
        if (aspect == null || aspect.isEmpty() || amount <= 0 || amounts.containsKey(aspect)) {
            throw new IllegalArgumentException("Invalid saved essentia entry: " + aspect);
        }
        long updated = Math.addExact(total, amount);
        amounts.put(aspect, amount);
        total = updated;
    }

    public Map<String, Long> amounts() {
        return readOnlyAmounts;
    }

    public long count() {
        return total;
    }

    public long usedBytes() {
        return (long) amounts.size() * bytesPerType + total / unitsPerByte
                + (total % unitsPerByte == 0 ? 0 : 1);
    }

    public long freeBytes() {
        return Math.max(0, totalBytes - usedBytes());
    }

    public int unusedUnits() {
        int remainder = (int) (total % unitsPerByte);
        return remainder == 0 ? 0 : unitsPerByte - remainder;
    }

    public long remainingUnits() {
        long capacity = Math.max(0, totalBytes - (long) amounts.size() * bytesPerType) * unitsPerByte;
        return Math.max(0, capacity - total);
    }

    public boolean canAddType() {
        return amounts.size() < maxTypes && remainingUnits() > (long) bytesPerType * unitsPerByte;
    }
}
