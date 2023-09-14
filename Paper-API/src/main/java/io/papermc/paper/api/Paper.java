package io.papermc.paper.api;

import com.google.common.collect.ImmutableList;
import io.papermc.paper.api.advancement.Advancement;
import io.papermc.paper.api.ban.BanList;
import io.papermc.paper.api.block.Block;
import io.papermc.paper.api.block.data.BlockData;
import io.papermc.paper.api.boss.*;
import io.papermc.paper.api.command.CommandException;
import io.papermc.paper.api.command.CommandSender;
import io.papermc.paper.api.command.ConsoleCommandSender;
import io.papermc.paper.api.entity.Boss;
import io.papermc.paper.api.entity.Entity;
import io.papermc.paper.api.entity.Player;
import io.papermc.paper.api.entity.SpawnCategory;
import io.papermc.paper.api.entity.ai.MobGoals;
import io.papermc.paper.api.inventory.*;
import io.papermc.paper.api.inventory.meta.ItemMeta;
import io.papermc.paper.api.inventory.recipe.Recipe;
import io.papermc.paper.api.location.Location;
import io.papermc.paper.api.loot.LootTable;
import io.papermc.paper.api.material.Material;
import io.papermc.paper.api.math.Position;
import io.papermc.paper.api.namespace.Keyed;
import io.papermc.paper.api.namespace.NamespacedKey;
import io.papermc.paper.api.permisson.Permissible;
import io.papermc.paper.api.player.OfflinePlayer;
import io.papermc.paper.api.profile.PlayerProfile;
import io.papermc.paper.api.scoreboard.Criteria;
import io.papermc.paper.api.scoreboard.ScoreboardManager;
import io.papermc.paper.api.threadedregion.scheduler.AsyncScheduler;
import io.papermc.paper.api.threadedregion.scheduler.GlobalRegionScheduler;
import io.papermc.paper.api.threadedregion.scheduler.RegionScheduler;
import io.papermc.paper.api.world.World;
import io.papermc.paper.api.world.WorldBorder;
import io.papermc.paper.api.world.WorldCreator;
import io.papermc.paper.api.world.generator.ChunkGenerator;
import io.papermc.paper.api.world.generator.structure.StructureType;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Contract;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Represents the Bukkit core, for version and Server singleton handling
 */
public final class Paper {
    private static Server server;

    /**
     * Static class cannot be initialized.
     */
    private Paper() {}

    /**
     * Gets the current {@link Server} singleton
     *
     * @return Server instance being ran
     */
    @NonNull
    public static Server getServer() {
        return server;
    }

    /**
     * Returns the de facto plugins directory, generally used for storing plugin jars to be loaded,
     * as well as their {@link Plugin#getDataFolder() data folders}.
     *
     * <p>Plugins should use {@link Plugin#getDataFolder()} rather than traversing this
     * directory manually when determining the location in which to store their data and configuration files.</p>
     *
     * @return plugins directory
     */
    @NonNull
    public static File getPluginsFolder() {
        return server.getPluginsFolder();
    }

    /**
     * Attempts to set the {@link Server} singleton.
     * <p>
     * This cannot be done if the Server is already set.
     *
     * @param server Server instance
     */
    public static void setServer(@NonNull Server server) {
        if (Paper.server != null) {
            throw new UnsupportedOperationException("Cannot redefine singleton Server");
        }

        Paper.server = server;
        server.getLogger().info(getVersionMessage());
    }
    /**
     * Gets message describing the version server is running.
     *
     * @return message describing the version server is running
     */
    @NonNull
    public static String getVersionMessage() {
        final var manifest = JarManifests.manifest(Paper.getServer().getClass());
        final String gitBranch = manifest == null ? null : manifest.getMainAttributes().getValue("Git-Branch");
        final String gitCommit = manifest == null ? null : manifest.getMainAttributes().getValue("Git-Commit");
        String branchMsg = " on " + gitBranch;
        if ("master".equals(gitBranch) || "main".equals(gitBranch)) {
            branchMsg = "";  // Don't show branch on main/master
        }
        return "This server is running " + getName() + " version " + getVersion() + " (Implementing API version " + getBukkitVersion() + ") (Git: " + gitCommit + branchMsg + ")";
    }

    /**
     * Gets the name of this server implementation.
     *
     * @return name of this server implementation
     */
    @NonNull
    public static String getName() {
        return server.getName();
    }

    /**
     * Gets the version string of this server implementation.
     *
     * @return version of this server implementation
     */
    @NonNull
    public static String getVersion() {
        return server.getVersion();
    }

    /**
     * Gets the Bukkit version that this server is running.
     *
     * @return version of Bukkit
     */
    @NonNull
    public static String getPaperVersion() {
        return server.getPaperVersion();
    }

    /**
     * Gets the version of game this server implements
     *
     * @return version of game
     */
    @NonNull
    public static String getMinecraftVersion() {
        return server.getMinecraftVersion();
    }

    /**
     * Gets a view of all currently logged in players. This {@linkplain
     * Collections#unmodifiableCollection(Collection) view} is a reused
     * object, making some operations like {@link Collection#size()}
     * zero-allocation.
     * <p>
     * The collection is a view backed by the internal representation, such
     * that, changes to the internal state of the server will be reflected
     * immediately. However, the reuse of the returned collection (identity)
     * is not strictly guaranteed for future or all implementations. Casting
     * the collection, or relying on interface implementations (like {@link
     * Serializable} or {@link List}), is deprecated.
     * <p>
     * Iteration behavior is undefined outside of self-contained main-thread
     * uses. Normal and immediate iterator use without consequences that
     * affect the collection are fully supported. The effects following
     * (non-exhaustive) {@link Entity#teleport(Location)}  teleportation},
     * {@link Player#setHealth(double) death}, and {@link Player#kick()}  kicking} are undefined. Any use of this collection from
     * asynchronous threads is unsafe.
     * <p>
     * For safe consequential iteration or mimicking the old array behavior,
     * using {@link Collection#toArray(Object[])} is recommended. For making
     * snapshots, {@link ImmutableList#copyOf(Collection)} is recommended.
     *
     * @return a view of currently online players.
     */
    @NonNull
    public static Collection<? extends Player> getOnlinePlayers() {
        return server.getOnlinePlayers();
    }

    /**
     * Get the maximum amount of players which can login to this server.
     *
     * @return the amount of players this server allows
     */
    public static int getMaxPlayers() {
        return server.getMaxPlayers();
    }

    /**
     * Set the maximum amount of players allowed to be logged in at once.
     *
     * @param maxPlayers The maximum amount of concurrent players
     */
    public static void setMaxPlayers(int maxPlayers) {
        server.setMaxPlayers(maxPlayers);
    }

    /**
     * Get the game port that the server runs on.
     *
     * @return the port number of this server
     */
    public static int getPort() {
        return server.getPort();
    }

    /**
     * Get the view distance from this server.
     *
     * @return the view distance from this server.
     */
    public static int getViewDistance() {
        return server.getViewDistance();
    }

    /**
     * Get the simulation distance from this server.
     *
     * @return the simulation distance from this server.
     */
    public static int getSimulationDistance() {
        return server.getSimulationDistance();
    }

    /**
     * Get the IP that this server is bound to, or empty string if not
     * specified.
     *
     * @return the IP string that this server is bound to, otherwise empty
     *     string
     */
    @NonNull
    public static String getIp() {
        return server.getIp();
    }

    /**
     * Get world type (level-type setting) for default world.
     *
     * @return the value of level-type (e.g. DEFAULT, FLAT, DEFAULT_1_1)
     */
    @NonNull
    public static String getWorldType() {
        return server.getWorldType();
    }

    /**
     * Get generate-structures setting.
     *
     * @return true if structure generation is enabled, false otherwise
     */
    public static boolean getGenerateStructures() {
        return server.getGenerateStructures();
    }

