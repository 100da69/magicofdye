package com.magicofdye;

import java.util.HashMap;
import java.util.Map;

public final class DyeRegistry {
    private static final Map<String, Hue> HUE_BY_DYE = new HashMap<>();
    private static final Map<String, Integer> KEY_TONE_DELTA = new HashMap<>();
    private static final Map<String, Integer> MAIN_TONE_DELTA = new HashMap<>();

    static {
        registerHue(Hue.RED, "dye_depot:maroon_dye", "minecraft:red_dye", "dye_depot:rose_dye", "dye_depot:coral_dye");
        registerHue(Hue.BLUE, "dye_depot:navy_dye", "dye_depot:slate_dye", "minecraft:blue_dye", "minecraft:light_blue_dye");
        registerHue(Hue.YELLOW, "dye_depot:olive_dye", "dye_depot:amber_dye", "minecraft:yellow_dye", "dye_depot:beige_dye");
        registerHue(Hue.GREEN, "dye_depot:verdant_dye", "minecraft:green_dye", "minecraft:lime_dye", "dye_depot:forest_dye");
        registerHue(Hue.ORANGE, "minecraft:brown_dye", "dye_depot:ginger_dye", "minecraft:orange_dye", "dye_depot:tan_dye");
        registerHue(Hue.MAGENTA, "dye_depot:indigo_dye", "minecraft:purple_dye", "minecraft:magenta_dye", "minecraft:pink_dye");
        registerHue(Hue.CYAN, "dye_depot:teal_dye", "minecraft:cyan_dye", "dye_depot:mint_dye", "dye_depot:aqua_dye");

        KEY_TONE_DELTA.put("minecraft:black_dye", 2);
        KEY_TONE_DELTA.put("minecraft:gray_dye", 1);
        KEY_TONE_DELTA.put("minecraft:light_gray_dye", -1);
        KEY_TONE_DELTA.put("minecraft:white_dye", -2);

        MAIN_TONE_DELTA.put("dye_depot:maroon_dye", 2);
        MAIN_TONE_DELTA.put("minecraft:red_dye", 1);
        MAIN_TONE_DELTA.put("dye_depot:rose_dye", -1);
        MAIN_TONE_DELTA.put("dye_depot:coral_dye", -2);
        MAIN_TONE_DELTA.put("dye_depot:navy_dye", 2);
        MAIN_TONE_DELTA.put("dye_depot:slate_dye", 1);
        MAIN_TONE_DELTA.put("minecraft:blue_dye", -1);
        MAIN_TONE_DELTA.put("minecraft:light_blue_dye", -2);
        MAIN_TONE_DELTA.put("dye_depot:olive_dye", 2);
        MAIN_TONE_DELTA.put("dye_depot:amber_dye", 1);
        MAIN_TONE_DELTA.put("minecraft:yellow_dye", -1);
        MAIN_TONE_DELTA.put("dye_depot:beige_dye", -2);
        MAIN_TONE_DELTA.put("dye_depot:verdant_dye", 2);
        MAIN_TONE_DELTA.put("minecraft:green_dye", 1);
        MAIN_TONE_DELTA.put("minecraft:lime_dye", -1);
        MAIN_TONE_DELTA.put("dye_depot:forest_dye", -2);
        MAIN_TONE_DELTA.put("minecraft:brown_dye", 2);
        MAIN_TONE_DELTA.put("dye_depot:ginger_dye", 1);
        MAIN_TONE_DELTA.put("minecraft:orange_dye", -1);
        MAIN_TONE_DELTA.put("dye_depot:tan_dye", -2);
        MAIN_TONE_DELTA.put("dye_depot:indigo_dye", 2);
        MAIN_TONE_DELTA.put("minecraft:purple_dye", 1);
        MAIN_TONE_DELTA.put("minecraft:magenta_dye", -1);
        MAIN_TONE_DELTA.put("minecraft:pink_dye", -2);
        MAIN_TONE_DELTA.put("dye_depot:teal_dye", 2);
        MAIN_TONE_DELTA.put("minecraft:cyan_dye", 1);
        MAIN_TONE_DELTA.put("dye_depot:mint_dye", -1);
        MAIN_TONE_DELTA.put("dye_depot:aqua_dye", -2);
    }

    private DyeRegistry() {}

    private static void registerHue(Hue hue, String... dyeIds) {
        for (String dyeId : dyeIds) {
            HUE_BY_DYE.put(dyeId, hue);
        }
    }

    public static Hue resolveHue(String dyeId) {
        return HUE_BY_DYE.get(dyeId);
    }

    public static boolean isKeyDye(String dyeId) {
        return KEY_TONE_DELTA.containsKey(dyeId);
    }

    public static int keyToneDelta(String dyeId) {
        return KEY_TONE_DELTA.getOrDefault(dyeId, 0);
    }

    public static int mainToneDelta(String dyeId) {
        return MAIN_TONE_DELTA.getOrDefault(dyeId, 0);
    }
}
