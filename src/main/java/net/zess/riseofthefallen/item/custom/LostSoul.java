package net.zess.riseofthefallen.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.zess.riseofthefallen.block.ModBlocks;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;


import java.util.List;
import java.util.Objects;

public class LostSoul extends Item {
    public LostSoul(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        if (!pContext.getLevel().isClientSide()) {
            BlockPos positionClicked = pContext.getClickedPos();
            BlockState state = pContext.getLevel().getBlockState(positionClicked.below(0));

            if (isHolyShrineBlock(state)) {
                if (setClosestSpectatorToSurvival(pContext.getLevel(), positionClicked.getX(), positionClicked.getY(), positionClicked.getZ())) {
                    pContext.getItemInHand().hurtAndBreak(1, Objects.requireNonNull(pContext.getPlayer()), player -> player.broadcastBreakEvent(player.getUsedItemHand()));
                }
            }
        }

        return  InteractionResult.SUCCESS;
    }

    private boolean isHolyShrineBlock(BlockState state) {
        return state.is(ModBlocks.HOLY_SHRINE.get());
    }

    // This function will find the closest player in spectator mode and change their game mode to Survival
    public static boolean setClosestSpectatorToSurvival(Level world, double x, double y, double z) {
        // Get the list of players in the world
        List<Player> players = (List<Player>) (List<?>) world.players();

        // Variable to track the closest player
        Player closestPlayer = null;
        double closestDistance = Double.MAX_VALUE;

        // Loop through all players to find the closest one in spectator mode
        for (Player player : players) {
            if (player instanceof ServerPlayer serverPlayer && player.isSpectator()) {
                double distance = player.distanceToSqr(x, y, z); // Distance to the given coordinates (e.g., a position in the world)
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestPlayer = player;
                }
            }
        }

        // If a closest player is found in spectator mode, change their game mode to survival
        if (closestPlayer != null) {
            (closestPlayer).teleportTo(x, y + 8, z);

            ReviveEffects(closestPlayer, world, x, y + 8, z);

            ((ServerPlayer) closestPlayer).setGameMode(GameType.SURVIVAL);
            return true;
        }

        return false;
    }

    private static void ReviveEffects(Player closestPlayer, Level world, double x, double y, double z) {

        world.playSound(null, x, y, z, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.5f, 1.0f);

        MobEffectInstance speedEffect = new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200,1);
        MobEffectInstance regenEffect = new MobEffectInstance(MobEffects.REGENERATION, 1200,1);
        MobEffectInstance shieldEffect = new MobEffectInstance(MobEffects.HEALTH_BOOST, 1200,2);
        MobEffectInstance slowEffect = new MobEffectInstance(MobEffects.SLOW_FALLING, 100,2);

        closestPlayer.addEffect(speedEffect);
        closestPlayer.addEffect(regenEffect);
        closestPlayer.addEffect(shieldEffect);
        closestPlayer.addEffect(slowEffect);
    }

}
