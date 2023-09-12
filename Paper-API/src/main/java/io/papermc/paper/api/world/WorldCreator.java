package io.papermc.paper.api.world;

import io.papermc.paper.api.Paper;
import io.papermc.paper.api.command.CommandSender;
import io.papermc.paper.api.namespace.NamespacedKey;
import io.papermc.paper.api.world.generator.BiomeProvider;
import io.papermc.paper.api.world.generator.ChunkGenerator;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;


import java.util.Random;

/**
 * Represents various types of options that may be used to create a world.
 */
public class WorldCreator {
    private final NamespacedKey key; // Paper
    private final String name;
    private long seed;
    private World.Environment environment = World.Environment.NORMAL;
    private ChunkGenerator generator = null;
    private BiomeProvider biomeProvider = null;
    private WorldType type = WorldType.NORMAL;
    private boolean generateStructures = true;
    private String generatorSettings = "";
    private boolean hardcore = false;
    private net.kyori.adventure.util.TriState keepSpawnLoaded = net.kyori.adventure.util.TriState.NOT_SET; // Paper

    /**
     * Creates an empty WorldCreationOptions for the given world name
     *
     * @param name Name of the world that will be created
     */
    public WorldCreator(@NonNull String name) {
        this(name, getWorldKey(name));
    }

    private static NamespacedKey getWorldKey(String name) {
        final String mainLevelName = Paper.getUnsafe().getMainLevelName();
        if (name.equals(mainLevelName)) {
            return NamespacedKey.minecraft("overworld");
        } else if (name.equals(mainLevelName + "_nether")) {
            return NamespacedKey.minecraft("the_nether");
        } else if (name.equals(mainLevelName + "_the_end")) {
            return NamespacedKey.minecraft("the_end");
        } else {
            return NamespacedKey.minecraft(name.toLowerCase(java.util.Locale.ENGLISH).replace(" ", "_"));
        }
    }

    /**
     * Creates an empty WorldCreator for the given world name and key
     *
     * @param levelName LevelName of the world that will be created
     * @param worldKey NamespacedKey of the world that will be created
     */
    public WorldCreator(@NonNull String levelName, @NonNull NamespacedKey worldKey) {
        if (levelName == null || worldKey == null) {
            throw new IllegalArgumentException("World name and key cannot be null");
        }
        this.name = levelName;
        this.seed = (new Random()).nextLong();
        this.key = worldKey;
    }

    /**
     * Creates an empty WorldCreator for the given key.
     * LevelName will be the Key part of the NamespacedKey.
     *
     * @param worldKey NamespacedKey of the world that will be created
     */
    public WorldCreator(@NonNull NamespacedKey worldKey) {
        this(worldKey.getKey(), worldKey);
    }

    /**
     * Gets the key for this WorldCreator
     *
     * @return the key
     */
    @NonNull
    public NamespacedKey key() {
        return key;
    }

    /**
     * Creates an empty WorldCreator for the given world name and key
     *
     * @param levelName LevelName of the world that will be created
     * @param worldKey NamespacedKey of the world that will be created
     */
    @NonNull
    public static WorldCreator ofNameAndKey(@NonNull String levelName, @NonNull NamespacedKey worldKey) {
        return new WorldCreator(levelName, worldKey);
    }

    /**
     * Creates an empty WorldCreator for the given key.
     * LevelName will be the Key part of the NamespacedKey.
     *
     * @param worldKey NamespacedKey of the world that will be created
     */
    @NonNull
    public static WorldCreator ofKey(@NonNull NamespacedKey worldKey) {
        return new WorldCreator(worldKey);
    }

    /**
     * Copies the options from the specified world
     *
     * @param world World to copy options from
     * @return This object, for chaining
     */
    @NonNull
    public WorldCreator copy(@NonNull World world) {
        if (world == null) {
            throw new IllegalArgumentException("World cannot be null");
        }

        seed = world.getSeed();
        environment = world.getEnvironment();
        generator = world.getGenerator();
        biomeProvider = world.getBiomeProvider();
        type = world.getWorldType();
        generateStructures = world.canGenerateStructures();
        hardcore = world.isHardcore();

        return this;
    }

