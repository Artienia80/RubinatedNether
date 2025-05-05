package corundum.rubinated_nether.utils;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.items.RuneItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class RuneUnlockCondition {
    private static final String ADVANCEMENT_PREFIX = "rubinated_nether:runes/";

    public static boolean isRuneUnlocked(RuneItem rune, Player player) {
        String advancementPath = ADVANCEMENT_PREFIX + rune.getRubination().name().toLowerCase();
        return player instanceof ServerPlayer serverPlayer &&
                serverPlayer.getAdvancements().getOrStartProgress(
                                serverPlayer.server.getAdvancements().get(RubinatedNether.id(advancementPath)))
                        .isDone();
    }
}