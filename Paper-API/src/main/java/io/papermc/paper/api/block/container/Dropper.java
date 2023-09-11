package io.papermc.paper.api.block.container;

import io.papermc.paper.api.block.container.Container;
import io.papermc.paper.api.entity.Item;
import io.papermc.paper.api.inventory.LootableBlockInventory;

/**
 * Represents a captured state of a dropper.
 */
public interface Dropper extends Container, LootableBlockInventory {

    /**
     * Tries to drop a randomly selected item from the dropper's inventory,
     * following the normal behavior of a dropper.
     * <p>
     * Normal behavior of a dropper is as follows:
     * <p>
     * If the block that the dropper is facing is an InventoryHolder,
     * the randomly selected ItemStack is placed within that
     * Inventory in the first slot that's available, starting with 0 and
     * counting up.  If the inventory is full, nothing happens.
     * <p>
     * If the block that the dropper is facing is not an InventoryHolder,
     * the randomly selected ItemStack is dropped on
     * the ground in the form of an {@link Item Item}.
     * <p>
     * If the block represented by this state is no longer a dropper, this will
     * do nothing.
     *
     * @throws IllegalStateException if this block state is not placed
     */
    void drop();
}