    /**
     * Copies the options from the specified {@link WorldCreator}
     *
     * @param creator World creator to copy options from
     * @return This object, for chaining
     */
    @NonNull
    public WorldCreator copy(@NonNull WorldCreator creator) {
        if (creator == null) {
            throw new IllegalArgumentException("Creator cannot be null");
        }

        seed = creator.seed();
        environment = creator.environment();
        generator = creator.generator();
        biomeProvider = creator.biomeProvider();
        type = creator.type();
        generateStructures = creator.generateStructures();
        generatorSettings = creator.generatorSettings();
        hardcore = creator.hardcore();

        return this;
    }

    /**
     * Gets the name of the world that is to be loaded or created.
     *
     * @return World name
     */
    @NonNull
    public String name() {
        return name;
    }

    /**
     * Gets the seed that will be used to create this world
     *
     * @return World seed
     */
    public long seed() {
        return seed;
    }

    /**
     * Sets the seed that will be used to create this world
     *
     * @param seed World seed
     * @return This object, for chaining
     */
    @NonNull
    public WorldCreator seed(long seed) {
        this.seed = seed;

        return this;
    }

    /**
     * Gets the environment that will be used to create or load the world
     *
     * @return World environment
     */
    public World.@NonNull Environment environment() {
        return environment;
    }

    /**
     * Sets the environment that will be used to create or load the world
     *
     * @param env World environment
     * @return This object, for chaining
     */
    @NonNull
    public WorldCreator environment(World.@NonNull Environment env) {
        this.environment = env;

        return this;
    }

    /**
     * Gets the type of the world that will be created or loaded
     *
     * @return World type
     */
    @NonNull
    public WorldType type() {
        return type;
    }

    /**
     * Sets the type of the world that will be created or loaded
     *
     * @param type World type
     * @return This object, for chaining
     */
    @NonNull
    public WorldCreator type(@NonNull WorldType type) {
        this.type = type;

        return this;
    }

    /**
     * Gets the generator that will be used to create or load the world.
     * <p>
     * This may be null, in which case the "natural" generator for this
     * environment will be used.
     *
     * @return Chunk generator
     */
    @Nullable
    public ChunkGenerator generator() {
        return generator;
    }

    /**
     * Sets the generator that will be used to create or load the world.
     * <p>
     * This may be null, in which case the "natural" generator for this
     * environment will be used.
     *
     * @param generator Chunk generator
     * @return This object, for chaining
     */
    @NonNull
    public WorldCreator generator(@Nullable ChunkGenerator generator) {
        this.generator = generator;

        return this;
    }

    /**
     * Sets the generator that will be used to create or load the world.
     * <p>
     * This may be null, in which case the "natural" generator for this
     * environment will be used.
     * <p>
     * If the generator cannot be found for the given name, the natural
     * environment generator will be used instead and a warning will be
     * printed to the console.
     *
     * @param generator Name of the generator to use, in "plugin:id" notation
     * @return This object, for chaining
     */
    @NonNull
    public WorldCreator generator(@Nullable String generator) {
        this.generator = getGeneratorForName(name, generator, Paper.getConsoleSender());

        return this;
    }

    /**
     * Sets the generator that will be used to create or load the world.
     * <p>
     * This may be null, in which case the "natural" generator for this
     * environment will be used.
     * <p>
     * If the generator cannot be found for the given name, the natural
     * environment generator will be used instead and a warning will be
     * printed to the specified output
     *
     * @param generator Name of the generator to use, in "plugin:id" notation
     * @param output {@link CommandSender} that will receive any error
     *     messages
     * @return This object, for chaining
     */
    @NonNull
    public WorldCreator generator(@Nullable String generator, @Nullable CommandSender output) {
        this.generator = getGeneratorForName(name, generator, output);

        return this;
    }

    /**
     * Gets the biome provider that will be used to create or load the world.
     * <p>
     * This may be null, in which case the biome provider from the {@link ChunkGenerator}
     * will be used. If no {@link ChunkGenerator} is specific the "natural" biome provider
     * for this environment will be used.
     *
     * @return Biome provider
     */
    @Nullable
    public BiomeProvider biomeProvider() {
        return biomeProvider;
    }

    /**
     * Sets the biome provider that will be used to create or load the world.
     * <p>
     * This may be null, in which case the biome provider from the
     * {@link ChunkGenerator} will be used. If no {@link ChunkGenerator} is
     * specific the "natural" biome provider for this environment will be used.
     *
     * @param biomeProvider Biome provider
     * @return This object, for chaining
     */
    @NonNull
    public WorldCreator biomeProvider(@Nullable BiomeProvider biomeProvider) {
        this.biomeProvider = biomeProvider;

        return this;
    }

