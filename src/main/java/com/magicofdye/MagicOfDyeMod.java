package com.magicofdye;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(MagicOfDyeMod.MOD_ID)
public class MagicOfDyeMod {
    public static final String MOD_ID = "magicofdye";

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredItem<Item> SIDE_DISH = ITEMS.register("side_dish", () -> new EnergyFoodItem(new Item.Properties()
            .food(new FoodProperties.Builder().alwaysEdible().build()).stacksTo(64), 16, FoodCategory.SIDE_DISH));
    public static final DeferredItem<Item> CARBS = ITEMS.register("carbs", () -> new EnergyFoodItem(new Item.Properties()
            .food(new FoodProperties.Builder().alwaysEdible().build()).stacksTo(64), 32, FoodCategory.CARBS));
    public static final DeferredItem<Item> DRINK = ITEMS.register("drink", () -> new EnergyFoodItem(new Item.Properties()
            .food(new FoodProperties.Builder().alwaysEdible().build()).stacksTo(16).craftRemainder(Items.GLASS_BOTTLE), 40, FoodCategory.DRINK));
    public static final DeferredItem<Item> MAIN_DISH = ITEMS.register("main_dish", () -> new EnergyFoodItem(new Item.Properties()
            .food(new FoodProperties.Builder().alwaysEdible().build()).stacksTo(64), 32, FoodCategory.MAIN_DISH));

    public MagicOfDyeMod(IEventBus modBus) {
        ITEMS.register(modBus);
        NeoForge.EVENT_BUS.addListener(this::onServerTickPost);
        NeoForge.EVENT_BUS.addListener(this::onLivingIncomingDamage);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    private void onServerTickPost(ServerTickEvent.Post event) {
        EnergyState.tickServer(event.getServer());
    }

    private void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player player && !player.level().isClientSide()) {
            EnergyState state = EnergyState.get(player.getUUID());
            if (state.isKeyActive()) {
                state.absorbKeyDamage(event.getAmount());
                event.setCanceled(true);
            }
        }
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("magicofdye")
                        .then(Commands.literal("status").executes(ctx -> {
                            Player player = ctx.getSource().getPlayerOrException();
                            EnergyState state = EnergyState.get(player.getUUID());
                            ctx.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal(state.describe()), false);
                            return 1;
                        }))
                        .then(Commands.literal("drain").executes(ctx -> {
                            Player player = ctx.getSource().getPlayerOrException();
                            EnergyState.get(player.getUUID()).drainAll();
                            ctx.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("All hue/tone drained."), false);
                            return 1;
                        }))
                        .then(Commands.literal("apply")
                                .then(Commands.argument("category", StringArgumentType.word())
                                        .then(Commands.argument("dye_id", StringArgumentType.string()).executes(ctx -> {
                                            Player player = ctx.getSource().getPlayerOrException();
                                            FoodCategory category = FoodCategory.valueOf(StringArgumentType.getString(ctx, "category").toUpperCase());
                                            String dyeId = StringArgumentType.getString(ctx, "dye_id");
                                            EnergyState.get(player.getUUID()).applyFood(category, dyeId);
                                            ctx.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("Applied " + category + " with " + dyeId), false);
                                            return 1;
                                        }))))
        );
    }
}
