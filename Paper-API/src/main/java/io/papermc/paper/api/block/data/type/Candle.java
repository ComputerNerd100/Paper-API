package io.papermc.paper.api.block.data.type;

import io.papermc.paper.api.block.data.Lightable;
import io.papermc.paper.api.block.data.Waterlogged;

/**
 * 'candles' represents the number of candles which are present.
 */
public interface Candle extends Lightable, Waterlogged {

    /**
     * Gets the value of the 'candles' property.
     *
     * @return the 'candles' value
     */
    int getCandles();

    /**
     * Sets the value of the 'candles' property.
     *
     * @param candles the new 'candles' value
     */
    void setCandles(int candles);

    /**
     * Gets the maximum allowed value of the 'candles' property.
     *
     * @return the maximum 'candles' value
     */
    int getMaximumCandles();

    // Paper start
    /**
     * Gets the minimum allowed value of the 'candles' property.
     *
     * @return the minimum 'candles' value
     */
    int getMinimumCandles();
    // Paper end
}
