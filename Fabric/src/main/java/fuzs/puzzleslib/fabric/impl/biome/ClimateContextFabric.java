package fuzs.puzzleslib.fabric.impl.biome;

import fuzs.puzzleslib.common.api.biome.v1.ClimateContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.minecraft.world.level.biome.Biome;

import java.util.Objects;

public record ClimateContextFabric(Biome.ClimateSettings settings,
                                   BiomeModificationContext.WeatherContext context) implements ClimateContext {

    @Override
    public void hasPrecipitation(boolean hasPrecipitation) {
        this.context.setPrecipitation(hasPrecipitation);
    }

    @Override
    public boolean hasPrecipitation() {
        return this.settings.hasPrecipitation();
    }

    @Override
    public void setTemperature(float temperature) {
        this.context.setTemperature(temperature);
    }

    @Override
    public float getTemperature() {
        return this.settings.temperature();
    }

    @Override
    public void setTemperatureModifier(Biome.TemperatureModifier temperatureModifier) {
        Objects.requireNonNull(temperatureModifier, "temperature modifier is null");
        this.context.setTemperatureModifier(temperatureModifier);
    }

    @Override
    public Biome.TemperatureModifier getTemperatureModifier() {
        return this.settings.temperatureModifier();
    }

    @Override
    public void setDownfall(float downfall) {
        this.context.setDownfall(downfall);
    }

    @Override
    public float getDownfall() {
        return this.settings.downfall();
    }
}
