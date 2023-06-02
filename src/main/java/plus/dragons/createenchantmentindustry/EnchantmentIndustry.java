package plus.dragons.createenchantmentindustry;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plus.dragons.createdragonlib.advancement.AdvancementFactory;
import plus.dragons.createdragonlib.init.SafeRegistrate;
import plus.dragons.createdragonlib.lang.Lang;
import plus.dragons.createdragonlib.lang.LangFactory;
import plus.dragons.createdragonlib.tag.TagGen;
import plus.dragons.createenchantmentindustry.compat.apotheosis.ApotheosisCompat;
import plus.dragons.createenchantmentindustry.content.contraptions.fluids.OpenEndedPipeEffects;
import plus.dragons.createenchantmentindustry.entry.*;
import plus.dragons.createenchantmentindustry.foundation.advancement.CeiAdvancements;
import plus.dragons.createenchantmentindustry.foundation.config.CeiConfigs;
import plus.dragons.createenchantmentindustry.foundation.ponder.content.CeiPonderIndex;

@Mod(EnchantmentIndustry.ID)
public class EnchantmentIndustry {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String NAME = "Create: Enchantment Industry";
    public static final String ID = "create_enchantment_industry";
    public static final SafeRegistrate REGISTRATE = new SafeRegistrate(ID);
    public static final Lang LANG = new Lang(ID);
    public static final AdvancementFactory ADVANCEMENT_FACTORY = AdvancementFactory.create(NAME, ID,
        CeiAdvancements::register);
    private static final LangFactory LANG_FACTORY = LangFactory.create(NAME, ID)
        .advancements(CeiAdvancements::register)
        .ponders(() -> {
            CeiPonderIndex.register();
            CeiPonderIndex.registerTags();
        })
        .tooltips()
        .ui();

    public EnchantmentIndustry() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        
        CeiConfigs.register(ModLoadingContext.get());
        
        registerEntries(modEventBus);
        modEventBus.register(this);
        modEventBus.addListener(EventPriority.LOWEST, ADVANCEMENT_FACTORY::datagen);
        modEventBus.addListener(EventPriority.LOWEST, LANG_FACTORY::datagen);
        registerForgeEvents(forgeEventBus);
        new TagGen.Builder(REGISTRATE)
                .addItemTagFactory(CeiTags::genItemTag)
                .addFluidTagFactory(CeiTags::genFluidTag)
                .build().activate();
        
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> EnchantmentIndustryClient::new);
    }

    private void registerEntries(IEventBus modEventBus) {
        CeiBlocks.register();
        CeiBlockEntities.register();
        CeiContainerTypes.register();
        CeiEntityTypes.register();
        CeiFluids.register();
        CeiItems.register();
        CeiRecipeTypes.register(modEventBus);
        CeiTags.register();
        REGISTRATE.registerEventListeners(modEventBus);
    }

    private void registerForgeEvents(IEventBus forgeEventBus) {
        forgeEventBus.addListener(CeiBlockEntities::remap);
        forgeEventBus.addListener(CeiBlocks::remap);
        forgeEventBus.addListener(CeiItems::remap);
        forgeEventBus.addListener(CeiItems::fillCreateItemGroup);
        forgeEventBus.addListener(CeiFluids::handleInkEffect);
        forgeEventBus.addListener(EnchantmentIndustry::appendExpDropOnCrushingWheelKill);
    }
    
    @SubscribeEvent
    public void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CeiAdvancements.register();
            CeiPackets.registerPackets();
            CeiFluids.registerLavaReaction();
            OpenEndedPipeEffects.register();
            ApotheosisCompat.addPotionMixingRecipes();
        });
    }

    public static ResourceLocation genRL(String name) {
        return new ResourceLocation(ID, name);
    }

    @SuppressWarnings("all")
    public static void appendExpDropOnCrushingWheelKill(LivingDropsEvent event){
        if (event.getSource() != CrushingWheelBlockEntity.DAMAGE_SOURCE)
            return;
        if(event.getDrops().isEmpty()) return;
        if(Math.random()<CeiConfigs.SERVER.crushingWheelDropExpRate.get()){
            var sample = event.getDrops().stream().findAny().get();
            event.getDrops().add(new ItemEntity(sample.level,sample.getX(),sample.getY(),sample.getZ(),
                    new ItemStack(AllItems.EXP_NUGGET.get()),sample.getDeltaMovement().x,sample.getDeltaMovement().y,sample.getDeltaMovement().z));
        }
    }

}
