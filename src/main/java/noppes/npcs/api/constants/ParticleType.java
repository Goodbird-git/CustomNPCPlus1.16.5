package noppes.npcs.api.constants;

import net.minecraft.particles.*;
import net.minecraft.network.*;

public class ParticleType
{
    public static final int NONE = 0;
    public static final int SMOKE = 1;
    public static final int PORTAL = 2;
    public static final int REDSTONE = 3;
    public static final int LIGHTNING = 4;
    public static final int LARGE_SMOKE = 5;
    public static final int MAGIC = 6;
    public static final int ENCHANT = 7;
    public static final int CRIT = 8;
    public static final int LAVA = 9;
    public static final int CAMPFIRE_COSY_SMOKE = 10;
    public static final int CAMPFIRE_SIGNAL_SMOKE = 11;
    public static final int BUBBLE_POP = 12;
    public static final int SNEEZE = 13;

    public static IParticleData getMCType(final int type) {
        if (type == 1) {
            return ParticleTypes.SMOKE;
        }
        if (type == 2) {
            return ParticleTypes.PORTAL;
        }
        if (type == 3) {
            return new RedstoneParticleType();
        }
        if (type == 4) {
            return ParticleTypes.ENCHANTED_HIT;
        }
        if (type == 5) {
            return ParticleTypes.LARGE_SMOKE;
        }
        if (type == 6) {
            return ParticleTypes.WITCH;
        }
        if (type == 7) {
            return ParticleTypes.ENCHANT;
        }
        if (type == 8) {
            return ParticleTypes.CRIT;
        }
        if (type == 9) {
            return ParticleTypes.LAVA;
        }
        if (type == 10) {
            return ParticleTypes.CAMPFIRE_COSY_SMOKE;
        }
        if (type == 11) {
            return ParticleTypes.CAMPFIRE_SIGNAL_SMOKE;
        }
        if (type == 12) {
            return ParticleTypes.BUBBLE_POP;
        }
        if (type == 13) {
            return ParticleTypes.SNEEZE;
        }
        return null;
    }

    static class RedstoneParticleType extends RedstoneParticleData
    {
        protected RedstoneParticleType() {
            super(1.0f, 0.0f, 0.0f, 1.0f);
        }

        public net.minecraft.particles.ParticleType<RedstoneParticleData> getType() {
            return ParticleTypes.DUST;
        }

        public void writeToNetwork(final PacketBuffer p_197553_1_) {
        }

        public String writeToString() {
            return ParticleTypes.DUST.getRegistryName().toString();
        }
    }
}
