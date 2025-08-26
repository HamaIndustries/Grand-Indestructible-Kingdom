package symbolics.division.gik;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import symbolics.division.gik.block.CardboardBlock;
import symbolics.division.gik.block.VerticalCardboardBlock;
import symbolics.division.gik.compat.AntisoakingAreaComponent;

import java.util.List;
import java.util.function.Function;

public class GIK implements ModInitializer {
    public static final String MOD_ID = "gik";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Identifier id(String id) {
        return Identifier.of(MOD_ID, id);
    }

    public static final RegistryKey<Block> CARDBOARD_KEY = RegistryKey.of(RegistryKeys.BLOCK, id("cardboard"));
    public static final Block CARDBOARD = Registry.register(Registries.BLOCK, CARDBOARD_KEY, new CardboardBlock(AbstractBlock.Settings.create().registryKey(CARDBOARD_KEY)));
    public static final RegistryKey<Item> CARDBOARD_ITEM_KEY = RegistryKey.of(RegistryKeys.ITEM, id("cardboard"));
    public static final Item CARDBOARD_ITEM = Registry.register(Registries.ITEM, CARDBOARD_ITEM_KEY, new BlockItem(CARDBOARD, new Item.Settings().registryKey(CARDBOARD_ITEM_KEY)));
    public static final Block SOAKED_CARDBOARD = registerBlock("soaked_cardboard", CardboardBlock::new);

    public static final RegistryKey<Block> VERTICAL_CARDBOARD_KEY = RegistryKey.of(RegistryKeys.BLOCK, id("vertical_cardboard"));
    public static final Block VERTICAL_CARDBOARD = Registry.register(Registries.BLOCK, VERTICAL_CARDBOARD_KEY, new VerticalCardboardBlock(AbstractBlock.Settings.create().registryKey(VERTICAL_CARDBOARD_KEY)));
    public static final RegistryKey<Item> VERTICAL_CARDBOARD_ITEM_KEY = RegistryKey.of(RegistryKeys.ITEM, id("vertical_cardboard"));
    public static final Item VERTICAL_CARDBOARD_ITEM = Registry.register(Registries.ITEM, VERTICAL_CARDBOARD_ITEM_KEY, new BlockItem(VERTICAL_CARDBOARD, new Item.Settings().registryKey(VERTICAL_CARDBOARD_ITEM_KEY)));
    public static final Block SOAKED_VERTICAL_CARDBOARD = registerBlock("soaked_vertical_cardboard", VerticalCardboardBlock::new);

    public static final Block CARDBOARD_TRAPDOOR = registerBlock("cardboard_trapdoor", settings -> new TrapdoorBlock(BlockSetType.OAK, settings));
    public static final Item CARDBOARD_TRAPDOOR_ITEM = registerItem("cardboard_trapdoor", settings -> new BlockItem(CARDBOARD_TRAPDOOR, settings));

    public static final TagKey<Block> CARDBOARD_BLOCK_TAG = TagKey.of(RegistryKeys.BLOCK, Identifier.of("c", "cardboard"));
    public static final TagKey<Item> CARDBOARD_ITEM_TAG = TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "cardboard"));

    public static Block registerBlock(String name, Function<AbstractBlock.Settings, Block> blockProvider) {
        Identifier id = id(name);
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        return Registry.register(Registries.BLOCK, key, blockProvider.apply(AbstractBlock.Settings.create().registryKey(key)));
    }

    public static Item registerItem(String name, Function<Item.Settings, Item> itemProvider) {
        Identifier id = id(name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        return Registry.register(Registries.ITEM, id, itemProvider.apply(new Item.Settings().registryKey(key)));
    }

    ItemGroup HOME_DEPOT = Registry.register(
            Registries.ITEM_GROUP,
            id(MOD_ID),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemGroup.gik"))
                    .icon(CARDBOARD_ITEM::getDefaultStack)
                    .entries((context, entries) -> {
                        entries.add(CARDBOARD_ITEM);
                        entries.add(VERTICAL_CARDBOARD_ITEM);
                        entries.add(CARDBOARD_TRAPDOOR_ITEM);
                    }).build()
    );


    @Override
    public void onInitialize() {
        ServerTickEvents.START_SERVER_TICK.register(GIK::tick);
        AntisoakingAreaComponent.register();
    }

    private static final List<Pair<Integer, Runnable>> actions = new ReferenceArrayList<>();

    public static void schedule(Runnable cb, int ticks) {
        actions.add(new Pair<>(ticks, cb));
    }

    private static void tick(MinecraftServer server) {
        var done = actions.stream().filter(
                action -> {
                    action.setLeft(action.getLeft() - 1);
                    if (action.getLeft() <= 0) {
                        action.getRight().run();
                        return true;
                    }
                    return false;
                }
        ).toList();
        actions.removeAll(done);
    }
}