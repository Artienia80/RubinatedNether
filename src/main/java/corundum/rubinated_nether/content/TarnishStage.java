package corundum.rubinated_nether.content;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.level.block.Block;

import java.util.function.IntFunction;

public enum TarnishStage {
    UNAFFECTED((byte) 0,"unaffected", 0.25D, 20.0, 5.0, 0.0, 1200),
    DISCOLORED((byte) 1, "discolored", 0.20D, 20.0, 10.0, 8.0, 1600),
    CORRODED((byte) 2, "corroded", 0.15D, 20.0, 15.0, 16.0, 2000),
    TARNISHED((byte) 3, "tarnished", 0.10D, 20.0, 10.0, 20.0, 2400),
    CRYSTALLIZED((byte) 4, "crystallized", 0.32D, 8.0, 4.0, 0.0, -1);

    public static final IntFunction<TarnishStage> BY_ID = ByIdMap.continuous(TarnishStage::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    public static final StreamCodec<ByteBuf, TarnishStage> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, TarnishStage::getId);
    public static final Codec<TarnishStage> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<TarnishStage, T>> decode(DynamicOps<T> ops, T input) {
            return null;
        }

        @Override
        public <T> DataResult<T> encode(TarnishStage input, DynamicOps<T> ops, T prefix) {
            return null;
        }
    };

    private final byte id;
    private final String name;
    final double speed;
    final double health;
    final double attack;
    final double armor;
    final int tarnishDuration;

    TarnishStage(byte id, String name, double speed, double health, double attack, double armor, int tarnishDuration) {
        this.id = id;
        this.name = name;
        this.speed = speed;
        this.health = health;
        this.attack = attack;
        this.armor = armor;
        this.tarnishDuration = tarnishDuration;
    }

    public byte getId(){
        return (byte) this.id;
    }

    public double getArmor() {
        return this.armor;
    }

    public double getAttack() {
        return this.attack;
    }

    public double getHealth() {
        return this.health;
    }

    public double getSpeed() {
        return this.speed;
    }

    public int getTarnishDuration() {
        return this.tarnishDuration;
    }

    public String getSerializedName() {
        return this.name;
    }

    public static TarnishStage byId(int id){
        id = Mth.clamp(id, 0, 4);
        return TarnishStage.values()[id];
    }
}