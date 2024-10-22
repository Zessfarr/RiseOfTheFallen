package net.zess.riseofthefallen.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.zess.riseofthefallen.RiseOfTheFallen;
import net.zess.riseofthefallen.item.custom.LostSoul;
import net.zess.riseofthefallen.item.custom.MagicStaff_Of_Healing;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RiseOfTheFallen.MOD_ID);

    public static final RegistryObject<Item> LOSTSOUL = ITEMS.register("lostsoul", () -> new LostSoul(new Item.Properties().durability(1)));
    public static final RegistryObject<Item> MAGICSTAFF_OF_HEALING = ITEMS.register("magicstaff_of_healing", () -> new MagicStaff_Of_Healing(new Item.Properties().durability(100)));

    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}
