package io.papermc.paper.api.scheduler.threadedregion;

import io.papermc.paper.api.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Consumer;

/**
 * The global region task scheduler may be used to schedule tasks that will execute on the global region.
 * <p>
 * The global region is responsible for maintaining world day time, world game time, weather cycle,
 * sleep night skipping, executing commands for console, and other misc. tasks that do not belong to any specific region.
 * </p>
 */
public interface GlobalRegionScheduler {

    /**
     * Schedules a task to be executed on the global region.
     * @param plugin The plugin that owns the task
     * @param run The task to execute
     */
    void execute(@NonNull Plugin plugin, @NonNull Runnable run);

    /**
     * Schedules a task to be executed on the global region on the next tick.
     * @param plugin The plugin that owns the task
     * @param task The task to execute
     * @return The {@link ScheduledTask} that represents the scheduled task.
     */
    @NonNull ScheduledTask run(@NonNull Plugin plugin, @NonNull Consumer<ScheduledTask> task);

    /**
     * Schedules a task to be executed on the global region after the specified delay in ticks.
     * @param plugin The plugin that owns the task
     * @param task The task to execute
     * @param delayTicks The delay, in ticks.
     * @return The {@link ScheduledTask} that represents the scheduled task.
     */
    @NonNull ScheduledTask runDelayed(@NonNull Plugin plugin, @NonNull Consumer<ScheduledTask> task, long delayTicks);

    /**
     * Schedules a repeating task to be executed on the global region after the initial delay with the
     * specified period.
     * @param plugin The plugin that owns the task
     * @param task The task to execute
     * @param initialDelayTicks The initial delay, in ticks.
     * @param periodTicks The period, in ticks.
     * @return The {@link ScheduledTask} that represents the scheduled task.
     */
    @NonNull ScheduledTask runAtFixedRate(@NonNull Plugin plugin, @NonNull Consumer<ScheduledTask> task,
                                          long initialDelayTicks, long periodTicks);

    /**
     * Attempts to cancel all tasks scheduled by the specified plugin.
     * @param plugin Specified plugin.
     */
    void cancelTasks(@NonNull Plugin plugin);
}

