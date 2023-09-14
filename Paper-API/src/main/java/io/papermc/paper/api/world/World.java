package io.papermc.paper.api.world;

import io.papermc.paper.api.Paper;
import io.papermc.paper.api.block.Block;
import io.papermc.paper.api.block.data.BlockData;

import io.papermc.paper.api.boss.DragonBattle;
import io.papermc.paper.api.effect.Effect;
import io.papermc.paper.api.entity.*;
import io.papermc.paper.api.inventory.ItemStack;
import io.papermc.paper.api.location.Location;
import io.papermc.paper.api.material.Material;
import io.papermc.paper.api.material.TreeType;
import io.papermc.paper.api.metadata.Metadatable;
import io.papermc.paper.api.namespace.Keyed;
import io.papermc.paper.api.particle.Particle;
import io.papermc.paper.api.persistance.PersistentDataHolder;
import io.papermc.paper.api.sound.Sound;
import io.papermc.paper.api.sound.SoundCategory;
import io.papermc.paper.api.util.BoundingBox;
import io.papermc.paper.api.util.Difficulty;
import io.papermc.paper.api.util.FeatureFlag;
import io.papermc.paper.api.util.RayTraceResult;
import io.papermc.paper.api.util.fluid.FluidCollisionMode;
import io.papermc.paper.api.util.game.GameEvent;
import io.papermc.paper.api.util.game.GameRule;
import io.papermc.paper.api.world.generator.BiomeProvider;
import io.papermc.paper.api.world.generator.BlockPopulator;
import io.papermc.paper.api.world.generator.ChunkGenerator;
import io.papermc.paper.api.world.generator.WorldInfo;
import io.papermc.paper.api.world.generator.structure.Structure;
import io.papermc.paper.api.world.generator.structure.StructureSearchResult;
import io.papermc.paper.api.world.generator.structure.StructureType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Contract;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Represents a world, which may contain entities, chunks and blocks
 */
public interface World extends RegionAccessor, WorldInfo, PluginMessageRecipient, Metadatable, PersistentDataHolder, Keyed, ForwardingAudience { // Paper

    /**
     * @return The amount of Entities in this world
     */
    int getEntityCount();

    /**
     * @return The amount of Tile Entities in this world
     */
    int getTileEntityCount();

    /**
     * @return The amount of Tickable Tile Entities in this world
     */
    int getTickableTileEntityCount();

    /**
     * @return The amount of Chunks in this world
     */
    int getChunkCount();

    /**
     * @return The amount of Players in this world
     */
    int getPlayerCount();
    // Paper end

    /**
     * Gets the {@link Block} at the given coordinates
     *
     * @param x X-coordinate of the block
     * @param y Y-coordinate of the block
     * @param z Z-coordinate of the block
     * @return Block at the given coordinates
     */
    @NonNull Block getBlockAt(int x, int y, int z);

    /**
     * Gets the {@link Block} at the given {@link Location}
     *
     * @param location Location of the block
     * @return Block at the given location
     */
    @NonNull Block getBlockAt(@NonNull Location location);

    /**
     * Gets the highest non-empty (impassable) block at the given coordinates.
     *
     * @param x X-coordinate of the block
     * @param z Z-coordinate of the block
     * @return Highest non-empty block
     */
    @NonNull Block getHighestBlockAt(int x, int z);

    /**
     * Gets the highest non-empty (impassable) block at the given coordinates.
     *
     * @param location Coordinates to get the highest block
     * @return Highest non-empty block
     */
    @NonNull Block getHighestBlockAt(@NonNull Location location);

    /**
     * Gets the highest block corresponding to the {@link HeightMap} at the
     * given coordinates.
     *
     * @param x X-coordinate of the block
     * @param z Z-coordinate of the block
     * @param heightMap the heightMap that is used to determine the highest
     * point
     * @return Highest block corresponding to the {@link HeightMap}
     */
    @NonNull Block getHighestBlockAt(int x, int z, @NonNull HeightMap heightMap);

    /**
     * Gets the highest block corresponding to the {@link HeightMap} at the
     * given coordinates.
     *
     * @param location Coordinates to get the highest block
     * @param heightMap the heightMap that is used to determine the highest
     * point
     * @return Highest block corresponding to the {@link HeightMap}
     */
    @NonNull Block getHighestBlockAt(@NonNull Location location, @NonNull HeightMap heightMap);

    /**
     * Gets the {@link Chunk} at the given coordinates
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return Chunk at the given coordinates
     */
    @NonNull Chunk getChunkAt(int x, int z);

    /**
     * Gets the {@link Chunk} at the given coordinates
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @param generate Whether the chunk should be fully generated or not
     * @return Chunk at the given coordinates
     */
    @NonNull Chunk getChunkAt(int x, int z, boolean generate);

    /**
     * Gets the {@link Chunk} at the given {@link Location}
     *
     * @param location Location of the chunk
     * @return Chunk at the given location
     */
    @NonNull Chunk getChunkAt(@NonNull Location location);

    /**
     * Gets the {@link Chunk} that contains the given {@link Block}
     *
     * @param block Block to get the containing chunk from
     * @return The chunk that contains the given block
     */
    @NonNull Chunk getChunkAt(@NonNull Block block);

    // Paper start - chunk long key API
    /**
     * Gets the chunk at the specified chunk key, which is the X and Z packed into a long.
     * <p>
     * See {@link Chunk#getChunkKey()} for easy access to the key, or you may calculate it as:
     * long chunkKey = (long) chunkX &amp; 0xffffffffL | ((long) chunkZ &amp; 0xffffffffL) &gt;&gt; 32;
     *
     * @param chunkKey The Chunk Key to look up the chunk by
     * @return The chunk at the specified key
     */
    @NonNull
    default Chunk getChunkAt(long chunkKey) {
        return getChunkAt(chunkKey, true);
    }

    /**
     * Gets the chunk at the specified chunk key, which is the X and Z packed into a long.
     * <p>
     * See {@link Chunk#getChunkKey()} for easy access to the key, or you may calculate it as:
     * long chunkKey = (long) chunkX &amp; 0xffffffffL | ((long) chunkZ &amp; 0xffffffffL) &gt;&gt; 32;
     *
     * @param chunkKey The Chunk Key to look up the chunk by
     * @param generate Whether the chunk should be fully generated or not
     * @return The chunk at the specified key
     */
    @NonNull
    default Chunk getChunkAt(long chunkKey, boolean generate) {
        return getChunkAt((int) chunkKey, (int) (chunkKey >> 32), generate);
    }

    /**
     * Checks if a {@link Chunk} has been generated at the specified chunk key,
     * which is the X and Z packed into a long.
     *
     * @param chunkKey The Chunk Key to look up the chunk by
     * @return true if the chunk has been generated, otherwise false
     */
    default boolean isChunkGenerated(long chunkKey) {
        return isChunkGenerated((int) chunkKey, (int) (chunkKey >> 32));
    }

    /**
     * Checks if the specified {@link Chunk} is loaded
     *
     * @param chunk The chunk to check
     * @return true if the chunk is loaded, otherwise false
     */
    boolean isChunkLoaded(@NonNull Chunk chunk);

    /**
     * Gets an array of all loaded {@link Chunk}s
     *
     * @return Chunk[] containing all loaded chunks
     */
    @NonNull Chunk[] getLoadedChunks();

    /**
     * Loads the specified {@link Chunk}.
     * <p>
     * <b>This method will keep the specified chunk loaded until one of the
     * unload methods is manually called. Callers are advised to instead use
     * getChunkAt which will only temporarily load the requested chunk.</b>
     *
     * @param chunk The chunk to load
     */
    void loadChunk(@NonNull Chunk chunk);

    /**
     * Checks if the {@link Chunk} at the specified coordinates is loaded
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return true if the chunk is loaded, otherwise false
     */
    boolean isChunkLoaded(int x, int z);

    /**
     * Checks if the {@link Chunk} at the specified coordinates is generated
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return true if the chunk is generated, otherwise false
     */
    boolean isChunkGenerated(int x, int z);

    /**
     * Loads the {@link Chunk} at the specified coordinates.
     * <p>
     * <b>This method will keep the specified chunk loaded until one of the
     * unload methods is manually called. Callers are advised to instead use
     * getChunkAt which will only temporarily load the requested chunk.</b>
     * <p>
     * If the chunk does not exist, it will be generated.
     * <p>
     * This method is analogous to {@link #loadChunk(int, int, boolean)} where
     * generate is true.
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     */
    void loadChunk(int x, int z);

    /**
     * Loads the {@link Chunk} at the specified coordinates.
     * <p>
     * <b>This method will keep the specified chunk loaded until one of the
     * unload methods is manually called. Callers are advised to instead use
     * getChunkAt which will only temporarily load the requested chunk.</b>
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @param generate Whether or not to generate a chunk if it doesn't
     *     already exist
     * @return true if the chunk has loaded successfully, otherwise false
     */
    boolean loadChunk(int x, int z, boolean generate);

    /**
     * Safely unloads and saves the {@link Chunk} at the specified coordinates
     * <p>
     * This method is analogous to {@link #unloadChunk(int, int, boolean)}
     * where save is true.
     *
     * @param chunk the chunk to unload
     * @return true if the chunk has unloaded successfully, otherwise false
     */
    boolean unloadChunk(@NonNull Chunk chunk);

    /**
     * Safely unloads and saves the {@link Chunk} at the specified coordinates
     * <p>
     * This method is analogous to {@link #unloadChunk(int, int, boolean)}
     * where save is true.
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return true if the chunk has unloaded successfully, otherwise false
     */
    boolean unloadChunk(int x, int z);

    /**
     * Safely unloads and optionally saves the {@link Chunk} at the specified
     * coordinates.
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @param save Whether or not to save the chunk
     * @return true if the chunk has unloaded successfully, otherwise false
     */
    boolean unloadChunk(int x, int z, boolean save);

    /**
     * Safely queues the {@link Chunk} at the specified coordinates for
     * unloading.
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return true is the queue attempt was successful, otherwise false
     */
    boolean unloadChunkRequest(int x, int z);

    /**
     * Resends the {@link Chunk} to all clients
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return Whether the chunk was actually refreshed
     *
     */
    boolean refreshChunk(int x, int z);

    /**
     * Gets whether the chunk at the specified chunk coordinates is force
     * loaded.
     * <p>
     * A force loaded chunk will not be unloaded due to lack of player activity.
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return force load status
     */
    boolean isChunkForceLoaded(int x, int z);

    /**
     * Sets whether the chunk at the specified chunk coordinates is force
     * loaded.
     * <p>
     * A force loaded chunk will not be unloaded due to lack of player activity.
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @param forced force load status
     */
    void setChunkForceLoaded(int x, int z, boolean forced);

    /**
     * Returns all force loaded chunks in this world.
     * <p>
     * A force loaded chunk will not be unloaded due to lack of player activity.
     *
     * @return unmodifiable collection of force loaded chunks
     */
    @NonNull Collection<Chunk> getForceLoadedChunks();

