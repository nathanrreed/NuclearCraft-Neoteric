package igentuman.nc.multiblock.kugelblitz;

import igentuman.nc.block.entity.kugelblitz.BlackHoleBE;
import igentuman.nc.block.entity.kugelblitz.ChamberPortBE;
import igentuman.nc.block.entity.kugelblitz.ChamberTerminalBE;
import igentuman.nc.block.kugelblitz.ChamberPortBlock;
import igentuman.nc.block.kugelblitz.ChamberTerminalBlock;
import igentuman.nc.container.ChamberPortContainer;
import igentuman.nc.container.ChamberTerminalContainer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.HashMap;

import static igentuman.nc.setup.registration.NCItems.ALL_NC_ITEMS;
import static igentuman.nc.setup.registration.Registries.*;
import static igentuman.nc.setup.registration.Tags.blockTag;
import static igentuman.nc.setup.registration.Tags.itemTag;

public class KugelblitzRegistration {

    public static final Item.Properties KUGELBLITZ_ITEM_PROPERTIES = new Item.Properties();
    public static final Block.Properties KUGELBLITZ_BLOCK_PROPERTIES = BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(4f).requiresCorrectToolForDrops();
    ;
    public static HashMap<String, DeferredHolder<BlockEntityType<?>, BlockEntityType<? extends BlockEntity>>> KUGELBLITZ_BE = new HashMap<>();
    public static HashMap<String, DeferredItem<Item>> KUGELBLITZ_ITEMS = new HashMap<>();
    public static HashMap<String, DeferredBlock<Block>> KUGELBLITZ_BLOCKS = new HashMap<>();
    public static TagKey<Block> CASING_BLOCKS = blockTag("kugelblitz_casing");
    public static TagKey<Item> CASING_ITEMS = itemTag("kugelblitz_casing");

    public static final DeferredHolder<MenuType<?>, MenuType<ChamberTerminalContainer>> CHAMBER_TERMINAL_CONTAINER = CONTAINERS.register("chamber_terminal",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new ChamberTerminalContainer(windowId, data.readBlockPos(), inv))
    );
    public static final DeferredHolder<MenuType<?>, MenuType<ChamberPortContainer>> CHAMBER_PORT_CONTAINER = CONTAINERS.register("chamber_port",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new ChamberPortContainer(windowId, data.readBlockPos(), inv))
    );

    /*
    1. Photon Concentrator
    2. Event Horizon Stabilizer
    3. Neutronium Frame
    4. Chamber port
    5. Chamber terminal
    6. Quantum Flux Regulator
    7. Quantum transformer
    */
    public static void init() {
        registerSimpleBlock("neutronium_frame");
        registerSimpleBlock("event_horizon_stabilizer");
        registerSimpleBlock("photon_concentrator");
        registerSimpleBlock("quantum_flux_regulator");
        registerSimpleBlock("quantum_transformer");

        KUGELBLITZ_BLOCKS.put("chamber_port", BLOCKS.register("chamber_port", () -> new ChamberPortBlock(KUGELBLITZ_BLOCK_PROPERTIES)));
        KUGELBLITZ_ITEMS.put("chamber_port", fromMultiblock(KUGELBLITZ_BLOCKS.get("chamber_port")));
        ALL_NC_ITEMS.put("chamber_port", KUGELBLITZ_ITEMS.get("chamber_port"));

        KUGELBLITZ_BE.put("chamber_port",
                BLOCK_ENTITIES.register("chamber_port",
                        () -> BlockEntityType.Builder.of(ChamberPortBE::new, KUGELBLITZ_BLOCKS.get("chamber_port").get())
                                .build(null)));

        KUGELBLITZ_BLOCKS.put("chamber_terminal", BLOCKS.register("chamber_terminal", () -> new ChamberTerminalBlock(KUGELBLITZ_BLOCK_PROPERTIES)));
        KUGELBLITZ_ITEMS.put("chamber_terminal", fromMultiblock(KUGELBLITZ_BLOCKS.get("chamber_terminal")));
        ALL_NC_ITEMS.put("chamber_terminal", KUGELBLITZ_ITEMS.get("chamber_terminal"));

        KUGELBLITZ_BE.put("chamber_terminal",
                BLOCK_ENTITIES.register("chamber_terminal",
                        () -> BlockEntityType.Builder.of(ChamberTerminalBE::new, KUGELBLITZ_BLOCKS.get("chamber_terminal").get())
                                .build(null)));

        KUGELBLITZ_BLOCKS.put("black_hole", BLOCKS.register("black_hole", () -> new ChamberTerminalBlock(KUGELBLITZ_BLOCK_PROPERTIES)));
        KUGELBLITZ_ITEMS.put("black_hole", fromMultiblock(KUGELBLITZ_BLOCKS.get("black_hole")));
        ALL_NC_ITEMS.put("black_hole", KUGELBLITZ_ITEMS.get("black_hole"));

        KUGELBLITZ_BE.put("black_hole",
                BLOCK_ENTITIES.register("black_hole",
                        () -> BlockEntityType.Builder.of(BlackHoleBE::new, KUGELBLITZ_BLOCKS.get("black_hole").get())
                                .build(null)));
    }

    private static void registerSimpleBlock(String key) {
        if (key.contains("photon")) {
            KUGELBLITZ_BLOCKS.put(key, BLOCKS.register(key, () -> new TransparentBlock(KUGELBLITZ_BLOCK_PROPERTIES)));
        } else {
            KUGELBLITZ_BLOCKS.put(key, BLOCKS.register(key, () -> new Block(KUGELBLITZ_BLOCK_PROPERTIES)));
        }
        KUGELBLITZ_ITEMS.put(key, fromMultiblock(KUGELBLITZ_BLOCKS.get(key)));
        ALL_NC_ITEMS.put(key, KUGELBLITZ_ITEMS.get(key));
    }

    public static <B extends Block> DeferredItem<Item> fromMultiblock(DeferredBlock<B> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), KUGELBLITZ_ITEM_PROPERTIES));
    }
}