    /**
     * Sets the biome provider that will be used to create or load the world.
     * <p>
     * This may be null, in which case the biome provider from the
     * {@link ChunkGenerator} will be used. If no {@link ChunkGenerator} is
     * specific the "natural" biome provider for this environment will be used.
     * <p>
     * If the biome provider cannot be found for the given name and no
     * {@link ChunkGenerator} is specific, the natural environment biome
     * provider will be used instead and a warning will be printed to the
     * specified output
     *
     * @param biomeProvider Name of the biome provider to use, in "plugin:id"
     * notation
     * @return This object, for chaining
     */
    @NonNull
    public WorldCreator biomeProvider(@Nullable String biomeProvider) {
        this.biomeProvider = getBiomeProviderForName(name, biomeProvider, Paper.getConsoleSender());

        return this;
    }

    /**
     * Sets the biome provider that will be used to create or load the world.
     * <p>
     * This may be null, in which case the biome provider from the
     * {@link ChunkGenerator} will be used. If no {@link ChunkGenerator} is
     * specific the "natural" biome provider for this environment will be used.
     * <p>
     * If the biome provider cannot be found for the given name and no
     * {@link ChunkGenerator} is specific, the natural environment biome
     * provider will be used instead and a warning will be printed to the
     * specified output
     *
     * @param biomeProvider Name of the biome provider to use, in "plugin:id"
     * notation
     * @param output {@link CommandSender} that will receive any error messages
     * @return This object, for chaining
     */
    @NonNull
    public WorldCreator biomeProvider(@Nullable String biomeProvider, @Nullable CommandSender output) {
        this.biomeProvider = getBiomeProviderForName(name, biomeProvider, output);

        return this;
    }

    /**
     * Sets the generator settings of the world that will be created or loaded.
     * <p>
     * Currently only {@link WorldType#FLAT} uses these settings, and expects
     * them to be in JSON format with a valid biome (1.18.2 and
     * above) defined. An example valid configuration is as follows:
     * <code>{"layers": [{"block": "stone", "height": 1}, {"block": "grass_block", "height": 1}], "biome":"plains"}</code>
     *
     * @param generatorSettings The settings that should be used by the
     * generator
     * @return This object, for chaining
     * @see <a href="https://minecraft.gamepedia.com/Custom_dimension">Custom
     * dimension</a> (scroll to "When the generator ID type is
     * <code>minecraft:flat</code>)"
     */
    @NonNull
    public WorldCreator generatorSettings(@NonNull String generatorSettings) {
        this.generatorSettings = generatorSettings;

        return this;
    }

    /**
     * Gets the generator settings of the world that will be created or loaded.
     *
     * @return The settings that should be used by the generator
     * @see #generatorSettings(java.lang.String)
     */
    @NonNull
    public String generatorSettings() {
        return generatorSettings;
    }

    /**
     * Sets whether or not worlds created or loaded with this creator will
     * have structures.
     *
     * @param generate Whether to generate structures
     * @return This object, for chaining
     */
    @NonNull
    public WorldCreator generateStructures(boolean generate) {
        this.generateStructures = generate;

        return this;
    }

    /**
     * Gets whether or not structures will be generated in the world.
     *
     * @return True if structures will be generated
     */
    public boolean generateStructures() {
        return generateStructures;
    }

    /**
     * Sets whether the world will be hardcore or not.
     *
     * In a hardcore world the difficulty will be locked to hard.
     *
     * @param hardcore Whether the world will be hardcore
     * @return This object, for chaining
     */
    @NonNull
    public WorldCreator hardcore(boolean hardcore) {
        this.hardcore = hardcore;

        return this;
    }

    /**
     * Gets whether the world will be hardcore or not.
     *
     * In a hardcore world the difficulty will be locked to hard.
     *
     * @return hardcore status
     */
    public boolean hardcore() {
        return hardcore;
    }

    /**
     * Creates a world with the specified options.
     * <p>
     * If the world already exists, it will be loaded from disk and some
     * options may be ignored.
     *
     * @return Newly created or loaded world
     */
    @Nullable
    public World createWorld() {
        return Paper.createWorld(this);
    }

