package fuzs.puzzleslib.neoforge.api.client.data.v3.sounds;

import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

/**
 * A base implementation of {@link SoundDefinitionsProvider} for generating the {@code sounds.json} file for the mod.
 * <p>
 * Subclasses implement {@link #registerSounds()} and register sound definitions via the various {@code add} methods,
 * mirroring the vanilla provider. Subtitle translation keys are added automatically for all sound events, matching the
 * key format used by the language providers.
 */
public abstract class AbstractSoundProvider extends SoundDefinitionsProvider {

    /**
     * @param context the data provider context
     */
    public AbstractSoundProvider(DataProviderContext context) {
        this(context.getModId(), context.getPackOutput());
    }

    /**
     * @param modId      the mod id
     * @param packOutput the pack output instance
     */
    public AbstractSoundProvider(String modId, PackOutput packOutput) {
        super(packOutput, modId);
    }

    /**
     * Creates a sound of type {@link SoundDefinition.SoundType#EVENT} for the given sound event.
     *
     * @param soundEvent the sound event
     * @return the sound
     */
    protected static SoundDefinition.Sound sound(SoundEvent soundEvent) {
        return sound(soundEvent.location(), SoundDefinition.SoundType.EVENT);
    }

    /**
     * Registers all sound definitions of this provider via the various {@code add} methods, mirroring the vanilla
     * provider.
     */
    @Override
    public abstract void registerSounds();

    /**
     * Adds the given sounds referenced by their names to the given sound event.
     *
     * @param soundEvent the sound event
     * @param sounds     the sound names
     */
    protected void add(SoundEvent soundEvent, String... sounds) {
        SoundDefinition definition = definition();
        for (String sound : sounds) {
            definition.with(sound(sound));
        }
        this.add(soundEvent, definition);
    }

    /**
     * Adds the given sounds referenced by their identifiers to the given sound event.
     *
     * @param soundEvent the sound event
     * @param sounds     the sound identifiers
     */
    protected void add(SoundEvent soundEvent, Identifier... sounds) {
        SoundDefinition definition = definition();
        for (Identifier sound : sounds) {
            definition.with(sound(sound));
        }
        this.add(soundEvent, definition);
    }

    /**
     * Adds the given sound events to the given sound event.
     *
     * @param soundEvent  the sound event
     * @param soundEvents the sound events to add
     */
    protected void add(SoundEvent soundEvent, SoundEvent... soundEvents) {
        SoundDefinition definition = definition();
        for (SoundEvent vanillaSoundEvent : soundEvents) {
            definition.with(sound(vanillaSoundEvent));
        }
        this.add(soundEvent, definition);
    }

    /**
     * Adds the given sounds to the given sound event.
     *
     * @param soundEvent the sound event
     * @param sounds     the sounds to add
     */
    protected void add(SoundEvent soundEvent, SoundDefinition.Sound... sounds) {
        this.add(soundEvent.location(), definition().with(sounds));
    }

    /**
     * Adds a record sound for the given sound event, looking for the sound under the {@code records/} directory and
     * disabling the automatic subtitle.
     *
     * @param soundEvent the sound event holder
     */
    protected void addRecord(Holder<SoundEvent> soundEvent) {
        Identifier identifier = soundEvent.unwrap().orThrow().identifier().withPrefix("records/");
        SoundDefinition soundDefinition = definition().with(sound(identifier).stream());
        this.add(soundEvent.value(), soundDefinition);
        soundDefinition.subtitle(null);
    }

    /**
     * Adds the given definition for the given sound event, setting the subtitle translation key to
     * {@code subtitles.<path>}.
     *
     * @param soundEvent the sound event identifier
     * @param definition the sound definition
     */
    @Override
    protected void add(Identifier soundEvent, SoundDefinition definition) {
        super.add(soundEvent, definition.subtitle("subtitles." + soundEvent.getPath()));
    }
}
