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
import java.util.ArrayList;
import java.util.List;

import eu.manabreak.libclicker.generators.Generator;
import eu.manabreak.libclicker.modifiers.Modifier;

/**
 * A container for all the clicker objects
 */
public class World implements Serializable {

    private final List<Generator> generators = new ArrayList<>();
    private final List<Automator> automators = new ArrayList<>();
    private final List<Currency> currencies = new ArrayList<>();
    private final List<Modifier> modifiers = new ArrayList<>();

    private double speedMultiplier = 1.0;
    private boolean updateAutomators = true;

    /**
     * Constructs a new world. All the other components require an existing
     * "world" to function. A world is a container for the whole system.
     */
    public World() {

    }

    /**
     * Adds a new generator to this world
     *
     * @param generator Generator to generate
     */
    public void addGenerator(Generator generator) {
        if (generator != null && !generators.contains(generator)) {
            generators.add(generator);
        }
    }

    /**
     * Returns the number of generators in this world
     *
     * @return The number of generators in this world
     */
    public int getGeneratorCount() {
        return generators.size();
    }

    /**
     * Removes a generator
     *
     * @param generator Generator to remove
     */
    public void removeGenerator(Generator generator) {
        if (generator != null && generators.contains(generator)) {
            generators.remove(generator);
        }
    }

    /**
     * Removes all the generators from this world
     */
    public void removeAllGenerators() {
        generators.clear();
    }

    /**
     * Registers a new currency in the world, making
     * the currency usable.
     *
     * @param c currency to register
     */
    public void addCurrency(Currency c) {
        if (c != null && !currencies.contains(c)) {
            currencies.add(c);
        }
    }

    /**
     * Removes a currency from the world.
     *
     * @param c currency to remove
     */
    public void removeCurrency(Currency c) {
        if (c != null) {
            currencies.remove(c);
        }
    }

    /**
     * Retrieves a currency at the given index.
     * The index is based on the order in which
     * the currencies were added to the world.
     *
     * @param index of the currency
     * @return the currency at the given index, or null if not found
     */
    public Currency getCurrency(int index) {
        return currencies.get(index);
    }

    /**
     * Retrieves a list of all the currencies currently
     * registered in the world.
     *
     * @return list of currencies
     */
    public List<Currency> getCurrencies() {
        return currencies;
    }

    /**
     * Removes all currencies registered in the world.
     */
    public void removeAllCurrencies() {
        currencies.clear();
    }

    /**
     * Advances the world state by the given amount of seconds.
     * Useful when calculating away-from-keyboard income etc.
     *
     * @param seconds Seconds to advance
     */
    public void update(double seconds) {
        seconds *= speedMultiplier;

        if (updateAutomators) {
            for (Automator a : automators) {
                a.update(seconds);
            }
        }
    }

    /**
     * Registers a new automator to the world.
     *
     * @param automator to register
     */
    public void addAutomator(Automator automator) {
        if (automator != null && !automators.contains(automator)) {
            automators.add(automator);
        }
    }

    /**
     * Registers a new modifier
     *
     * @param modifier to register
     */
    public void addModifier(Modifier modifier) {
        if (modifier != null && !modifiers.contains(modifier)) {
            modifiers.add(modifier);
        }
    }

    /**
     * Retrieves the global speed multiplier
     *
     * @return the speed multiplier
     */
    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    /**
     * Sets the global speed multiplier
     *
     * @param multiplier of the world update speed
     */
    public void setSpeedMultiplier(double multiplier) {
        speedMultiplier = multiplier;
    }

    /**
     * Disables all automators
     */
    public void disableAutomators() {
        updateAutomators = false;
    }

    /**
     * Enables all automators
     */
    public void enableAutomators() {
        updateAutomators = true;
    }

    /**
     * Removes an automator from the world
     *
     * @param automator to remove
     */
    public void removeAutomator(Automator automator) {
        if (automator != null) {
            automators.remove(automator);
        }
    }

    /**
     * Retrieves all the automators registered in the world
     *
     * @return list of automators
     */
    public List<Automator> getAutomators() {
        return automators;
    }

    /**
     * Retrieves all the modifiers registered in the world
     *
     * @return list of modifiers
     */
    public List<Modifier> getModifiers() {
        return modifiers;
    }

    /**
     * Removes a modifier from the world
     *
     * @param modifier to remove
     */
    public void removeModifier(Modifier modifier) {
        if (modifier != null) {
            modifiers.remove(modifier);
        }
    }

    /**
     * Queries whether or not the automators are enabled.
     *
     * @return True if automation is enabled, false otherwise.
     */
    public boolean isAutomationEnabled() {
        return updateAutomators;
    }
}
