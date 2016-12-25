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
import java.util.Random;

import eu.manabreak.libclicker.modifiers.GeneratorModifier;

/**
 * A base class for all the generators.
 * <p>
 * Generators are used to produce resources (currencies), and
 * can be controlled either manually or automatically by using
 * an Automator.
 *
 * @author Harri Pellikka
 */
public class Generator extends Item implements Serializable {
    public interface Callback {
        void onProcessed();
    }

    /**
     * Callback for extended functionality
     */
    private Callback callback = null;

    /**
     * Currency this generator should generate
     */
    private Currency currency;

    /**
     * How many times this generator has been processed
     */
    private long timesProcessed = 0;

    /**
     * Base amount of resources this generator generates
     */
    private BigInteger baseAmount;

    /**
     * Multiplier used to increase the amount of resources generated
     * when this generator is upgraded
     */
    private double amountMultiplier;

    /**
     * Probability for this generator to "work"
     */
    private double probability;

    /**
     * Should this generator use probability?
     */
    private boolean useProbability;

    /**
     * RNG for probability
     */
    private Random random;

    /**
     * Should we take remainders into consideration?
     */
    private boolean useRemainder;

    /**
     * Remainder of the last processing cycle
     */
    private double remainder;

    /**
     * Cooldown time between processing cycles.
     */
    private double cooldown;

    /**
     * List of active modifiers attached to this generator
     */
    private List<GeneratorModifier> modifiers = new ArrayList<>();

    /**
     * Builder class for creating new generators
     */
    public static class Builder {
        private final World mWorld;
        private String mName = "Nameless generator";
        private Callback mOnProcessed = null;
        private Currency mCurrency = null;
        private BigInteger mBaseAmount = BigInteger.ONE;
        private double mAmountMultiplier = 1.1;
        private long mMaxLevel = Long.MAX_VALUE;
        private BigInteger mBasePrice = BigInteger.ONE;
        private double mPriceMultiplier = 1.1;
        private double mProbability = 1.0;
        private boolean mProbabilitySet = false;
        private boolean mUseRemainder = true;
        private double mCooldown = 0.0;

        /**
         * Creates a new generator builder
         *
         * @param world World to build the generator into
         */
        public Builder(World world) {
            mWorld = world;
        }

        public Builder cooldown(double cooldown) {
            mCooldown = cooldown;
            return this;
        }

        /**
         * Store remainder of resources and add an extra
         * when the remainder "overflows"
         *
         * @return This builder for chaining
         */
        public Builder useRemainder() {
            mUseRemainder = true;
            return this;
        }

        /**
         * Discard remainder of resources when generating.
         *
         * @return This builder for chaining
         */
        public Builder discardRemainder() {
            mUseRemainder = false;
            return this;
        }

        /**
         * Sets the name for the generator
         *
         * @param name Name for the generator
         * @return This builder for chaining
         */
        public Builder name(String name) {
            mName = name;
            return this;
        }

        /**
         * Sets the multiplier for resource generation. This multiplier
         * is used in the formula (amount) = (base amount) * (multiplier) ^ (level)
         *
         * @param multiplier Amount generation multiplier per level
         * @return This builder for chaining
         */
        public Builder multiplier(double multiplier) {
            mAmountMultiplier = multiplier;
            return this;
        }

        /**
         * Sets the maximum allowed level for this generator. The max level must
         * be greated than zero.
         *
         * @param maxLevel Maximum allowed level for this generator
         * @return This builder for chaining
         */
        public Builder maxLevel(long maxLevel) {
            if (maxLevel <= 0)
                throw new IllegalArgumentException("Max level must be greater than 0");
            mMaxLevel = maxLevel;
            return this;
        }

        /**
         * Sets the base amount of resources generated by this generator.
         * This is the amount the generator generates at level 1 and is used
         * as the base for the higher levels.
         *
         * @param amount Base amount of resources generated at level 1
         * @return This builder for chaining
         */
        public Builder baseAmount(BigInteger amount) {
            if (amount == null) throw new IllegalArgumentException("Base amount cannot be null");
            mBaseAmount = amount;
            return this;
        }

        /**
         * Sets the base amount of resources generated by this generator.
         * This is the amount the generator generates at level 1 and is used
         * as the base for the higher levels.
         *
         * @param amount Base amount of resources generated at level 1
         * @return This builder for chaining
         */
        public Builder baseAmount(long amount) {
            mBaseAmount = new BigInteger("" + amount);
            return this;
        }

        /**
         * Sets the base amount of resources generated by this generator.
         * This is the amount the generator generates at level 1 and is used
         * as the base for the higher levels.
         *
         * @param amount Base amount of resources generated at level 1
         * @return This builder for chaining
         */
        public Builder baseAmount(int amount) {
            mBaseAmount = new BigInteger("" + amount);
            return this;
        }

