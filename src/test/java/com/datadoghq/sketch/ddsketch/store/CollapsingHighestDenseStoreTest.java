/* Unless explicitly stated otherwise all files in this repository are licensed under the Apache License 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2020 Datadog, Inc.
 */

package com.datadoghq.sketch.ddsketch.store;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;

abstract class CollapsingHighestDenseStoreTest extends StoreTest {

    abstract int maxNumBins();

    @Override
    Store newStore() {
        return new CollapsingHighestDenseStore(maxNumBins());
    }

    @Override
    Map<Integer, Long> getCounts(Bin... bins) {
        final OptionalInt minIndex = Arrays.stream(bins)
            .filter(bin -> bin.getCount() > 0)
            .mapToInt(Bin::getIndex)
            .min();
        if (!minIndex.isPresent()) {
            return Collections.emptyMap();
        }
        final int maxStorableIndex = (int) Math.min(Integer.MAX_VALUE, (long) minIndex.getAsInt() + maxNumBins() - 1);
        return Arrays.stream(bins)
            .collect(Collectors.groupingBy(
                bin -> Math.min(bin.getIndex(), maxStorableIndex),
                Collectors.summingLong(Bin::getCount)
            ));
    }

    static class CollapsingHighestDenseStoreTest1 extends CollapsingHighestDenseStoreTest {

        @Override
        int maxNumBins() {
            return 1;
        }
    }

    static class CollapsingHighestDenseStoreTest20 extends CollapsingHighestDenseStoreTest {

        @Override
        int maxNumBins() {
            return 20;
        }
    }

    static class CollapsingHighestDenseStoreTest1000 extends CollapsingHighestDenseStoreTest {

        @Override
        int maxNumBins() {
            return 1000;
        }
    }
}
