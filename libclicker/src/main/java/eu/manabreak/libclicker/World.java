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

import eu.manabreak.libclicker.modifiers.Modifier;

/**
 * A container for all the clicker objects
 */
public class World implements Serializable {
    /**
     * Active generators
     */
    private List<Generator> generators = new ArrayList<>();

    /**
     * Active automators
     */
    private List<Automator> automators = new ArrayList<>();

    /**
     * Currencies in use
     */
    private List<Currency> currencies = new ArrayList<>();

    /**
     * Modifiers in use
     */
    private List<Modifier> modifiers = new ArrayList<>();

    /**
     * Speed multiplier - used to multiply the time the world advances
     */
    private double speedMultiplier = 1.0;

    /**
     * Should automators be updated?
     */
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
     * @param generator Generator to add
     */
    void addGenerator(Generator generator) {
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
    void removeGenerator(Generator generator) {
        if (generator != null && generators.contains(generator)) {
            generators.remove(generator);
        }
    }

    /**
     * Removes all the generators from this world
     */
    void removeAllGenerators() {
        generators.clear();
    }

    void addCurrency(Currency c) {
        if (c != null && !currencies.contains(c)) {
            currencies.add(c);
        }
    }

    void removeCurrency(Currency c) {
        if (c != null) {
            currencies.remove(c);
        }
    }

    Currency getCurrency(int index) {
        return currencies.get(index);
    }

    List<Currency> getCurrencies() {
        return currencies;
    }

    void removeAllCurrencies() {
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

    void addAutomator(Automator automator) {
        if (automator != null && !automators.contains(automator)) {
            automators.add(automator);
        }
    }

    public void addModifier(Modifier modifier) {
        if (modifier != null && !modifiers.contains(modifier)) {
            modifiers.add(modifier);
        }
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public void setSpeedMultiplier(double multiplier) {
        speedMultiplier = multiplier;
    }

    public void disableAutomators() {
        updateAutomators = false;
    }

    public void enableAutomators() {
        updateAutomators = true;
    }

    void removeAutomator(Automator automator) {
        if (automator != null) {
            automators.remove(automator);
        }
    }

    List<Automator> getAutomators() {
        return automators;
    }

    List<Modifier> getModifiers() {
        return modifiers;
    }

    public void removeModifier(Modifier modifier) {
        if (modifier != null) {
            modifiers.remove(modifier);
        }
    }

    boolean isAutomationEnabled() {
        return updateAutomators;
    }
}
