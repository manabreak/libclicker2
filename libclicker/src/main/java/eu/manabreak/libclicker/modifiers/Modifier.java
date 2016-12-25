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
package eu.manabreak.libclicker.modifiers;

import java.io.Serializable;

import eu.manabreak.libclicker.Item;
import eu.manabreak.libclicker.World;
import eu.manabreak.libclicker.generators.Generator;

/**
 * A base class for all the modifiers.
 * <p>
 * A modifier does "something" to a component (generator, automator, the
 * world etc), for example speeds up, slows down, increases production
 * or something similar.
 *
 * @author Harri Pellikka
 */
public abstract class Modifier extends Item implements Serializable {
    private boolean enabled = false;

    protected Modifier(World world) {
        super(world);
    }

    protected abstract void onEnable();

    protected abstract void onDisable();

    /**
     * Enables this modifier, i.e. makes it active
     */
    public void enable() {
        if (!enabled) {
            enabled = true;
            world.addModifier(this);
            onEnable();
        }
    }

    /**
     * Disables this modifier, i.e. makes it inactive
     */
    public void disable() {
        if (enabled) {
            onDisable();
            world.removeModifier(this);
            enabled = false;
        }
    }

    /**
     * Checks whether or not this modifier is enabled
     *
     * @return True if enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Builder class for the modifiers
     */
    public static class Builder {
        /**
         * Constructs a new modifier builder
         */
        public Builder() {

        }

        /**
         * Apply the modifier to a world
         *
         * @param world World to modify
         * @return A world target to set the modification details
         */
        public final WorldTarget modify(World world) {
            return new WorldTarget(world);
        }

        /**
         * Apply the modifier to a generator
         *
         * @param gen Generator to modify
         * @return A generator target to set the modification details
         */
        public final GeneratorTarget modify(World world, Generator gen) {
            return new GeneratorTarget(world, gen);
        }

        /**
         * A modifier settings class for world modifiers.
         * Keeps track of all the parameters the modifier should
         * modify.
         */
        public static class WorldTarget {
            World world;
            private double speedMultiplier = 1.0;
            private boolean disableActivators = false;

            WorldTarget(World w) {
                world = w;
            }

            /**
             * Speeds up all the processing by the given multiplier.
             *
             * @param multiplier Multiplier for advancing the time
             * @return This target for chaining
             */
            public WorldTarget speedBy(double multiplier) {
                speedMultiplier = multiplier;
                return this;
            }

            /**
             * Disables all the activators
             *
             * @return This target for chaining
             */
            public WorldTarget disableActivators() {
                disableActivators = true;
                return this;
            }

            /**
             * Creates the actual modifier based on the given settings
             *
             * @return Modifier
             */
            public Modifier build() {
                WorldModifier m = new WorldModifier(world);
                m.speedMultiplier = speedMultiplier;
                m.disableActivators = disableActivators;
                return m;
            }
        }

        /**
         * A modifier settings class for generator modifiers.
         * Keeps track of all the parameters the modifier should
         * modify.
         */
        public static class GeneratorTarget {
            private final World world;
            private Generator generator;
            private double multiplier = 1.0;

            GeneratorTarget(World world, Generator gen) {
                this.world = world;
                generator = gen;
            }

            /**
             * Multiplies the production of the generator.
             *
             * @param multiplier Multiplier
             * @return This target for chaining
             */
            public GeneratorTarget multiplier(double multiplier) {
                this.multiplier = multiplier;
                return this;
            }

            /**
             * Constructs the actual modifier with the given settings
             *
             * @return Modifier as per the given settings
             */
            public Modifier build() {
                GeneratorModifier m = new GeneratorModifier(world, generator);
                m.multiplier = multiplier;
                return m;
            }
        }
    }
}
