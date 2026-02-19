package com.magicofdye;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EnergyFoodItem extends Item {
    private final int useTicks;
    private final FoodCategory category;

    public EnergyFoodItem(Properties properties, int useTicks, FoodCategory category) {
        super(properties);
        this.useTicks = useTicks;
        this.category = category;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return useTicks;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        ItemStack result = super.finishUsingItem(stack, level, user);
        if (!level.isClientSide() && user instanceof Player player) {
            String dyeId = readDyeId(stack);
            EnergyState.get(player.getUUID()).applyFood(category, dyeId);
        }
        return result;
    }

    private static String readDyeId(ItemStack stack) {
        CompoundTag customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
        if (customData.contains("dye")) {
            return customData.getString("dye");
        }
        return "minecraft:red_dye";
    }
}
