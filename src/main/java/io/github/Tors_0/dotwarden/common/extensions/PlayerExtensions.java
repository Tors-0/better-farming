package io.github.Tors_0.dotwarden.common.extensions;

public interface PlayerExtensions {
    int dotwarden$getPowerLevel();
    void dotwarden$setPowerLevel(int value);
    void dotwarden$addPowerLevel(int value);
    int dotwarden$getPower();
    void dotwarden$setPower(int value);
    void dotwarden$addPower(int value);
    boolean dotwarden$hasSacrificed();
    void dotwarden$setSacrifice(boolean state);
}
