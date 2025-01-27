package igentuman.nc.setup.registration;

import igentuman.nc.NuclearCraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import static igentuman.nc.NuclearCraft.rl;
import static igentuman.nc.setup.registration.Registries.SOUND_EVENTS;

public final class NCSounds {

    private NCSounds() {
    }

    public static final List<Supplier<SoundEvent>> GEIGER_SOUNDS = initGeigerSounds();
    public static final Supplier<SoundEvent> ITEM_CHARGED = SOUND_EVENTS.register("charge_energy", () -> SoundEvent.createVariableRangeEvent(rl("charge_energy")));
    public static final Supplier<SoundEvent> FUSION_CHARGING = SOUND_EVENTS.register("tile.fusion_charging", () -> SoundEvent.createVariableRangeEvent(rl("tile.fusion_charging")));
    public static final Supplier<SoundEvent> FUSION_READY = SOUND_EVENTS.register("tile.fusion_ready", () -> SoundEvent.createVariableRangeEvent(rl("tile.fusion_ready")));
    public static final Supplier<SoundEvent> FUSION_RUNNING = SOUND_EVENTS.register("tile.fusion_running", () -> SoundEvent.createVariableRangeEvent(rl("tile.fusion_running")));
    public static final Supplier<SoundEvent> FUSION_SWITCH = SOUND_EVENTS.register("tile.fusion_switch", () -> SoundEvent.createVariableRangeEvent(rl("tile.fusion_switch")));
    public static final Supplier<SoundEvent> FISSION_REACTOR = SOUND_EVENTS.register("tile.fission_reactor", () -> SoundEvent.createVariableRangeEvent(rl("tile.fission_reactor")));
    public static final Supplier<SoundEvent> RECORD_WANDERER = SOUND_EVENTS.register("music.wanderer", SoundEvent::createVariableRangeEvent);
    public static final Supplier<SoundEvent> RECORD_END_OF_THE_WORLD = SOUND_EVENTS.register("music.end_of_the_world", SoundEvent::createVariableRangeEvent);
    public static final Supplier<SoundEvent> RECORD_MONEY_FOR_NOTHING = SOUND_EVENTS.register("music.money_for_nothing", SoundEvent::createVariableRangeEvent);
    public static final Supplier<SoundEvent> RECORD_HYPERSPACE = SOUND_EVENTS.register("music.hyperspace", () -> SoundEvent.createVariableRangeEvent(rl("music.hyperspace")));

    public static final ResourceKey<JukeboxSong> WANDERER_KEY = ResourceKey.create(net.minecraft.core.registries.Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(NuclearCraft.MODID, "wanderer"));
    public static final ResourceKey<JukeboxSong> END_OF_THE_WORLD_KEY = ResourceKey.create(net.minecraft.core.registries.Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(NuclearCraft.MODID, "end_of_the_world"));
    public static final ResourceKey<JukeboxSong> MONEY_FOR_NOTHING_KEY = ResourceKey.create(net.minecraft.core.registries.Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(NuclearCraft.MODID, "money_for_nothing"));
    public static final ResourceKey<JukeboxSong> HYPERSPACE_KEY = ResourceKey.create(net.minecraft.core.registries.Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(NuclearCraft.MODID, "hyperspace"));

    public static final HashMap<String, ResourceKey<JukeboxSong>> SOUND_MAP = initSoundMap();

    private static HashMap<String, ResourceKey<JukeboxSong>> initSoundMap() {
        HashMap<String, ResourceKey<JukeboxSong>> soundMap = new HashMap<>();
        soundMap.put("wanderer", WANDERER_KEY);
        soundMap.put("end_of_the_world", END_OF_THE_WORLD_KEY);
        soundMap.put("money_for_nothing", MONEY_FOR_NOTHING_KEY);
        soundMap.put("hyperspace", HYPERSPACE_KEY);
        return soundMap;
    }

    private static List<Supplier<SoundEvent>> initGeigerSounds() {
        return List.of(
                SOUND_EVENTS.register("geiger_1", () -> SoundEvent.createVariableRangeEvent(rl("geiger_1"))),
                SOUND_EVENTS.register("geiger_2", () -> SoundEvent.createVariableRangeEvent(rl("geiger_2"))),
                SOUND_EVENTS.register("geiger_3", () -> SoundEvent.createVariableRangeEvent(rl("geiger_3"))),
                SOUND_EVENTS.register("geiger_4", () -> SoundEvent.createVariableRangeEvent(rl("geiger_4"))),
                SOUND_EVENTS.register("geiger_5", () -> SoundEvent.createVariableRangeEvent(rl("geiger_5")))
        );
    }

    public static void init() {
    }
}