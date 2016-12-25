package eu.manabreak.libclicker.modifiers;

import eu.manabreak.libclicker.World;
import eu.manabreak.libclicker.generators.Generator;

/**
 * Modifier for generators.
 */
public class GeneratorModifier extends Modifier {
    private final Generator generator;
    double multiplier = 1.0;

    GeneratorModifier(World world, Generator generator) {
        super(world);
        this.generator = generator;
    }

    @Override
    protected void onEnable() {
        generator.attachModifier(this);
    }

    @Override
    protected void onDisable() {
        generator.detachModifier(this);
    }

    public double getMultiplier() {
        return multiplier;
    }
}
