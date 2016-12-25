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
package eu.manabreak.libclicker.generators;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import eu.manabreak.libclicker.Item;
import eu.manabreak.libclicker.World;
import eu.manabreak.libclicker.modifiers.GeneratorModifier;
import eu.manabreak.libclicker.utils.RandomUtils;

/**
 * A base class for all the generators.
 * <p>
 * Generators are used to produce resources (e.g. currencies), and
 * can be controlled either manually or automatically by using
 * an Automator.
 */
public class Generator<T extends Resource> extends Item implements Serializable {
    public interface Callback {
        void onProcessed();
    }

    private Callback callback = null;
    private T resource;
    private long timesProcessed = 0;
    private BigInteger baseAmount;
    private double amountMultiplier;
    private double probability;
    private boolean useProbability;
    private boolean useRemainder;
    private double remainder;
    private double cooldown;
    private List<GeneratorModifier> modifiers = new ArrayList<>();

    /**
     * Builder class for creating new generators
     */
    public static class Builder {
        private final World world;
        private String name = "Nameless generator";
        private Callback onProcessed = null;
        private Resource resource;
        private BigInteger baseAmount = BigInteger.ONE;
        private double amountMultiplier = 1.1;
        private long maxLevel = Long.MAX_VALUE;
        private BigInteger basePrice = BigInteger.ONE;
        private double priceMultiplier = 1.1;
        private double probability = 1.0;
        private boolean probabilitySet = false;
        private boolean useRemainder = true;
        private double cooldown = 0.0;

        /**
         * Creates a new generator builder
         *
         * @param world World to build the generator into
         */
        public Builder(World world) {
            this.world = world;
        }

        /**
         * Sets the cooldown of this generator (in seconds).
         * This is the minimum time between processing this
         * generator.
         *
         * @param cooldown in seconds
         * @return This builder for chaining
         */
        public Builder cooldown(double cooldown) {
            this.cooldown = cooldown;
            return this;
        }

        /**
         * Store remainder of resources and generate an extra
         * when the remainder "overflows"
         *
         * @return This builder for chaining
         */
        public Builder useRemainder() {
            useRemainder = true;
            return this;
        }

        /**
         * Discard remainder of resources when generating.
         *
         * @return This builder for chaining
         */
        public Builder discardRemainder() {
            useRemainder = false;
            return this;
        }

        /**
         * Sets the name for the generator
         *
         * @param name Name for the generator
         * @return This builder for chaining
         */
        public Builder name(String name) {
            this.name = name;
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
            amountMultiplier = multiplier;
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
            this.maxLevel = maxLevel;
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
            baseAmount = amount;
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
            baseAmount = new BigInteger("" + amount);
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
            baseAmount = new BigInteger("" + amount);
            return this;
        }

        /**
         * Sets the currency that should be generated by the generator.
         *
         * @param resource Resource to generate
         * @return This builder for chaining
         * @throws IllegalArgumentException Thrown if the currency is null
         */
        public Builder generate(Resource resource) throws IllegalArgumentException {
            if (resource == null) throw new IllegalArgumentException("Currency cannot be null");
            this.resource = resource;
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
            onProcessed = callback;
            return this;
        }

        public Builder price(BigInteger price) {
            basePrice = price;
            return this;
        }

        public Builder price(long price) {
            basePrice = new BigInteger("" + price);
            return this;
        }

        public Builder price(int price) {
            basePrice = new BigInteger("" + price);
            return this;
        }

        public Builder priceMultiplier(double multiplier) {
            priceMultiplier = multiplier;
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
            this.probability = probability;
            probabilitySet = true;
            return this;
        }

        /**
         * Constructs the generator based on the given parameters
         *
         * @return The generator
         */
        public Generator build() {
            Generator g = new Generator(world, name);
            g.callback = onProcessed;
            g.resource = resource;
            g.amountMultiplier = amountMultiplier;
            g.baseAmount = baseAmount;
            g.maxItemLevel = maxLevel;
            g.basePrice = basePrice;
            g.priceMultiplier = priceMultiplier;
            g.probability = probability;
            g.useProbability = probabilitySet;
            g.useRemainder = useRemainder;
            g.cooldown = cooldown;
            world.addGenerator(g);
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
            if (!useProbability || RandomUtils.nextDouble() < probability) return true;
        }
        return false;
    }

    /**
     * Processes this generator, generating resources as per the rules
     * of this generator.
     */
    public void process() {
        if (isWorking()) {
            resource.generate(getGeneratedAmount());
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