        /**
         * Sets the currency that should be generated by the generator.
         *
         * @param c Currency to generate
         * @return This builder for chaining
         * @throws IllegalArgumentException Thrown if the currency is null
         */
        public Builder generate(Currency c) throws IllegalArgumentException {
            if (c == null) throw new IllegalArgumentException("Currency cannot be null");
            mCurrency = c;
            return this;
        }

        /**
         * Sets a callback for the generator to be called when the generator
         * has finished its processing cycle (i.e. has generated something).
         *
         * @param callback Callback to call after generating something
         * @return This builder for chaining
         */
        public Builder callback(Callback callback) {
            mOnProcessed = callback;
            return this;
        }

        public Builder price(BigInteger price) {
            mBasePrice = price;
            return this;
        }

        public Builder price(long price) {
            mBasePrice = new BigInteger("" + price);
            return this;
        }

        public Builder price(int price) {
            mBasePrice = new BigInteger("" + price);
            return this;
        }

        public Builder priceMultiplier(double multiplier) {
            mPriceMultiplier = multiplier;
            return this;
        }

        /**
         * Set a probability for this generator to "work" when it's processed
         *
         * @param probability Probability percentage (between 0.0 and 1.0)
         * @return This builder for chaining
         */
        public Builder probability(double probability) {
            if (probability < 0 || probability > 1.0)
                throw new IllegalArgumentException("Probability should be between 0.0 and 1.0");
            mProbability = probability;
            mProbabilitySet = true;
            return this;
        }

        /**
         * Constructs the generator based on the given parameters
         *
         * @return The generator
         */
        public Generator build() {
            Generator g = new Generator(mWorld, mName);
            g.callback = mOnProcessed;
            g.currency = mCurrency;
            g.amountMultiplier = mAmountMultiplier;
            g.baseAmount = mBaseAmount;
            g.maxItemLevel = mMaxLevel;
            g.basePrice = mBasePrice;
            g.priceMultiplier = mPriceMultiplier;
            g.probability = mProbability;
            g.useProbability = mProbabilitySet;
            g.random = new Random();
            g.random.setSeed(g.hashCode());
            g.useRemainder = mUseRemainder;
            g.cooldown = mCooldown;
            mWorld.addGenerator(g);
            return g;
        }
    }

    /**
     * Constructs a new generator
     */
    private Generator(World world) {
        super(world);
    }

    /**
     * Constructs a new generator
     *
     * @param name Name of this generator
     */
    private Generator(World world, String name) {
        super(world, name);
    }

    /**
     * Upgrades this generator by one level
     */
    public void upgrade() {
        if (itemLevel < maxItemLevel) {
            itemLevel++;
        }
    }

    /**
     * Downgrades this generator by one level
     */
    public void downgrade() {
        if (itemLevel > 0) {
            itemLevel--;
        }
    }

    /**
     * Retrieves the amount this generator currently is generating per
     * processing cycle
     *
     * @return Amount of resources generated by this generator
     */
    public BigInteger getGeneratedAmount() {
        if (itemLevel == 0) return BigInteger.ZERO;

        BigDecimal tmp = new BigDecimal(baseAmount);
        tmp = tmp.multiply(new BigDecimal(Math.pow(amountMultiplier, itemLevel - 1)));
        if (useRemainder) {
            double tmpRem = tmp.remainder(BigDecimal.ONE).doubleValue();
            remainder += tmpRem;
            if (remainder >= 0.999) {
                remainder -= 1.0;
                tmp = tmp.add(new BigDecimal(1));
            }
        }

        tmp = processModifiers(tmp);

        return tmp.toBigInteger();
    }

    private BigDecimal processModifiers(BigDecimal val) {
        if (modifiers.size() == 0) return val;

        for (GeneratorModifier m : modifiers) {
            double d = m.getMultiplier();
            if (d != 1.0) {
                val = val.multiply(new BigDecimal(d));
            }
        }

        return val;
    }

    /**
     * Determines if this generator should generate anything based on its
     * properties such as item level and probability.
     *
     * @return True if should work, false otherwise
     */
    private boolean isWorking() {
        if (itemLevel > 0) {
            if (!useProbability || random.nextDouble() < probability) return true;
        }
        return false;
    }

    /**
     * Processes this generator, generating resources as per the rules
     * of this generator.
     */
    public void process() {
        if (isWorking()) {
            currency.add(getGeneratedAmount());
            timesProcessed++;
            if (callback != null) callback.onProcessed();
        }
    }

    /**
     * Retrieves the number of times this generator has done its processing
     *
     * @return Number of times processed
     */
    public long getTimesProcessed() {
        return timesProcessed;
    }

    public void attachModifier(GeneratorModifier modifier) {
        if (modifier != null && !modifiers.contains(modifier)) {
            modifiers.add(modifier);
        }
    }

    public void detachModifier(GeneratorModifier modifier) {
        if (modifier != null) {
            modifiers.remove(modifier);
        }
    }
}