    /**
     * Get max world size.
     *
     * @return the maximum world size as specified for the server
     */
    public static int getMaxWorldSize() {
        return server.getMaxWorldSize();
    }

    /**
     * Gets whether this server allows the End or not.
     *
     * @return whether this server allows the End or not
     */
    public static boolean getAllowEnd() {
        return server.getAllowEnd();
    }

    /**
     * Gets whether this server allows the Nether or not.
     *
     * @return whether this server allows the Nether or not
     */
    public static boolean getAllowNether() {
        return server.getAllowNether();
    }

    @NonNull
    public static List<String> getInitialEnabledPacks() {
        return server.getInitialEnabledPacks();
    }

    @NonNull
    public static List<String> getInitialDisabledPacks() {
        return server.getInitialDisabledPacks();
    }

    /**
     * Gets the server resource pack uri, or empty string if not specified.
     *
     * @return the server resource pack uri, otherwise empty string
     */
    @NonNull
    public static String getResourcePack() {
        return server.getResourcePack();
    }

    /**
     * Gets the SHA-1 digest of the server resource pack, or empty string if
     * not specified.
     *
     * @return the SHA-1 digest of the server resource pack, otherwise empty
     *     string
     */
    @NonNull
    public static String getResourcePackHash() {
        return server.getResourcePackHash();
    }

    /**
     * Gets the custom prompt message to be shown when the server resource
     * pack is required, or empty string if not specified.
     *
     * @return the custom prompt message to be shown when the server resource,
     *     otherwise empty string
     */
    @NonNull
    public static String getResourcePackPrompt() {
        return server.getResourcePackPrompt();
    }

    /**
     * Gets whether the server resource pack is enforced.
     *
     * @return whether the server resource pack is enforced
     */
    public static boolean isResourcePackRequired() {
        return server.isResourcePackRequired();
    }

    /**
     * Gets whether this server has a whitelist or not.
     *
     * @return whether this server has a whitelist or not
     */
    public static boolean hasWhitelist() {
        return server.hasWhitelist();
    }

    /**
     * Sets if the server is whitelisted.
     *
     * @param value true for whitelist on, false for off
     */
    public static void setWhitelist(boolean value) {
        server.setWhitelist(value);
    }

    /**
     * Gets whether the server whitelist is enforced.
     *
     * If the whitelist is enforced, non-whitelisted players will be
     * disconnected when the server whitelist is reloaded.
     *
     * @return whether the server whitelist is enforced
     */
    public static boolean isWhitelistEnforced() {
        return server.isWhitelistEnforced();
    }

    /**
     * Sets if the server whitelist is enforced.
     *
     * If the whitelist is enforced, non-whitelisted players will be
     * disconnected when the server whitelist is reloaded.
     *
     * @param value true for enforced, false for not
     */
    public static void setWhitelistEnforced(boolean value) {
        server.setWhitelistEnforced(value);
    }

    /**
     * Gets a list of whitelisted players.
     *
     * @return a set containing all whitelisted players
     */
    @NonNull
    public static Set<OfflinePlayer> getWhitelistedPlayers() {
        return server.getWhitelistedPlayers();
    }

    /**
     * Reloads the whitelist from disk.
     */
    public static void reloadWhitelist() {
        server.reloadWhitelist();
    }


    /**
     * Gets the name of the update folder. The update folder is used to safely
     * update plugins at the right moment on a plugin load.
     * <p>
     * The update folder name is relative to the plugins folder.
     *
     * @return the name of the update folder
     */
    @NonNull
    public static String getUpdateFolder() {
        return server.getUpdateFolder();
    }

    /**
     * Gets the update folder. The update folder is used to safely update
     * plugins at the right moment on a plugin load.
     *
     * @return the update folder
     */
    @NonNull
    public static File getUpdateFolderFile() {
        return server.getUpdateFolderFile();
    }

    /**
     * Gets the value of the connection throttle setting.
     *
     * @return the value of the connection throttle setting
     */
    public static long getConnectionThrottle() {
        return server.getConnectionThrottle();
    }

    /**
     * Gets the default ticks per {@link SpawnCategory} spawns value.
     * <p>
     * <b>Example Usage:</b>
     * <ul>
     * <li>A value of 1 will mean the server will attempt to spawn {@link SpawnCategory} mobs
     *     every tick.
     * <li>A value of 400 will mean the server will attempt to spawn {@link SpawnCategory} mobs
     *     every 400th tick.
     * <li>A value below 0 will be reset back to Minecraft's default.
     * </ul>
     * <p>
     * <b>Note:</b> If set to 0, {@link SpawnCategory} mobs spawning will be disabled.
     * <p>
     * Minecraft default: 1.
     * <br>
     * <b>Note: </b> the {@link SpawnCategory#MISC} are not consider.
     *
     * @param spawnCategory the category of spawn
     * @return the default ticks per {@link SpawnCategory} mobs spawn value
     */
    public static int getTicksPerSpawns(@NonNull SpawnCategory spawnCategory) {
        return server.getTicksPerSpawns(spawnCategory);
    }

    /**
     * Gets a player object by the given username.
     * <p>
     * This method may not return objects for offline players.
     *
     * @param name the name to look up
     * @return a player if one was found, null otherwise
     */
    @Nullable
    public static Player getPlayer(@NonNull String name) {
        return server.getPlayer(name);
    }

    /**
     * Gets the player with the exact given name, case insensitive.
     *
     * @param name Exact name of the player to retrieve
     * @return a player object if one was found, null otherwise
     */
    @Nullable
    public static Player getPlayerExact(@NonNull String name) {
        return server.getPlayerExact(name);
    }

    /**
     * Attempts to match any players with the given name, and returns a list
     * of all possibly matches.
     * <p>
     * This list is not sorted in any particular order. If an exact match is
     * found, the returned list will only contain a single result.
     *
     * @param name the (partial) name to match
     * @return list of all possible players
     */
    @NonNull
    public static List<Player> matchPlayer(@NonNull String name) {
        return server.matchPlayer(name);
    }

    /**
     * Gets the player with the given UUID.
     *
     * @param id UUID of the player to retrieve
     * @return a player object if one was found, null otherwise
     */
    @Nullable
    public static Player getPlayer(@NonNull UUID id) {
        return server.getPlayer(id);
    }

    // Paper start
    /**
     * Gets the unique ID of the player currently known as the specified player name
     * In Offline Mode, will return an Offline UUID
     *
     * @param playerName the player name to look up the unique ID for
     * @return A UUID, or null if that player name is not registered with Minecraft and the server is in online mode
     */
    @Nullable
    public static UUID getPlayerUniqueId(@NonNull String playerName) {
        return server.getPlayerUniqueId(playerName);
    }
    // Paper end

    /**
     * Gets the plugin manager for interfacing with plugins.
     *
     * @return a plugin manager for this Server instance
     */
    @NonNull
    public static PluginManager getPluginManager() {
        return server.getPluginManager();
    }

    /**
     * Gets the scheduler for managing scheduled events.
     *
     * @return a scheduling service for this server
     */
    @NonNull
    public static PaperScheduler getScheduler() {
        return server.getScheduler();
    }

    /**
     * Gets a services manager.
     *
     * @return s services manager
     */
    @NonNull
    public static ServicesManager getServicesManager() {
        return server.getServicesManager();
    }

    /**
     * Gets a list of all worlds on this server.
     *
     * @return a list of worlds
     */
    @NonNull
    public static List<World> getWorlds() {
        return server.getWorlds();
    }

    // Paper start
    /**
     * Gets whether the worlds are being ticked right now.
     *
     * @return true if the worlds are being ticked, false otherwise.
     */
    public static boolean isTickingWorlds(){
        return server.isTickingWorlds();
    }
    // Paper end

