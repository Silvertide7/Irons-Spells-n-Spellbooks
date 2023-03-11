package io.redspace.ironsspellbooks.spells.ice;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.spells.AbstractSpell;
import io.redspace.ironsspellbooks.spells.SpellType;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Optional;

public class FrostbiteSpell extends AbstractSpell {
    public FrostbiteSpell() {
        this(1);
    }

    public FrostbiteSpell(int level) {
        super(SpellType.FROSTBITE_SPELL);
        this.level = level;
        this.manaCostPerLevel = 50;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 3;
        this.castTime = 0;
        this.baseManaCost = 100;
        uniqueInfo.add(Component.translatable("ui.irons_spellbooks.frostbite_success_chance", Utils.stringTruncation(getSpellPower(null), 1)));
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
    public void onCast(Level level, LivingEntity entity, PlayerMagicData playerMagicData) {
        HitResult raycast = Utils.raycastForEntity(level, entity, 48, true);
        if (raycast.getType() == HitResult.Type.ENTITY) {
            Entity target = ((EntityHitResult) raycast).getEntity();
            if (target instanceof LivingEntity livingTarget) {
                float threshold = getSpellPower(entity);
                float hpPercent = livingTarget.getHealth() / livingTarget.getMaxHealth();
                boolean success = false;
                /*
                *   The Chance to succeed and inflict frostbite is based off of the current target's health
                *   If their health is below our spell power, we automatically succeed
                *   Otherwise, we have a chance to succeed
                * */
                if (livingTarget.getHealth() <= threshold)
                    success = true;
                //else if()
                //livingTarget.addEffect();
            }
        }
        super.onCast(level, entity, playerMagicData);
    }
}