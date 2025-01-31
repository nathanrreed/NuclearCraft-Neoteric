package igentuman.nc.item;

import igentuman.nc.content.materials.Materials;
import net.minecraft.tags.TagKey;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

import static igentuman.nc.datagen.recipes.recipes.AbstractRecipeProvider.ingotIngredient;
import static igentuman.nc.setup.registration.NCItems.LITHIUM_ION_CELL;
import static igentuman.nc.setup.registration.Tags.*;

public enum Tiers implements Tier {
    TOUGH(INCORRECT_FOR_TOUGH, 10000, 12.0F, 10.0F, 22, () -> ingotIngredient(Materials.tough_alloy).getInputsRaw().getFirst()),
    THORIUM(INCORRECT_FOR_THORIUM, 10000, 10.0F, 6.0F, 18, () -> ingotIngredient(Materials.thorium).getInputsRaw().getFirst()),
    QNP(INCORRECT_FOR_QNP, 50000, 20.0F, 14.0F, 25, () -> Ingredient.of(LITHIUM_ION_CELL.get()));

    private final TagKey<Block> incorrectBlocksForDrops;
    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantmentValue;
    private final LazyLoadedValue<Ingredient> repairIngredient;

    Tiers(TagKey<Block> pIncorrectBlockForDrops, int pUses, float pSpeed, float pDamage, int pEnchantmentValue, Supplier<Ingredient> pRepairIngredient) {
        this.incorrectBlocksForDrops = pIncorrectBlockForDrops;
        this.uses = pUses;
        this.speed = pSpeed;
        this.damage = pDamage;
        this.enchantmentValue = pEnchantmentValue;
        this.repairIngredient = new LazyLoadedValue<>(pRepairIngredient);
    }

    public int getUses() {
        return this.uses;
    }

    public float getSpeed() {
        return this.speed;
    }

    public float getAttackDamageBonus() {
        return this.damage;
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return this.incorrectBlocksForDrops;
    }

    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}