package fuzs.puzzleslib.common.impl.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.puzzleslib.common.api.util.v1.CodecExtras;
import fuzs.puzzleslib.common.impl.core.proxy.ProxyImpl;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagFile;

import java.util.Collections;
import java.util.List;

/**
 * Custom {@link Codec Codecs} used during data generation.
 */
public final class DataGenerationCodecs {
    /**
     * A custom {@link Codec} for {@link TagFile} which adds both NeoForge and Fabric remove fields.
     * <p>
     * The respective codecs for those fields are directly copied from the corresponding loader.
     */
    public static final Codec<TagFile> TAG_FILE_CODEC = CodecExtras.encodeOnly(RecordCodecBuilder.create((RecordCodecBuilder.Instance<TagFile> instance) -> instance.group(
                    TagEntry.CODEC.listOf().fieldOf("values").forGetter(TagFile::entries),
                    Codec.BOOL.optionalFieldOf("replace", false).forGetter(TagFile::replace),
                    TagEntry.CODEC.listOf().optionalFieldOf("remove", List.of()).forGetter(ProxyImpl.get()::getTagFileRemovals),
                    TagEntry.CODEC.listOf()
                            .lenientOptionalFieldOf("fabric:remove", Collections.emptyList())
                            .forGetter(ProxyImpl.get()::getTagFileRemovals))
            .apply(instance,
                    (List<TagEntry> entries, Boolean replace, List<TagEntry> _, List<TagEntry> _) -> new TagFile(entries,
                            replace))));

    private DataGenerationCodecs() {
        // NO-OP
    }
}
