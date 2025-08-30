package symbolics.division.gik;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.enums.SlabType;
import net.minecraft.client.data.*;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import symbolics.division.gik.block.CardboardBlock;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class GIKDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();
        pack.addProvider(GIKModels::new);
        pack.addProvider(RecipeProvider::new);
        pack.addProvider((output, registriesFuture) -> new ItemTagProvider(output, RegistryKeys.ITEM, registriesFuture));
        pack.addProvider((output, registriesFuture) -> new BlockTagProvider(output, RegistryKeys.BLOCK, registriesFuture));
    }

    public static class GIKModels extends FabricModelProvider {
        public GIKModels(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
//            registerCardboard(GIK.CARDBOARD, blockStateModelGenerator);
//            registerCardboard(GIK.SOAKED_CARDBOARD, blockStateModelGenerator);
//            registerVerticalCardboard(GIK.VERTICAL_CARDBOARD, blockStateModelGenerator);
//            registerVerticalCardboard(GIK.SOAKED_VERTICAL_CARDBOARD, blockStateModelGenerator);
//

            // uncomment to allow parent
//            blockStateModelGenerator.registerTrapdoor(GIK.CARDBOARD_TRAPDOOR);


//            blockStateModelGenerator.registerTrapdoor(GIK.SOAKED_CARDBOARD_TRAPDOOR);
//            blockStateModelGenerator.registerItemModel(GIK.CARDBOARD_ITEM, blockStateModelGenerator.uploadBlockItemModel(GIK.CARDBOARD_ITEM, GIK.CARDBOARD));
//            blockStateModelGenerator.registerItemModel(GIK.VERTICAL_CARDBOARD_ITEM, blockStateModelGenerator.uploadBlockItemModel(GIK.VERTICAL_CARDBOARD_ITEM, GIK.VERTICAL_CARDBOARD));

//            blockStateModelGenerator.registerParented(GIK.CARDBOARD_TRAPDOOR, GIK.SOAKED_CARDBOARD_TRAPDOOR);
            blockStateModelGenerator.registerParentedTrapdoor(GIK.CARDBOARD_TRAPDOOR, GIK.SOAKED_CARDBOARD_TRAPDOOR);
        }

        private static void registerCardboard(Block block, BlockStateModelGenerator blockStateModelGenerator) {
            Identifier id = TextureMap.getId(block);
            TextureMap textures = getBlockTexture(id, block);
            blockStateModelGenerator.blockStateCollector.accept(
                    registerCardboard(block, blockStateModelGenerator, textures)
                            .coordinate(
                                    BlockStateVariantMap.operations(CardboardBlock.AXIS)
                                            .register(Direction.Axis.Z, BlockStateModelGenerator.NO_OP)
                                            .register(Direction.Axis.X, BlockStateModelGenerator.ROTATE_Y_90)
                                            .register(Direction.Axis.Y, BlockStateModelGenerator.NO_OP)
                            )
            );
        }

        private static void registerVerticalCardboard(Block block, BlockStateModelGenerator blockStateModelGenerator) {
            Identifier id = TextureMap.getId(block);
            TextureMap textures = getBlockTexture(id, block);
            blockStateModelGenerator.blockStateCollector.accept(
                    registerCardboard(block, blockStateModelGenerator, textures)
                            .coordinate(
                                    BlockStateVariantMap.operations(CardboardBlock.AXIS)
                                            .register(Direction.Axis.Z, BlockStateModelGenerator.ROTATE_X_90)
                                            .register(Direction.Axis.X, BlockStateModelGenerator.ROTATE_X_90.then(BlockStateModelGenerator.ROTATE_Y_90))
                                            .register(Direction.Axis.Y, BlockStateModelGenerator.NO_OP)
                            )
            );
        }

        private static VariantsBlockModelDefinitionCreator registerCardboard(Block block, BlockStateModelGenerator blockStateModelGenerator, TextureMap textures) {
            Model slabModel = new Model(Optional.of(GIK.id("block/directed_slab")), Optional.empty(), TextureKey.TOP, TextureKey.SIDE, TextureKey.FRONT, TextureKey.BACK);
            Model slabModelTop = new Model(Optional.of(GIK.id("block/directed_slab_top")), Optional.of("_top"), TextureKey.TOP, TextureKey.SIDE, TextureKey.FRONT, TextureKey.BACK);

            WeightedVariant bottom = BlockStateModelGenerator.createWeightedVariant(
                    slabModel.upload(block, textures, blockStateModelGenerator.modelCollector)
            );
            WeightedVariant top = BlockStateModelGenerator.createWeightedVariant(
                    slabModelTop.upload(block, textures, blockStateModelGenerator.modelCollector)
            );

            Model blockModel = new Model(Optional.of(GIK.id("block/cardboard_orientable")), Optional.empty(), TextureKey.TOP, TextureKey.SIDE, TextureKey.FRONT, TextureKey.BACK);
            WeightedVariant full = BlockStateModelGenerator.createWeightedVariant(
                    blockModel.uploadWithoutVariant(block, "_full", textures, blockStateModelGenerator.modelCollector)
            );

            return VariantsBlockModelDefinitionCreator.of(block)
                    .with(
                            BlockStateVariantMap.models(Properties.SLAB_TYPE)
                                    .register(SlabType.BOTTOM, bottom)
                                    .register(SlabType.TOP, top)
                                    .register(SlabType.DOUBLE, full)
                    );
        }

        private static TextureMap getBlockTexture(Identifier id, Block block) {
            return new TextureMap()
                    .put(TextureKey.TEXTURE, id)
                    .put(TextureKey.FRONT, TextureMap.getSubId(block, "_corrugation"))
                    .put(TextureKey.BACK, TextureMap.getSubId(block, "_corrugation"))
                    .put(TextureKey.SIDE, TextureMap.getSubId(block, "_corrugation_side"))
                    .put(TextureKey.TOP, TextureMap.getSubId(block, "_face_board"))
                    .put(TextureKey.PARTICLE, TextureMap.getSubId(block, "_face_board_slab"));
        }


        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        }
    }

    private static class RecipeProvider extends FabricRecipeProvider {
        public RecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup wrapperLookup, RecipeExporter recipeExporter) {
            return new RecipeGenerator(wrapperLookup, recipeExporter) {

                @Override
                public void generate() {
                    offerSlabRecipe(RecipeCategory.BUILDING_BLOCKS, GIK.CARDBOARD_ITEM, Items.PAPER);
                    offerSingleOutputShapelessRecipe(GIK.CARDBOARD_ITEM, GIK.VERTICAL_CARDBOARD_ITEM, null);
                    offerSingleOutputShapelessRecipe(GIK.VERTICAL_CARDBOARD_ITEM, GIK.CARDBOARD_ITEM, null);
                    offer2x2CompactingRecipe(RecipeCategory.BUILDING_BLOCKS, GIK.CARDBOARD_TRAPDOOR, Items.PAPER);
                }
            };
        }

        @Override
        public String getName() {
            return "cardboard recipes";
        }
    }

    private static class ItemTagProvider extends FabricTagProvider<Item> {
        public ItemTagProvider(FabricDataOutput output, RegistryKey<? extends Registry<Item>> registryKey, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registryKey, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            getTagBuilder(GIK.CARDBOARD_ITEM_TAG)
                    .add(GIK.CARDBOARD_ITEM_KEY.getValue())
                    .add(GIK.VERTICAL_CARDBOARD_ITEM_KEY.getValue())
                    .add(Registries.ITEM.getId(GIK.CARDBOARD_TRAPDOOR_ITEM));

            getTagBuilder(ItemTags.TRAPDOORS)
                    .add(GIK.CARDBOARD_TRAPDOOR_ITEM.getRegistryEntry().registryKey().getValue())
            ;
        }
    }

    private static class BlockTagProvider extends FabricTagProvider<Block> {
        public BlockTagProvider(FabricDataOutput output, RegistryKey<? extends Registry<Block>> registryKey, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registryKey, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            getTagBuilder(GIK.CARDBOARD_BLOCK_TAG)
                    .add(GIK.CARDBOARD_KEY.getValue())
                    .add(GIK.VERTICAL_CARDBOARD_KEY.getValue())
                    .add(Registries.BLOCK.getId(GIK.SOAKED_CARDBOARD))
                    .add(Registries.BLOCK.getId(GIK.SOAKED_VERTICAL_CARDBOARD))
                    .add(Registries.BLOCK.getId(GIK.CARDBOARD_TRAPDOOR));

//            getTagBuilder(BlockTags.TRAPDOORS)
//                    .add(GIK.CARDBOARD_TRAPDOOR.getRegistryEntry().registryKey().getValue());

            getTagBuilder(GIK.SOAKED)
                    .add(Registries.BLOCK.getId(GIK.SOAKED_CARDBOARD))
                    .add(Registries.BLOCK.getId(GIK.SOAKED_VERTICAL_CARDBOARD))
                    .add(Registries.BLOCK.getId(GIK.SOAKED_CARDBOARD_TRAPDOOR));
        }
    }
}