    /**
     * Creates or loads a world with the given name using the specified
     * options.
     * <p>
     * If the world is already loaded, it will just return the equivalent of
     * getWorld(creator.name()).
     * <p>
     * Do note that un/loading worlds mid-tick may have potential side effects, we strongly recommend
     * ensuring that you're not un/loading worlds midtick by checking {@link Paper#isTickingWorlds()}
     *
     * @param creator the options to use when creating the world
     * @return newly created or loaded world
     */
    @Nullable
    public static World createWorld(@NonNull WorldCreator creator) {
        return server.createWorld(creator);
    }

    /**
     * Unloads a world with the given name.
     * <p>
     * Do note that un/loading worlds mid-tick may have potential side effects, we strongly recommend
     * ensuring that you're not un/loading worlds midtick by checking {@link Paper#isTickingWorlds()}
     *
     * @param name Name of the world to unload
     * @param save whether to save the chunks before unloading
     * @return true if successful, false otherwise
     */
    public static boolean unloadWorld(@NonNull String name, boolean save) {
        return server.unloadWorld(name, save);
    }

    /**
     * Unloads the given world.
     * <p>
     * Do note that un/loading worlds mid-tick may have potential side effects, we strongly recommend
     * ensuring that you're not un/loading worlds midtick by checking {@link Paper#isTickingWorlds()}
     *
     * @param world the world to unload
     * @param save whether to save the chunks before unloading
     * @return true if successful, false otherwise
     */
    public static boolean unloadWorld(@NonNull World world, boolean save) {
        return server.unloadWorld(world, save);
    }

    /**
     * Gets the world with the given name.
     *
     * @param name the name of the world to retrieve
     * @return a world with the given name, or null if none exists
     */
    @Nullable
    public static World getWorld(@NonNull String name) {
        return server.getWorld(name);
    }

    /**
     * Gets the world from the given Unique ID.
     *
     * @param uid a unique-id of the world to retrieve
     * @return a world with the given Unique ID, or null if none exists
     */
    @Nullable
    public static World getWorld(@NonNull UUID uid) {
        return server.getWorld(uid);
    }

    /**
     * Gets the world from the given NamespacedKey
     *
     * @param worldKey the NamespacedKey of the world to retrieve
     * @return a world with the given NamespacedKey, or null if none exists
     */
    @Nullable
    public static World getWorld(@NonNull NamespacedKey worldKey) {
        return server.getWorld(worldKey);
    }

    /**
     * Create a new virtual {@link WorldBorder}.
     *
     * @return the created world border instance
     *
     * @see Player#setWorldBorder(WorldBorder)
     */
    @NonNull
    public static WorldBorder createWorldBorder() {
        return server.createWorldBorder();
    }

    /**
     * Gets the map from the given item ID.
     *
     * @param id the id of the map to get
     * @return a map view if it exists, or null otherwise
     */
    //@Deprecated // Paper - Not a magic value
    @Nullable
    public static MapView getMap(int id) {
        return server.getMap(id);
    }

    /**
     * Create a new map with an automatically assigned ID.
     *
     * @param world the world the map will belong to
     * @return a newly created map view
     */
    @NonNull
    public static MapView createMap(@NonNull World world) {
        return server.createMap(world);
    }

    /**
     * Create a new explorer map targeting the closest nearby structure of a
     * given {@link StructureType}.
     * <br>
     * This method uses implementation default values for radius and
     * findUnexplored (usually 100, true).
     *
     * @param world the world the map will belong to
     * @param location the origin location to find the nearest structure
     * @param structureType the type of structure to find
     * @param mapIcon the map icon to use on the map
     * @return a newly created item stack or null if it can't find a location
     *
     * @see World#locateNearestStructure(Location, StructureType, int, boolean)
     */
    public static @Nullable ItemStack createExplorerMap(@NonNull World world, @NonNull Location location, @NonNull StructureType structureType, @NonNull MapCursor.Type mapIcon) {
        return server.createExplorerMap(world, location, structureType, mapIcon);
    }

    /**
     * Create a new explorer map targeting the closest nearby structure of a
     * given {@link StructureType}.
     *
     * @param world the world the map will belong to
     * @param location the origin location to find the nearest structure
     * @param structureType the type of structure to find
     * @param mapIcon the map icon to use on the map
     * @param radius radius to search, see World#locateNearestStructure for more
     *               information
     * @param findUnexplored whether to find unexplored structures
     * @return the newly created item stack or null if it can't find a location
     *
     * @see World#locateNearestStructure(Location, StructureType, int, boolean)
     */
    public static @Nullable ItemStack createExplorerMap(@NonNull World world, @NonNull Location location, @NonNull StructureType structureType, @NonNull MapCursor.Type mapIcon, int radius, boolean findUnexplored) {
        return server.createExplorerMap(world, location, structureType, mapIcon, radius, findUnexplored);
    }
    // Paper end

    /**
     * Reloads the server, refreshing settings and plugin information.
     */
    public static void reload() {
        server.reload();
    }

    /**
     * Reload only the Minecraft data for the server. This includes custom
     * advancements and loot tables.
     */
    public static void reloadData() {
        server.reloadData();
    }

    /**
     * Updates all advancement, tag, and recipe data for all connected clients.
     * Useful for updating clients to new advancements/recipes/tags.
     * @see #updateRecipes()
     */
    public static void updateResources() {
        server.updateResources();
    }

    /**
     * Updates recipe data and the recipe book for all connected clients. Useful for
     * updating clients to new recipes.
     * @see #updateResources()
     */
    public static void updateRecipes() {
        server.updateRecipes();
    }

    /**
     * Returns the primary logger associated with this server instance.
     *
     * @return Logger associated with this server
     */
    @NonNull
    public static Logger getLogger() {
        return server.getLogger();
    }

    /**
     * Gets a {@link PluginCommand} with the given name or alias.
     *
     * @param name the name of the command to retrieve
     * @return a plugin command if found, null otherwise
     */
    @Nullable
    public static PluginCommand getPluginCommand(@NonNull String name) {
        return server.getPluginCommand(name);
    }

    /**
     * Writes loaded players to disk.
     */
    public static void savePlayers() {
        server.savePlayers();
    }

    /**
     * Dispatches a command on this server, and executes it if found.
     *
     * @param sender the apparent sender of the command
     * @param commandLine the command + arguments. Example: <code>test abc
     *     123</code>
     * @return returns false if no target is found
     * @throws CommandException thrown when the executor for the given command
     *     fails with an unhandled exception
     */
    public static boolean dispatchCommand(@NonNull CommandSender sender, @NonNull String commandLine) throws CommandException {
        return server.dispatchCommand(sender, commandLine);
    }

    /**
     * Adds a recipe to the crafting manager.
     *
     * @param recipe the recipe to add
     * @return true if the recipe was added, false if it wasn't for some
     *     reason
     */
    @Contract("null -> false")
    public static boolean addRecipe(@Nullable Recipe recipe) {
        return server.addRecipe(recipe);
    }

    /**
     * Adds a recipe to the crafting manager.
     *
     * @param recipe the recipe to add
     * @param resendRecipes true to update the client with the full set of recipes
     * @return true if the recipe was added, false if it wasn't for some reason
     */
    @Contract("null, _ -> false")
    public static boolean addRecipe(@Nullable Recipe recipe, boolean resendRecipes) {
        return server.addRecipe(recipe, resendRecipes);
    }

    /**
     * Get a list of all recipes for a given item. The stack size is ignored
     * in comparisons. If the durability is -1, it will match any data value.
     *
     * @param result the item to match against recipe results
     * @return a list of recipes with the given result
     */
    @NonNull
    public static List<Recipe> getRecipesFor(@NonNull ItemStack result) {
        return server.getRecipesFor(result);
    }

    /**
     * Get the {@link Recipe} for the given key.
     *
     * @param recipeKey the key of the recipe to return
     * @return the recipe for the given key or null.
     */
    @Nullable
    public static Recipe getRecipe(@NonNull NamespacedKey recipeKey) {
        return server.getRecipe(recipeKey);
    }

