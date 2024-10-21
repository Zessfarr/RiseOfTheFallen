package net.zess.riseofthefallen.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.zess.riseofthefallen.block.ModBlocks;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;


import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

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

        return InteractionResult.SUCCESS;
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
            if (player instanceof ServerPlayer serverPlayer && serverPlayer.isSpectator()) {
                double distance = player.distanceToSqr(x, y, z); // Distance to the given coordinates (e.g., a position in the world)
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestPlayer = player;
                }
            }
        }

        // If a closest player is found in spectator mode, change their game mode to survival
        if (closestPlayer != null) {
            closestPlayer.teleportTo(x, y + 8, z);
            ((ServerPlayer) closestPlayer).setGameMode(GameType.SURVIVAL);
            ReviveEffects(closestPlayer, world, x, y + 8, z);
            return true;
        }
        return false;
    }

    private static void ReviveEffects(Player closestPlayer, Level world, double x, double y, double z) {
        if (world instanceof ServerLevel) {
            ((ServerLevel) world).sendParticles(ParticleTypes.ENCHANT, closestPlayer.getX(), closestPlayer.getY(), closestPlayer.getZ(), 200, 1, 1, 1, 0.0);
            ((ServerLevel) world).sendParticles(ParticleTypes.EXPLOSION, closestPlayer.getX(), closestPlayer.getY(), closestPlayer.getZ(), 25, 1, 1, 1, 0.0);
            ((ServerLevel) world).sendParticles(ParticleTypes.CRIT, closestPlayer.getX(), closestPlayer.getY(), closestPlayer.getZ(), 200, 2.0, 2.0, 2.0, 0.0);
        }

        world.playSound(null, x, y, z, SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 0.1f, 1.0f);


        // Freeze in mid-air and make invincible
        closestPlayer.setNoGravity(true);
        closestPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 4, false, false));

        // Schedule a task to apply Slow Falling effect after 3 seconds
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                world.playSound(null, x, y, z, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.9f, 1.0f);
                closestPlayer.setNoGravity(false);
                closestPlayer.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 40, 0));
                world.playSound(null, x, y, z, SoundEvents.AMETHYST_BLOCK_STEP, SoundSource.PLAYERS, 1f, 1.0f);
            }
        }, 1000); // 1 seconds delay



        closestPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 1));
        closestPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1200, 1));
        closestPlayer.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 1200, 2));
        closestPlayer.sendSystemMessage(Component.literal("You have been blessed with a second chance!"));
        closestPlayer.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 1)); // Glowing effect for extra visibility
    }


}
