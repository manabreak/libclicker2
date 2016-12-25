/*
 * The MIT License
 *
 * Copyright 2015 Harri Pellikka.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package eu.manabreak.libclicker;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import eu.manabreak.libclicker.modifiers.Modifier;

/**
 * Base class for all the purchasable "items".
 */
public abstract class Item implements Serializable {
    /**
     * World this item belongs to
     */
    protected final World world;

    /**
     * Modifiers applied to this item
     */
    protected final List<Modifier> modifiers = new ArrayList<>();

    /**
     * The base price of the item (i.e. the price of the first level of this item)
     */
    protected BigInteger basePrice = BigInteger.ONE;

    /**
     * Name of this item
     */
    protected String name = "Nameless Item";

    /**
     * Description text for this item
     */
    protected String description = "No description.";

    /**
     * Current level of this item
     */
    protected long itemLevel = 0;

    /**
     * Max. item level
     */
    protected long maxItemLevel = Long.MAX_VALUE;

    /**
     * Price multiplier per level. This is used in the price formula
     * like this: price = (base price) * (price multiplier) ^ (item level)
     */
    protected double priceMultiplier = 1.145;

    /**
     * Constructs a new item
     *
     * @param world World this item belongs to
     */
    protected Item(World world) {
        this.world = world;
    }

    /**
     * Constructs a new item
     *
     * @param world World this item belongs to
     * @param name  Name of this item
     */
    protected Item(World world, String name) {
        this.world = world;
        setName(name);
    }

    /**
     * Retrieves the name of this item
     *
     * @return Name of this item
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this item
     *
     * @param name New name for this item
     */
    public void setName(String name) {
        if (name == null || name.length() == 0)
            throw new RuntimeException("Item name cannot be null or empty");
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retrieves the base price of this item
     *
     * @return Base price of this item
     */
    public BigInteger getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(int basePrice) {
        this.basePrice = new BigInteger("" + basePrice);
    }

    public BigInteger getPrice() {
        BigDecimal tmp = new BigDecimal(basePrice);
        tmp = tmp.multiply(new BigDecimal(Math.pow(priceMultiplier, itemLevel)));
        return tmp.toBigInteger();
    }

    public PurchaseResult buyWith(Currency currency) {
        if (currency == null) throw new IllegalArgumentException("Currency cannot be null");
        if (itemLevel >= maxItemLevel)
            return PurchaseResult.MAX_LEVEL_REACHED;

        BigInteger price = getPrice();
        BigInteger result = currency.getValue().subtract(price);
        if (result.signum() < 0) {
            return PurchaseResult.INSUFFICIENT_FUNDS;
        }
        currency.sub(price);
        upgrade();
        return PurchaseResult.OK;
    }

    /**
     * Sets the base price of this item
     *
     * @param basePrice New base price for this item
     */
    public void setBasePrice(BigInteger basePrice) {
        if (basePrice == null) throw new RuntimeException("Base price cannot be null");
        if (basePrice.equals(BigInteger.ZERO))
            throw new RuntimeException("Base price cannot be zero");
        this.basePrice = basePrice;
    }

    public void setBasePrice(long basePrice) {
        this.basePrice = new BigInteger("" + basePrice);
    }

    /**
     * Retrieves the price multiplier
     *
     * @return Price multiplier
     */
    public double getPriceMultiplier() {
        return priceMultiplier;
    }

    /**
     * Sets the price multiplier of this item
     *
     * @param multiplier Price multiplier
     */
    public void setPriceMultiplier(double multiplier) {
        priceMultiplier = multiplier;
    }

    public long getMaxItemLevel() {
        return maxItemLevel;
    }

    public void setMaxItemLevel(long maxLvl) {
        if (maxLvl <= 0) throw new RuntimeException("Max item level cannot be zero or negative");
        maxItemLevel = maxLvl;
    }

    public long getItemLevel() {
        return itemLevel;
    }

    public void setItemLevel(long lvl) {
        itemLevel = lvl < 0 ? 0 : lvl > maxItemLevel ? maxItemLevel : lvl;
    }

    public void upgrade() {
        if (itemLevel < maxItemLevel) {
            itemLevel++;
        }
    }

    public void downgrade() {
        if (itemLevel > 0) {
            itemLevel--;
        }
    }

    public void maximize() {
        itemLevel = maxItemLevel;
    }
}
