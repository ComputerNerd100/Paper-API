package io.papermc.paper.api.block.data.type;

import io.papermc.paper.api.block.data.Directional;

/**
 * Similar to {@link io.papermc.paper.api.block.data.Powerable}, 'triggered' indicates whether or not the
 * dispenser is currently activated.
 */
public interface Dispenser extends Directional {

    /**
     * Gets the value of the 'triggered' property.
     *
     * @return the 'triggered' value
     */
    boolean isTriggered();

    /**
     * Sets the value of the 'triggered' property.
     *
     * @param triggered the new 'triggered' value
     */
    void setTriggered(boolean triggered);
}

