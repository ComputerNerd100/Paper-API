package io.papermc.paper.api.scheduler.threadedregion;

import io.papermc.paper.api.entity.Entity;
import io.papermc.paper.api.location.Location;
import io.papermc.paper.api.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Consumer;

/**
 * The region task scheduler can be used to schedule tasks by location to be executed on the region which owns the location.
 * <p>
 * <b>Note</b>: It is entirely inappropriate to use the region scheduler to schedule tasks for entities.
 * If you wish to schedule tasks to perform actions on entities, you should be using {@link Entity#getScheduler()}
 * as the entity scheduler will "follow" an entity if it is teleported, whereas the region task scheduler
 * will not.
 * </p>
 */
public interface RegionScheduler {

    /**
     * Schedules a task to be executed on the region which owns the location.
     *
     * @param plugin The plugin that owns the task
     * @param world  The world of the region that owns the task
     * @param chunkX The chunk X coordinate of the region that owns the task
     * @param chunkZ The chunk Z coordinate of the region that owns the task
     * @param run    The task to execute
     */
    void execute(@NonNull Plugin plugin, @NonNull World world, int chunkX, int chunkZ, @NonNull Runnable run);

    /**
     * Schedules a task to be executed on the region which owns the location.
     *
     * @param plugin   The plugin that owns the task
     * @param location The location at which the region executing should own
     * @param run      The task to execute
     */
    default void execute(@NonNull Plugin plugin, @NonNull Location location, @NonNull Runnable run) {
        this.execute(plugin, location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4, run);
    }

    /**
     * Schedules a task to be executed on the region which owns the location on the next tick.
     *
     * @param plugin The plugin that owns the task
     * @param world  The world of the region that owns the task
     * @param chunkX The chunk X coordinate of the region that owns the task
     * @param chunkZ The chunk Z coordinate of the region that owns the task
     * @param task   The task to execute
     * @return The {@link ScheduledTask} that represents the scheduled task.
     */
    @NonNull ScheduledTask run(@NonNull Plugin plugin, @NonNull World world, int chunkX, int chunkZ, @NonNull Consumer<ScheduledTask> task);

    /**
     * Schedules a task to be executed on the region which owns the location on the next tick.
     *
     * @param plugin   The plugin that owns the task
     * @param location The location at which the region executing should own
     * @param task     The task to execute
     * @return The {@link ScheduledTask} that represents the scheduled task.
     */
    default @NonNull ScheduledTask run(@NonNull Plugin plugin, @NonNull Location location, @NonNull Consumer<ScheduledTask> task) {
        return this.run(plugin, location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4, task);
    }

    /**
     * Schedules a task to be executed on the region which owns the location after the specified delay in ticks.
     *
     * @param plugin     The plugin that owns the task
     * @param world      The world of the region that owns the task
     * @param chunkX     The chunk X coordinate of the region that owns the task
     * @param chunkZ     The chunk Z coordinate of the region that owns the task
     * @param task       The task to execute
     * @param delayTicks The delay, in ticks.
     * @return The {@link ScheduledTask} that represents the scheduled task.
     */
    @NonNull ScheduledTask runDelayed(@NonNull Plugin plugin, @NonNull World world, int chunkX, int chunkZ, @NonNull Consumer<ScheduledTask> task,
                                      long delayTicks);

    /**
     * Schedules a task to be executed on the region which owns the location after the specified delay in ticks.
     *
     * @param plugin     The plugin that owns the task
     * @param location   The location at which the region executing should own
     * @param task       The task to execute
     * @param delayTicks The delay, in ticks.
     * @return The {@link ScheduledTask} that represents the scheduled task.
     */
    default @NonNull ScheduledTask runDelayed(@NonNull Plugin plugin, @NonNull Location location, @NonNull Consumer<ScheduledTask> task,
                                              long delayTicks) {
        return this.runDelayed(plugin, location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4, task, delayTicks);
    }

    /**
     * Schedules a repeating task to be executed on the region which owns the location after the initial delay with the
     * specified period.
     *
     * @param plugin            The plugin that owns the task
     * @param world             The world of the region that owns the task
     * @param chunkX            The chunk X coordinate of the region that owns the task
     * @param chunkZ            The chunk Z coordinate of the region that owns the task
     * @param task              The task to execute
     * @param initialDelayTicks The initial delay, in ticks.
     * @param periodTicks       The period, in ticks.
     * @return The {@link ScheduledTask} that represents the scheduled task.
     */
    @NonNull ScheduledTask runAtFixedRate(@NonNull Plugin plugin, @NonNull World world, int chunkX, int chunkZ, @NonNull Consumer<ScheduledTask> task,
                                          long initialDelayTicks, long periodTicks);

    /**
     * Schedules a repeating task to be executed on the region which owns the location after the initial delay with the
     * specified period.
     *
     * @param plugin            The plugin that owns the task
     * @param location          The location at which the region executing should own
     * @param task              The task to execute
     * @param initialDelayTicks The initial delay, in ticks.
     * @param periodTicks       The period, in ticks.
     * @return The {@link ScheduledTask} that represents the scheduled task.
     */
    default @NonNull ScheduledTask runAtFixedRate(@NonNull Plugin plugin, @NonNull Location location, @NonNull Consumer<ScheduledTask> task,
                                                  long initialDelayTicks, long periodTicks) {
        return this.runAtFixedRate(plugin, location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4, task, initialDelayTicks, periodTicks);
    }
}

