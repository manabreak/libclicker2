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
import java.math.BigInteger;

/**
 * Automator class for automating generators.
 * <p>
 * Normally generators are manually controlled, i.e. they generate resources
 * when explicitly told to. Automators are used to trigger generators
 * during the world's update cycles.
 *
 * @author Harri Pellikka
 */
public class Automator extends Item implements Serializable {
    private Generator generator;
    private double tickRate = 1.0;
    private double tickTimer = 0.0;
    private double multiplier;
    private boolean enabled;
    private double actualTickRate;

    public static class Builder {
        private final World world;
        private Generator generator;
        private double tickRate = 1.0;
        private String name = "Nameless automator";
        private boolean enabled = true;
        private BigInteger basePrice = BigInteger.ONE;
        private double priceMultiplier = 1.1;
        private double tickRateMultiplier = 1.08;

        /**
         * Constructs a new automator builder
         *
         * @param world World the automator belongs to
         */
        public Builder(World world) {
            this.world = world;
        }

        /**
         * Constructs a new automator builder for the given generator
         *
         * @param world     World the automator belongs to
         * @param generator Generator to automate
         */
        public Builder(World world, Generator generator) {
            this.world = world;
            this.generator = generator;
        }

        public Builder basePrice(int price) {
            basePrice = new BigInteger("" + price);
            return this;
        }

        public Builder basePrice(long price) {
            basePrice = new BigInteger("" + price);
            return this;
        }

        public Builder basePrice(BigInteger price) {
            basePrice = price;
            return this;
        }

        public Builder priceMultiplier(double multiplier) {
            priceMultiplier = multiplier;
            return this;
        }

        public Builder tickRateMultiplier(double multiplier) {
            tickRateMultiplier = multiplier;
            return this;
        }

        /**
         * Sets the target generator this automator should automate.
         *
         * @param generator Generator to automate
         * @return This builder for chaining
         */
        public Builder automate(Generator generator) {
            this.generator = generator;
            return this;
        }

        /**
         * Sets the name for this automator.
         *
         * @param name Name
         * @return This builder for chaining
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the tick rate of this automator, i.e. how often
         * this automator should do its business.
         *
         * @param seconds Tick rate in seconds
         * @return This builder for chaining
         */
        public Builder every(double seconds) {
            tickRate = seconds;
            return this;
        }

        /**
         * Constructs the automator based on the given properties.
         *
         * @return The automator
         */
        public Automator build() {
            if (generator == null) throw new IllegalStateException("Generator cannot be null");

            Automator a = new Automator(world, name);
            a.generator = generator;
            a.tickRate = tickRate;
            a.enabled = enabled;
            a.basePrice = basePrice;
            a.priceMultiplier = priceMultiplier;
            a.multiplier = tickRateMultiplier;
            world.addAutomator(a);
            return a;
        }
    }

    private Automator(World world, String name) {
        super(world, name);
    }

    /**
     * Enables this automator. Automators are enabled by default when
     * they are created.
     */
    public void enable() {
        if (!enabled) {
            getWorld().addAutomator(this);
            enabled = true;
        }
    }

    /**
     * Disables this automator, effectively turning the automation off.
     */
    public void disable() {
        if (enabled) {
            getWorld().removeAutomator(this);
            enabled = false;
        }
    }

    @Override
    public void upgrade() {
        super.upgrade(); //To change body of generated methods, choose Tools | Templates.
        actualTickRate = getFinalTickRate();
        System.out.println("Upgraded, final tick rate now: " + actualTickRate);
    }

    private double getFinalTickRate() {
        if (itemLevel == 0) return 0.0;
        double r = tickRate;
        double m = Math.pow(multiplier, itemLevel - 1);
        return r / m;
    }

    void update(double delta) {
        if (!enabled || itemLevel == 0) return;

        tickTimer += delta;
        while (tickTimer >= actualTickRate) {
            tickTimer -= actualTickRate;
            generator.process();
        }
    }

    /**
     * Retrieves the tick rate of this automator.
     *
     * @return Tick rate in seconds
     */
    public double getTickRate() {
        return tickRate;
    }

    /**
     * Sets the tick rate of this automator.
     *
     * @param tickRate Tick rate in seconds
     */
    public void setTickRate(double tickRate) {
        this.tickRate = tickRate;
        if (this.tickRate < 0.0) this.tickRate = 0.0;
    }

    /**
     * Retrieves the percentage of the tick. Useful
     * when creating progress bars for generators.
     *
     * @return Percentage of tick completion
     */
    public double getTimerPercentage() {
        return tickRate != 0.0 ? tickTimer / tickRate : 1.0;
    }
}
