package igentuman.nc.mixin;

import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModConfigSpec.class)
public abstract class ForgeConfigSpecMixin {

    @Inject(method = "isCorrect*", at = @At("HEAD"), remap = false, cancellable = true)
    private void isCorrect(UnmodifiableCommentedConfig config, CallbackInfoReturnable<Boolean> cir) {
        String file = ((CommentedFileConfig) config).getFile().toString();
        if (file.contains("NuclearCraft/materials.toml")) {
            // cir.setReturnValue(true);
            // cir.cancel();
        }
    }
}