    /**
     * Adds a plugin ticket for the specified chunk, loading the chunk if it is
     * not already loaded.
     * <p>
     * A plugin ticket will prevent a chunk from unloading until it is
     * explicitly removed. A plugin instance may only have one ticket per chunk,
     * but each chunk can have multiple plugin tickets.
     * </p>
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @param plugin Plugin which owns the ticket
     * @return {@code true} if a plugin ticket was added, {@code false} if the
     * ticket already exists for the plugin
     * @throws IllegalStateException If the specified plugin is not enabled
     * @see #removePluginChunkTicket(int, int, Plugin)
     */
    boolean addPluginChunkTicket(int x, int z, @NonNull Plugin plugin);

    /**
     * Removes the specified plugin's ticket for the specified chunk
     * <p>
     * A plugin ticket will prevent a chunk from unloading until it is
     * explicitly removed. A plugin instance may only have one ticket per chunk,
     * but each chunk can have multiple plugin tickets.
     * </p>
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @param plugin Plugin which owns the ticket
     * @return {@code true} if the plugin ticket was removed, {@code false} if
     * there is no plugin ticket for the chunk
     * @see #addPluginChunkTicket(int, int, Plugin)
     */
    boolean removePluginChunkTicket(int x, int z, @NonNull Plugin plugin);

    /**
     * Removes all plugin tickets for the specified plugin
     * <p>
     * A plugin ticket will prevent a chunk from unloading until it is
     * explicitly removed. A plugin instance may only have one ticket per chunk,
     * but each chunk can have multiple plugin tickets.
     * </p>
     *
     * @param plugin Specified plugin
     * @see #addPluginChunkTicket(int, int, Plugin)
     * @see #removePluginChunkTicket(int, int, Plugin)
     */
    void removePluginChunkTickets(@NonNull Plugin plugin);

    /**
     * Retrieves a collection specifying which plugins have tickets for the
     * specified chunk. This collection is not updated when plugin tickets are
     * added or removed to the chunk.
     * <p>
     * A plugin ticket will prevent a chunk from unloading until it is
     * explicitly removed. A plugin instance may only have one ticket per chunk,
     * but each chunk can have multiple plugin tickets.
     * </p>
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return unmodifiable collection containing which plugins have tickets for
     * the chunk
     * @see #addPluginChunkTicket(int, int, Plugin)
     * @see #removePluginChunkTicket(int, int, Plugin)
     */
    @NonNull Collection<Plugin> getPluginChunkTickets(int x, int z);

    /**
     * Returns a map of which plugins have tickets for what chunks. The returned
     * map is not updated when plugin tickets are added or removed to chunks. If
     * a plugin has no tickets, it will be absent from the map.
     * <p>
     * A plugin ticket will prevent a chunk from unloading until it is
     * explicitly removed. A plugin instance may only have one ticket per chunk,
     * but each chunk can have multiple plugin tickets.
     * </p>
     *
     * @return unmodifiable map containing which plugins have tickets for what
     * chunks
     * @see #addPluginChunkTicket(int, int, Plugin)
     * @see #removePluginChunkTicket(int, int, Plugin)
     */
    @NonNull Map<Plugin, Collection<Chunk>> getPluginChunkTickets();

    /**
     * Drops an item at the specified {@link Location}
     *
     * @param location Location to drop the item
     * @param item ItemStack to drop
     * @return ItemDrop entity created as a result of this method
     */
    @NonNull Item dropItem(@NonNull Location location, @NonNull ItemStack item);

    /**
     * Drops an item at the specified {@link Location}
     * Note that functions will run before the entity is spawned
     *
     * @param location Location to drop the item
     * @param item ItemStack to drop
     * @param function the function to be run before the entity is spawned.
     * @return ItemDrop entity created as a result of this method
     */
    @NonNull Item dropItem(@NonNull Location location, @NonNull ItemStack item, @Nullable Consumer<Item> function);

    /**
     * Drops an item at the specified {@link Location} with a random offset
     *
     * @param location Location to drop the item
     * @param item ItemStack to drop
     * @return ItemDrop entity created as a result of this method
     */
    @NonNull Item dropItemNaturally(@NonNull Location location, @NonNull ItemStack item);

    /**
     * Drops an item at the specified {@link Location} with a random offset
     * Note that functions will run before the entity is spawned
     *
     * @param location Location to drop the item
     * @param item ItemStack to drop
     * @param function the function to be run before the entity is spawned.
     * @return ItemDrop entity created as a result of this method
     */
    @NonNull Item dropItemNaturally(@NonNull Location location, @NonNull ItemStack item, @Nullable Consumer<Item> function);

    /**
     * Creates an {@link Arrow} entity at the given {@link Location}
     *
     * @param location Location to spawn the arrow
     * @param direction Direction to shoot the arrow in
     * @param speed Speed of the arrow. A recommend speed is 0.6
     * @param spread Spread of the arrow. A recommend spread is 12
     * @return Arrow entity spawned as a result of this method
     */
    @NonNull Arrow spawnArrow(@NonNull Location location, @NonNull Vector direction, float speed, float spread);

    /**
     * Creates an arrow entity of the given class at the given {@link Location}
     *
     * @param <T> type of arrow to spawn
     * @param location Location to spawn the arrow
     * @param direction Direction to shoot the arrow in
     * @param speed Speed of the arrow. A recommend speed is 0.6
     * @param spread Spread of the arrow. A recommend spread is 12
     * @param clazz the Entity class for the arrow
     * {@link SpectralArrow},{@link Arrow},{@link TippedArrow}
     * @return Arrow entity spawned as a result of this method
     */
    @NonNull <T extends AbstractArrow> T spawnArrow(@NonNull Location location, @NonNull Vector direction, float speed, float spread, @NonNull Class<T> clazz);

    /**
     * Creates a tree at the given {@link Location}
     *
     * @param location Location to spawn the tree
     * @param type Type of the tree to create
     * @return true if the tree was created successfully, otherwise false
     */
    boolean generateTree(@NonNull Location location, @NonNull TreeType type);

    /**
     * Strikes lightning at the given {@link Location}
     *
     * @param loc The location to strike lightning
     * @return The lightning entity.
     */
    @NonNull LightningStrike strikeLightning(@NonNull Location loc);

    /**
     * Strikes lightning at the given {@link Location} without doing damage
     *
     * @param loc The location to strike lightning
     * @return The lightning entity.
     */
    @NonNull LightningStrike strikeLightningEffect(@NonNull Location loc);

    /**
     * Finds the location of the nearest unobstructed Lightning Rod in a 128-block
     * radius around the given location. Returns {@code null} if no Lightning Rod is found.
     *
     * <p>Note: To activate a Lightning Rod, the position one block above it must be struck by lightning.</p>
     *
     * @param location {@link Location} to search for Lightning Rod around
     * @return {@link Location} of Lightning Rod or {@code null}
     */
    @Nullable Location findLightningRod(@NonNull Location location);

    /**
     * Finds a target {@link Location} for lightning to strike.
     * <p>It selects from (in the following order):</p>
     * <ol>
     *  <li>the block above the nearest Lightning Rod, found using {@link World#findLightningRod(Location)}</li>
     *  <li>a random {@link LivingEntity} that can see the sky in a 6x6 cuboid
     *      around input X/Z coordinates. Y ranges from <i>the highest motion-blocking
     *      block at the input X/Z - 3</i> to <i>the height limit + 3</i></li>
     * </ol>
     * <p>Returns {@code null} if no target is found.</p>
     *
     * @param location {@link Location} to search for target around
     * @return lightning target or {@code null}
     */
    @Nullable Location findLightningTarget(@NonNull Location location);

    /**
     * Get a list of all entities in this World
     *
     * @return A List of all Entities currently residing in this world
     */
    @NonNull List<Entity> getEntities();

    /**
     * Get a list of all living entities in this World
     *
     * @return A List of all LivingEntities currently residing in this world
     */
    @NonNull List<LivingEntity> getLivingEntities();

    /**
     * Get a collection of all entities in this World matching the given
     * class/interface
     *
     * @param <T> an entity subclass
     * @param classes The classes representing the types of entity to match
     * @return A List of all Entities currently residing in this world that
     *     match the given class/interface
     */
    @Deprecated
    @NonNull
    //TODO: Investigate this deprecation
    <T extends Entity> Collection<T> getEntitiesByClass(@NonNull Class<T>... classes);

    /**
     * Get a collection of all entities in this World matching the given
     * class/interface
     *
     * @param <T> an entity subclass
     * @param cls The class representing the type of entity to match
     * @return A List of all Entities currently residing in this world that
     *     match the given class/interface
     */
    @NonNull <T extends Entity> Collection<T> getEntitiesByClass(@NonNull Class<T> cls);

    /**
     * Get a collection of all entities in this World matching any of the
     * given classes/interfaces
     *
     * @param classes The classes representing the types of entity to match
     * @return A List of all Entities currently residing in this world that
     *     match one or more of the given classes/interfaces
     */
    @NonNull Collection<Entity> getEntitiesByClasses(@NonNull Class<?>... classes);

    /**
     * Gets nearby LivingEntities within the specified radius (bounding box)
     * @param loc Center location
     * @param radius Radius
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    @NonNull
    default Collection<LivingEntity> getNearbyLivingEntities(@NonNull Location loc, double radius) {
        return getNearbyEntitiesByType(LivingEntity.class, loc, radius, radius, radius);
    }

    /**
     * Gets nearby LivingEntities within the specified radius (bounding box)
     * @param loc Center location
     * @param xzRadius X/Z Radius
     * @param yRadius Y Radius
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    @NonNull
    default Collection<LivingEntity> getNearbyLivingEntities(@NonNull Location loc, double xzRadius, double yRadius) {
        return getNearbyEntitiesByType(LivingEntity.class, loc, xzRadius, yRadius, xzRadius);
    }

    /**
     * Gets nearby LivingEntities within the specified radius (bounding box)
     * @param loc Center location
     * @param xRadius X Radius
     * @param yRadius Y Radius
     * @param zRadius Z radius
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    @NonNull
    default Collection<LivingEntity> getNearbyLivingEntities(@NonNull Location loc, double xRadius, double yRadius, double zRadius) {
        return getNearbyEntitiesByType(LivingEntity.class, loc, xRadius, yRadius, zRadius);
    }

    /**
     * Gets nearby LivingEntities within the specified radius (bounding box)
     * @param loc Center location
     * @param radius X Radius
     * @param predicate a predicate used to filter results
     * @return the collection of living entities near location. This will always be a non-null collection
     */
    @NonNull
    default Collection<LivingEntity> getNearbyLivingEntities(@NonNull Location loc, double radius, @Nullable Predicate<LivingEntity> predicate) {
        return getNearbyEntitiesByType(LivingEntity.class, loc, radius, radius, radius, predicate);
    }

    /**
     * Gets nearby LivingEntities within the specified radius (bounding box)
     * @param loc Center location
     * @param xzRadius X/Z Radius
     * @param yRadius Y Radius
     * @param predicate a predicate used to filter results
     * @return the collection of living entities near location. This will always be a non-null collection
     */
    @NonNull
    default Collection<LivingEntity> getNearbyLivingEntities(@NonNull Location loc, double xzRadius, double yRadius, @Nullable Predicate<LivingEntity> predicate) {
        return getNearbyEntitiesByType(LivingEntity.class, loc, xzRadius, yRadius, xzRadius, predicate);
    }

