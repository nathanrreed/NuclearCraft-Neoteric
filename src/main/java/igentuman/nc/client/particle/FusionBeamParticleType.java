package igentuman.nc.client.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class FusionBeamParticleType extends ParticleType<FusionBeamParticleData> {

    public FusionBeamParticleType() {
        super(false);
    }

    @Override
    public MapCodec<FusionBeamParticleData> codec() {
        return FusionBeamParticleData.CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, FusionBeamParticleData> streamCodec() {
        return FusionBeamParticleData.STREAM_CODEC;
    }
}