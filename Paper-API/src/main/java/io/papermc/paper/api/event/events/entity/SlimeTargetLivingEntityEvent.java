package io.papermc.paper.api.event.events.entity;

import io.papermc.paper.api.entity.LivingEntity;
import io.papermc.paper.api.event.util.Param;

/**
 * Fired when a Slime decides to change direction to target a LivingEntity.
 * <p>
 * This event does not fire for the entity's actual movement. Only when it
 * is choosing to start moving.
 */
public interface SlimeTargetLivingEntityEvent extends SlimePathfindEvent {

    /**
     * Get the targeted entity
     *
     * @return Targeted entity
     */
    @Param(1)
    LivingEntity target();
}
