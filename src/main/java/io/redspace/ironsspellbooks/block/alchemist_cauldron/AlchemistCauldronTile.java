package io.redspace.ironsspellbooks.block.alchemist_cauldron;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.capabilities.spell.SpellData;
import io.redspace.ironsspellbooks.gui.overlays.ImbuedSpellOverlay;
import io.redspace.ironsspellbooks.item.InkItem;
import io.redspace.ironsspellbooks.item.Scroll;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import io.redspace.ironsspellbooks.spells.SpellRarity;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

import java.util.Stack;

import static io.redspace.ironsspellbooks.block.alchemist_cauldron.AlchemistCauldronBlock.MAX_LEVELS;

public class AlchemistCauldronTile extends BlockEntity {
    public final NonNullList<ItemStack> floatingItems = NonNullList.withSize(MAX_LEVELS, ItemStack.EMPTY);
    public final Stack<ItemStack> storedItems = new Stack<>();
    private final int[] cooktimes = new int[MAX_LEVELS];

    public AlchemistCauldronTile(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockRegistry.ALCHEMIST_CAULDRON_TILE.get(), pWorldPosition, pBlockState);
    }


    public static void serverTick(Level level, BlockPos pos, BlockState blockState, AlchemistCauldronTile cauldronTile) {
        boolean isLit = AlchemistCauldronBlock.isLit(blockState);
        for (int i = 0; i < cauldronTile.floatingItems.size(); i++) {
            ItemStack itemStack = cauldronTile.floatingItems.get(i);
            if (itemStack.isEmpty() || !isLit)
                cauldronTile.cooktimes[i] = 0;
            else
                cauldronTile.cooktimes[i]++;
            if (cauldronTile.cooktimes[i] > 100) {
                //TODO: also check if the cauldron has space
                cauldronTile.meltComponent(itemStack);
                cauldronTile.setChanged();

            }

        }
    }

    public void meltComponent(ItemStack itemStack) {
        if(itemStack.getItem() instanceof Scroll scroll){
            var spellData = SpellData.getSpellData(itemStack);
            SpellRarity rarity = spellData.getSpell().getRarity();
        }
        itemStack.shrink(1);
        level.playSound(null, this.getBlockPos(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.MASTER, 1, 1);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    public boolean addItem(ItemStack newItem) {
        for (int i = 0; i < floatingItems.size(); i++) {
            if (floatingItems.get(i).isEmpty()) {
                var newItemCopy = newItem.copy();
                newItemCopy.setCount(1);
                floatingItems.set(i, newItemCopy);
                setChanged();
                IronsSpellbooks.LOGGER.debug("{}", floatingItems.toString());
                return true;
            }
        }
        return false;
    }

    public int getItemWaterColor(ItemStack itemStack) {
        if (this.getLevel() == null)
            return 0;
        //TODO: ink/potion colors
        return BiomeColors.getAverageWaterColor(this.getLevel(), this.getBlockPos());
    }

//    public Stack<ItemStack> getStoredItems(){
//        return storedItems;
//    }

    public int getAverageWaterColor() {
        //TODO: figure out how this shit is actually going to work
        return getItemWaterColor(null);
    }

    @Override
    public void load(CompoundTag tag) {
        ContainerHelper.loadAllItems(tag, this.floatingItems);
        super.load(tag);
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        ContainerHelper.saveAllItems(tag, this.floatingItems);
        super.saveAdditional(tag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        //var packet = ClientboundBlockEntityDataPacket.create(this);
        //irons_spellbooks.LOGGER.debug("getUpdatePacket: packet.getTag:{}", packet.getTag());
        CompoundTag nbt = getUpdateTag();
        return ClientboundBlockEntityDataPacket.create(this, (block) -> nbt);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        //irons_spellbooks.LOGGER.debug("onDataPacket: pkt.getTag:{}", pkt.getTag());
        handleUpdateTag(pkt.getTag());
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        //irons_spellbooks.LOGGER.debug("handleUpdateTag: tag:{}", tag);
        if (tag != null) {
            load(tag);
        }
    }

    static Object2ObjectOpenHashMap<Item, AlchemistCauldronInteraction> newInteractionMap() {
        var map = Util.make(new Object2ObjectOpenHashMap<Item, AlchemistCauldronInteraction>(), (o2o) -> {
            o2o.defaultReturnValue((blockState, level, blockPos, player, interactionHand, i, itemStack) -> {
                return InteractionResult.PASS;
            });
        });

        map.put(Items.WATER_BUCKET, (blockState, level, pos, player, hand, currentLevel, itemstack) -> {
            if (currentLevel < MAX_LEVELS) {
                createFilledResult(player, hand, level, blockState, pos, MAX_LEVELS, new ItemStack(Items.BUCKET), SoundEvents.BUCKET_EMPTY);
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                return InteractionResult.PASS;
            }
        });
        map.put(Items.BUCKET, (blockState, level, pos, player, hand, currentLevel, itemstack) -> {
            var storedItems = ((AlchemistCauldronTile) level.getBlockEntity(pos)).storedItems;
            if (storedItems.empty() && currentLevel == MAX_LEVELS) {
                createFilledResult(player, hand, level, blockState, pos, 0, new ItemStack(Items.WATER_BUCKET), SoundEvents.BUCKET_FILL);
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                return InteractionResult.PASS;
            }
        });
        map.put(Items.GLASS_BOTTLE, (blockState, level, pos, player, hand, currentLevel, itemstack) -> {
            if (currentLevel > 0) {
                //TODO: safety checks?
                var storedItems = ((AlchemistCauldronTile) level.getBlockEntity(pos)).storedItems;
                if (storedItems.empty()) {
                    //No items means we only hold water, so we should create a water bottle and decrement level
                    createFilledResult(player, hand, level, blockState, pos, currentLevel - 1, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER), SoundEvents.BOTTLE_FILL);
                } else {
                    //If we have an item ready, pop it but don't change the level
                    createFilledResult(player, hand, level, blockState, pos, currentLevel, storedItems.pop(), SoundEvents.BOTTLE_FILL_DRAGONBREATH);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);

            }
            return InteractionResult.PASS;
        });
        map.put(Items.POTION, (blockState, level, pos, player, hand, currentLevel, itemstack) -> {
            if (currentLevel < MAX_LEVELS && PotionUtils.getPotion(itemstack) == Potions.WATER) {
                createFilledResult(player, hand, level, blockState, pos, currentLevel + 1, new ItemStack(Items.GLASS_BOTTLE), SoundEvents.BOTTLE_EMPTY);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            return InteractionResult.PASS;
        });


        return map;
    }


    private static void createFilledResult(Player player, InteractionHand hand, Level level, BlockState blockState, BlockPos blockPos, int newLevel, ItemStack resultItem, SoundEvent soundEvent) {
        player.setItemInHand(hand, ItemUtils.createFilledResult(player.getItemInHand(hand), player, resultItem));
        level.setBlock(blockPos, blockState.setValue(AlchemistCauldronBlock.LEVEL, newLevel), 3);
        level.playSound(null, blockPos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}
