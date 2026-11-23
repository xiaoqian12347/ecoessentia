package com.xiaoqian.ecoessentia.storage;

import org.junit.Test;

import static org.junit.Assert.*;

public class EssentiaStoreTest {
    private EssentiaStore small() {
        return new EssentiaStore(100, 10, 32, 2);
    }

    @Test
    public void simulationDoesNotChangeContentsOrTypeCount() {
        EssentiaStore store = small();
        assertEquals(2880, store.insert("aer", Long.MAX_VALUE, true));
        assertEquals(0, store.count());
        assertEquals(0, store.usedBytes());
        assertTrue(store.amounts().isEmpty());
        store.insert("aer", 123, false);
        assertEquals(123, store.extract("aer", Long.MAX_VALUE, true));
        assertEquals(123, store.count());
    }

    @Test
    public void capacityIncludesTypeOverheadAndPartialBytes() {
        EssentiaStore store = small();
        assertEquals(1, store.insert("aer", 1, false));
        assertEquals(11, store.usedBytes());
        assertEquals(31, store.unusedUnits());
        assertEquals(2879, store.remainingUnits());
        assertEquals(2879, store.insert("aer", Long.MAX_VALUE, false));
        assertEquals(100, store.usedBytes());
        assertEquals(0, store.freeBytes());
        assertEquals(0, store.insert("aer", 1, false));
        assertEquals(0, store.insert("ignis", 1, false));
    }

    @Test
    public void secondTypePaysItsOwnOverhead() {
        EssentiaStore store = small();
        store.insert("aer", 2000, false);
        assertEquals(560, store.insert("ignis", Long.MAX_VALUE, false));
        assertEquals(2560, store.count());
        assertEquals(100, store.usedBytes());
    }

    @Test
    public void typeLimitStillAllowsExistingAspect() {
        EssentiaStore store = small();
        store.insert("aer", 1, false);
        store.insert("ignis", 1, false);
        assertEquals(0, store.insert("terra", 1, false));
        assertEquals(1, store.insert("aer", 1, false));
        assertFalse(store.canAddType());
    }

    @Test
    public void fullExtractionReleasesTypeAndBytes() {
        EssentiaStore store = small();
        store.insert("aer", 100, false);
        store.insert("ignis", 100, false);
        assertEquals(100, store.extract("aer", Long.MAX_VALUE, false));
        assertEquals(1, store.amounts().size());
        assertTrue(store.canAddType());
        assertEquals(1, store.insert("terra", 1, false));
        store.extract("terra", 1, false);
        store.extract("ignis", 100, false);
        assertEquals(0, store.usedBytes());
        assertEquals(0, store.count());
    }

    @Test
    public void supportsMoreThanSignedIntAndLongSizedRequests() {
        EssentiaStore store = new EssentiaStore(256 * 1000 * 1024L, 64 * 1024, 8 * 64, 25);
        long capacity = (256 * 1000 * 1024L - 64 * 1024) * 8 * 64;
        assertEquals(capacity, store.insert("aer", Long.MAX_VALUE, false));
        assertTrue(store.count() > Integer.MAX_VALUE);
        assertEquals(capacity, store.extract("aer", Long.MAX_VALUE, false));
        assertEquals(0, store.count());
    }

    @Test
    public void nativeUnitScaleIsRespected() {
        EssentiaStore store = new EssentiaStore(100, 10, 8000 * 4, 25);
        assertEquals(90L * 8000 * 4, store.insert("aer", Long.MAX_VALUE, false));
        assertEquals(100, store.usedBytes());
    }

    @Test
    public void savedUnknownAndOverfullAspectsArePreserved() {
        EssentiaStore store = small();
        store.load("missing_addon_aspect", 10000);
        assertEquals(10000, store.count());
        assertEquals(0, store.remainingUnits());
        assertEquals(0, store.insert("aer", 1, false));
        assertEquals(10000, store.extract("missing_addon_aspect", Long.MAX_VALUE, false));
        assertEquals(0, store.usedBytes());
    }

    @Test
    public void invalidRequestsCannotCreateOrDestroyEssentia() {
        EssentiaStore store = small();
        store.insert("aer", 42, false);
        assertEquals(0, store.insert("aer", -1, false));
        assertEquals(0, store.insert("aer", 0, false));
        assertEquals(0, store.insert(null, 10, false));
        assertEquals(0, store.extract("aer", -1, false));
        assertEquals(0, store.extract("aer", 0, false));
        assertEquals(0, store.extract("ignis", 10, false));
        assertEquals(42, store.count());
    }

    @Test
    public void loadingAmountsKeepsExactLongValues() {
        EssentiaStore original = small();
        original.load("aer", 9000000000L);
        original.load("ignis", 123);
        EssentiaStore loaded = small();
        original.amounts().forEach(loaded::load);
        assertEquals(original.amounts(), loaded.amounts());
        assertEquals(original.count(), loaded.count());
        assertEquals(original.usedBytes(), loaded.usedBytes());
    }

    @Test(expected = ArithmeticException.class)
    public void rejectsOverflowingSavedTotals() {
        EssentiaStore store = small();
        store.load("aer", Long.MAX_VALUE);
        store.load("ignis", 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNegativeSavedAmounts() {
        small().load("aer", -1);
    }

    @Test
    public void allFluidReferenceTiersAllowExactlyTwentyFiveTypes() {
        int[] sizes = {16, 64, 256};
        int[] multipliers = {4, 16, 64};
        for (int i = 0; i < sizes.length; i++) {
            EssentiaStore store = new EssentiaStore(sizes[i] * 1000L * 1024,
                    multipliers[i] * 1024, multipliers[i] * 8, 25);
            for (int type = 0; type < 25; type++) {
                assertEquals(1, store.insert("aspect" + type, 1, false));
            }
            assertEquals(0, store.insert("aspect25", 1, false));
            assertEquals(25, store.count());
        }
    }
}
