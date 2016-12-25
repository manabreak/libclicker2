package eu.manabreak.libclicker.generators;

import java.math.BigInteger;

/**
 * Represents a resource that can be generated
 * by the generators.
 */
public interface Resource {
    void generate(BigInteger amount);
}