    /**
     * Get the {@link Recipe} for the list of ItemStacks provided.
     *
     * <p>The list is formatted as a crafting matrix where the index follow
     * the pattern below:</p>
     *
     * <pre>
     * [ 0 1 2 ]
     * [ 3 4 5 ]
     * [ 6 7 8 ]
     * </pre>
     *
     * <p>NOTE: This method will not modify the provided ItemStack array, for that, use
     * {@link #craftItem(ItemStack[], World, Player)}.</p>
     *
     * @param craftingMatrix list of items to be crafted from.
     *                       Must not contain more than 9 items.
     * @param world The world the crafting takes place in.
     * @return the {@link Recipe} resulting from the given crafting matrix.
     */
    @Nullable
    public static Recipe getCraftingRecipe(@NonNull ItemStack[] craftingMatrix, @NonNull World world) {
        return server.getCraftingRecipe(craftingMatrix, world);
    }

    /**
     * Get the crafted item using the list of {@link ItemStack} provided.
     *
     * <p>The list is formatted as a crafting matrix where the index follow
     * the pattern below:</p>
     *
     * <pre>
     * [ 0 1 2 ]
     * [ 3 4 5 ]
     * [ 6 7 8 ]
     * </pre>
     *
     * <p>The {@link World} and {@link Player} arguments are required to fulfill the Bukkit Crafting
     * events.</p>
     *
     * <p>Calls {@link PrepareItemCraftEvent} to imitate the {@link Player}
     * initiating the crafting event.</p>
     *
     * @param craftingMatrix list of items to be crafted from.
     *                       Must not contain more than 9 items.
     * @param world The world the crafting takes place in.
     * @param player The player to imitate the crafting event on.
     * @return the {@link ItemStack} resulting from the given crafting matrix, if no recipe is found
     * an ItemStack of {@link Material#AIR} is returned.
     */
    @NonNull
    public static ItemStack craftItem(@NonNull ItemStack[] craftingMatrix, @NonNull World world, @NonNull Player player) {
        return server.craftItem(craftingMatrix, world, player);
    }

    /**
     * Get an iterator through the list of crafting recipes.
     *
     * @return an iterator
     */
    @NonNull
    public static Iterator<Recipe> recipeIterator() {
        return server.recipeIterator();
    }

    /**
     * Clears the list of crafting recipes.
     */
    public static void clearRecipes() {
        server.clearRecipes();
    }

    /**
     * Resets the list of crafting recipes to the default.
     */
    public static void resetRecipes() {
        server.resetRecipes();
    }

    /**
     * Remove a recipe from the server.
     *
     * <b>Note that removing a recipe may cause permanent loss of data
     * associated with that recipe (eg whether it has been discovered by
     * players).</b>
     *
     * @param key NamespacedKey of recipe to remove.
     * @return True if recipe was removed
     */
    public static boolean removeRecipe(@NonNull NamespacedKey key) {
        return server.removeRecipe(key);
    }

    /**
     * Remove a recipe from the server.
     * <p>
     * <b>Note that removing a recipe may cause permanent loss of data
     * associated with that recipe (eg whether it has been discovered by
     * players).</b>
     *
     * @param key NamespacedKey of recipe to remove.
     * @param resendRecipes true to update all clients on the new recipe list.
     *                      Will only update if a recipe was actually removed
     * @return True if recipe was removed
     */
    public static boolean removeRecipe(@NonNull NamespacedKey key, boolean resendRecipes) {
        return server.removeRecipe(key, resendRecipes);
    }

    /**
     * Gets a list of command aliases defined in the server properties.
     *
     * @return a map of aliases to command names
     */
    @NonNull
    public static Map<String, String[]> getCommandAliases() {
        return server.getCommandAliases();
    }

    /**
     * Gets the radius, in blocks, around each worlds spawn point to protect.
     *
     * @return spawn radius, or 0 if none
     */
    public static int getSpawnRadius() {
        return server.getSpawnRadius();
    }

    /**
     * Sets the radius, in blocks, around each worlds spawn point to protect.
     *
     * @param value new spawn radius, or 0 if none
     */
    public static void setSpawnRadius(int value) {
        server.setSpawnRadius(value);
    }

    /**
     * Gets whether the server only allow players with Mojang-signed public key
     * to join
     *
     * @return true if only Mojang-signed players can join, false otherwise
     */
    public static boolean isEnforcingSecureProfiles() {
        return server.isEnforcingSecureProfiles();
    }

    /**
     * Gets whether the Server hide online players in server status.
     *
     * @return true if the server hide online players, false otherwise
     */
    public static boolean getHideOnlinePlayers() {
        return server.getHideOnlinePlayers();
    }

    /**
     * Gets whether the Server is in online mode or not.
     *
     * @return true if the server authenticates clients, false otherwise
     */
    public static boolean getOnlineMode() {
        return server.getOnlineMode();
    }

    /**
     * Gets whether this server allows flying or not.
     *
     * @return true if the server allows flight, false otherwise
     */
    public static boolean getAllowFlight() {
        return server.getAllowFlight();
    }

    /**
     * Gets whether the server is in hardcore mode or not.
     *
     * @return true if the server mode is hardcore, false otherwise
     */
    public static boolean isHardcore() {
        return server.isHardcore();
    }

    /**
     * Shutdowns the server, stopping everything.
     */
    public static void shutdown() {
        server.shutdown();
    }

    /**
     * Broadcast a message to all players.
     * <p>
     * This is the same as calling {@link #broadcast(Component,
     * String)} with the {@link Server#BROADCAST_CHANNEL_USERS} permission.
     *
     * @param message the message
     * @return the number of players
     */
    public static int broadcast(@NonNull Component message) {
        return server.broadcast(message);
    }
    /**
     * Broadcasts the specified message to every user with the given
     * permission name.
     *
     * @param message message to broadcast
     * @param permission the required permission {@link Permissible
     *     permissibles} must have to receive the broadcast
     * @return number of message recipients
     */
    public static int broadcast(@NonNull Component message, @NonNull String permission) {
        return server.broadcast(message, permission);
    }

    /**
     * Gets the player by the given name, regardless if they are offline or
     * online.
     * <p>
     * This will not make a web request to get the UUID for the given name,
     * thus this method will not block. However this method will return
     * {@code null} if the player is not cached.
     * </p>
     *
     * @param name the name of the player to retrieve
     * @return an offline player if cached, {@code null} otherwise
     * @see #getOfflinePlayer(java.util.UUID)
     */
    @Nullable
    public static OfflinePlayer getOfflinePlayerIfCached(@NonNull String name) {
        return server.getOfflinePlayerIfCached(name);
    }

    /**
     * Gets the player by the given UUID, regardless if they are offline or
     * online.
     * <p>
     * This will return an object even if the player does not exist. To this
     * method, all players will exist.
     *
     * @param id the UUID of the player to retrieve
     * @return an offline player
     */
    @NonNull
    public static OfflinePlayer getOfflinePlayer(@NonNull UUID id) {
        return server.getOfflinePlayer(id);
    }


    /**
     * Gets a set containing all current IPs that are banned.
     *
     * @return a set containing banned IP addresses
     */
    @NonNull
    public static Set<String> getIPBans() {
        return server.getIPBans();
    }

    /**
     * Bans the specified address from the server.
     *
     * @param address the IP address to ban
     */
    public static void banIP(@NonNull InetAddress address) {
        server.banIP(address);
    }

    /**
     * Unbans the specified address from the server.
     *
     * @param address the IP address to unban
     */
    public static void unbanIP(@NonNull InetAddress address) {
        server.unbanIP(address);
    }

    /**
     * Gets a set containing all banned players.
     *
     * @return a set containing banned players
     */
    @NonNull
    public static Set<OfflinePlayer> getBannedPlayers() {
        return server.getBannedPlayers();
    }

    /**
     * Gets a ban list for the supplied type.
     *
     * @param type the type of list to fetch, cannot be null
     * @param <T> The ban target
     *
     * @return a ban list of the specified type
     */
    @NonNull
    public static <T extends BanList<?>> T getBanList(@NonNull BanList.Type type) {
        return server.getBanList(type);
    }

