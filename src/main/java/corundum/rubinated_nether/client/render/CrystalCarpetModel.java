package corundum.rubinated_nether.client.render;

import corundum.rubinated_nether.content.blocks.CrystallizedBronzeCrystalBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CrystalCarpetModel implements IDynamicBakedModel {
    public static final ModelProperty<CarpetData> DATA = new ModelProperty<>();

    public record CarpetData(BlockPos pos, long hiddenSides) {
    }

    private final BakedModel fallback;
    private final BakedModel[][] tiles;

    public CrystalCarpetModel(BakedModel fallback, BakedModel[][] tiles) {
        this.fallback = fallback;
        this.tiles = tiles;
    }

    public static int tileIndex(Direction facing, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        int u = switch (facing) {
            case UP, DOWN, SOUTH -> x;
            case NORTH -> -x;
            case EAST -> -z;
            case WEST -> z;
        };
        int v = switch (facing) {
            case UP -> z;
            case DOWN -> -z;
            default -> -y;
        };
        return Math.floorMod(u, 10) * 6 + Math.floorMod(v, 6);
    }

    @Override
    public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
        long hidden = 0L;
        for (Direction facing : Direction.values()) {
            if (!state.getValue(CrystallizedBronzeCrystalBlock.faceProperty(facing))) {
                continue;
            }
            for (Direction side : Direction.values()) {
                if (side.getAxis() == facing.getAxis()) {
                    continue;
                }
                BlockState neighbor = level.getBlockState(pos.relative(side));
                if (neighbor.is(state.getBlock()) && neighbor.getValue(CrystallizedBronzeCrystalBlock.faceProperty(facing))) {
                    hidden |= 1L << (facing.ordinal() * 6 + side.ordinal());
                }
            }
        }
        return ModelData.of(DATA, new CarpetData(pos.immutable(), hidden));
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
        CarpetData carpet = data.get(DATA);
        if (state == null || carpet == null) {
            return fallback.getQuads(state, side, rand, data, renderType);
        }

        List<BakedQuad> quads = new ArrayList<>();
        for (Direction facing : Direction.values()) {
            if (!state.getValue(CrystallizedBronzeCrystalBlock.faceProperty(facing))) {
                continue;
            }
            if (side != null && (carpet.hiddenSides() & (1L << (facing.ordinal() * 6 + side.ordinal()))) != 0L) {
                continue;
            }
            BakedModel tile = tiles[facing.ordinal()][tileIndex(facing, carpet.pos())];
            quads.addAll(tile.getQuads(state, side, rand, ModelData.EMPTY, renderType));
        }
        return quads;
    }

    @Override
    public @NotNull ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        return ChunkRenderTypeSet.of(RenderType.cutout());
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon() {
        return fallback.getParticleIcon();
    }

    @Override
    public @NotNull ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }
}
