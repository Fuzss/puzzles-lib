package fuzs.puzzleslib.fabric.impl.biome;

import fuzs.puzzleslib.common.api.biome.v2.context.ClimateContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.minecraft.world.level.biome.Biome;

import java.util.Objects;

public record ClimateContextFabricImpl(BiomeModificationContext.WeatherContext context,
                                       Biome.ClimateSettings biome) implements ClimateContext {
    @Override
    public void hasPrecipitation(boolean hasPrecipitation) {
        this.context.setPrecipitation(hasPrecipitation);
    }

    @Override
    public boolean hasPrecipitation() {
        return this.biome.hasPrecipitation();
    }

    @Override
    public void setTemperature(float temperature) {
        this.context.setTemperature(temperature);
    }

    @Override
    public float getTemperature() {
        return this.biome.temperature();
    }

    @Override
    public void setTemperatureModifier(Biome.TemperatureModifier temperatureModifier) {
        Objects.requireNonNull(temperatureModifier, "temperature modifier is null");
        this.context.setTemperatureModifier(temperatureModifier);
    }

    @Override
    public Biome.TemperatureModifier getTemperatureModifier() {
        return this.biome.temperatureModifier();
    }

    @Override
    public void setDownfall(float downfall) {
        this.context.setDownfall(downfall);
    }

    @Override
    public float getDownfall() {
        return this.biome.downfall();
    }
}