    /**
     * Gets a set containing all player operators.
     *
     * @return a set containing player operators
     */
    @NonNull
    public static Set<OfflinePlayer> getOperators() {
        return server.getOperators();
    }

    /**
     * Gets the default {@link GameMode} for new players.
     *
     * @return the default game mode
     */
    @NonNull
    public static GameMode getDefaultGameMode() {
        return server.getDefaultGameMode();
    }

    /**
     * Sets the default {@link GameMode} for new players.
     *
     * @param mode the new game mode
     */
    public static void setDefaultGameMode(@NonNull GameMode mode) {
        server.setDefaultGameMode(mode);
    }

    /**
     * Gets a {@link ConsoleCommandSender} that may be used as an input source
     * for this server.
     *
     * @return a console command sender
     */
    @NonNull
    public static ConsoleCommandSender getConsoleSender() {
        return server.getConsoleSender();
    }

    // Paper start
    /**
     * Creates a special {@link CommandSender} which redirects command feedback (in the form of chat messages) to the
     * specified listener. The returned sender will have the same effective permissions as {@link #getConsoleSender()}.
     *
     * @param feedback feedback listener
     * @return a command sender
     */
    @NonNull
    public static CommandSender createCommandSender(final @NonNull Consumer<? super Component> feedback) {
        return server.createCommandSender(feedback);
    }
    // Paper end

    /**
     * Gets the folder that contains all of the various {@link World}s.
     *
     * @return folder that contains all worlds
     */
    @NonNull
    public static File getWorldContainer() {
        return server.getWorldContainer();
    }

    /**
     * Gets every player that has ever played on this server.
     * <p>
     * <b>This method can be expensive as it loads all the player data files from the disk.</b>
     *
     * @return an array containing all previous players
     */
    @NonNull
    public static OfflinePlayer[] getOfflinePlayers() {
        return server.getOfflinePlayers();
    }

    /**
     * Gets the {@link Messenger} responsible for this server.
     *
     * @return messenger responsible for this server
     */
    @NonNull
    public static Messenger getMessenger() {
        return server.getMessenger();
    }

    /**
     * Gets the {@link HelpMap} providing help topics for this server.
     *
     * @return a help map for this server
     */
    @NonNull
    public static HelpMap getHelpMap() {
        return server.getHelpMap();
    }

    /**
     * Creates an empty inventory with the specified type. If the type
     * is {@link InventoryType#CHEST}, the new inventory has a size of 27;
     * otherwise the new inventory has the normal size for its type.
     * <br>
     * {@link InventoryType#WORKBENCH} will not process crafting recipes if
     * created with this method. Use
     * {@link Player#openWorkbench(Location, boolean)} instead.
     * <br>
     * {@link InventoryType#ENCHANTING} will not process {@link ItemStack}s
     * for possible enchanting results. Use
     * {@link Player#openEnchanting(Location, boolean)} instead.
     *
     * @param owner the holder of the inventory, or null to indicate no holder
     * @param type the type of inventory to create
     * @return a new inventory
     * @throws IllegalArgumentException if the {@link InventoryType} cannot be
     * viewed.
     *
     * @see InventoryType#isCreatable()
     */
    @NonNull
    public static Inventory createInventory(@Nullable InventoryHolder owner, @NonNull InventoryType type) {
        return server.createInventory(owner, type);
    }

    /**
     * Creates an empty inventory with the specified type and title. If the type
     * is {@link InventoryType#CHEST}, the new inventory has a size of 27;
     * otherwise the new inventory has the normal size for its type.<br>
     * It should be noted that some inventory types do not support titles and
     * may not render with said titles on the Minecraft client.
     * <br>
     * {@link InventoryType#WORKBENCH} will not process crafting recipes if
     * created with this method. Use
     * {@link Player#openWorkbench(Location, boolean)} instead.
     * <br>
     * {@link InventoryType#ENCHANTING} will not process {@link ItemStack}s
     * for possible enchanting results. Use
     * {@link Player#openEnchanting(Location, boolean)} instead.
     *
     * @param owner The holder of the inventory; can be null if there's no holder.
     * @param type The type of inventory to create.
     * @param title The title of the inventory, to be displayed when it is viewed.
     * @return The new inventory.
     * @throws IllegalArgumentException if the {@link InventoryType} cannot be
     * viewed.
     *
     * @see InventoryType#isCreatable()
     */
    @NonNull
    public static Inventory createInventory(@Nullable InventoryHolder owner, @NonNull InventoryType type, @NonNull Component title) {
        return server.createInventory(owner, type, title);
    }

    /**
     * Creates an empty inventory of type {@link InventoryType#CHEST} with the
     * specified size.
     *
     * @param owner the holder of the inventory, or null to indicate no holder
     * @param size a multiple of 9 as the size of inventory to create
     * @return a new inventory
     * @throws IllegalArgumentException if the size is not a multiple of 9
     */
    @NonNull
    public static Inventory createInventory(@Nullable InventoryHolder owner, int size) throws IllegalArgumentException {
        return server.createInventory(owner, size);
    }

    /**
     * Creates an empty inventory of type {@link InventoryType#CHEST} with the
     * specified size and title.
     *
     * @param owner the holder of the inventory, or null to indicate no holder
     * @param size a multiple of 9 as the size of inventory to create
     * @param title the title of the inventory, displayed when inventory is
     *     viewed
     * @return a new inventory
     * @throws IllegalArgumentException if the size is not a multiple of 9
     */
    @NonNull
    public static Inventory createInventory(@Nullable InventoryHolder owner, int size, @NonNull Component title) throws IllegalArgumentException {
        return server.createInventory(owner, size, title);
    }

    // Paper start
    /**
     * Creates an empty merchant.
     *
     * @param title the title of the corresponding merchant inventory, displayed
     * when the merchant inventory is viewed
     * @return a new merchant
     */
    public static @NonNull Merchant createMerchant(@Nullable Component title) {
        return server.createMerchant(title);
    }

    /**
     * Gets the amount of consecutive neighbor updates before skipping
     * additional ones.
     *
     * @return the amount of consecutive neighbor updates, if the value is
     * negative then the limit it's not used
     */
    public static int getMaxChainedNeighborUpdates() {
        return server.getMaxChainedNeighborUpdates();
    }


    /**
     * Gets user-specified limit for number of {@link SpawnCategory} mobs that can spawn in
     * a chunk.
     *
     * <b>Note: the {@link SpawnCategory#MISC} are not consider.</b>
     *
     * @param spawnCategory the category spawn
     * @return the {@link SpawnCategory} spawn limit
     */
    public static int getSpawnLimit(@NonNull SpawnCategory spawnCategory) {
        return server.getSpawnLimit(spawnCategory);
    }

    /**
     * Checks the current thread against the expected primary thread for the
     * server.
     * <p>
     * <b>Note:</b> this method should not be used to indicate the current
     * synchronized state of the runtime. A current thread matching the main
     * thread indicates that it is synchronized, but a mismatch <b>does not
     * preclude</b> the same assumption.
     *
     * @return true if the current thread matches the expected primary thread,
     *     false otherwise
     */
    public static boolean isPrimaryThread() {
        return server.isPrimaryThread();
    }

    /**
     * Gets the message that is displayed on the server list.
     *
     * @return the server's MOTD
     */
    @NonNull public static Component motd() {
        return server.motd();
    }

    /**
     * Set the message that is displayed on the server list.
     *
     * @param motd The message to be displayed
     */
    public static void motd(final @NonNull Component motd) {
        server.motd(motd);
    }

    /**
     * Gets the default message that is displayed when the server is stopped.
     *
     * @return the shutdown message
     */
    public static @Nullable Component shutdownMessage() {
        return server.shutdownMessage();
    }

    /**
     * Gets the current warning state for the server.
     *
     * @return the configured warning state
     */
    @NonNull
    public static WarningState getWarningState() {
        return server.getWarningState();
    }

