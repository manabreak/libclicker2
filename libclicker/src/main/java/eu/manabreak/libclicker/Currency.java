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

/**
 * Base class for all currencies.
 *
 * @author Harri Pellikka
 */
public class Currency implements Serializable {
    /**
     * Name of this currency
     */
    private String name;

    /**
     * Huge number to hold the amount for this currency
     */
    private BigInteger value = BigInteger.ZERO;

    private final World world;

    public static class Builder {
        private final World world;
        private String name = "Gold";

        public Builder(World world) {
            this.world = world;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Currency build() {
            Currency c = new Currency(world, name);
            world.addCurrency(c);
            return c;
        }
    }

    /**
     * Constructs a new currency with initial amount of 0
     *
     * @param world to add this currency into
     * @param name  of the currency
     */
    private Currency(World world, String name) {
        this.world = world;
        this.name = name;
    }

    /**
     * Retrieves the name of this currency
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    public String getAmountAsString() {
        return value.toString();
    }

    @Override
    public String toString() {
        return name + ": " + getAmountAsString();
    }

    public BigInteger getValue() {
        return value;
    }

    public void add(BigInteger other) {
        value = value.add(other);
    }

    public void sub(BigInteger other) {
        value = value.subtract(other);
    }

    public void multiply(double multiplier) {
        BigDecimal tmp = new BigDecimal(value);
        tmp = tmp.multiply(new BigDecimal(multiplier));
        value = tmp.toBigInteger();
    }

    void set(BigInteger newValue) {
        value = newValue;
    }
}
