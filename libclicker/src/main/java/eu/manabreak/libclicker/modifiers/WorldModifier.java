package eu.manabreak.libclicker.modifiers;

import eu.manabreak.libclicker.World;

/**
 * Modifier for worlds
 */
public class WorldModifier extends Modifier {
    double speedMultiplier;
    boolean disableActivators;

    private double speedMultiplierBefore;
    private double speedMultiplierAfter;

    public WorldModifier(World world) {
        super(world);
    }

    @Override
    protected void onEnable() {
        if (speedMultiplier != 1.0) {
            speedMultiplierBefore = getWorld().getSpeedMultiplier();
            speedMultiplierAfter = speedMultiplier * speedMultiplierBefore;
            world.setSpeedMultiplier(speedMultiplierAfter);
        }

        if (disableActivators) {
            getWorld().disableAutomators();
        }
    }

    @Override
    protected void onDisable() {
        if (speedMultiplier != 1.0) {
            double d = getWorld().getSpeedMultiplier();
            d /= speedMultiplier;
            getWorld().setSpeedMultiplier(d);
        }

        if (disableActivators) {
            getWorld().enableAutomators();
        }
    }
}