    /**
     * Gets the instance of the item factory (for {@link ItemMeta}).
     *
     * @return the item factory
     * @see ItemFactory
     */
    @NonNull
    public static ItemFactory getItemFactory() {
        return server.getItemFactory();
    }

    /**
     * Gets the instance of the scoreboard manager.
     * <p>
     * This will only exist after the first world has loaded.
     *
     * @return the scoreboard manager or null if no worlds are loaded.
     */
    @NonNull
    public static ScoreboardManager getScoreboardManager() {
        return server.getScoreboardManager();
    }

    /**
     * Get (or create) a new {@link Criteria} by its name.
     *
     * @param name the criteria name
     * @return the criteria
     * @see Criteria Criteria for a list of constants
     */
    @NonNull
    public static Criteria getScoreboardCriteria(@NonNull String name) {
        return server.getScoreboardCriteria(name);
    }

    /**
     * Gets an instance of the server's default server-icon.
     *
     * @return the default server-icon; null values may be used by the
     *     implementation to indicate no defined icon, but this behavior is
     *     not guaranteed
     */
    @Nullable
    public static CachedServerIcon getServerIcon() {
        return server.getServerIcon();
    }

    /**
     * Loads an image from a file, and returns a cached image for the specific
     * server-icon.
     * <p>
     * Size and type are implementation defined. An incompatible file is
     * guaranteed to throw an implementation-defined {@link Exception}.
     *
     * @param file the file to load the from
     * @return a cached server-icon that can be used for a {@link
     *     ServerListPingEvent#setServerIcon(CachedServerIcon)}
     * @throws IllegalArgumentException if image is null
     * @throws Exception if the image does not meet current server server-icon
     *     specifications
     */
    @NonNull
    public static CachedServerIcon loadServerIcon(@NonNull File file) throws IllegalArgumentException, Exception {
        return server.loadServerIcon(file);
    }

    /**
     * Creates a cached server-icon for the specific image.
     * <p>
     * Size and type are implementation defined. An incompatible file is
     * guaranteed to throw an implementation-defined {@link Exception}.
     *
     * @param image the image to use
     * @return a cached server-icon that can be used for a {@link
     *     ServerListPingEvent#setServerIcon(CachedServerIcon)}
     * @throws IllegalArgumentException if image is null
     * @throws Exception if the image does not meet current server
     *     server-icon specifications
     */
    @NonNull
    public static CachedServerIcon loadServerIcon(@NonNull BufferedImage image) throws IllegalArgumentException, Exception {
        return server.loadServerIcon(image);
    }

    /**
     * Set the idle kick timeout. Any players idle for the specified amount of
     * time will be automatically kicked.
     * <p>
     * A value of 0 will disable the idle kick timeout.
     *
     * @param threshold the idle timeout in minutes
     */
    public static void setIdleTimeout(int threshold) {
        server.setIdleTimeout(threshold);
    }

    /**
     * Gets the idle kick timeout.
     *
     * @return the idle timeout in minutes
     */
    public static int getIdleTimeout() {
        return server.getIdleTimeout();
    }

    /**
     * Create a ChunkData for use in a generator.
     *
     * See {@link ChunkGenerator#generateChunkData(World, Random, int, int, BiomeGrid)}
     *
     * @param world the world to create the ChunkData for
     * @return a new ChunkData for the world
     *
     */
    public static ChunkGenerator.@NonNull ChunkData createChunkData(@NonNull World world) {
        return server.createChunkData(world);
    }

    /**
     * Creates a boss bar instance to display to players. The progress
     * defaults to 1.0
     *
     * @param title the title of the boss bar
     * @param color the color of the boss bar
     * @param style the style of the boss bar
     * @param flags an optional list of flags to set on the boss bar
     * @return the created boss bar
     */
    @NonNull
    public static BossBar createBossBar(@Nullable String title, @NonNull BarColor color, @NonNull BarStyle style, @NonNull BarFlag... flags) {
        return server.createBossBar(title, color, style, flags);
    }

    /**
     * Creates a boss bar instance to display to players. The progress defaults
     * to 1.0.
     * <br>
     * This instance is added to the persistent storage of the server and will
     * be editable by commands and restored after restart.
     *
     * @param key the key of the boss bar that is used to access the boss bar
     * @param title the title of the boss bar
     * @param color the color of the boss bar
     * @param style the style of the boss bar
     * @param flags an optional list of flags to set on the boss bar
     * @return the created boss bar
     */
    @NonNull
    public static KeyedBossBar createBossBar(@NonNull NamespacedKey key, @Nullable String title, @NonNull BarColor color, @NonNull BarStyle style, @NonNull BarFlag... flags) {
        return server.createBossBar(key, title, color, style, flags);
    }

    /**
     * Gets an unmodifiable iterator through all persistent bossbars.
     * <ul>
     *   <li><b>not</b> bound to a {@link Boss}</li>
     *   <li>
     *     <b>not</b> created using
     *     {@link #createBossBar(String, BarColor, BarStyle, BarFlag...)}
     *   </li>
     * </ul>
     *
     * e.g. bossbars created using the bossbar command
     *
     * @return a bossbar iterator
     */
    @NonNull
    public static Iterator<KeyedBossBar> getBossBars() {
        return server.getBossBars();
    }

    /**
     * Gets the {@link KeyedBossBar} specified by this key.
     * <ul>
     *   <li><b>not</b> bound to a {@link Boss}</li>
     *   <li>
     *     <b>not</b> created using
     *     {@link #createBossBar(String, BarColor, BarStyle, BarFlag...)}
     *   </li>
     * </ul>
     *
     * e.g. bossbars created using the bossbar command
     *
     * @param key unique bossbar key
     * @return bossbar or null if not exists
     */
    @Nullable
    public static KeyedBossBar getBossBar(@NonNull NamespacedKey key) {
        return server.getBossBar(key);
    }

    /**
     * Removes a {@link KeyedBossBar} specified by this key.
     * <ul>
     *   <li><b>not</b> bound to a {@link Boss}</li>
     *   <li>
     *     <b>not</b> created using
     *     {@link #createBossBar(String, BarColor, BarStyle, BarFlag...)}
     *   </li>
     * </ul>
     *
     * e.g. bossbars created using the bossbar command
     *
     * @param key unique bossbar key
     * @return true if removal succeeded or false
     */
    public static boolean removeBossBar(@NonNull NamespacedKey key) {
        return server.removeBossBar(key);
    }

    /**
     * Gets an entity on the server by its UUID
     *
     * @param uuid the UUID of the entity
     * @return the entity with the given UUID, or null if it isn't found
     */
    @Nullable
    public static Entity getEntity(@NonNull UUID uuid) {
        return server.getEntity(uuid);
    }

    /**
     * Gets the current server TPS
     * @return current server TPS (1m, 5m, 15m in Paper-Server)
     */
    @NonNull
    public static double[] getTPS() {
        return server.getTPS();
    }

    /**
     * Get a sample of the servers last tick times (in nanos)
     *
     * @return A sample of the servers last tick times (in nanos)
     */
    @NonNull
    public static long[] getTickTimes() {
        return server.getTickTimes();
    }

    /**
     * Get the average tick time (in millis)
     *
     * @return Average tick time (in millis)
     */
    public static double getAverageTickTime() {
        return server == null ? 0D : server.getAverageTickTime();
    }

    /**
     * Get the advancement specified by this key.
     *
     * @param key unique advancement key
     * @return advancement or null if not exists
     */
    @Nullable
    public static Advancement getAdvancement(@NonNull NamespacedKey key) {
        return server.getAdvancement(key);
    }

    /**
     * Get an iterator through all advancements. Advancements cannot be removed
     * from this iterator,
     *
     * @return an advancement iterator
     */
    @NonNull
    public static Iterator<Advancement> advancementIterator() {
        return server.advancementIterator();
    }

