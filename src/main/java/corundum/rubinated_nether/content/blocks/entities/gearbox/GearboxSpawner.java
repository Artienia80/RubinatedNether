package corundum.rubinated_nether.content.blocks.entities.gearbox;

import com.mojang.datafixers.util.Either;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.PlayerDetector;
import net.neoforged.neoforge.common.extensions.IOwnedSpawner;
import org.jetbrains.annotations.Nullable;

public final class GearboxSpawner implements IOwnedSpawner {
    public static final int DETECT_PLAYER_SPAWN_BUFFER = 40;
    private static final int DEFAULT_TARGET_COOLDOWN_LENGTH = 36000;
    private static final int DEFAULT_PLAYER_SCAN_RANGE = 14;
    private static final int MAX_MOB_TRACKING_DISTANCE = 47;
    private static final int MAX_MOB_TRACKING_DISTANCE_SQR = Mth.square(47);
    private static final float SPAWNING_AMBIENT_SOUND_CHANCE = 0.02F;
    private final GearboxSpawnerData data;
    private final int requiredPlayerRange;
    private final int targetCooldownLength;
    private PlayerDetector playerDetector;
    private final PlayerDetector.EntitySelector entitySelector;
    private boolean overridePeacefulAndMobSpawnRule;

    public GearboxSpawner(PlayerDetector playerDetector, PlayerDetector.EntitySelector entitySelector) {
        this(new GearboxSpawnerData(), 36000, 14, playerDetector, entitySelector);
    }

    public GearboxSpawner(
            GearboxSpawnerData data,
            int targetCooldownLength,
            int requiredPlayerRange,
            PlayerDetector playerDetector,
            PlayerDetector.EntitySelector entitySelector
    ) {
        this.data = data;
        this.targetCooldownLength = targetCooldownLength;
        this.requiredPlayerRange = requiredPlayerRange;
        this.playerDetector = playerDetector;
        this.entitySelector = entitySelector;
    }

    public PlayerDetector getPlayerDetector() {
        return this.playerDetector;
    }

    public PlayerDetector.EntitySelector getEntitySelector() {
        return this.entitySelector;
    }

    public boolean canSpawnInLevel(Level level) {
        if (this.overridePeacefulAndMobSpawnRule) {
            return true;
        } else {
            return level.getDifficulty() != Difficulty.PEACEFUL && level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING);
        }
    }

    @Override
    public @Nullable Either<BlockEntity, Entity> getOwner() {
        return null;
    }
}
