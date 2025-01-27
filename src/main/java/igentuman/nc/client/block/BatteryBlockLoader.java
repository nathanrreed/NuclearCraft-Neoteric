package igentuman.nc.client.block;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

import static igentuman.nc.NuclearCraft.MODID;

public class BatteryBlockLoader implements IGeometryLoader<BatteryBlockLoader.BatteryModelGeometry> {

    public static final ResourceLocation BATTERY_LOADER = ResourceLocation.fromNamespaceAndPath(MODID, "battery_loader");

    @Override
    public BatteryModelGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        String side = jsonObject.get("textures").getAsJsonObject().get("down").getAsString();
        String up = jsonObject.get("textures").getAsJsonObject().get("up").getAsString();

        Material sideDefault = ClientHooks.getBlockMaterial(ResourceLocation.parse(side));
        Material sideIn = ClientHooks.getBlockMaterial(ResourceLocation.parse(side + "_in"));
        Material sideOut = ClientHooks.getBlockMaterial(ResourceLocation.parse(side + "_out"));
        Material sideNone = ClientHooks.getBlockMaterial(ResourceLocation.parse(side + "_non"));

        Material topDefault = ClientHooks.getBlockMaterial(ResourceLocation.parse(up));
        Material topIn = ClientHooks.getBlockMaterial(ResourceLocation.parse(up + "_in"));
        Material topOut = ClientHooks.getBlockMaterial(ResourceLocation.parse(up + "_out"));
        Material topNone = ClientHooks.getBlockMaterial(ResourceLocation.parse(up + "_non"));

        return new BatteryModelGeometry(sideDefault, sideIn, sideOut, sideNone, topDefault, topIn, topOut, topNone);
    }

    public static class BatteryModelGeometry implements IUnbakedGeometry<BatteryModelGeometry> {
        public final Material sideDefault;
        public final Material sideIn;
        public final Material sideOut;
        public final Material sideNone;
        public final Material topDefault;
        public final Material topIn;
        public final Material topOut;
        public final Material topNone;

        public BatteryModelGeometry(Material side, Material sideIn, Material sideOut, Material sideNone, Material top, Material topIn, Material topOut, Material topNone) {
            this.sideDefault = side;
            this.topDefault = top;
            this.sideIn = sideIn;
            this.sideOut = sideOut;
            this.sideNone = sideNone;
            this.topIn = topIn;
            this.topOut = topOut;
            this.topNone = topNone;
        }

        @Override
        public BakedModel bake(IGeometryBakingContext iGeometryBakingContext, ModelBaker modelBaker, Function<Material, TextureAtlasSprite> function, ModelState modelState, ItemOverrides itemOverrides) {
            return new BatteryBlockBakedModel(modelState, function, itemOverrides, iGeometryBakingContext.getTransforms(), this);
        }
    }
}