    /**
     * Creates a new {@link BlockData} instance for the specified Material, with
     * all properties initialized to unspecified defaults.
     *
     * @param material the material
     * @return new data instance
     */
    @NonNull
    public static BlockData createBlockData(@NonNull Material material) {
        return server.createBlockData(material);
    }

    /**
     * Creates a new {@link BlockData} instance for the specified Material, with
     * all properties initialized to unspecified defaults.
     *
     * @param material the material
     * @param consumer consumer to run on new instance before returning
     * @return new data instance
     */
    @NonNull
    public static BlockData createBlockData(@NonNull Material material, @Nullable Consumer<BlockData> consumer) {
        return server.createBlockData(material, consumer);
    }

    /**
     * Creates a new {@link BlockData} instance with material and properties
     * parsed from provided data.
     *
     * @param data data string
     * @return new data instance
     * @throws IllegalArgumentException if the specified data is not valid
     */
    @NonNull
    public static BlockData createBlockData(@NonNull String data) throws IllegalArgumentException {
        return server.createBlockData(data);
    }

    /**
     * Creates a new {@link BlockData} instance for the specified Material, with
     * all properties initialized to unspecified defaults, except for those
     * provided in data.
     *
     * @param material the material
     * @param data data string
     * @return new data instance
     * @throws IllegalArgumentException if the specified data is not valid
     */
    @NonNull
    @Contract("null, null -> fail")
    public static BlockData createBlockData(@Nullable Material material, @Nullable String data) throws IllegalArgumentException {
        return server.createBlockData(material, data);
    }

    /**
     * Gets a tag which has already been defined within the server. Plugins are
     * suggested to use the concrete tags in {@link Tag} rather than this method
     * which makes no guarantees about which tags are available, and may also be
     * less performant due to lack of caching.
     * <br>
     * Tags will be searched for in an implementation specific manner, but a
     * path consisting of namespace/tags/registry/key is expected.
     * <br>
     * Server implementations are allowed to handle only the registries
     * indicated in {@link Tag}.
     *
     * @param <T> type of the tag
     * @param registry the tag registry to look at
     * @param tag the name of the tag
     * @param clazz the class of the tag entries
     * @return the tag or null
     */
    @Nullable
    public static <T extends Keyed> Tag<T> getTag(@NonNull String registry, @NonNull NamespacedKey tag, @NonNull Class<T> clazz) {
        return server.getTag(registry, tag, clazz);
    }

    /**
     * Gets a all tags which have been defined within the server.
     * <br>
     * Server implementations are allowed to handle only the registries
     * indicated in {@link Tag}.
     * <br>
     * No guarantees are made about the mutability of the returned iterator.
     *
     * @param <T> type of the tag
     * @param registry the tag registry to look at
     * @param clazz the class of the tag entries
     * @return all defined tags
     */
    @NonNull
    public static <T extends Keyed> Iterable<Tag<T>> getTags(@NonNull String registry, @NonNull Class<T> clazz) {
        return server.getTags(registry, clazz);
    }

    /**
     * Gets the specified {@link LootTable}.
     *
     * @param key the name of the LootTable
     * @return the LootTable, or null if no LootTable is found with that name
     */
    @Nullable
    public static LootTable getLootTable(@NonNull NamespacedKey key) {
        return server.getLootTable(key);
    }

    /**
     * Selects entities using the given Vanilla selector.
     * <br>
     * No guarantees are made about the selector format, other than they match
     * the Vanilla format for the active Minecraft version.
     * <br>
     * Usually a selector will start with '@', unless selecting a Player in
     * which case it may simply be the Player's name or UUID.
     * <br>
     * Note that in Vanilla, elevated permissions are usually required to use
     * '@' selectors, but this method should not check such permissions from the
     * sender.
     *
     * @param sender the sender to execute as, must be provided
     * @param selector the selection string
     * @return a list of the selected entities. The list will not be null, but
     * no further guarantees are made.
     * @throws IllegalArgumentException if the selector is malformed in any way
     * or a parameter is null
     */
    @NonNull
    public static List<Entity> selectEntities(@NonNull CommandSender sender, @NonNull String selector) throws IllegalArgumentException {
        return server.selectEntities(sender, selector);
    }

    /**
     * Gets the structure manager for loading and saving structures.
     *
     * @return the structure manager
     */
    @NonNull
    public static StructureManager getStructureManager() {
        return server.getStructureManager();
    }

    /**
     * Returns the registry for the given class.
     * <br>
     * If no registry is present for the given class null will be returned.
     * <br>
     * Depending on the implementation not every registry present in
     * {@link Registry} will be returned by this method.
     *
     * @param tClass of the registry to get
     * @param <T> type of the registry
     * @return the corresponding registry or null if not present
     */
    @Nullable
    public static <T extends Keyed> Registry<T> getRegistry(@NonNull Class<T> tClass) {
        return server.getRegistry(tClass);
    }

    /**
     * @return the unsafe values instance
     * @see UnsafeValues
     */
    @Deprecated
    @NonNull
    public static UnsafeValues getUnsafe() {
        return server.getUnsafe();
    }

    /**
     * Gets the active {@link org.bukkit.command.CommandMap}
     *
     * @return the active command map
     */
    @NonNull
    public static CommandMap getCommandMap() {
        return server.getCommandMap();
    }

    /**
     * Reload the Permissions in permissions.yml
     */
    public static void reloadPermissions() {
        server.reloadPermissions();
    }

    /**
     * Reload the Command Aliases in commands.yml
     *
     * @return Whether the reload was successful
     */
    public static boolean reloadCommandAliases() {
        return server.reloadCommandAliases();
    }

    /**
     * Checks if player names should be suggested when a command returns {@code null} as
     * their tab completion result.
     *
     * @return true if player names should be suggested
     */
    public static boolean suggestPlayerNamesWhenNullTabCompletions() {
        return server.suggestPlayerNamesWhenNullTabCompletions();
    }

    /**
     * Gets the default no permission message used on the server
     *
     * @return the default message
     */
    @NonNull
    public static Component permissionMessage() {
        return server.permissionMessage();
    }

    /**
     * Creates a PlayerProfile for the specified uuid, with name as null.
     *
     * If a player with the passed uuid exists on the server at the time of creation, the returned player profile will
     * be populated with the properties of said player (including their uuid and name).
     *
     * @param uuid UUID to create profile for
     * @return A PlayerProfile object
     */
    @NonNull
    public static PlayerProfile createProfile(@NonNull UUID uuid) {
        return server.createProfile(uuid);
    }

    /**
     * Creates a PlayerProfile for the specified name, with UUID as null.
     *
     * If a player with the passed name exists on the server at the time of creation, the returned player profile will
     * be populated with the properties of said player (including their uuid and name).
     * <p>
     * E.g. if the player 'jeb_' is currently playing on the server, calling {@code createProfile("JEB_")} will
     * yield a profile with the name 'jeb_', their uuid and their textures.
     * To bypass this pre-population on a case-insensitive name match, see {@link #createProfileExact(UUID, String)}.
     * <p>
     *
     * @param name Name to create profile for
     * @return A PlayerProfile object
     */
    @NonNull
    public static PlayerProfile createProfile(@NonNull String name) {
        return server.createProfile(name);
    }

    /**
     * Creates a PlayerProfile for the specified name/uuid
     *
     * Both UUID and Name can not be null at same time. One must be supplied.
     * If a player with the passed uuid or name exists on the server at the time of creation, the returned player
     * profile will be populated with the properties of said player (including their uuid and name).
     * <p>
     * E.g. if the player 'jeb_' is currently playing on the server, calling {@code createProfile(null, "JEB_")} will
     * yield a profile with the name 'jeb_', their uuid and their textures.
     * To bypass this pre-population on an case-insensitive name match, see {@link #createProfileExact(UUID, String)}.
     * <p>
     *
     * The name comparison will compare the {@link String#toLowerCase()} version of both the passed name parameter and
     * a players name to honour the case-insensitive nature of a mojang profile lookup.
     *
     * @param uuid UUID to create profile for
     * @param name Name to create profile for
     * @return A PlayerProfile object
     */
    @NonNull
    public static PlayerProfile createProfile(@Nullable UUID uuid, @Nullable String name) {
        return server.createProfile(uuid, name);
    }

