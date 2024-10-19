package net.zess.riseofthefallen.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.zess.riseofthefallen.RiseOfTheFallen;
import net.zess.riseofthefallen.block.ModBlocks;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RiseOfTheFallen.MOD_ID);

    public static final RegistryObject<CreativeModeTab> RISEOFTHEFALLEN_TAB = CREATIVE_MODE_TABS.register("riseofthefallen_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.LOSTSOUL.get()))
            .title(Component.translatable("creativetab.riseofthefallen_tab"))
            .displayItems((pParameters, pOutput) -> {
                pOutput.accept(ModItems.LOSTSOUL.get());

                pOutput.accept(ModBlocks.HOLY_SHRINE.get());
            })
            .build());

    public static void register(IEventBus eventbus){
        CREATIVE_MODE_TABS.register(eventbus);
    }
}
