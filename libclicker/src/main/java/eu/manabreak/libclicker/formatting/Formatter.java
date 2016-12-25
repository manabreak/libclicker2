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
package eu.manabreak.libclicker.formatting;

import eu.manabreak.libclicker.Currency;
import eu.manabreak.libclicker.Item;

/**
 * A formatter for BigInteger values.
 *
 * @author Harri Pellikka
 */
public class Formatter {

    protected final boolean groupDigits;
    protected final String thousandSeparator;
    protected final boolean showDecimals;
    protected final int decimals;
    protected final String decimalSeparator;
    protected final boolean cutAtHighest;
    protected final String[] abbreviations;

    protected Formatter(Builder builder) {
        groupDigits = builder.groupDigits;
        thousandSeparator = builder.thousandSeparator;
        showDecimals = builder.showDecimals;
        decimals = builder.decimals;
        decimalSeparator = builder.decimalSeparator;
        cutAtHighest = builder.cutAtHighest;
        abbreviations = builder.abbreviations;
    }

    public static class ForItemPrice extends Builder {
        private Item item;

        public ForItemPrice(Item item) {
            this.item = item;
        }

        @Override
        public ItemPriceFormatter build() {
            return new ItemPriceFormatter(this, item);
        }
    }

    public static class ForCurrency extends Builder {
        private Currency currency;

        public ForCurrency(Currency c) {
            currency = c;
        }

        public CurrencyFormatter build() {
            return new CurrencyFormatter(this, currency);
        }
    }

    public static abstract class Builder {
        private boolean groupDigits = true;
        private String thousandSeparator = ",";
        private boolean showDecimals = false;
        private int decimals = 2;
        private String decimalSeparator;
        private boolean cutAtHighest = true;
        private String[] abbreviations = null;

        private Builder() {

        }

        public Builder showHighestThousand() {
            cutAtHighest = true;
            return this;
        }

        public Builder showFully() {
            cutAtHighest = false;
            return this;
        }

        public Builder groupDigits() {
            return groupDigits(",");
        }

        public Builder groupDigits(String separator) {
            groupDigits = true;
            thousandSeparator = separator;
            return this;
        }

        public Builder dontGroupDigits() {
            groupDigits = false;
            thousandSeparator = null;
            return this;
        }

        public Builder showDecimals() {
            return showDecimals(2, ".");
        }

        public Builder showDecimals(int count) {
            return showDecimals(count, ".");
        }

        public Builder showDecimals(String separator) {
            return showDecimals(2, separator);
        }

        public Builder showDecimals(int count, String separator) {
            showDecimals = true;
            decimals = count;
            decimalSeparator = separator;
            return this;
        }

        public Builder dontShowDecimals() {
            showDecimals = false;
            decimals = 0;
            decimalSeparator = null;
            return this;
        }

        public Builder useAbbreviations(String[] abbreviations) {
            this.abbreviations = abbreviations;
            return this;
        }

        public abstract Formatter build();
    }


    private String rawString = "";

    public void setRawString(String raw) {
        rawString = raw;
        if (rawString == null) rawString = "";
    }

    @Override
    public String toString() {
        String raw = rawString;
        if (cutAtHighest) {
            int length = raw.length();
            if (length < 4) return raw;
            int rem = length % 3;
            rem = rem == 0 ? 3 : rem;
            String top = raw.substring(0, rem);

            if (showDecimals) {
                top += decimalSeparator;
                int decimals = Math.min(this.decimals, length - rem);
                top += raw.substring(rem, rem + decimals);
            }

            if (abbreviations != null) {
                int tri = (raw.length() - 1) / 3;
                if (tri > 0 && tri <= abbreviations.length) {
                    top += abbreviations[tri - 1];
                }
            }

            return top;
        } else {
            if (groupDigits) {
                int len = raw.length() - 3;
                for (int i = len; i > 0; --i) {
                    if ((len - i) % 3 == 0) {
                        raw = raw.substring(0, i) + thousandSeparator + raw.substring(i);
                    }
                }
            }
            return raw;
        }
    }
}
