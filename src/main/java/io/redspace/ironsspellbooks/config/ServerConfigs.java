package io.redspace.ironsspellbooks.config;

import io.redspace.ironsspellbooks.spells.SpellRarity;
import io.redspace.ironsspellbooks.spells.SpellType;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.*;
import java.util.stream.Collectors;

public class ServerConfigs {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final SpellConfigParameters DEFAULT_CONFIG = new SpellConfigParameters(true, 10, SpellRarity.COMMON, 1, 1, 10);
    public static final ForgeConfigSpec.ConfigValue<Boolean> SWORDS_CONSUME_MANA;
    //public static final ForgeConfigSpec.ConfigValue<Boolean> CAN_ATTACK_OWN_SUMMONS;

    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> RARITY_CONFIG;

    //https://forge.gemwire.uk/wiki/Configs

    private static final Map<SpellType, SpellConfigParameters> SPELL_CONFIGS = new HashMap<>();
    private static final Queue<DelayedConfigConstructor> CONFIG_LIST = new LinkedList<>();

    static {
        BUILDER.comment("Individual Spell Configuration");


        createSpellConfig(SpellType.BLOOD_SLASH_SPELL, true, 5, SpellRarity.RARE, 10);
        createSpellConfig(SpellType.BLOOD_STEP_SPELL, true, 5, SpellRarity.UNCOMMON, 5);
        createSpellConfig(SpellType.HEARTSTOP_SPELL, true, 10, SpellRarity.COMMON, 120);
        createSpellConfig(SpellType.RAISE_DEAD_SPELL, true, 5, SpellRarity.UNCOMMON, 240);
        createSpellConfig(SpellType.RAY_OF_SIPHONING_SPELL, true, 10, SpellRarity.COMMON, 15);
        createSpellConfig(SpellType.WITHER_SKULL_SPELL, true, 10, SpellRarity.UNCOMMON, 2);
        createSpellConfig(SpellType.EVASION_SPELL, false, 5, SpellRarity.EPIC, 180);
        createSpellConfig(SpellType.MAGIC_ARROW_SPELL, false, 10, SpellRarity.RARE, 12);
        createSpellConfig(SpellType.MAGIC_MISSILE_SPELL, false, 10, SpellRarity.COMMON, 1);
        createSpellConfig(SpellType.TELEPORT_SPELL, false, 4, SpellRarity.UNCOMMON, 3);
        createSpellConfig(SpellType.CHAIN_CREEPER_SPELL, true, 6, SpellRarity.UNCOMMON, 10);
        createSpellConfig(SpellType.FANG_STRIKE_SPELL, true, 15, SpellRarity.COMMON, 5);
        createSpellConfig(SpellType.FANG_WARD_SPELL, true, 15, SpellRarity.COMMON, 15);
        createSpellConfig(SpellType.FIRECRACKER_SPELL, true, 10, SpellRarity.COMMON, 1.5);
        createSpellConfig(SpellType.INVISIBILITY_SPELL, true, 6, SpellRarity.RARE, 60);
        createSpellConfig(SpellType.LOB_CREEPER_SPELL, true, 10, SpellRarity.UNCOMMON, 2);
        createSpellConfig(SpellType.SHIELD_SPELL, true, 8, SpellRarity.COMMON, 40);
        createSpellConfig(SpellType.SUMMON_HORSE_SPELL, true, 5, SpellRarity.COMMON, 20);
        createSpellConfig(SpellType.SUMMON_VEX_SPELL, true, 5, SpellRarity.RARE, 240);
        createSpellConfig(SpellType.BLAZE_STORM_SPELL, true, 10, SpellRarity.COMMON, 25);
        createSpellConfig(SpellType.BURNING_DASH_SPELL, true, 10, SpellRarity.COMMON, 10);
        createSpellConfig(SpellType.FIREBALL_SPELL, true, 3, SpellRarity.EPIC, 25);
        createSpellConfig(SpellType.FIREBOLT_SPELL, true, 10, SpellRarity.COMMON, 1);
        createSpellConfig(SpellType.FIRE_BREATH_SPELL, true, 5, SpellRarity.COMMON, 25);
        createSpellConfig(SpellType.WALL_OF_FIRE_SPELL, true, 5, SpellRarity.COMMON, 30);
        createSpellConfig(SpellType.ANGEL_WING_SPELL, true, 5, SpellRarity.EPIC, 120);
        createSpellConfig(SpellType.CLOUD_OF_REGENERATION_SPELL, true, 10, SpellRarity.COMMON, 40);
        createSpellConfig(SpellType.GREATER_HEAL_SPELL, true, 1, SpellRarity.LEGENDARY, 60);
        createSpellConfig(SpellType.HEAL_SPELL, true, 10, SpellRarity.COMMON, 25);
        createSpellConfig(SpellType.WISP_SPELL, true, 10, SpellRarity.COMMON, 1);
        createSpellConfig(SpellType.CONE_OF_COLD_SPELL, true, 5, SpellRarity.COMMON, 25);
        createSpellConfig(SpellType.FROSTBITE_SPELL, false, 0, SpellRarity.COMMON, 0);
        createSpellConfig(SpellType.FROST_STEP, true, 8, SpellRarity.RARE, 10);
        createSpellConfig(SpellType.ICICLE_SPELL, true, 10, SpellRarity.COMMON, 1);
        createSpellConfig(SpellType.SUMMON_POLAR_BEAR_SPELL, true, 10, SpellRarity.RARE, 240);
        createSpellConfig(SpellType.ASCENSION_SPELL, true, 10, SpellRarity.RARE, 30);
        createSpellConfig(SpellType.ELECTROCUTE_SPELL, true, 5, SpellRarity.COMMON, 25);
        createSpellConfig(SpellType.LIGHTNING_BOLT_SPELL, true, 10, SpellRarity.EPIC, 25);
        createSpellConfig(SpellType.LIGHTNING_LANCE_SPELL, true, 10, SpellRarity.RARE, 12);
        createSpellConfig(SpellType.ABYSSAL_SHROUD_SPELL, true, 1, SpellRarity.LEGENDARY, 300);


        BUILDER.comment("Other Configuration");
        BUILDER.push("MISC");

        RARITY_CONFIG = BUILDER.worldRestart()
                .comment(String.format("rarityConfig array values must sum to 1: [%s, %s, %s, %s, %s]", SpellRarity.COMMON, SpellRarity.UNCOMMON, SpellRarity.RARE, SpellRarity.EPIC, SpellRarity.LEGENDARY))
                .defineList("rarityConfig", List.of(.3d, .25d, .2d, .15d, .1d), x -> true);

        SWORDS_CONSUME_MANA = BUILDER.worldRestart().define("swordsConsumeMana", true);
        //CAN_ATTACK_OWN_SUMMONS = BUILDER.worldRestart().define("canAttackOwnSummons", true);

        SPEC = BUILDER.build();
    }

