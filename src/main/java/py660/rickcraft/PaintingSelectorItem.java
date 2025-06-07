package py660.rickcraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PaintingSelectorItem extends Item {
    public PaintingSelectorItem(Settings settings) {
        super(settings);
    }

    //public static final String MOD_ID = "rickcraft";
    //public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void replacePainting(ServerWorld world, PlayerEntity player, PaintingEntity painting){
        PaintingVariant origVariant = painting.getVariant().value();
        //LOGGER.info(String.valueOf(origVariant));
        BlockPos attachedBlockPos = painting.getAttachedBlockPos();
        Direction facing = painting.getFacing();
        int paintingWidth = origVariant.width();
        int paintingHeight = origVariant.height();

        final DynamicRegistryManager registryManager = world.getRegistryManager();
        Registry<PaintingVariant> variantRegistry = registryManager.getOrThrow(RegistryKey.ofRegistry(Identifier.of("minecraft:painting_variant")));
        RegistryEntry.Reference<PaintingVariant> variantEntry = variantRegistry.getEntry(Identifier.of(Rickcraft.MOD_ID, "rickroll" + String.valueOf(paintingWidth) + String.valueOf(paintingHeight))).get();

        painting.remove(Entity.RemovalReason.DISCARDED);
        PaintingEntity rickroll = new PaintingEntity(world, attachedBlockPos, facing, variantEntry);
        world.spawnEntity(rickroll);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient) {
            player.playSound(SoundEvents.ENTITY_PAINTING_PLACE, 1.0F, 1.0F);
            return ActionResult.PASS;
        }
        int distanceFromPlayerMax = 5;
        Box myBox = new Box(player.getBlockPos()).expand(distanceFromPlayerMax);
        List<PaintingEntity> entityList = player.getWorld().getEntitiesByClass(PaintingEntity.class, myBox, e->true);
        for (PaintingEntity painting : entityList){
            replacePainting((ServerWorld) world, player, painting);
        }
        return ActionResult.SUCCESS;
    }
}