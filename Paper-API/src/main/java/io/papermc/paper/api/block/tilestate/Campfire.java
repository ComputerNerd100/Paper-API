package io.papermc.paper.api.block.tilestate;


import io.papermc.paper.api.inventory.Inventory;
import io.papermc.paper.api.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a captured state of a campfire.
 */
public interface Campfire extends TileState {

    /**
     * @return The size of the inventory
     * @see Inventory#getSize()
     */
    int getSize();

    /**
     * @param index The index of the Slot's ItemStack to return
     * @return The ItemStack in the slot
     * @see Inventory#getItem(int)
     */
    @Nullable
    ItemStack getItem(int index);

    /**
     * @param index The index where to put the ItemStack
     * @param item The ItemStack to set
     * @see Inventory#setItem(int, ItemStack)
     */
    void setItem(int index, @Nullable ItemStack item);

    /**
     * Get cook time.
     *
     * This is the amount of time the item has been cooking for.
     *
     * @param index item slot index
     * @return Cook time
     */
    int getCookTime(int index);

    /**
     * Set cook time.
     *
     * This is the amount of time the item has been cooking for.
     *
     * @param index item slot index
     * @param cookTime Cook time
     */
    void setCookTime(int index, int cookTime);

    /**
     * Get cook time total.
     *
     * This is the amount of time the item is required to cook for.
     *
     * @param index item slot index
     * @return Cook time total
     */
    int getCookTimeTotal(int index);

    /**
     * Set cook time.
     *
     * This is the amount of time the item is required to cook for.
     *
     * @param index item slot index
     * @param cookTimeTotal Cook time total
     */
    void setCookTimeTotal(int index, int cookTimeTotal);

    // Paper start
    /**
     * Disable cooking in all slots.
     */
    void stopCooking();

    /**
     * Re-enable cooking in all slots.
     */
    void startCooking();

    /**
     * Disable cooking in the specified slot index.
     *
     * @param index item slot index
     * @return whether the slot had cooking enabled before this call
     */
    boolean stopCooking(int index);

    /**
     * Re-enable cooking in the specified slot index.
     *
     * @param index item slot index
     * @return whether the slot couldn't cook before this call
     */
    boolean startCooking(int index);

    /**
     * State of slot index.
     *
     * @param index item slot index
     * @return {@code true} if the specified slot index cannot cook
     */
    boolean isCookingDisabled(int index);
    // Paper end
}
