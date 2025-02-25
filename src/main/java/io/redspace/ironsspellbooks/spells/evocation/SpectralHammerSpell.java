package io.redspace.ironsspellbooks.spells.evocation;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.entity.spells.spectral_hammer.SpectralHammer;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class SpectralHammerSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(IronsSpellbooks.MODID, "spectral_hammer");

    private static final int distance = 12;

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.dimensions", 1 + getRadius(spellLevel, caster) * 2, 1 + getRadius(spellLevel, caster) * 2, getDepth(spellLevel, caster) + 1),
                Component.translatable("ui.irons_spellbooks.distance", distance)
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(10)
            .build();

    public SpectralHammerSpell() {
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 1;
        this.castTime = 25;
        this.baseManaCost = 15;
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public boolean checkPreCastConditions(Level level, LivingEntity entity, MagicData playerMagicData) {
        return Utils.getTargetBlock(level, entity, ClipContext.Fluid.NONE, distance).getType() == HitResult.Type.BLOCK;
    }

    @Override
    public void onCast(Level world, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        var blockPosition = Utils.getTargetBlock(world, entity, ClipContext.Fluid.NONE, distance);
        var face = blockPosition.getDirection();

        int radius = getRadius(spellLevel, entity);
        int depth = getDepth(spellLevel, entity);

        var spectralHammer = new SpectralHammer(world, entity, blockPosition, depth, radius);
        Vec3 position = Vec3.atCenterOf(blockPosition.getBlockPos());

        if (!face.getAxis().isVertical()) {
            position = position.subtract(0, 2, 0).subtract(entity.getForward().normalize().scale(1.5));
        } else if (face == Direction.DOWN) {
            position = position.subtract(0, 3, 0);
        }

        spectralHammer.setPos(position.x, position.y, position.z);
        world.addFreshEntity(spectralHammer);
        //IronsSpellbooks.LOGGER.debug("SpectralHammerSpell.onCast pos:{}", position);
        super.onCast(world, spellLevel, entity, playerMagicData);
    }

    private int getDepth(int spellLevel, LivingEntity caster) {
        return (int) getSpellPower(spellLevel, caster);
    }

    private int getRadius(int spellLevel, LivingEntity caster) {
        return (int) Math.max(getSpellPower(spellLevel, caster) * .5f, 1);
    }
}