    public static SpellConfigParameters getSpellConfig(SpellType spellType) {
        return SPELL_CONFIGS.getOrDefault(spellType, DEFAULT_CONFIG);
    }

    public static SpellConfigParameters getSpellConfig(int spellId) {
        return getSpellConfig(SpellType.getTypeFromValue(spellId));
    }

    private static void createSpellConfig(SpellType spell, boolean enabledByDefault, int defaultMaxLevel, SpellRarity defaultMinRarity, double cooldownInSeconds) {
        BUILDER.push(createSpellConfigTitle(spell.getId()));

        CONFIG_LIST.add(new DelayedConfigConstructor(
                BUILDER.define("Enabled", enabledByDefault),
                BUILDER.define("MaxLevel", defaultMaxLevel),
                BUILDER.defineEnum("MinRarity", defaultMinRarity),
                BUILDER.define("ManaCostMultiplier", 1d),
                BUILDER.define("SpellPowerMultiplier", 1d),
                BUILDER.define("CooldownInSeconds", cooldownInSeconds),
                spell
        ));

        BUILDER.pop();
    }

    private static String createSpellConfigTitle(String str) {
        var words = str.split("[_| ]");
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
        }
        return Arrays.stream(words).sequential().collect(Collectors.joining("-"));
    }

    public static void cacheConfigs() {
        while (!CONFIG_LIST.isEmpty())
            CONFIG_LIST.remove().construct();
    }

    //TODO: is this being static going to fuck shit up? (seems to work fine...)
    private static class DelayedConfigConstructor {
        final ForgeConfigSpec.ConfigValue<Boolean> ENABLED;
        final ForgeConfigSpec.ConfigValue<Integer> MAX_LEVEL;
        final ForgeConfigSpec.ConfigValue<SpellRarity> MIN_RARITY;
        final ForgeConfigSpec.ConfigValue<Double> M_MULT;
        final ForgeConfigSpec.ConfigValue<Double> P_MULT;
        final ForgeConfigSpec.ConfigValue<Double> CS;
        SpellType spellType;

        DelayedConfigConstructor(
                ForgeConfigSpec.ConfigValue<Boolean> ENABLED,
                ForgeConfigSpec.ConfigValue<Integer> MAX_LEVEL,
                ForgeConfigSpec.ConfigValue<SpellRarity> MIN_RARITY,
                ForgeConfigSpec.ConfigValue<Double> M_MULT,
                ForgeConfigSpec.ConfigValue<Double> P_MULT,
                ForgeConfigSpec.ConfigValue<Double> CS,
                SpellType spellType) {
            this.ENABLED = ENABLED;
            this.MAX_LEVEL = MAX_LEVEL;
            this.MIN_RARITY = MIN_RARITY;
            this.M_MULT = M_MULT;
            this.P_MULT = P_MULT;
            this.CS = CS;
            this.spellType = spellType;
        }

        void construct() {
            SPELL_CONFIGS.put(spellType, new SpellConfigParameters(ENABLED.get(), MAX_LEVEL.get(), MIN_RARITY.get(), P_MULT.get(), M_MULT.get(), CS.get()));
        }
    }

}