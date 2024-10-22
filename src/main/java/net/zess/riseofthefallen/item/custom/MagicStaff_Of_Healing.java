package net.zess.riseofthefallen.item.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class MagicStaff_Of_Healing extends Item {
    public MagicStaff_Of_Healing(Properties pProperties) {
        super(pProperties);
    }

    //This Function detects when the player uses an item that is in their hand.
    //It takes the current level the player is in, what player is using the item and what is in their hand.
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide) {

            //Damages the item by 1 durability when the player uses the item.
            itemStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(p.getUsedItemHand()));

            //Heals and Regeneration health for the players if they are in range by 30 blocks
            List<Player> nearbyPlayers = level.getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(30));

            //The for loop will affect each player in range
            for (Player nearbyPlayer : nearbyPlayers) {
                applyHealingEffects(nearbyPlayer, level);
            }
        }

        //This says hey! We were successful on using the item.
        return InteractionResultHolder.success(itemStack);
    }

    private void applyHealingEffects(Player player, Level level) {
        if (level instanceof ServerLevel) {
            ((ServerLevel) level).sendParticles(ParticleTypes.HEART, player.getX(), player.getY() + 1, player.getZ(), 50, 1.0, 1.0, 1.0, 0.1);
            ((ServerLevel) level).sendParticles(ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY() + 1, player.getZ(), 50, 1.0, 1.0, 1.0, 0.1);

            player.addEffect(new MobEffectInstance(MobEffects.HEAL, 1,1));
            player.addEffect((new MobEffectInstance(MobEffects.REGENERATION, 200, 1)));

            //Sends a message to the player they have been healed.
            player.sendSystemMessage(Component.literal("You have been blessed by the gift of health!"));

            //Plays sound effects when the Use function is called
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1f, 1f);
        }


    }
}