    /**
     * Creates an exact PlayerProfile for the specified name/uuid
     *
     * Both UUID and Name can not be null at same time. One must be supplied.
     * If a player with the passed uuid or name exists on the server at the time of creation, the returned player
     * profile will be populated with the properties of said player.
     * <p>
     * Compared to {@link #createProfile(UUID, String)}, this method will never mutate the passed uuid or name.
     * If a player with either the same uuid or a matching name (case-insensitive) is found on the server, their
     * properties, such as textures, will be pre-populated in the profile, however the passed uuid and name stay intact.
     *
     * @param uuid UUID to create profile for
     * @param name Name to create profile for
     * @return A PlayerProfile object
     */
    @NonNull
    public static PlayerProfile createProfileExact(@Nullable UUID uuid, @Nullable String name) {
        return server.createProfileExact(uuid, name);
    }

    public static int getCurrentTick() {
        return server.getCurrentTick();
    }

    /**
     * Checks if the server is in the process of being shutdown.
     *
     * @return true if server is in the process of being shutdown
     */
    public static boolean isStopping() {
        return server.isStopping();
    }

    /**
     * Returns the {@link MobGoals} manager
     *
     * @return the mob goals manager
     */
    @NonNull
    public static MobGoals getMobGoals() {
        return server.getMobGoals();
    }

    /**
     * @return the datapack manager
     */
    @NonNull
    public static DatapackManager getDatapackManager() {
        return server.getDatapackManager();
    }

    /**
     * Gets the potion brewer.
     *
     * @return the potion brewer
     */
    public static @NonNull PotionBrewer getPotionBrewer() {
        return server.getPotionBrewer();
    }

    /**
     * Returns the region task scheduler. The region task scheduler can be used to schedule
     * tasks by location to be executed on the region which owns the location.
     * <p>
     * <b>Note</b>: It is entirely inappropriate to use the region scheduler to schedule tasks for entities.
     * If you wish to schedule tasks to perform actions on entities, you should be using {@link Entity#getScheduler()}
     * as the entity scheduler will "follow" an entity if it is teleported, whereas the region task scheduler
     * will not.
     * </p>
     * <p><b>If you do not need/want to make your plugin run on Folia, use {@link #getScheduler()} instead.</b></p>
     * @return the region task scheduler
     */
    public static @NonNull RegionScheduler getRegionScheduler() {
        return server.getRegionScheduler();
    }

    /**
     * Returns the async task scheduler. The async task scheduler can be used to schedule tasks
     * that execute asynchronously from the server tick process.
     * @return the async task scheduler
     */
    public static @NonNull AsyncScheduler getAsyncScheduler() {
        return server.getAsyncScheduler();
    }

    /**
     * Returns the global region task scheduler. The global task scheduler can be used to schedule
     * tasks to execute on the global region.
     * <p>
     * The global region is responsible for maintaining world day time, world game time, weather cycle,
     * sleep night skipping, executing commands for console, and other misc. tasks that do not belong to any specific region.
     * </p>
     * <p><b>If you do not need/want to make your plugin run on Folia, use {@link #getScheduler()} instead.</b></p>
     * @return the global region scheduler
     */
    public static @NonNull GlobalRegionScheduler getGlobalRegionScheduler() {
        return server.getGlobalRegionScheduler();
    }

    /**
     * Returns whether the current thread is ticking a region and that the region being ticked
     * owns the chunk at the specified world and block position.
     * @param world Specified world.
     * @param position Specified block position.
     */
    public static boolean isOwnedByCurrentRegion(@NonNull World world, @NonNull Position position) {
        return server.isOwnedByCurrentRegion(world, position);
    }

    /**
     * Returns whether the current thread is ticking a region and that the region being ticked
     * owns the chunks centered at the specified block position within the specified square radius.
     * Specifically, this function checks that every chunk with position x in [centerX - radius, centerX + radius] and
     * position z in [centerZ - radius, centerZ + radius] is owned by the current ticking region.
     * @param world Specified world.
     * @param position Specified block position.
     * @param squareRadiusChunks Specified square radius. Must be >= 0. Note that this parameter is <i>not</i> a <i>squared</i>
     *                           radius, but rather a <i>Chebyshev Distance</i>.
     */
    public static boolean isOwnedByCurrentRegion(@NonNull World world, @NonNull Position position, int squareRadiusChunks) {
        return server.isOwnedByCurrentRegion(world, position, squareRadiusChunks);
    }

    /**
     * Returns whether the current thread is ticking a region and that the region being ticked
     * owns the chunk at the specified world and block position as included in the specified location.
     * @param location Specified location, must have a non-null world.
     */
    public static boolean isOwnedByCurrentRegion(@NonNull Location location) {
        return server.isOwnedByCurrentRegion(location);
    }

    /**
     * Returns whether the current thread is ticking a region and that the region being ticked
     * owns the chunks centered at the specified world and block position as included in the specified location
     * within the specified square radius.
     * Specifically, this function checks that every chunk with position x in [centerX - radius, centerX + radius] and
     * position z in [centerZ - radius, centerZ + radius] is owned by the current ticking region.
     * @param location Specified location, must have a non-null world.
     * @param squareRadiusChunks Specified square radius. Must be >= 0. Note that this parameter is <i>not</i> a <i>squared</i>
     *                           radius, but rather a <i>Chebyshev Distance</i>.
     */
    public static boolean isOwnedByCurrentRegion(@NonNull Location location, int squareRadiusChunks) {
        return server.isOwnedByCurrentRegion(location, squareRadiusChunks);
    }

    /**
     * Returns whether the current thread is ticking a region and that the region being ticked
     * owns the chunk at the specified block position.
     * @param block Specified block position.
     */
    public static boolean isOwnedByCurrentRegion(@NonNull Block block) {
        return server.isOwnedByCurrentRegion(block.getLocation());
    }

    /**
     * Returns whether the current thread is ticking a region and that the region being ticked
     * owns the chunk at the specified world and chunk position.
     * @param world Specified world.
     * @param chunkX Specified x-coordinate of the chunk position.
     * @param chunkZ Specified z-coordinate of the chunk position.
     */
    public static boolean isOwnedByCurrentRegion(@NonNull World world, int chunkX, int chunkZ) {
        return server.isOwnedByCurrentRegion(world, chunkX, chunkZ);
    }

    /**
     * Returns whether the current thread is ticking a region and that the region being ticked
     * owns the chunks centered at the specified world and chunk position within the specified
     * square radius.
     * Specifically, this function checks that every chunk with position x in [centerX - radius, centerX + radius] and
     * position z in [centerZ - radius, centerZ + radius] is owned by the current ticking region.
     * @param world Specified world.
     * @param chunkX Specified x-coordinate of the chunk position.
     * @param chunkZ Specified z-coordinate of the chunk position.
     * @param squareRadiusChunks Specified square radius. Must be >= 0. Note that this parameter is <i>not</i> a <i>squared</i>
     *                           radius, but rather a <i>Chebyshev Distance</i>.
     */
    public static boolean isOwnedByCurrentRegion(@NonNull World world, int chunkX, int chunkZ, int squareRadiusChunks) {
        return server.isOwnedByCurrentRegion(world, chunkX, chunkZ, squareRadiusChunks);
    }

    /**
     * Returns whether the current thread is ticking a region and that the region being ticked
     * owns the specified entity. Note that this function is the only appropriate method of checking
     * for ownership of an entity, as retrieving the entity's location is undefined unless the entity is owned
     * by the current region.
     * @param entity Specified entity.
     */
    public static boolean isOwnedByCurrentRegion(@NonNull Entity entity) {
        return server.isOwnedByCurrentRegion(entity);
    }
}

