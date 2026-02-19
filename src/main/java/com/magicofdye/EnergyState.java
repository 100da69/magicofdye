package com.magicofdye;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnergyState {
    private static final Map<UUID, EnergyState> STATES = new HashMap<>();
    private static long tickCounter = 0;

    private final EnumMap<Hue, Integer> hue = new EnumMap<>(Hue.class);
    private int tonePoints = 10;
    private Tone previousToneAtTen = Tone.NULL;
    private int keyTicks = 0;
    private float absorbedDamage = 0f;

    private EnergyState() {
        for (Hue h : Hue.values()) {
            if (h != Hue.WHITE) {
                hue.put(h, 0);
            }
        }
    }

    public static EnergyState get(UUID playerId) {
        return STATES.computeIfAbsent(playerId, ignored -> new EnergyState());
    }

    public static void tickServer(MinecraftServer server) {
        tickCounter++;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            EnergyState state = get(player.getUUID());
            if (tickCounter % 60 == 0) {
                state.decay();
            }
            state.tickKey(player);
        }
    }

    public void applyFood(FoodCategory category, String dyeId) {
        Hue target = DyeRegistry.resolveHue(dyeId);
        switch (category) {
            case SIDE_DISH -> tonePoints = clamp(tonePoints + DyeRegistry.keyToneDelta(dyeId), 0, 20);
            case CARBS -> {
                if (target != null) addHue(target, 4);
            }
            case DRINK -> {
                if (target != null) {
                    for (Hue h : hue.keySet()) {
                        addHue(h, h == target ? 6 : -1);
                    }
                }
            }
            case MAIN_DISH -> {
                if (target != null) {
                    addHue(target, 2);
                    tonePoints = clamp(tonePoints + DyeRegistry.mainToneDelta(dyeId), 0, 20);
                }
            }
        }
        capturePreviousToneIfNeeded();
        if (hue.values().stream().allMatch(v -> v >= 20)) {
            keyTicks = 200;
            absorbedDamage = 0f;
        }
    }

    private void capturePreviousToneIfNeeded() {
        if (tonePoints != 10) {
            Tone resolved = toneFromPoints();
            if (resolved != Tone.NULL && resolved != Tone.KEY) {
                previousToneAtTen = resolved;
            }
        }
    }

    public void decay() {
        for (Hue h : hue.keySet()) {
            addHue(h, -1);
        }
        if (keyTicks <= 0) {
            tonePoints = clamp(tonePoints - 1, 0, 20);
            capturePreviousToneIfNeeded();
        }
    }

    public void drainAll() {
        for (Hue h : hue.keySet()) {
            hue.put(h, 0);
        }
        tonePoints = 10;
        previousToneAtTen = Tone.NULL;
        keyTicks = 0;
        absorbedDamage = 0f;
    }

    public boolean isKeyActive() {
        return keyTicks > 0;
    }

    public void absorbKeyDamage(float amount) {
        absorbedDamage += amount;
    }

    public void tickKey(ServerPlayer player) {
        if (keyTicks <= 0) return;
        keyTicks--;
        if (keyTicks == 0 && absorbedDamage > 0f) {
            player.hurt(player.damageSources().indirectMagic(player, player), absorbedDamage * 0.8f);
            absorbedDamage = 0f;
        }
    }

    public Hue currentColor() {
        Hue[] priority = {Hue.RED, Hue.BLUE, Hue.YELLOW, Hue.GREEN, Hue.ORANGE, Hue.MAGENTA, Hue.CYAN};
        int max = 0;
        Hue selected = Hue.WHITE;
        for (Hue h : priority) {
            int value = hue.getOrDefault(h, 0);
            if (value > max) {
                max = value;
                selected = h;
            }
        }
        return selected;
    }

    public Tone currentTone() {
        if (currentColor() == Hue.WHITE) return Tone.NULL;
        if (keyTicks > 0) return Tone.KEY;
        return toneFromPoints();
    }

    private Tone toneFromPoints() {
        if (tonePoints <= 4) return Tone.LIGHT;
        if (tonePoints <= 9) return Tone.BASE;
        if (tonePoints == 10) return previousToneAtTen;
        if (tonePoints <= 15) return Tone.TINTED;
        return Tone.DARK;
    }

    public String describe() {
        return "Hue=" + hue + ", Color=" + currentColor() + ", Tone=" + currentTone() + ", tonePoints=" + tonePoints + ", keyTicks=" + keyTicks;
    }

    private void addHue(Hue h, int delta) {
        if (h == Hue.WHITE) return;
        hue.put(h, clamp(hue.getOrDefault(h, 0) + delta, 0, 20));
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
