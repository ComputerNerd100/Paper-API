package io.papermc.paper.api.block.tilestate;

public interface Lidded {

    /**
     * Sets the block's animated state to open and prevents it from being closed
     * until {@link #close()} is called.
     */
    void open();

    /**
     * Sets the block's animated state to closed even if a player is currently
     * viewing this block.
     */
    void close();

    /**
     * Checks if the block's animation state.
     *
     * @return true if the block's animation state is set to open.
     */
    boolean isOpen();
}
