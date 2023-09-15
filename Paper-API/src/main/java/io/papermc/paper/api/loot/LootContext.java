package io.papermc.paper.api.loot;

import com.google.common.base.Preconditions;
import io.papermc.paper.api.entity.Entity;
import io.papermc.paper.api.entity.HumanEntity;
import io.papermc.paper.api.inventory.ItemStack;
import io.papermc.paper.api.location.Location;
import io.papermc.paper.api.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents additional information a {@link LootTable} can use to modify it's
 * generated loot.
 */
public final class LootContext {

    public static final int DEFAULT_LOOT_MODIFIER = -1;

    private final Location location;
    private final float luck;
    private final int lootingModifier;
    private final Entity lootedEntity;
    private final HumanEntity killer;

    private LootContext(@NonNull Location location, float luck, int lootingModifier, @Nullable Entity lootedEntity, @Nullable HumanEntity killer) {
        Preconditions.checkArgument(location != null, "LootContext location cannot be null");
        Preconditions.checkArgument(location.getWorld() != null, "LootContext World cannot be null");
        this.location = location;
        this.luck = luck;
        this.lootingModifier = lootingModifier;
        this.lootedEntity = lootedEntity;
        this.killer = killer;
    }

    /**
     * The {@link Location} to store where the loot will be generated.
     *
     * @return the Location of where the loot will be generated
     */
    @NonNull
    public Location getLocation() {
        return location;
    }

    /**
     * Represents the {@link PotionEffectType#LUCK} that an
     * entity can have. The higher the value the better chance of receiving more
     * loot.
     *
     * @return luck
     */
    public float getLuck() {
        return luck;
    }

    /**
     * Represents the
     * {@link Enchantment#LOOT_BONUS_MOBS} the
     * {@link #getKiller()} entity has on their equipped item.
     *
     * This value is only set via
     * {@link LootContext.Builder#lootingModifier(int)}. If not set, the
     * {@link #getKiller()} entity's looting level will be used instead.
     *
     * @return the looting level
     */
    public int getLootingModifier() {
        return lootingModifier;
    }

    /**
     * Get the {@link Entity} that was killed. Can be null.
     *
     * @return the looted entity or null
     */
    @Nullable
    public Entity getLootedEntity() {
        return lootedEntity;
    }

    /**
     * Get the {@link HumanEntity} who killed the {@link #getLootedEntity()}.
     * Can be null.
     *
     * @return the killer entity, or null.
     */
    @Nullable
    public HumanEntity getKiller() {
        return killer;
    }

    /**
     * Utility class to make building {@link LootContext} easier. The only
     * required argument is {@link Location} with a valid (non-null)
     * {@link World}.
     */
    public static class Builder {

        private final Location location;
        private float luck;
        private int lootingModifier = LootContext.DEFAULT_LOOT_MODIFIER;
        private Entity lootedEntity;
        private HumanEntity killer;

        /**
         * Creates a new LootContext.Builder instance to facilitate easy
         * creation of {@link LootContext}s.
         *
         * @param location the location the LootContext should use
         */
        public Builder(@NonNull Location location) {
            this.location = location;
        }

        /**
         * Set how much luck to have when generating loot.
         *
         * @param luck the luck level
         * @return the Builder
         */
        @NonNull
        public Builder luck(float luck) {
            this.luck = luck;
            return this;
        }

        /**
         * Set the {@link Enchantment#LOOT_BONUS_MOBS}
         * level equivalent to use when generating loot. Values less than or
         * equal to 0 will force the {@link LootTable} to only return a single
         * {@link ItemStack} per pool.
         *
         * @param modifier the looting level modifier
         * @return the Builder
         */
        @NonNull
        public Builder lootingModifier(int modifier) {
            this.lootingModifier = modifier;
            return this;
        }

        /**
         * The entity that was killed.
         *
         * @param lootedEntity the looted entity
         * @return the Builder
         */
        @NonNull
        public Builder lootedEntity(@Nullable Entity lootedEntity) {
            this.lootedEntity = lootedEntity;
            return this;
        }

        /**
         * Set the {@link HumanEntity} that killed
         * {@link #getLootedEntity()}. This entity will be used to get the
         * looting level if {@link #lootingModifier(int)} is not set.
         *
         * @param killer the killer entity
         * @return the Builder
         */
        @NonNull
        public Builder killer(@Nullable HumanEntity killer) {
            this.killer = killer;
            return this;
        }

        /**
         * Create a new {@link LootContext} instance using the supplied
         * parameters.
         *
         * @return a new {@link LootContext} instance
         */
        @NonNull
        public LootContext build() {
            return new LootContext(location, luck, lootingModifier, lootedEntity, killer);
        }
    }
}
