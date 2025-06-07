package py660.rickcraft;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.slf4j.Logger;

import java.util.function.Function;

public class ModItems {
    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        // Create the item key.
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Rickcraft.MOD_ID, name));

        // Create the item instance.
        Item item = itemFactory.apply(settings.registryKey(itemKey));

        // Register the item.
        Registry.register(Registries.ITEM, itemKey, item);

        return item;
    }

    public static final Item PAINTING_SELECTOR = register("painting_selector", PaintingSelectorItem::new, new Item.Settings().maxCount(1));

    public static void initialize() {

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
                .register((itemGroup) -> itemGroup.add(ModItems.PAINTING_SELECTOR));

        AttackEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
            // Manual spectator check is necessary because AttackBlockCallbacks fire before the spectator check
            if (!player.isSpectator() &&
                    ItemStack.areItemsEqual(player.getStackInHand(hand), PAINTING_SELECTOR.getDefaultStack()) &&
                    entity instanceof PaintingEntity painting) {
                if (world instanceof ServerWorld serverWorld) {
                    PaintingSelectorItem.replacePainting(serverWorld, player, painting);
                    return ActionResult.SUCCESS;
                }
                else {
                    player.playSound(SoundEvents.ENTITY_PAINTING_PLACE, 1.0F, 1.0F);
                }
            }
            return ActionResult.PASS;
        });
    }
}