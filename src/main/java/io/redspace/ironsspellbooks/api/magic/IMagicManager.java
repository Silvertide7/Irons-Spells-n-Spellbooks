package io.redspace.ironsspellbooks.api.magic;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import net.minecraft.server.level.ServerPlayer;

public interface IMagicManager {
    void setPlayerCurrentMana(ServerPlayer serverPlayer, int newManaValue);

    void addCooldown(ServerPlayer serverPlayer, AbstractSpell spell, CastSource castSource);
}
