package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.entities.VaseBlockEntity;
import corundum.rubinated_nether.utils.BEBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class BronzeVaseBlock extends TarnishingBronzeBlock implements BEBlock<VaseBlockEntity> {
    public static final ResourceLocation CONTENTS = RubinatedNether.id("contents");
    private static final Component UNKNOWN_CONTENTS = Component.translatable("container.bronzeVase.unknownContents");

    public BronzeVaseBlock(TarnishStage tarnishStage, BlockBehaviour.Properties properties) {
        super(tarnishStage, properties);
    }

    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof VaseBlockEntity blockEntity) {
            if (!level.isClientSide && player.isCreative() && !blockEntity.isEmpty()) {
                ItemStack itemstack = new ItemStack(RNBlocks.BRONZE_VASE.asItem());
                itemstack.applyComponents(blockentity.collectComponents());
                ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + (double)0.5F, (double)pos.getY() + (double)0.5F, (double)pos.getZ() + (double)0.5F, itemstack);
                itementity.setDefaultPickUpDelay();
                level.addFreshEntity(itementity);
            } else {
                blockEntity.unpackLootTable(player);
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        BlockEntity blockentity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockentity instanceof VaseBlockEntity blockEntity) {
            params = params.withDynamicDrop(CONTENTS, (consumer) -> {
                for(int i = 0; i < blockEntity.getContainerSize(); ++i) {
                    consumer.accept(blockEntity.getItem(i));
                }

            });
        }

        return super.getDrops(state, params);
    }

    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            super.onRemove(state, level, pos, newState, isMoving);
            if (blockentity instanceof VaseBlockEntity) {
                level.updateNeighbourForOutputSignal(pos, state.getBlock());
            }
        }

    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else if (player.isSpectator()) {
            return InteractionResult.CONSUME;
        } else {
            BlockEntity var7 = level.getBlockEntity(pos);
            if (var7 instanceof VaseBlockEntity) {
                VaseBlockEntity blockEntity = (VaseBlockEntity)var7;
                player.openMenu(blockEntity);
                //player.awardStat(Stats.OPEN_SHULKER_BOX);
                PiglinAi.angerNearbyPiglins(player, true);
                return InteractionResult.CONSUME;
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if (stack.has(DataComponents.CONTAINER_LOOT)) {
            tooltipComponents.add(UNKNOWN_CONTENTS);
        }

        int i = 0;
        int j = 0;

        for(ItemStack itemstack : stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).nonEmptyItems()) {
            ++j;
            if (i <= 4) {
                ++i;
                tooltipComponents.add(Component.translatable("container.bronzeVase.itemCount", itemstack.getHoverName(), itemstack.getCount()));
            }
        }

        if (j - i > 0) {
            tooltipComponents.add(Component.translatable("container.bronzeVase.more", j - i).withStyle(ChatFormatting.ITALIC));
        }

    }

    @Override
    public BlockEntityType<? extends VaseBlockEntity> getBlockEntityType() {
        return RNBlockEntities.BRONZE_VASE.get();
    }

    @Override
    public Class<? extends VaseBlockEntity> getBlockEntityClass() {
        return VaseBlockEntity.class;
    }
}
