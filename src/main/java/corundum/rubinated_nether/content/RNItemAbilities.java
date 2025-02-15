/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package corundum.rubinated_nether.content;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.extensions.IBlockExtension;
import net.neoforged.neoforge.common.extensions.IItemExtension;

public class RNItemAbilities {

    public static final ItemAbility DRILL_CRACK = ItemAbility.get("drill_crack");

    private static Set<ItemAbilities> of(ItemAbilities... actions) {
        return Stream.of(actions).collect(Collectors.toCollection(Sets::newIdentityHashSet));
    }
}
