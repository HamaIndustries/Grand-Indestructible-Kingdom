package symbolics.division.gik;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.block.enums.SlabType;
import net.minecraft.client.data.*;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import symbolics.division.gik.block.CardboardBlock;

import java.util.Optional;

public class GIKDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();
        pack.addProvider(GIKModels::new);
    }

    public static class GIKModels extends FabricModelProvider {
        public GIKModels(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
            blockStateModelGenerator.blockStateCollector.accept(
                    registerCardboard(GIK.CARDBOARD, "cardboard", blockStateModelGenerator)
                            .coordinate(
                                    BlockStateVariantMap.operations(CardboardBlock.AXIS)
                                            .register(Direction.Axis.Z, BlockStateModelGenerator.NO_OP)
                                            .register(Direction.Axis.X, BlockStateModelGenerator.ROTATE_Y_90)
                                            .register(Direction.Axis.Y, BlockStateModelGenerator.NO_OP)
                            )
            );
            blockStateModelGenerator.blockStateCollector.accept(
                    registerCardboard(GIK.VERTICAL_CARDBOARD, "vertical_cardboard", blockStateModelGenerator)
                            .coordinate(
                                    BlockStateVariantMap.operations(CardboardBlock.AXIS)
                                            .register(Direction.Axis.Z, BlockStateModelGenerator.ROTATE_X_90)
                                            .register(Direction.Axis.X, BlockStateModelGenerator.ROTATE_X_90.then(BlockStateModelGenerator.ROTATE_Y_90))
                                            .register(Direction.Axis.Y, BlockStateModelGenerator.NO_OP)
                            )
            );

            blockStateModelGenerator.registerTrapdoor(GIK.CARDBOARD_TRAPDOOR);
            blockStateModelGenerator.registerItemModel(GIK.CARDBOARD_ITEM, blockStateModelGenerator.uploadBlockItemModel(GIK.CARDBOARD_ITEM, GIK.CARDBOARD));
            blockStateModelGenerator.registerItemModel(GIK.VERTICAL_CARDBOARD_ITEM, blockStateModelGenerator.uploadBlockItemModel(GIK.VERTICAL_CARDBOARD_ITEM, GIK.VERTICAL_CARDBOARD));
        }

        private static VariantsBlockModelDefinitionCreator registerCardboard(Block block, String name, BlockStateModelGenerator blockStateModelGenerator) {
            Identifier id = TextureMap.getId(block);
            TextureMap textures = getBlockTexture(id, block);
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

//            blockStateModelGenerator.registerItemModel(block);

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
//            itemModelGenerator.register
//			itemModelGenerator.registerWithTextureSource(GIK.CARDBOARD_ITEM, Items.OAK_LOG, Models.GENERATED);
        }
    }
}