    /**
     * Gets nearby LivingEntities within the specified radius (bounding box)
     * @param loc Center location
     * @param xRadius X Radius
     * @param yRadius Y Radius
     * @param zRadius Z radius
     * @param predicate a predicate used to filter results
     * @return the collection of living entities near location. This will always be a non-null collection.
     */
    @NonNull
    default Collection<LivingEntity> getNearbyLivingEntities(@NonNull Location loc, double xRadius, double yRadius, double zRadius, @Nullable Predicate<LivingEntity> predicate) {
        return getNearbyEntitiesByType(LivingEntity.class, loc, xRadius, yRadius, zRadius, predicate);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param radius X/Y/Z Radius
     * @return the collection of living entities near location. This will always be a non-null collection.
     */
    @NonNull
    default Collection<Player> getNearbyPlayers(@NonNull Location loc, double radius) {
        return getNearbyEntitiesByType(Player.class, loc, radius, radius, radius);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param xzRadius X/Z Radius
     * @param yRadius Y Radius
     * @return the collection of living entities near location. This will always be a non-null collection.
     */
    @NonNull
    default Collection<Player> getNearbyPlayers(@NonNull Location loc, double xzRadius, double yRadius) {
        return getNearbyEntitiesByType(Player.class, loc, xzRadius, yRadius, xzRadius);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param xRadius X Radius
     * @param yRadius Y Radius
     * @param zRadius Z Radius
     * @return the collection of players near location. This will always be a non-null collection.
     */
    @NonNull
    default Collection<Player> getNearbyPlayers(@NonNull Location loc, double xRadius, double yRadius, double zRadius) {
        return getNearbyEntitiesByType(Player.class, loc, xRadius, yRadius, zRadius);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param radius X/Y/Z Radius
     * @param predicate a predicate used to filter results
     * @return the collection of players near location. This will always be a non-null collection.
     */
    @NonNull
    default Collection<Player> getNearbyPlayers(@NonNull Location loc, double radius, @Nullable Predicate<Player> predicate) {
        return getNearbyEntitiesByType(Player.class, loc, radius, radius, radius, predicate);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param xzRadius X/Z Radius
     * @param yRadius Y Radius
     * @param predicate a predicate used to filter results
     * @return the collection of players near location. This will always be a non-null collection.
     */
    @NonNull
    default Collection<Player> getNearbyPlayers(@NonNull Location loc, double xzRadius, double yRadius, @Nullable Predicate<Player> predicate) {
        return getNearbyEntitiesByType(Player.class, loc, xzRadius, yRadius, xzRadius, predicate);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param xRadius X Radius
     * @param yRadius Y Radius
     * @param zRadius Z Radius
     * @param predicate a predicate used to filter results
     * @return the collection of players near location. This will always be a non-null collection.
     */
    @NonNull
    default Collection<Player> getNearbyPlayers(@NonNull Location loc, double xRadius, double yRadius, double zRadius, @Nullable Predicate<Player> predicate) {
        return getNearbyEntitiesByType(Player.class, loc, xRadius, yRadius, zRadius, predicate);
    }

    /**
     * Gets all nearby entities of the specified type, within the specified radius (bounding box)
     * @param clazz Type to filter by
     * @param loc Center location
     * @param radius X/Y/Z radius to search within
     * @param <T> the entity type
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    @NonNull
    default <T extends Entity> Collection<T> getNearbyEntitiesByType(@Nullable Class<? extends T> clazz, @NonNull Location loc, double radius) {
        return getNearbyEntitiesByType(clazz, loc, radius, radius, radius, null);
    }

    /**
     * Gets all nearby entities of the specified type, within the specified radius, with x and x radius matching (bounding box)
     * @param clazz Type to filter by
     * @param loc Center location
     * @param xzRadius X/Z radius to search within
     * @param yRadius Y radius to search within
     * @param <T> the entity type
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    @NonNull
    default <T extends Entity> Collection<T> getNearbyEntitiesByType(@Nullable Class<? extends T> clazz, @NonNull Location loc, double xzRadius, double yRadius) {
        return getNearbyEntitiesByType(clazz, loc, xzRadius, yRadius, xzRadius, null);
    }

    /**
     * Gets all nearby entities of the specified type, within the specified radius (bounding box)
     * @param clazz Type to filter by
     * @param loc Center location
     * @param xRadius X Radius
     * @param yRadius Y Radius
     * @param zRadius Z Radius
     * @param <T> the entity type
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    @NonNull
    default <T extends Entity> Collection<T> getNearbyEntitiesByType(@Nullable Class<? extends T> clazz, @NonNull Location loc, double xRadius, double yRadius, double zRadius) {
        return getNearbyEntitiesByType(clazz, loc, xRadius, yRadius, zRadius, null);
    }

    /**
     * Gets all nearby entities of the specified type, within the specified radius (bounding box)
     * @param clazz Type to filter by
     * @param loc Center location
     * @param radius X/Y/Z radius to search within
     * @param predicate a predicate used to filter results
     * @param <T> the entity type
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    @NonNull
    default <T extends Entity> Collection<T> getNearbyEntitiesByType(@Nullable Class<? extends T> clazz, @NonNull Location loc, double radius, @Nullable Predicate<T> predicate) {
        return getNearbyEntitiesByType(clazz, loc, radius, radius, radius, predicate);
    }

    /**
     * Gets all nearby entities of the specified type, within the specified radius, with x and x radius matching (bounding box)
     * @param clazz Type to filter by
     * @param loc Center location
     * @param xzRadius X/Z radius to search within
     * @param yRadius Y radius to search within
     * @param predicate a predicate used to filter results
     * @param <T> the entity type
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    @NonNull
    default <T extends Entity> Collection<T> getNearbyEntitiesByType(@Nullable Class<? extends T> clazz, @NonNull Location loc, double xzRadius, double yRadius, @Nullable Predicate<T> predicate) {
        return getNearbyEntitiesByType(clazz, loc, xzRadius, yRadius, xzRadius, predicate);
    }

    /**
     * Gets all nearby entities of the specified type, within the specified radius (bounding box)
     * @param clazz Type to filter by
     * @param loc Center location
     * @param xRadius X Radius
     * @param yRadius Y Radius
     * @param zRadius Z Radius
     * @param predicate a predicate used to filter results
     * @param <T> the entity type
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    @NonNull
    default <T extends Entity> Collection<T> getNearbyEntitiesByType(@Nullable Class<? extends Entity> clazz, @NonNull Location loc, double xRadius, double yRadius, double zRadius, @Nullable Predicate<T> predicate) {
        if (clazz == null) {
            clazz = Entity.class;
        }
        List<T> nearby = new ArrayList<>();
        for (Entity paperEntity : getNearbyEntities(loc, xRadius, yRadius, zRadius)) {
            //noinspection unchecked
            if (clazz.isAssignableFrom(paperEntity.getClass()) && (predicate == null || predicate.test((T) paperEntity))) {
                //noinspection unchecked
                nearby.add((T) paperEntity);
            }
        }
        return nearby;
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The {@link Consumer} will always be executed synchronously
     * on the main Server Thread.
     *
     * @param x Chunk X-coordinate of the chunk - floor(world coordinate / 16)
     * @param z Chunk Z-coordinate of the chunk - floor(world coordinate / 16)
     * @param cb Callback to receive the chunk when it is loaded.
     *           will be executed synchronously
     */
    default void getChunkAtAsync(int x, int z, @NonNull Consumer<Chunk> cb) {
        getChunkAtAsync(x, z, true).thenAccept(cb).exceptionally((ex) -> {
            Paper.getLogger().log(java.util.logging.Level.WARNING, "Exception in chunk load callback", ex);
            return null;
        });
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The {@link java.util.function.Consumer} will always be executed synchronously
     * on the main Server Thread.
     *
     * @param x Chunk X-coordinate of the chunk - floor(world coordinate / 16)
     * @param z Chunk Z-coordinate of the chunk - floor(world coordinate / 16)
     * @param gen Should we generate a chunk if it doesn't exist or not
     * @param cb Callback to receive the chunk when it is loaded.
     *           will be executed synchronously
     */
    default void getChunkAtAsync(int x, int z, boolean gen, @NonNull Consumer<Chunk> cb) {
        getChunkAtAsync(x, z, gen).thenAccept(cb).exceptionally((ex) -> {
            Paper.getLogger().log(java.util.logging.Level.WARNING, "Exception in chunk load callback", ex);
            return null;
        });
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given {@link Location}
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The {@link Consumer} will always be executed synchronously
     * on the main Server Thread.
     *
     * @param loc Location of the chunk
     * @param cb Callback to receive the chunk when it is loaded.
     *           will be executed synchronously
     */
    default void getChunkAtAsync(@NonNull Location loc, @NonNull Consumer<Chunk> cb) {
        getChunkAtAsync((int)Math.floor(loc.getX()) >> 4, (int)Math.floor(loc.getZ()) >> 4, true, cb);
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given {@link Location}
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The {@link Consumer} will always be executed synchronously
     * on the main Server Thread.
     *
     * @param loc Location of the chunk
     * @param gen Should the chunk generate if it doesn't exist
     * @param cb Callback to receive the chunk when it is loaded.
     *           will be executed synchronously
     */
    default void getChunkAtAsync(@NonNull Location loc, boolean gen, @NonNull Consumer<Chunk> cb) {
        getChunkAtAsync((int)Math.floor(loc.getX()) >> 4, (int)Math.floor(loc.getZ()) >> 4, gen, cb);
    }

    /**
     * Requests {@link Chunk} to be loaded that contains the given {@link Block}
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The {@link Consumer} will always be executed synchronously
     * on the main Server Thread.
     *
     * @param block Block to get the containing chunk from
     * @param cb Callback to receive the chunk when it is loaded.
     *           will be executed synchronously
     */
    default void getChunkAtAsync(@NonNull Block block, @NonNull Consumer<Chunk> cb) {
        getChunkAtAsync(block.getX() >> 4, block.getZ() >> 4, true, cb);
    }

    /**
     * Requests {@link Chunk} to be loaded that contains the given {@link Block}
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The {@link Consumer} will always be executed synchronously
     * on the main Server Thread.
     *
     * @param block Block to get the containing chunk from
     * @param gen Should the chunk generate if it doesn't exist
     * @param cb Callback to receive the chunk when it is loaded.
     *           will be executed synchronously
     */
    default void getChunkAtAsync(@NonNull Block block, boolean gen, @NonNull Consumer<Chunk> cb) {
        getChunkAtAsync(block.getX() >> 4, block.getZ() >> 4, gen, cb);
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The future will always be executed synchronously
     * on the main Server Thread.
     * @param loc Location to load the corresponding chunk from
     * @return Future that will resolve when the chunk is loaded
     */
    @NonNull
    default CompletableFuture<Chunk> getChunkAtAsync(@NonNull Location loc) {
        return getChunkAtAsync((int)Math.floor(loc.getX()) >> 4, (int)Math.floor(loc.getZ()) >> 4, true);
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The future will always be executed synchronously
     * on the main Server Thread.
     * @param loc Location to load the corresponding chunk from
     * @param gen Should the chunk generate if it doesn't exist
     * @return Future that will resolve when the chunk is loaded
     */
    @NonNull
    default CompletableFuture<Chunk> getChunkAtAsync(@NonNull Location loc, boolean gen) {
        return getChunkAtAsync((int)Math.floor(loc.getX()) >> 4, (int)Math.floor(loc.getZ()) >> 4, gen);
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The future will always be executed synchronously
     * on the main Server Thread.
     * @param block Block to load the corresponding chunk from
     * @return Future that will resolve when the chunk is loaded
     */
    @NonNull
    default CompletableFuture<Chunk> getChunkAtAsync(@NonNull Block block) {
        return getChunkAtAsync(block.getX() >> 4, block.getZ() >> 4, true);
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The future will always be executed synchronously
     * on the main Server Thread.
     * @param block Block to load the corresponding chunk from
     * @param gen Should the chunk generate if it doesn't exist
     * @return Future that will resolve when the chunk is loaded
     */
    @NonNull
    default CompletableFuture<Chunk> getChunkAtAsync(@NonNull Block block, boolean gen) {
        return getChunkAtAsync(block.getX() >> 4, block.getZ() >> 4, gen);
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The future will always be executed synchronously
     * on the main Server Thread.
     *
     * @param x Chunk X-coordinate of the chunk - floor(world coordinate / 16)
     * @param z Chunk Z-coordinate of the chunk - floor(world coordinate / 16)
     * @return Future that will resolve when the chunk is loaded
     */
    @NonNull
    default CompletableFuture<Chunk> getChunkAtAsync(int x, int z) {
        return getChunkAtAsync(x, z, true);
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The future will always be executed synchronously
     * on the main Server Thread.
     *
     * @param x Chunk X-coordinate of the chunk - floor(world coordinate / 16)
     * @param z Chunk Z-coordinate of the chunk - floor(world coordinate / 16)
     * @param gen Should we generate a chunk if it doesn't exist or not
     * @return Future that will resolve when the chunk is loaded
     */
    @NonNull
    default CompletableFuture<Chunk> getChunkAtAsync(int x, int z, boolean gen) {
        return getChunkAtAsync(x, z, gen, false);
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The future will always be executed synchronously
     * on the main Server Thread.
     * @param loc Location to load the corresponding chunk from
     * @return Future that will resolve when the chunk is loaded
     */
    @NonNull
    default CompletableFuture<Chunk> getChunkAtAsyncUrgently(@NonNull Location loc) {
        return getChunkAtAsync((int)Math.floor(loc.getX()) >> 4, (int)Math.floor(loc.getZ()) >> 4, true, true);
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The future will always be executed synchronously
     * on the main Server Thread.
     * @param loc Location to load the corresponding chunk from
     * @param gen Should the chunk generate if it doesn't exist
     * @return Future that will resolve when the chunk is loaded
     */
    @NonNull
    default CompletableFuture<Chunk> getChunkAtAsyncUrgently(@NonNull Location loc, boolean gen) {
        return getChunkAtAsync((int)Math.floor(loc.getX()) >> 4, (int)Math.floor(loc.getZ()) >> 4, gen, true);
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The future will always be executed synchronously
     * on the main Server Thread.
     * @param block Block to load the corresponding chunk from
     * @return Future that will resolve when the chunk is loaded
     */
    @NonNull
    default CompletableFuture<Chunk> getChunkAtAsyncUrgently(@NonNull Block block) {
        return getChunkAtAsync(block.getX() >> 4, block.getZ() >> 4, true, true);
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The future will always be executed synchronously
     * on the main Server Thread.
     * @param block Block to load the corresponding chunk from
     * @param gen Should the chunk generate if it doesn't exist
     * @return Future that will resolve when the chunk is loaded
     */
    @NonNull
    default CompletableFuture<Chunk> getChunkAtAsyncUrgently(@NonNull Block block, boolean gen) {
        return getChunkAtAsync(block.getX() >> 4, block.getZ() >> 4, gen, true);
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The future will always be executed synchronously
     * on the main Server Thread.
     *
     * @param x X Coord
     * @param z Z Coord
     * @return Future that will resolve when the chunk is loaded
     */
    @NonNull
    default CompletableFuture<Chunk> getChunkAtAsyncUrgently(int x, int z) {
        return getChunkAtAsync(x, z, true, true);
    }

    @NonNull
    CompletableFuture<Chunk> getChunkAtAsync(int x, int z, boolean gen, boolean urgent);

    /**
     * Get a list of all players in this World
     *
     * @return A list of all Players currently residing in this world
     */
    @NonNull List<Player> getPlayers();

    @NonNull
    @Override
    default Iterable<? extends Audience> audiences() {
        return this.getPlayers();
    }

    /**
     * Returns a list of entities within a bounding box centered around a
     * Location.
     * <p>
     * This may not consider entities in currently unloaded chunks. Some
     * implementations may impose artificial restrictions on the size of the
     * search bounding box.
     *
     * @param location The center of the bounding box
     * @param x 1/2 the size of the box along x axis
     * @param y 1/2 the size of the box along y axis
     * @param z 1/2 the size of the box along z axis
     * @return the collection of entities near location. This will always be a
     *      non-null collection.
     */
    @NonNull Collection<Entity> getNearbyEntities(@NonNull Location location, double x, double y, double z);

    /**
     * Gets an entity in this world by its UUID
     *
     * @param uuid the UUID of the entity
     * @return the entity with the given UUID, or null if it isn't found
     */
    @Nullable Entity getEntity(@NonNull UUID uuid);

    /**
     * Returns a list of entities within a bounding box centered around a
     * Location.
     * <p>
     * This may not consider entities in currently unloaded chunks. Some
     * implementations may impose artificial restrictions on the size of the
     * search bounding box.
     *
     * @param location The center of the bounding box
     * @param x 1/2 the size of the box along x axis
     * @param y 1/2 the size of the box along y axis
     * @param z 1/2 the size of the box along z axis
     * @param filter only entities that fulfill this predicate are considered,
     *     or <code>null</code> to consider all entities
     * @return the collection of entities near location. This will always be a
     *     non-null collection.
     */
    @NonNull Collection<Entity> getNearbyEntities(@NonNull Location location, double x, double y, double z, @Nullable Predicate<Entity> filter);

    /**
     * Returns a list of entities within the given bounding box.
     * <p>
     * This may not consider entities in currently unloaded chunks. Some
     * implementations may impose artificial restrictions on the size of the
     * search bounding box.
     *
     * @param boundingBox the bounding box
     * @return the collection of entities within the bounding box, will always
     *     be a non-null collection
     */
    @NonNull Collection<Entity> getNearbyEntities(@NonNull BoundingBox boundingBox);

    /**
     * Returns a list of entities within the given bounding box.
     * <p>
     * This may not consider entities in currently unloaded chunks. Some
     * implementations may impose artificial restrictions on the size of the
     * search bounding box.
     *
     * @param boundingBox the bounding box
     * @param filter only entities that fulfill this predicate are considered,
     *     or <code>null</code> to consider all entities
     * @return the collection of entities within the bounding box, will always
     *     be a non-null collection
     */
    @NonNull Collection<Entity> getNearbyEntities(@NonNull BoundingBox boundingBox, @Nullable Predicate<Entity> filter);

    /**
     * Performs a ray trace that checks for entity collisions.
     * <p>
     * This may not consider entities in currently unloaded chunks. Some
     * implementations may impose artificial restrictions on the maximum
     * distance.
     *
     * @param start the start position
     * @param direction the ray direction
     * @param maxDistance the maximum distance
     * @return the closest ray trace hit result, or <code>null</code> if there
     *     is no hit
     * @see #rayTraceEntities(Location, Vector, double, double, Predicate)
     */
    @Nullable RayTraceResult rayTraceEntities(@NonNull Location start, @NonNull Vector direction, double maxDistance);

    /**
     * Performs a ray trace that checks for entity collisions.
     * <p>
     * This may not consider entities in currently unloaded chunks. Some
     * implementations may impose artificial restrictions on the maximum
     * distance.
     *
     * @param start the start position
     * @param direction the ray direction
     * @param maxDistance the maximum distance
     * @param raySize entity bounding boxes will be uniformly expanded (or
     *     shrinked) by this value before doing collision checks
     * @return the closest ray trace hit result, or <code>null</code> if there
     *     is no hit
     * @see #rayTraceEntities(Location, Vector, double, double, Predicate)
     */
    @Nullable RayTraceResult rayTraceEntities(@NonNull Location start, @NonNull Vector direction, double maxDistance, double raySize);

    /**
     * Performs a ray trace that checks for entity collisions.
     * <p>
     * This may not consider entities in currently unloaded chunks. Some
     * implementations may impose artificial restrictions on the maximum
     * distance.
     *
     * @param start the start position
     * @param direction the ray direction
     * @param maxDistance the maximum distance
     * @param filter only entities that fulfill this predicate are considered,
     *     or <code>null</code> to consider all entities
     * @return the closest ray trace hit result, or <code>null</code> if there
     *     is no hit
     * @see #rayTraceEntities(Location, Vector, double, double, Predicate)
     */
    @Nullable RayTraceResult rayTraceEntities(@NonNull Location start, @NonNull Vector direction, double maxDistance, @Nullable Predicate<Entity> filter);

    /**
     * Performs a ray trace that checks for entity collisions.
     * <p>
     * This may not consider entities in currently unloaded chunks. Some
     * implementations may impose artificial restrictions on the maximum
     * distance.
     *
     * @param start the start position
     * @param direction the ray direction
     * @param maxDistance the maximum distance
     * @param raySize entity bounding boxes will be uniformly expanded (or
     *     shrinked) by this value before doing collision checks
     * @param filter only entities that fulfill this predicate are considered,
     *     or <code>null</code> to consider all entities
     * @return the closest ray trace hit result, or <code>null</code> if there
     *     is no hit
     */
    @Nullable RayTraceResult rayTraceEntities(@NonNull Location start, @NonNull Vector direction, double maxDistance, double raySize, @Nullable Predicate<Entity> filter);

    /**
     * Performs a ray trace that checks for block collisions using the blocks'
     * precise collision shapes.
     * <p>
     * This takes collisions with passable blocks into account, but ignores
     * fluids.
     * <p>
     * This may cause loading of chunks! Some implementations may impose
     * artificial restrictions on the maximum distance.
     *
     * @param start the start location
     * @param direction the ray direction
     * @param maxDistance the maximum distance
     * @return the ray trace hit result, or <code>null</code> if there is no hit
     * @see #rayTraceBlocks(Location, Vector, double, FluidCollisionMode, boolean)
     */
    @Nullable RayTraceResult rayTraceBlocks(@NonNull Location start, @NonNull Vector direction, double maxDistance);

    /**
     * Performs a ray trace that checks for block collisions using the blocks'
     * precise collision shapes.
     * <p>
     * This takes collisions with passable blocks into account.
     * <p>
     * This may cause loading of chunks! Some implementations may impose
     * artificial restrictions on the maximum distance.
     *
     * @param start the start location
     * @param direction the ray direction
     * @param maxDistance the maximum distance
     * @param fluidCollisionMode the fluid collision mode
     * @return the ray trace hit result, or <code>null</code> if there is no hit
     * @see #rayTraceBlocks(Location, Vector, double, FluidCollisionMode, boolean)
     */
    @Nullable RayTraceResult rayTraceBlocks(@NonNull Location start, @NonNull Vector direction, double maxDistance, @NonNull FluidCollisionMode fluidCollisionMode);

    /**
     * Performs a ray trace that checks for block collisions using the blocks'
     * precise collision shapes.
     * <p>
     * If collisions with passable blocks are ignored, fluid collisions are
     * ignored as well regardless of the fluid collision mode.
     * <p>
     * Portal blocks are only considered passable if the ray starts within
     * them. Apart from that collisions with portal blocks will be considered
     * even if collisions with passable blocks are otherwise ignored.
     * <p>
     * This may cause loading of chunks! Some implementations may impose
     * artificial restrictions on the maximum distance.
     *
     * @param start the start location
     * @param direction the ray direction
     * @param maxDistance the maximum distance
     * @param fluidCollisionMode the fluid collision mode
     * @param ignorePassableBlocks whether to ignore passable but collidable
     *     blocks (ex. tall grass, signs, fluids, ..)
     * @return the ray trace hit result, or <code>null</code> if there is no hit
     */
    @Nullable RayTraceResult rayTraceBlocks(@NonNull Location start, @NonNull Vector direction, double maxDistance, @NonNull FluidCollisionMode fluidCollisionMode, boolean ignorePassableBlocks);

    /**
     * Performs a ray trace that checks for both block and entity collisions.
     * <p>
     * Block collisions use the blocks' precise collision shapes. The
     * <code>raySize</code> parameter is only taken into account for entity
     * collision checks.
     * <p>
     * If collisions with passable blocks are ignored, fluid collisions are
     * ignored as well regardless of the fluid collision mode.
     * <p>
     * Portal blocks are only considered passable if the ray starts within them.
     * Apart from that collisions with portal blocks will be considered even if
     * collisions with passable blocks are otherwise ignored.
     * <p>
     * This may cause loading of chunks! Some implementations may impose
     * artificial restrictions on the maximum distance.
     *
     * @param start the start location
     * @param direction the ray direction
     * @param maxDistance the maximum distance
     * @param fluidCollisionMode the fluid collision mode
     * @param ignorePassableBlocks whether to ignore passable but collidable
     *     blocks (ex. tall grass, signs, fluids, ..)
     * @param raySize entity bounding boxes will be uniformly expanded (or
     *     shrinked) by this value before doing collision checks
     * @param filter only entities that fulfill this predicate are considered,
     *     or <code>null</code> to consider all entities
     * @return the closest ray trace hit result with either a block or an
     *     entity, or <code>null</code> if there is no hit
     */
    @Nullable RayTraceResult rayTrace(@NonNull Location start, @NonNull Vector direction, double maxDistance, @NonNull FluidCollisionMode fluidCollisionMode, boolean ignorePassableBlocks, double raySize, @Nullable Predicate<Entity> filter);

    /**
     * Gets the default spawn {@link Location} of this world
     *
     * @return The spawn location of this world
     */
    @NonNull Location getSpawnLocation();

    /**
     * Sets the spawn location of the world.
     * <br>
     * The location provided must be equal to this world.
     *
     * @param location The {@link Location} to set the spawn for this world at.
     * @return True if it was successfully set.
     */
    boolean setSpawnLocation(@NonNull Location location);

    /**
     * Sets the spawn location of the world
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param angle the angle
     * @return True if it was successfully set.
     */
    boolean setSpawnLocation(int x, int y, int z, float angle);

    /**
     * Sets the spawn location of the world
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return True if it was successfully set.
     */
    boolean setSpawnLocation(int x, int y, int z);

    /**
     * Gets the relative in-game time of this world.
     * <p>
     * The relative time is analogous to hours * 1000
     *
     * @return The current relative time
     * @see #getFullTime() Returns an absolute time of this world
     */
    long getTime();

    /**
     * Sets the relative in-game time on the server.
     * <p>
     * The relative time is analogous to hours * 1000
     * <p>
     * Note that setting the relative time below the current relative time
     * will actually move the clock forward a day. If you require to rewind
     * time, please see {@link #setFullTime(long)}
     *
     * @param time The new relative time to set the in-game time to (in
     *     hours*1000)
     * @see #setFullTime(long) Sets the absolute time of this world
     */
    void setTime(long time);

    /**
     * Gets the full in-game time on this world
     *
     * @return The current absolute time
     * @see #getTime() Returns a relative time of this world
     */
    long getFullTime();

    /**
     * Sets the in-game time on the server
     * <p>
     * Note that this sets the full time of the world, which may cause adverse
     * effects such as breaking redstone clocks and any scheduled events
     *
     * @param time The new absolute time to set this world to
     * @see #setTime(long) Sets the relative time of this world
     */
    void setFullTime(long time);

    /**
     * Check if it is currently daytime in this world
     *
     * @return True if it is daytime
     */
    boolean isDayTime();

    /**
     * Gets the full in-game time on this world since the world generation
     *
     * @return The current absolute time since the world generation
     * @see #getTime() Returns a relative time of this world
     * @see #getFullTime() Returns an absolute time of this world
     */
    long getGameTime();

    /**
     * Returns whether the world has an ongoing storm.
     *
     * @return Whether there is an ongoing storm
     */
    boolean hasStorm();

    /**
     * Set whether there is a storm. A duration will be set for the new
     * current conditions.
     *
     * This will implicitly call {@link #setClearWeatherDuration(int)} with 0
     * ticks to reset the world's clear weather.
     *
     * @param hasStorm Whether there is rain and snow
     */
    void setStorm(boolean hasStorm);

    /**
     * Get the remaining time in ticks of the current conditions.
     *
     * @return Time in ticks
     */
    int getWeatherDuration();

    /**
     * Set the remaining time in ticks of the current conditions.
     *
     * @param duration Time in ticks
     */
    void setWeatherDuration(int duration);

    /**
     * Returns whether there is thunder.
     *
     * @return Whether there is thunder
     */
    boolean isThundering();

    /**
     * Set whether it is thundering.
     *
     * This will implicitly call {@link #setClearWeatherDuration(int)} with 0
     * ticks to reset the world's clear weather.
     *
     * @param thundering Whether it is thundering
     */
    void setThundering(boolean thundering);

    /**
     * Get the thundering duration.
     *
     * @return Duration in ticks
     */
    int getThunderDuration();

    /**
     * Set the thundering duration.
     *
     * @param duration Duration in ticks
     */
    void setThunderDuration(int duration);

    /**
     * Returns whether the world has clear weather.
     *
     * This will be true such that {@link #isThundering()} and
     * {@link #hasStorm()} are both false.
     *
     * @return true if clear weather
     */
    boolean isClearWeather();

    /**
     * Set the clear weather duration.
     *
     * The clear weather ticks determine whether or not the world will be
     * allowed to rain or storm. If clear weather ticks are &gt; 0, the world will
     * not naturally do either until the duration has elapsed.
     *
     * This method is equivalent to calling {@code /weather clear} with a set
     * amount of ticks.
     *
     * @param duration duration in ticks
     */
    void setClearWeatherDuration(int duration);

    /**
     * Get the clear weather duration.
     *
     * @return duration in ticks
     */
    int getClearWeatherDuration();

    /**
     * Creates explosion at given coordinates with given power
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param power The power of explosion, where 4F is TNT
     * @return false if explosion was canceled, otherwise true
     */
    boolean createExplosion(double x, double y, double z, float power);

    /**
     * Creates explosion at given coordinates with given power and optionally
     * setting blocks on fire.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @return false if explosion was canceled, otherwise true
     */
    boolean createExplosion(double x, double y, double z, float power, boolean setFire);

    /**
     * Creates explosion at given coordinates with given power and optionally
     * setting blocks on fire or breaking blocks.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @param breakBlocks Whether or not to have blocks be destroyed
     * @return false if explosion was canceled, otherwise true
     */
    boolean createExplosion(double x, double y, double z, float power, boolean setFire, boolean breakBlocks);

    /**
     * Creates explosion at given coordinates with given power and optionally
     * setting blocks on fire or breaking blocks.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @param breakBlocks Whether or not to have blocks be destroyed
     * @param source the source entity, used for tracking damage
     * @return false if explosion was canceled, otherwise true
     */
    boolean createExplosion(double x, double y, double z, float power, boolean setFire, boolean breakBlocks, @Nullable Entity source);

    /**
     * Creates explosion at given coordinates with given power
     *
     * @param loc Location to blow up
     * @param power The power of explosion, where 4F is TNT
     * @return false if explosion was canceled, otherwise true
     */
    boolean createExplosion(@NonNull Location loc, float power);

    /**
     * Creates explosion at given coordinates with given power and optionally
     * setting blocks on fire.
     *
     * @param loc Location to blow up
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @return false if explosion was canceled, otherwise true
     */
    boolean createExplosion(@NonNull Location loc, float power, boolean setFire);

    // Paper start
    /**
     * Creates explosion at given location with given power and optionally
     * setting blocks on fire, with the specified entity as the source.
     *
     * @param source The source entity of the explosion
     * @param loc Location to blow up
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @param breakBlocks Whether or not to have blocks be destroyed
     * @return false if explosion was canceled, otherwise true
     */
    boolean createExplosion(@Nullable Entity source, @NonNull Location loc, float power, boolean setFire, boolean breakBlocks);

    /**
     * Creates explosion at given location with given power and optionally
     * setting blocks on fire, with the specified entity as the source.
     *
     * Will destroy other blocks
     *
     * @param source The source entity of the explosion
     * @param loc Location to blow up
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @return false if explosion was canceled, otherwise true
     */
    default boolean createExplosion(@Nullable Entity source, @NonNull Location loc, float power, boolean setFire) {
        return createExplosion(source, loc, power, setFire, true);
    }
    /**
     * Creates explosion at given location with given power, with the specified entity as the source.
     * Will set blocks on fire and destroy blocks.
     *
     * @param source The source entity of the explosion
     * @param loc Location to blow up
     * @param power The power of explosion, where 4F is TNT
     * @return false if explosion was canceled, otherwise true
     */
    default boolean createExplosion(@Nullable Entity source, @NonNull Location loc, float power) {
        return createExplosion(source, loc, power, true, true);
    }
    /**
     * Creates explosion at given entities location with given power and optionally
     * setting blocks on fire, with the specified entity as the source.
     *
     * @param source The source entity of the explosion
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @param breakBlocks Whether or not to have blocks be destroyed
     * @return false if explosion was canceled, otherwise true
     */
    default boolean createExplosion(@NonNull Entity source, float power, boolean setFire, boolean breakBlocks) {
        return createExplosion(source, source.getLocation(), power, setFire, breakBlocks);
    }
    /**
     * Creates explosion at given entities location with given power and optionally
     * setting blocks on fire, with the specified entity as the source.
     *
     * Will destroy blocks.
     *
     * @param source The source entity of the explosion
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @return false if explosion was canceled, otherwise true
     */
    default boolean createExplosion(@NonNull Entity source, float power, boolean setFire) {
        return createExplosion(source, source.getLocation(), power, setFire, true);
    }

    /**
     * Creates explosion at given entities location with given power and optionally
     * setting blocks on fire, with the specified entity as the source.
     *
     * @param source The source entity of the explosion
     * @param power The power of explosion, where 4F is TNT
     * @return false if explosion was canceled, otherwise true
     */
    default boolean createExplosion(@NonNull Entity source, float power) {
        return createExplosion(source, source.getLocation(), power, true, true);
    }

    /**
     * Creates explosion at given coordinates with given power and optionally
     * setting blocks on fire or breaking blocks.
     *
     * @param loc Location to blow up
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @param breakBlocks Whether or not to have blocks be destroyed
     * @return false if explosion was canceled, otherwise true
     */
    boolean createExplosion(@NonNull Location loc, float power, boolean setFire, boolean breakBlocks);

    /**
     * Creates explosion at given coordinates with given power and optionally
     * setting blocks on fire or breaking blocks.
     *
     * @param loc Location to blow up
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @param breakBlocks Whether or not to have blocks be destroyed
     * @param source the source entity, used for tracking damage
     * @return false if explosion was canceled, otherwise true
     */
    boolean createExplosion(@NonNull Location loc, float power, boolean setFire, boolean breakBlocks, @Nullable Entity source);

    /**
     * Gets the current PVP setting for this world.
     *
     * @return True if PVP is enabled
     */
    boolean getPVP();

    /**
     * Sets the PVP setting for this world.
     *
     * @param pvp True/False whether PVP should be Enabled.
     */
    void setPVP(boolean pvp);

    /**
     * Gets the chunk generator for this world
     *
     * @return ChunkGenerator associated with this world
     */
    @Nullable ChunkGenerator getGenerator();

    /**
     * Gets the biome provider for this world
     *
     * @return BiomeProvider associated with this world
     */
    @Nullable BiomeProvider getBiomeProvider();

    /**
     * Saves world to disk
     */
    void save();

    /**
     * Gets a list of all applied {@link BlockPopulator}s for this World
     *
     * @return List containing any or none BlockPopulators
     */
    @NonNull List<BlockPopulator> getPopulators();

    /**
     * Spawn a {@link FallingBlock} entity at the given {@link Location} of
     * the specified {@link BlockData}. The BlockData dictates what is falling.
     * When the FallingBlock hits the ground, it will place that block.
     *
     * @param location The {@link Location} to spawn the FallingBlock
     * @param data The {@link BlockData} of the FallingBlock to spawn
     * @return The spawned {@link FallingBlock} instance
     * @throws IllegalArgumentException if {@link Location} or {@link
     *     BlockData} are null
     */
    @NonNull FallingBlock spawnFallingBlock(@NonNull Location location, @NonNull BlockData data) throws IllegalArgumentException;

    /**
     * Spawn a {@link FallingBlock} entity at the given {@link Location} of the
     * specified {@link Material}. The material dictates what is falling.
     * When the FallingBlock hits the ground, it will place that block.
     * <p>
     * The Material must be a block type, check with {@link Material#isBlock()
     * material.isBlock()}. The Material may not be air.
     *
     * @param location The {@link Location} to spawn the FallingBlock
     * @param material The block {@link Material} type
     * @param data The block data
     * @return The spawned {@link FallingBlock} instance
     * @throws IllegalArgumentException if {@link Location} or {@link
     *     Material} are null or {@link Material} is not a block
     * @deprecated Magic value
     */
    @Deprecated
    @NonNull FallingBlock spawnFallingBlock(@NonNull Location location, @NonNull Material material, byte data) throws IllegalArgumentException;

    /**
     * Plays an effect to all players within a default radius around a given
     * location.
     *
     * @param location the {@link Location} around which players must be to
     *     hear the sound
     * @param effect the {@link Effect}
     * @param data a data bit needed for some effects
     */
    void playEffect(@NonNull Location location, @NonNull Effect effect, int data);

    /**
     * Plays an effect to all players within a given radius around a location.
     *
     * @param location the {@link Location} around which players must be to
     *     hear the effect
     * @param effect the {@link Effect}
     * @param data a data bit needed for some effects
     * @param radius the radius around the location
     */
    void playEffect(@NonNull Location location, @NonNull Effect effect, int data, int radius);

    /**
     * Plays an effect to all players within a default radius around a given
     * location.
     *
     * @param <T> data dependant on the type of effect
     * @param location the {@link Location} around which players must be to
     *     hear the sound
     * @param effect the {@link Effect}
     * @param data a data bit needed for some effects
     */
    <T> void playEffect(@NonNull Location location, @NonNull Effect effect, @Nullable T data);

    /**
     * Plays an effect to all players within a given radius around a location.
     *
     * @param <T> data dependant on the type of effect
     * @param location the {@link Location} around which players must be to
     *     hear the effect
     * @param effect the {@link Effect}
     * @param data a data bit needed for some effects
     * @param radius the radius around the location
     */
    <T> void playEffect(@NonNull Location location, @NonNull Effect effect, @Nullable T data, int radius);

    /**
     * Get empty chunk snapshot (equivalent to all air blocks), optionally
     * including valid biome data. Used for representing an ungenerated chunk,
     * or for fetching only biome data without loading a chunk.
     *
     * @param x - chunk x coordinate
     * @param z - chunk z coordinate
     * @param includeBiome - if true, snapshot includes per-coordinate biome
     *     type
     * @param includeBiomeTemp - if true, snapshot includes per-coordinate
     *     raw biome temperature
     * @return The empty snapshot.
     */
    @NonNull ChunkSnapshot getEmptyChunkSnapshot(int x, int z, boolean includeBiome, boolean includeBiomeTemp);

    /**
     * Sets the spawn flags for this.
     *
     * @param allowMonsters - if true, monsters are allowed to spawn in this
     *     world.
     * @param allowAnimals - if true, animals are allowed to spawn in this
     *     world.
     */
    void setSpawnFlags(boolean allowMonsters, boolean allowAnimals);

    /**
     * Gets whether animals can spawn in this world.
     *
     * @return whether animals can spawn in this world.
     */
    boolean getAllowAnimals();

    /**
     * Gets whether monsters can spawn in this world.
     *
     * @return whether monsters can spawn in this world.
     */
    boolean getAllowMonsters();

    /**
     * Gets the temperature for the given block coordinates.
     * <p>
     * It is safe to run this method when the block does not exist, it will
     * not create the block.
     * <p>
     * This method will return the raw temperature without adjusting for block
     * height effects.
     *
     * @param x X coordinate of the block
     * @param y Y coordinate of the block
     * @param z Z coordinate of the block
     * @return Temperature of the requested block
     */
    double getTemperature(int x, int y, int z);

    /**
     * Gets the humidity for the given block coordinates.
     * <p>
     * It is safe to run this method when the block does not exist, it will
     * not create the block.
     *
     * @param x X coordinate of the block
     * @param y Y coordinate of the block
     * @param z Z coordinate of the block
     * @return Humidity of the requested block
     */
    double getHumidity(int x, int y, int z);

    /**
     * Gets the maximum height to which chorus fruits and nether portals can
     * bring players within this dimension.
     *
     * This excludes portals that were already built above the limit as they
     * still connect normally. May not be greater than {@link #getMaxHeight()}.
     *
     * @return maximum logical height for chorus fruits and nether portals
     */
    int getLogicalHeight();

    /**
     * Gets if this world is natural.
     *
     * When false, compasses spin randomly, and using a bed to set the respawn
     * point or sleep, is disabled. When true, nether portals can spawn
     * zombified piglins.
     *
     * @return true if world is natural
     */
    boolean isNatural();

    /**
     * Gets if beds work in this world.
     *
     * A non-working bed will blow up when trying to sleep. {@link #isNatural()}
     * defines if a bed can be used to set spawn point.
     *
     * @return true if beds work in this world
     */
    boolean isBedWorks();

    /**
     * Gets if this world has skylight access.
     *
     * @return true if this world has skylight access
     */
    boolean hasSkyLight();

    /**
     * Gets if this world has a ceiling.
     *
     * @return true if this world has a bedrock ceiling
     */
    boolean hasCeiling();

    /**
     * Gets if this world allow to piglins to survive without shaking and
     * transforming to zombified piglins.
     *
     * @return true if piglins will not transform to zombified piglins
     */
    boolean isPiglinSafe();

    /**
     * Gets if this world allows players to charge and use respawn anchors.
     *
     * @return true if players can charge and use respawn anchors
     */
    boolean isRespawnAnchorWorks();

    /**
     * Gets if players with the bad omen effect in this world will trigger a
     * raid.
     *
     * @return true if raids will be triggered
     */
    boolean hasRaids();

    /**
     * Gets if various water/lava mechanics will be triggered in this world, eg:
     * <br>
     * <ul>
     * <li>Water is evaporated</li>
     * <li>Sponges dry</li>
     * <li>Lava spreads faster and further</li>
     * </ul>
     *
     * @return true if this world has the above mechanics
     */
    boolean isUltraWarm();

    /**
     * Gets the sea level for this world.
     * <p>
     * This is often half of {@link #getMaxHeight()}
     *
     * @return Sea level
     */
    int getSeaLevel();

    /**
     * Gets whether the world's spawn area should be kept loaded into memory
     * or not.
     *
     * @return true if the world's spawn area will be kept loaded into memory.
     */
    boolean getKeepSpawnInMemory();

    /**
     * Sets whether the world's spawn area should be kept loaded into memory
     * or not.
     *
     * @param keepLoaded if true then the world's spawn area will be kept
     *     loaded into memory.
     */
    void setKeepSpawnInMemory(boolean keepLoaded);

    /**
     * Gets whether or not the world will automatically save
     *
     * @return true if the world will automatically save, otherwise false
     */
    boolean isAutoSave();

    /**
     * Sets whether or not the world will automatically save
     *
     * @param value true if the world should automatically save, otherwise
     *     false
     */
    void setAutoSave(boolean value);

    /**
     * Sets the Difficulty of the world.
     *
     * @param difficulty the new difficulty you want to set the world to
     */
    void setDifficulty(@NonNull Difficulty difficulty);

    /**
     * Gets the Difficulty of the world.
     *
     * @return The difficulty of the world.
     */
    @NonNull Difficulty getDifficulty();

    /**
     * Gets the folder of this world on disk.
     *
     * @return The folder of this world.
     */
    @NonNull File getWorldFolder();

    /**
     * Gets the type of this world.
     *
     * @return Type of this world.
     * @deprecated world type is only used to select the default word generation
     * settings and is not stored in Vanilla worlds, making it impossible for
     * this method to always return the correct value.
     */
    @Nullable
    @Deprecated
    WorldType getWorldType();

    /**
     * Gets whether or not structures are being generated.
     *
     * @return True if structures are being generated.
     */
    boolean canGenerateStructures();

    /**
     * Gets whether the world is hardcore or not.
     *
     * In a hardcore world the difficulty is locked to hard.
     *
     * @return hardcore status
     */
    boolean isHardcore();

    /**
     * Sets whether the world is hardcore or not.
     *
     * In a hardcore world the difficulty is locked to hard.
     *
     * @param hardcore Whether the world is hardcore
     */
    void setHardcore(boolean hardcore);

    /**
     * Gets the world's ticks per {@link SpawnCategory} mob spawns value
     * <p>
     * This value determines how many ticks there are between attempts to
     * spawn {@link SpawnCategory} mobs.
     * <p>
     * <b>Example Usage:</b>
     * <ul>
     * <li>A value of 1 will mean the server will attempt to spawn {@link SpawnCategory} mobs in
     *     this world every tick.
     * <li>A value of 400 will mean the server will attempt to spawn {@link SpawnCategory} mobs
     *     in this world every 400th tick.
     * <li>A value below 0 will be reset back to Minecraft's default.
     * </ul>
     * <p>
     * <b>Note:</b>
     * If set to 0, {@link SpawnCategory} mobs spawning will be disabled for this world.
     * <p>
     * Minecraft default: 1.
     *
     * @param spawnCategory the category spawn
     * @return The world's ticks per {@link SpawnCategory} mob spawns value
     */
    long getTicksPerSpawns(@NonNull SpawnCategory spawnCategory);

    /**
     * Sets the world's ticks per {@link SpawnCategory} mob spawns value
     * <p>
     * This value determines how many ticks there are between attempts to
     * spawn {@link SpawnCategory} mobs.
     * <p>
     * <b>Example Usage:</b>
     * <ul>
     * <li>A value of 1 will mean the server will attempt to spawn {@link SpawnCategory} mobs in
     *     this world on every tick.
     * <li>A value of 400 will mean the server will attempt to spawn {@link SpawnCategory} mobs
     *     in this world every 400th tick.
     * <li>A value below 0 will be reset back to Minecraft's default.
     * </ul>
     * <p>
     * <b>Note:</b>
     * If set to 0, {@link SpawnCategory} mobs spawning will be disabled for this world.
     * <p>
     * Minecraft default: 1.
     *
     * @param spawnCategory the category spawn
     * @param ticksPerCategorySpawn the ticks per {@link SpawnCategory} mob spawns value you
     *     want to set the world to
     */
    void setTicksPerSpawns(@NonNull SpawnCategory spawnCategory, int ticksPerCategorySpawn);

    /**
     * Gets the limit for number of {@link SpawnCategory} entities that can spawn in a chunk in
     * this world
     *
     * @param spawnCategory the entity category
     * @return The ambient spawn limit
     */
    int getSpawnLimit(@NonNull SpawnCategory spawnCategory);

    /**
     * Sets the limit for number of {@link SpawnCategory} entities that can spawn in a chunk in
     * this world
     * <p>
     * <b>Note:</b> If set to a negative number the world will use the
     * server-wide spawn limit instead.
     *
     * @param spawnCategory the entity category
     * @param limit the new mob limit
     */
    void setSpawnLimit(@NonNull SpawnCategory spawnCategory, int limit);

    /**
     * Play a Sound at the provided Location in the World.
     * <p>
     * This function will fail silently if Location or Sound are null.
     *
     * @param location The location to play the sound
     * @param sound The sound to play
     * @param volume The volume of the sound
     * @param pitch The pitch of the sound
     */
    void playSound(@NonNull Location location, @NonNull Sound sound, float volume, float pitch);

    /**
     * Play a Sound at the provided Location in the World.
     * <p>
     * This function will fail silently if Location or Sound are null. No
     * sound will be heard by the players if their clients do not have the
     * respective sound for the value passed.
     *
     * @param location The location to play the sound
     * @param sound The internal sound name to play
     * @param volume The volume of the sound
     * @param pitch The pitch of the sound
     */
    void playSound(@NonNull Location location, @NonNull String sound, float volume, float pitch);

    /**
     * Play a Sound at the provided Location in the World.
     * <p>
     * This function will fail silently if Location or Sound are null.
     *
     * @param location The location to play the sound
     * @param sound The sound to play
     * @param category the category of the sound
     * @param volume The volume of the sound
     * @param pitch The pitch of the sound
     */
    void playSound(@NonNull Location location, @NonNull Sound sound, @NonNull SoundCategory category, float volume, float pitch);

    /**
     * Play a Sound at the provided Location in the World.
     * <p>
     * This function will fail silently if Location or Sound are null. No sound
     * will be heard by the players if their clients do not have the respective
     * sound for the value passed.
     *
     * @param location The location to play the sound
     * @param sound The internal sound name to play
     * @param category The category of the sound
     * @param volume The volume of the sound
     * @param pitch The pitch of the sound
     */
    void playSound(@NonNull Location location, @NonNull String sound, @NonNull SoundCategory category, float volume, float pitch);

    /**
     * Play a Sound at the location of the provided entity in the World.
     * <p>
     * This function will fail silently if Entity or Sound are null.
     *
     * @param entity The entity to play the sound
     * @param sound The sound to play
     * @param volume The volume of the sound
     * @param pitch The pitch of the sound
     */
    void playSound(@NonNull Entity entity, @NonNull Sound sound, float volume, float pitch);

    /**
     * Play a Sound at the location of the provided entity in the World.
     * <p>
     * This function will fail silently if Entity or Sound are null.
     *
     * @param entity The entity to play the sound
     * @param sound The sound to play
     * @param volume The volume of the sound
     * @param pitch The pitch of the sound
     */
    void playSound(@NonNull Entity entity, @NonNull String sound, float volume, float pitch);

    /**
     * Play a Sound at the location of the provided entity in the World.
     * <p>
     * This function will fail silently if Entity or Sound are null.
     *
     * @param entity The entity to play the sound
     * @param sound The sound to play
     * @param category The category of the sound
     * @param volume The volume of the sound
     * @param pitch The pitch of the sound
     */
    void playSound(@NonNull Entity entity, @NonNull Sound sound, @NonNull SoundCategory category, float volume, float pitch);

    /**
     * Play a Sound at the location of the provided entity in the World.
     * <p>
     * This function will fail silently if Entity or Sound are null.
     *
     * @param entity The entity to play the sound
     * @param sound The sound to play
     * @param category The category of the sound
     * @param volume The volume of the sound
     * @param pitch The pitch of the sound
     */
    void playSound(@NonNull Entity entity, @NonNull String sound, @NonNull SoundCategory category, float volume, float pitch);

    /**
     * Get an array containing the names of all the {@link GameRule}s.
     *
     * @return An array of {@link GameRule} names.
     */
    @NonNull String[] getGameRules();

    /**
     * Gets the current state of the specified rule
     * <p>
     * Will return null if rule passed is null
     *
     * @param rule Rule to look up value of
     * @return String value of rule
     * @deprecated use {@link #getGameRuleValue(GameRule)} instead
     */
    @Deprecated
    @Contract("null -> null; !null -> !null")
    @Nullable String getGameRuleValue(@Nullable String rule);

    /**
     * Set the specified gamerule to specified value.
     * <p>
     * The rule may attempt to validate the value passed, will return true if
     * value was set.
     * <p>
     * If rule is null, the function will return false.
     *
     * @param rule Rule to set
     * @param value Value to set rule to
     * @return True if rule was set
     * @deprecated use {@link #setGameRule(GameRule, Object)} instead.
     */
    @Deprecated
    boolean setGameRuleValue(@NonNull String rule, @NonNull String value);

    /**
     * Checks if string is a valid game rule
     *
     * @param rule Rule to check
     * @return True if rule exists
     */
    boolean isGameRule(@NonNull String rule);

    /**
     * Get the current value for a given {@link GameRule}.
     *
     * @param rule the GameRule to check
     * @param <T> the GameRule's type
     * @return the current value
     */
    @Nullable <T> T getGameRuleValue(@NonNull GameRule<T> rule);

    /**
     * Get the default value for a given {@link GameRule}. This value is not
     * guaranteed to match the current value.
     *
     * @param rule the rule to return a default value for
     * @param <T> the type of GameRule
     * @return the default value
     */
    @Nullable <T> T getGameRuleDefault(@NonNull GameRule<T> rule);

    /**
     * Set the given {@link GameRule}'s new value.
     *
     * @param rule the GameRule to update
     * @param newValue the new value
     * @param <T> the value type of the GameRule
     * @return true if the value was successfully set
     */
    <T> boolean setGameRule(@NonNull GameRule<T> rule, @NonNull T newValue);

    /**
     * Gets the world border for this world.
     *
     * @return The world border for this world.
     */
    @NonNull WorldBorder getWorldBorder();

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location.
     *
     * @param particle the particle to spawn
     * @param location the location to spawn at
     * @param count the number of particles
     */
    void spawnParticle(@NonNull Particle particle, @NonNull Location location, int count);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location.
     *
     * @param particle the particle to spawn
     * @param x the position on the x axis to spawn at
     * @param y the position on the y axis to spawn at
     * @param z the position on the z axis to spawn at
     * @param count the number of particles
     */
    void spawnParticle(@NonNull Particle particle, double x, double y, double z, int count);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location.
     *
     * @param <T> type of particle data (see {@link Particle#getDataType()}
     * @param particle the particle to spawn
     * @param location the location to spawn at
     * @param count the number of particles
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     */
    <T> void spawnParticle(@NonNull Particle particle, @NonNull Location location, int count, @Nullable T data);


    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location.
     *
     * @param <T> type of particle data (see {@link Particle#getDataType()}
     * @param particle the particle to spawn
     * @param x the position on the x axis to spawn at
     * @param y the position on the y axis to spawn at
     * @param z the position on the z axis to spawn at
     * @param count the number of particles
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     */
    <T> void spawnParticle(@NonNull Particle particle, double x, double y, double z, int count, @Nullable T data);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param particle the particle to spawn
     * @param location the location to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     */
    void spawnParticle(@NonNull Particle particle, @NonNull Location location, int count, double offsetX, double offsetY, double offsetZ);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param particle the particle to spawn
     * @param x the position on the x axis to spawn at
     * @param y the position on the y axis to spawn at
     * @param z the position on the z axis to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     */
    void spawnParticle(@NonNull Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param <T> type of particle data (see {@link Particle#getDataType()}
     * @param particle the particle to spawn
     * @param location the location to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     */
    <T> void spawnParticle(@NonNull Particle particle, @NonNull Location location, int count, double offsetX, double offsetY, double offsetZ, @Nullable T data);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param <T> type of particle data (see {@link Particle#getDataType()}
     * @param particle the particle to spawn
     * @param x the position on the x axis to spawn at
     * @param y the position on the y axis to spawn at
     * @param z the position on the z axis to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     */
    <T> void spawnParticle(@NonNull Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, @Nullable T data);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param particle the particle to spawn
     * @param location the location to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param extra the extra data for this particle, depends on the
     *              particle used (normally speed)
     */
    void spawnParticle(@NonNull Particle particle, @NonNull Location location, int count, double offsetX, double offsetY, double offsetZ, double extra);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param particle the particle to spawn
     * @param x the position on the x axis to spawn at
     * @param y the position on the y axis to spawn at
     * @param z the position on the z axis to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param extra the extra data for this particle, depends on the
     *              particle used (normally speed)
     */
    void spawnParticle(@NonNull Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param <T> type of particle data (see {@link Particle#getDataType()}
     * @param particle the particle to spawn
     * @param location the location to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param extra the extra data for this particle, depends on the
     *              particle used (normally speed)
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     */
    <T> void spawnParticle(@NonNull Particle particle, @NonNull Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable T data);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param <T> type of particle data (see {@link Particle#getDataType()}
     * @param particle the particle to spawn
     * @param x the position on the x axis to spawn at
     * @param y the position on the y axis to spawn at
     * @param z the position on the z axis to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param extra the extra data for this particle, depends on the
     *              particle used (normally speed)
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     */
    default <T> void spawnParticle(@NonNull Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable T data) { spawnParticle(particle, null, null, x, y, z, count, offsetX, offsetY, offsetZ, extra, data, true); }// Paper start - Expand Particle API
    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param particle the particle to spawn
     * @param receivers List of players to receive the particles, or null for all in world
     * @param source Source of the particles to be used in visibility checks, or null if no player source
     * @param x the position on the x axis to spawn at
     * @param y the position on the y axis to spawn at
     * @param z the position on the z axis to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param extra the extra data for this particle, depends on the
     *              particle used (normally speed)
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     * @param <T> Type
     */
    default <T> void spawnParticle(@NonNull Particle particle, @Nullable List<Player> receivers, @NonNull Player source, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable T data) { spawnParticle(particle, receivers, source, x, y, z, count, offsetX, offsetY, offsetZ, extra, data, true); }
    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param particle the particle to spawn
     * @param receivers List of players to receive the particles, or null for all in world
     * @param source Source of the particles to be used in visibility checks, or null if no player source
     * @param x the position on the x axis to spawn at
     * @param y the position on the y axis to spawn at
     * @param z the position on the z axis to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param extra the extra data for this particle, depends on the
     *              particle used (normally speed)
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     * @param <T> Type
     * @param force allows the particle to be seen further away from the player
     *              and shows to players using any vanilla client particle settings
     */
    <T> void spawnParticle(@NonNull Particle particle, @Nullable List<Player> receivers, @Nullable Player source, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable T data, boolean force);
    // Paper end


    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param <T> type of particle data (see {@link Particle#getDataType()}
     * @param particle the particle to spawn
     * @param location the location to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param extra the extra data for this particle, depends on the
     *              particle used (normally speed)
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     * @param force whether to send the particle to players within an extended
     *              range and encourage their client to render it regardless of
     *              settings
     */
    <T> void spawnParticle(@NonNull Particle particle, @NonNull Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable T data, boolean force);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param <T> type of particle data (see {@link Particle#getDataType()}
     * @param particle the particle to spawn
     * @param x the position on the x axis to spawn at
     * @param y the position on the y axis to spawn at
     * @param z the position on the z axis to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param extra the extra data for this particle, depends on the
     *              particle used (normally speed)
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     * @param force whether to send the particle to players within an extended
     *              range and encourage their client to render it regardless of
     *              settings
     */
    <T> void spawnParticle(@NonNull Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable T data, boolean force);

    /**
     * Find the closest nearby structure of a given {@link StructureType}.
     * Finding unexplored structures can, and will, block if the world is
     * looking in chunks that have not generated yet. This can lead to the world
     * temporarily freezing while locating an unexplored structure.
     * <p>
     * The {@code radius} is not a rigid square radius. Each structure may alter
     * how many chunks to check for each iteration. Do not assume that only a
     * radius x radius chunk area will be checked. For example,
     * {@link StructureType#WOODLAND_MANSION} can potentially check up to 20,000
     * blocks away (or more) regardless of the radius used.
     * <p>
     * This will <i>not</i> load or generate chunks. This can also lead to
     * instances where the server can hang if you are only looking for
     * unexplored structures. This is because it will keep looking further and
     * further out in order to find the structure.
     * <p>
     * The difference between searching for a {@link StructureType} and a
     * {@link Structure} is, that a {@link StructureType} can refer to multiple
     * {@link Structure Structures} while searching for a {@link Structure}
     * while only search for the given {@link Structure}.
     *
     * @param origin where to start looking for a structure
     * @param structureType the type of structure to find
     * @param radius the radius, in chunks, around which to search
     * @param findUnexplored true to only find unexplored structures
     * @return the closest {@link Location} and {@link Structure}, or null if no
     * structure of the specified type exists.
     * @see #locateNearestStructure(Location, Structure, int, boolean)
     */
    @Nullable
    StructureSearchResult locateNearestStructure(@NonNull Location origin, @NonNull StructureType structureType, int radius, boolean findUnexplored);

    /**
     * Find the closest nearby structure of a given {@link Structure}. Finding
     * unexplored structures can, and will, block if the world is looking in
     * chunks that have not generated yet. This can lead to the world
     * temporarily freezing while locating an unexplored structure.
     * <p>
     * The {@code radius} is not a rigid square radius. Each structure may alter
     * how many chunks to check for each iteration. Do not assume that only a
     * radius x radius chunk area will be checked. For example,
     * {@link Structure#MANSION} can potentially check up to 20,000 blocks away
     * (or more) regardless of the radius used.
     * <p>
     * This will <i>not</i> load or generate chunks. This can also lead to
     * instances where the server can hang if you are only looking for
     * unexplored structures. This is because it will keep looking further and
     * further out in order to find the structure.
     * <p>
     * The difference between searching for a {@link StructureType} and a
     * {@link Structure} is, that a {@link StructureType} can refer to multiple
     * {@link Structure Structures} while searching for a {@link Structure}
     * while only search for the given {@link Structure}.
     *
     * @param origin where to start looking for a structure
     * @param structure the structure to find
     * @param radius the radius, in chunks, around which to search
     * @param findUnexplored true to only find unexplored structures
     * @return the closest {@link Location} and {@link Structure}, or null if no
     * structure was found.
     * @see #locateNearestStructure(Location, StructureType, int, boolean)
     */
    @Nullable
    StructureSearchResult locateNearestStructure(@NonNull Location origin, @NonNull Structure structure, int radius, boolean findUnexplored);

    /**
     * Locates the nearest biome based on an origin, biome type, and radius to search.
     * Step defaults to {@code 8}.
     *
     * @param origin Origin location
     * @param biome Biome to find
     * @param radius radius to search
     * @return Location of biome or null if not found in specified radius
     */
    @Nullable
    Location locateNearestBiome(@NonNull Location origin, @NonNull Biome biome, int radius);

    /**
     * Locates the nearest biome based on an origin, biome type, and radius to search
     * and step
     *
     * @param origin Origin location
     * @param biome Biome to find
     * @param radius radius to search
     * @param step Search step 1 would mean checking every block, 8 would be every 8th block
     * @return Location of biome or null if not found in specified radius
     */
    @Nullable
    Location locateNearestBiome(@NonNull Location origin, @NonNull Biome biome, int radius, int step);

    /**
     * Gets the coordinate scaling of this world.
     *
     * @return the coordinate scale
     */
    double getCoordinateScale();

    /**
     * Checks if this world has a fixed time
     *
     * @return whether this world has fixed time
     */
    boolean isFixedTime();

    /**
     * Gets the collection of materials that burn infinitely in this world.
     *
     * @return the materials that will forever stay lit by fire
     */
    @NonNull
    Collection<Material> getInfiniburn();

    /**
     * Posts a specified game event at a location
     *
     * @param sourceEntity optional source entity
     * @param gameEvent the game event to post
     * @param position the position in the world where to post the event to listeners
     */
    void sendGameEvent(@Nullable Entity sourceEntity, @NonNull GameEvent gameEvent, @NonNull Vector position);

    /**
     * Returns the view distance used for this world.
     *
     * @return the view distance used for this world
     */
    int getViewDistance();

    /**
     * Returns the simulation distance used for this world.
     *
     * @return the simulation distance used for this world
     */
    int getSimulationDistance();

    /**
     * Sets the view distance for this world.
     * @param viewDistance view distance in [2, 32]
     */
    void setViewDistance(int viewDistance);

    /**
     * Sets the simulation distance for this world.
     * @param simulationDistance simulation distance in [2, 32]
     */
    void setSimulationDistance(int simulationDistance);

    /**
     * Gets the sending view distance for this world.
     * <p>
     * Sending view distance is the view distance where chunks will load in for players in this world.
     * </p>
     * @return The sending view distance for this world.
     */
    int getSendViewDistance();

    /**
     * Sets the sending view distance for this world.
     * <p>
     * Sending view distance is the view distance where chunks will load in for players in this world.
     * </p>
     * @param viewDistance view distance in [2, 32] or -1
     */
    void setSendViewDistance(int viewDistance);

    /**
     * Finds the nearest raid close to the given location.
     *
     * @param location the origin location
     * @param radius the radius
     * @return the closest {@link Raid}, or null if no raids were found
     */
    @Nullable Raid locateNearestRaid(@NonNull Location location, int radius);

    /**
     * Gets all raids that are going on over this world.
     *
     * @return the list of all active raids
     */
    @NonNull List<Raid> getRaids();

    /**
     * Get the {@link DragonBattle} associated with this world.
     *
     * If this world's environment is not {@link Environment#THE_END}, null will
     * be returned.
     * <p>
     * If an end world, a dragon battle instance will be returned regardless of
     * whether or not a dragon is present in the world or a fight sequence has
     * been activated. The dragon battle instance acts as a state holder.
     *
     * @return the dragon battle instance
     */
    @Nullable DragonBattle getEnderDragonBattle();

    /**
     * Get all {@link FeatureFlag} enabled in this world.
     *
     * @return all enabled {@link FeatureFlag}
     */
    @NonNull Set<FeatureFlag> getFeatureFlags();

    /**
     * Represents various map environment types that a world may be
     */
    enum Environment {

        /**
         * Represents the "normal"/"surface world" map
         */
        NORMAL(0),
        /**
         * Represents a nether based map ("hell")
         */
        NETHER(-1),
        /**
         * Represents the "end" map
         */
        THE_END(1),
        /**
         * Represents a custom dimension
         */
        CUSTOM(-999);

        private final int id;
        private static final Map<Integer, Environment> lookup = new HashMap<Integer, Environment>();

        Environment(int id) {
            this.id = id;
        }

        /**
         * Gets the dimension ID of this environment
         *
         * @return dimension ID
         * @deprecated Magic value
         */
        @Deprecated
        public int getId() {
            return id;
        }

        /**
         * Get an environment by ID
         *
         * @param id The ID of the environment
         * @return The environment
         * @deprecated Magic value
         */
        @Deprecated
        @Nullable
        public static Environment getEnvironment(int id) {
            return lookup.get(id);
        }

        static {
            for (Environment env : values()) {
                lookup.put(env.getId(), env);
            }
        }
    }
}

