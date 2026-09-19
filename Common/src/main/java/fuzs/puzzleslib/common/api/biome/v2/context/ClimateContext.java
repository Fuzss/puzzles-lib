package fuzs.puzzleslib.common.api.biome.v2.context;

import net.minecraft.world.level.biome.Biome;

/**
 * @see Biome.ClimateSettings
 */
public interface ClimateContext {
    /**
     * @see Biome.ClimateSettings#hasPrecipitation()
     * @see Biome.BiomeBuilder#hasPrecipitation(boolean)
     */
    void hasPrecipitation(boolean hasPrecipitation);

    /**
     * @see Biome.ClimateSettings#hasPrecipitation()
     * @see Biome.BiomeBuilder#hasPrecipitation(boolean)
     */
    boolean hasPrecipitation();

    /**
     * @see Biome.ClimateSettings#temperature()
     * @see Biome.BiomeBuilder#temperature(float)
     */
    void setTemperature(float temperature);

    /**
     * @see Biome.ClimateSettings#temperature()
     * @see Biome.BiomeBuilder#temperature(float)
     */
    float getTemperature();

    /**
     * @see Biome.ClimateSettings#temperatureModifier()
     * @see Biome.BiomeBuilder#temperatureAdjustment(Biome.TemperatureModifier)
     */
    void setTemperatureModifier(Biome.TemperatureModifier temperatureModifier);

    /**
     * @see Biome.ClimateSettings#temperatureModifier()
     * @see Biome.BiomeBuilder#temperatureAdjustment(Biome.TemperatureModifier)
     */
    Biome.TemperatureModifier getTemperatureModifier();

    /**
     * @see Biome.ClimateSettings#downfall()
     * @see Biome.BiomeBuilder#downfall(float)
     */
    void setDownfall(float downfall);

    /**
     * @see Biome.ClimateSettings#downfall()
     * @see Biome.BiomeBuilder#downfall(float)
     */
    float getDownfall();
}
