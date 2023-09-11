package io.papermc.paper.api.entity.minecart;

import io.papermc.paper.api.entity.Minecart;
import io.papermc.paper.api.inventory.InventoryHolder;
import io.papermc.paper.api.inventory.LootableEntityInventory;

/**
 * Represents a Minecart with a Hopper inside it
 */
public interface HopperMinecart extends Minecart, InventoryHolder, LootableEntityInventory {

    /**
     * Checks whether or not this Minecart will pick up
     * items into its inventory.
     *
     * @return true if the Minecart will pick up items
     */
    boolean isEnabled();

    /**
     * Sets whether this Minecart will pick up items.
     *
     * @param enabled new enabled state
     */
    void setEnabled(boolean enabled);
}

