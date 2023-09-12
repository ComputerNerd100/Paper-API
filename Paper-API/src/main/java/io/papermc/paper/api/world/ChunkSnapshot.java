package io.papermc.paper.api.world;

import io.papermc.paper.api.block.Biome;
import io.papermc.paper.api.block.data.BlockData;
import io.papermc.paper.api.material.Material;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents a static, thread-safe snapshot of chunk of blocks.
 * <p>
 * Purpose is to allow clean, efficient copy of a chunk data to be made, and
 * then handed off for processing in another thread (e.g. map rendering)
 */
public interface ChunkSnapshot {

    /**
     * Gets the X-coordinate of this chunk
     *
     * @return X-coordinate
     */
    int getX();

    /**
     * Gets the Z-coordinate of this chunk
     *
     * @return Z-coordinate
     */
    int getZ();

    /**
     * Gets name of the world containing this chunk
     *
     * @return Parent World Name
     */
    @NonNull
    String getWorldName();

    /**
     * Get block type for block at corresponding coordinate in the chunk
     *
     * @param x 0-15
     * @param y world minHeight (inclusive) - world maxHeight (exclusive)
     * @param z 0-15
     * @return block material type
     */
    @NonNull
    Material getBlockType(int x, int y, int z);

    /**
     * Get block data for block at corresponding coordinate in the chunk
     *
     * @param x 0-15
     * @param y world minHeight (inclusive) - world maxHeight (exclusive)
     * @param z 0-15
     * @return block material type
     */
    @NonNull
    BlockData getBlockData(int x, int y, int z);

    /**
     * Get block data for block at corresponding coordinate in the chunk
     *
     * @param x 0-15
     * @param y world minHeight (inclusive) - world maxHeight (exclusive)
     * @param z 0-15
     * @return 0-15
     * @deprecated Magic value
     */
    @Deprecated
    int getData(int x, int y, int z);

    /**
     * Get sky light level for block at corresponding coordinate in the chunk
     *
     * @param x 0-15
     * @param y world minHeight (inclusive) - world maxHeight (exclusive)
     * @param z 0-15
     * @return 0-15
     */
    int getBlockSkyLight(int x, int y, int z);

    /**
     * Get light level emitted by block at corresponding coordinate in the
     * chunk
     *
     * @param x 0-15
     * @param y world minHeight (inclusive) - world maxHeight (exclusive)
     * @param z 0-15
     * @return 0-15
     */
    int getBlockEmittedLight(int x, int y, int z);

    /**
     * Gets the highest non-air coordinate at the given coordinates
     *
     * @param x X-coordinate of the blocks (0-15)
     * @param z Z-coordinate of the blocks (0-15)
     * @return Y-coordinate of the highest non-air block
     */
    int getHighestBlockYAt(int x, int z);

    /**
     * Get biome at given coordinates
     *
     * @param x X-coordinate (0-15)
     * @param z Z-coordinate (0-15)
     * @return Biome at given coordinate
     * @deprecated biomes are now 3-dimensional
     */
    @NonNull
    @Deprecated
    Biome getBiome(int x, int z);

    /**
     * Get biome at given coordinates
     *
     * @param x X-coordinate (0-15)
     * @param y Y-coordinate (world minHeight (inclusive) - world maxHeight (exclusive))
     * @param z Z-coordinate (0-15)
     * @return Biome at given coordinate
     */
    @NonNull
    Biome getBiome(int x, int y, int z);

    /**
     * Get raw biome temperature at given coordinates
     *
     * @param x X-coordinate (0-15)
     * @param z Z-coordinate (0-15)
     * @return temperature at given coordinate
     * @deprecated biomes are now 3-dimensional
     */
    @Deprecated
    double getRawBiomeTemperature(int x, int z);

    /**
     * Get raw biome temperature at given coordinates
     *
     * @param x X-coordinate (0-15)
     * @param y Y-coordinate (world minHeight (inclusive) - world maxHeight (exclusive))
     * @param z Z-coordinate (0-15)
     * @return temperature at given coordinate
     */
    double getRawBiomeTemperature(int x, int y, int z);

    /**
     * Get world full time when chunk snapshot was captured
     *
     * @return time in ticks
     */
    long getCaptureFullTime();

    /**
     * Test if section is empty
     *
     * @param sy - section Y coordinate (block Y / 16, world minHeight (inclusive) - world maxHeight (exclusive))
     * @return true if empty, false if not
     */
    boolean isSectionEmpty(int sy);

    /**
     * Tests if this snapshot contains the specified block.
     *
     * @param block block to test
     * @return if the block is contained within
     */
    boolean contains(@NonNull BlockData block);

    /**
     * Tests if this chunk contains the specified biome.
     *
     * @param biome biome to test
     * @return if the biome is contained within
     */
    boolean contains(@NonNull Biome biome);
}
