package corundum.rubinated_nether.client;

import corundum.rubinated_nether.networking.MusicBoxPlayPayload;
import corundum.rubinated_nether.networking.MusicBoxRetargetPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * A music box song that follows an entity. Unlike a vanilla entity-bound sound, the entity it follows can be
 * swapped while it plays, so dropping or picking up the box moves the song instead of restarting it.
 * Client only: reached from the payload handlers, never loaded on a dedicated server.
 */
public class MusicBoxSoundInstance extends AbstractTickableSoundInstance {
    private static final List<MusicBoxSoundInstance> ACTIVE = new ArrayList<>();
    private static final int MAX_TICKS_WITHOUT_TARGET = 40;

    private int targetId;
    private int ticksWithoutTarget;

    private MusicBoxSoundInstance(SoundEvent sound, int targetId) {
        super(sound, SoundSource.RECORDS, SoundInstance.createUnseededRandom());
        this.targetId = targetId;
        this.volume = 4.0F;
        this.pitch = 1.0F;
        Entity target = target();
        if (target != null) {
            follow(target);
        }
    }

    public static void handlePlay(MusicBoxPlayPayload payload) {
        ACTIVE.removeIf(AbstractTickableSoundInstance::isStopped);
        MusicBoxSoundInstance instance = new MusicBoxSoundInstance(
                SoundEvent.createVariableRangeEvent(payload.sound()), payload.entityId());
        ACTIVE.add(instance);
        Minecraft.getInstance().getSoundManager().play(instance);
    }

    public static void handleRetarget(MusicBoxRetargetPayload payload) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.level.getEntity(payload.toEntityId()) == null) {
            return;
        }
        for (MusicBoxSoundInstance instance : ACTIVE) {
            if (!instance.isStopped() && instance.targetId == payload.fromEntityId()) {
                instance.targetId = payload.toEntityId();
                instance.ticksWithoutTarget = 0;
            }
        }
    }

    private Entity target() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.level == null ? null : minecraft.level.getEntity(this.targetId);
    }

    private void follow(Entity entity) {
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
    }

    @Override
    public void tick() {
        if (Minecraft.getInstance().level == null) {
            stop();
            return;
        }
        Entity target = target();
        if (target == null || target.isRemoved()) {
            // The server stops the song when the box is really gone; just do not hang around forever
            if (++this.ticksWithoutTarget > MAX_TICKS_WITHOUT_TARGET) {
                stop();
            }
            return;
        }
        this.ticksWithoutTarget = 0;
        follow(target);
    }
}
