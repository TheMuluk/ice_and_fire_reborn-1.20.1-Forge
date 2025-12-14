package net.muluk;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.muluk.iceandfirereborn.item.ModItems;
import org.slf4j.Logger;

@Mod(IceAndFireReborn.MOD_ID)
public class IceAndFireReborn {

    public static final String MOD_ID = "iceandfirereborn";
    private static final Logger LOGGER = LogUtils.getLogger();

    public IceAndFireReborn(FMLJavaModLoadingContext context) {

        IEventBus modEventBus = context.getModEventBus();
        ModItems.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {}

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() != null) {
            ResourceLocation tabLoc = event.getTabKey().location();
            if ("iceandfire".equals(tabLoc.getNamespace()) && "items".equals(tabLoc.getPath())) {
                event.accept(ModItems.DRAGON_SADDLE.get());
            }
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {}

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {}
    }
}
