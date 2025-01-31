package igentuman.nc.recipes;

import igentuman.nc.block.entity.fission.FissionControllerBE;
import igentuman.nc.block.entity.processor.NuclearFurnaceBE;
import igentuman.nc.block.entity.turbine.TurbineControllerBE;
import igentuman.nc.content.processors.Processors;
import igentuman.nc.recipes.ingredient.ItemStackIngredient;
import igentuman.nc.recipes.ingredient.creator.IngredientCreatorAccess;
import igentuman.nc.recipes.type.NcRecipe;
import igentuman.nc.registry.RecipeTypeDeferredRegister;
import igentuman.nc.registry.RecipeTypeRegistryObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static igentuman.nc.NuclearCraft.MODID;
import static igentuman.nc.NuclearCraft.rl;

public class NcRecipeType<RECIPE extends NcRecipe> implements RecipeType<RECIPE>,
        INcRecipeTypeProvider<RECIPE> {

    public static final RecipeTypeDeferredRegister RECIPE_TYPES = new RecipeTypeDeferredRegister(MODID);
    public static boolean initialized = false;
    public static final HashMap<String, RecipeTypeRegistryObject<? extends NcRecipe>> ALL_RECIPES = initializeRecipes();

    private static HashMap<String, RecipeTypeRegistryObject<? extends NcRecipe>> initializeRecipes() {
        HashMap<String, RecipeTypeRegistryObject<? extends NcRecipe>> recipes = new HashMap<>();
        recipes.put(FissionControllerBE.NAME, register(FissionControllerBE.NAME));
        recipes.put("nc_ore_veins", register("nc_ore_veins"));
        recipes.put("fusion_core", register("fusion_core"));
        recipes.put("fusion_coolant", register("fusion_coolant"));
        recipes.put("fission_boiling", register("fission_boiling"));
        recipes.put(TurbineControllerBE.NAME, register(TurbineControllerBE.NAME));

        for (String processorName : Processors.all().keySet()) {
            if (Processors.all().get(processorName).hasRecipes()) {
                recipes.put(processorName, register(processorName));
            }
        }
        initialized = true;
        return recipes;
    }

    public static <RECIPE extends NcRecipe> RecipeTypeRegistryObject<RECIPE> register(String name) {
        return RECIPE_TYPES.register(name, () -> new NcRecipeType<>(name));
    }

    private List<RECIPE> cachedRecipes = Collections.emptyList();
    private final ResourceLocation registryName;

    private NcRecipeType(String name) {
        this.registryName = rl(name);
    }

    public static void invalidateCache() {
        if (!initialized) return;
        for (RecipeTypeRegistryObject<? extends NcRecipe> recipeType : ALL_RECIPES.values()) {
            recipeType.getRecipeType().cachedRecipes = Collections.emptyList();
        }
    }

    public static void init() {
    }

    @Override
    public String toString() {
        return registryName.toString();
    }

    @Override
    public ResourceLocation getRegistryName() {
        return registryName;
    }

    @Override
    public NcRecipeType<RECIPE> getRecipeType() {
        return this;
    }


    @NotNull
    @Override
    public List<RECIPE> getRecipes(@Nullable Level world) {
        if (Processors.all().containsKey(registryName.getPath()) && !Processors.all().get(registryName.getPath()).config().isRegistered()) {
            return Collections.emptyList();
        }
        if (world == null) {
            world = ServerLifecycleHooks.getCurrentServer().overworld();
            if (world == null) {
                return cachedRecipes;
            }
        }
        if (cachedRecipes.isEmpty()) {
            RecipeManager recipeManager = world.getRecipeManager();
            List<RECIPE> recipes;
            if (this.registryName.getPath().equals("nuclear_furnace")) {
                recipes = getSmeltingRecipes(recipeManager);
            } else {
                recipes = recipeManager.getAllRecipesFor(this).stream().map(RecipeHolder::value).toList();
            }
            cachedRecipes = recipes.stream()
                    .filter(recipe -> !recipe.isIncomplete())
                    .toList();
        }
        return cachedRecipes;
    }

    private List<RECIPE> getSmeltingRecipes(RecipeManager recipeManager) {
        List<RecipeHolder<SmeltingRecipe>> smelting = recipeManager.getAllRecipesFor(SMELTING);
        List<RECIPE> recipes = new ArrayList<>();
        for (RecipeHolder<SmeltingRecipe> recipe : smelting) {
            if (recipe.value().isIncomplete()) {
                continue;
            }
            ItemStackIngredient output = IngredientCreatorAccess.item().from(recipe.value().getResultItem(RegistryAccess.EMPTY)); //TODO ADD
            recipes.add((RECIPE) new NuclearFurnaceBE.Recipe(
                    List.of(IngredientCreatorAccess.item().from(recipe.value().getIngredients().getFirst())),
                    List.of(output),
                    List.of(),
                    List.of(),
                    recipe.value().getCookingTime() / 1000D, 1, 1, 1));
        }
        return recipes;
    }

//    private String getNFRecipeId(RecipeHolder<SmeltingRecipe> recipe) {
//        return recipe.id().getPath().toString().replaceAll("[^a-z0-9/._-]", "_") + "_nf";
//    }

    /**
     * Helper for getting a recipe from a world's recipe manager.
     */
    public static <C extends RecipeInput, RECIPE_TYPE extends Recipe<C>> Optional<RecipeHolder<RECIPE_TYPE>> getRecipeFor(RecipeType<RECIPE_TYPE> recipeType, C inventory, Level level) {
        return level.getRecipeManager().getRecipeFor(recipeType, inventory, level)
                .filter(recipe -> !recipe.value().isIncomplete());
    }

    /**
     * Helper for getting a recipe from a world's recipe manager.
     */
    public static Optional<RecipeHolder<?>> byKey(Level level, ResourceLocation id) {
        return level.getRecipeManager().byKey(id)
                .filter(recipe -> !recipe.value().isIncomplete());
    }

    public boolean isLoaded = false;

    public void loadRecipes(Level level) {
        if (isLoaded) return;
        getRecipes(level);
        isLoaded = true;
    }

    public void loadRecipes(RecipeManager manager) {
        getRecipes(manager);
        isLoaded = true;
    }

    private void getRecipes(RecipeManager manager) {
        List<RecipeHolder<RECIPE>> recipes = manager.getAllRecipesFor(this);
        cachedRecipes = recipes.stream()
                .filter(recipe -> !recipe.value().isIncomplete())
                .map(RecipeHolder::value)
                .toList();
    }
}