    /**
     * Creates a new {@link WorldCreator} for the given world name
     *
     * @param name Name of the world to load or create
     * @return Resulting WorldCreator
     */
    @NonNull
    public static WorldCreator name(@NonNull String name) {
        return new WorldCreator(name);
    }

    /**
     * Attempts to get the {@link ChunkGenerator} with the given name.
     * <p>
     * If the generator is not found, null will be returned and a message will
     * be printed to the specified {@link CommandSender} explaining why.
     * <p>
     * The name must be in the "plugin:id" notation, or optionally just
     * "plugin", where "plugin" is the safe-name of a plugin and "id" is an
     * optional unique identifier for the generator you wish to request from
     * the plugin.
     *
     * @param world Name of the world this will be used for
     * @param name Name of the generator to retrieve
     * @param output Where to output if errors are present
     * @return Resulting generator, or null
     */
    @Nullable
    public static ChunkGenerator getGeneratorForName(@NonNull String world, @Nullable String name, @Nullable CommandSender output) {
        ChunkGenerator result = null;

        if (world == null) {
            throw new IllegalArgumentException("World name must be specified");
        }

        if (output == null) {
            output = Bukkit.getConsoleSender();
        }

        if (name != null) {
            String[] split = name.split(":", 2);
            String id = (split.length > 1) ? split[1] : null;
            Plugin plugin = Bukkit.getPluginManager().getPlugin(split[0]);

            if (plugin == null) {
                output.sendMessage("Could not set generator for world '" + world + "': Plugin '" + split[0] + "' does not exist");
            } else if (!plugin.isEnabled()) {
                output.sendMessage("Could not set generator for world '" + world + "': Plugin '" + plugin.getDescription().getFullName() + "' is not enabled");
            } else {
                result = plugin.getDefaultWorldGenerator(world, id);
            }
        }

        return result;
    }

    /**
     * Attempts to get the {@link BiomeProvider} with the given name.
     * <p>
     * If the biome provider is not found, null will be returned and a message
     * will be printed to the specified {@link CommandSender} explaining why.
     * <p>
     * The name must be in the "plugin:id" notation, or optionally just
     * "plugin", where "plugin" is the safe-name of a plugin and "id" is an
     * optional unique identifier for the biome provider you wish to request
     * from the plugin.
     *
     * @param world Name of the world this will be used for
     * @param name Name of the biome provider to retrieve
     * @param output Where to output if errors are present
     * @return Resulting biome provider, or null
     */
    @Nullable
    public static BiomeProvider getBiomeProviderForName(@NonNull String world, @Nullable String name, @Nullable CommandSender output) {
        BiomeProvider result = null;

        if (world == null) {
            throw new IllegalArgumentException("World name must be specified");
        }

        if (output == null) {
            output = Bukkit.getConsoleSender();
        }

        if (name != null) {
            String[] split = name.split(":", 2);
            String id = (split.length > 1) ? split[1] : null;
            Plugin plugin = Bukkit.getPluginManager().getPlugin(split[0]);

            if (plugin == null) {
                output.sendMessage("Could not set biome provider for world '" + world + "': Plugin '" + split[0] + "' does not exist");
            } else if (!plugin.isEnabled()) {
                output.sendMessage("Could not set set biome provider for world '" + world + "': Plugin '" + plugin.getDescription().getFullName() + "' is not enabled");
            } else {
                result = plugin.getDefaultBiomeProvider(world, id);
            }
        }

        return result;
    }

    // Paper start

    /**
     * Returns the current intent to keep the world loaded, @see {@link WorldCreator#keepSpawnLoaded(net.kyori.adventure.util.TriState)}
     *
     * @return the current tristate value
     */
    @NonNull
    public net.kyori.adventure.util.TriState keepSpawnLoaded() {
        return keepSpawnLoaded;
    }

    /**
     * Controls if a world should be kept loaded or not, default (NOT_SET) will use the servers standard
     * configuration, otherwise, will act as an override towards this setting
     *
     * @param keepSpawnLoaded the new value
     * @return This object, for chaining
     */
    @NonNull
    public WorldCreator keepSpawnLoaded(@NonNull net.kyori.adventure.util.TriState keepSpawnLoaded) {
        java.util.Objects.requireNonNull(keepSpawnLoaded, "keepSpawnLoaded");
        this.keepSpawnLoaded = keepSpawnLoaded;
        return this;
    }
}
