package fuzs.puzzleslib.fabric.impl.data;

import fuzs.puzzleslib.common.api.data.v3.tags.AbstractTagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;

import java.util.ArrayList;
import java.util.List;

public final class FabricTagAppender<T> extends AbstractTagAppender<T> {

    public FabricTagAppender(TagBuilder builder) {
        super(builder);
    }

    @Override
    public AbstractTagAppender<T> remove(Identifier id) {
        this.builder.remove(TagEntry.element(id));
        return this;
    }

    @Override
    public AbstractTagAppender<T> removeOptional(Identifier id) {
        this.builder.remove(TagEntry.optionalElement(id));
        return this;
    }

    @Override
    public AbstractTagAppender<T> removeTag(Identifier id) {
        this.builder.remove(TagEntry.tag(id));
        return this;
    }

    @Override
    public AbstractTagAppender<T> removeOptionalTag(Identifier id) {
        this.builder.remove(TagEntry.optionalTag(id));
        return this;
    }

    @Override
    public List<String> asStringList() {
        List<String> list = new ArrayList<>();
        for (TagEntry tagEntry : this.builder.build()) {
            list.add(this.elementOrTag(tagEntry));
        }

        for (TagEntry tagEntry : this.builder.getRemovals()) {
            list.add("!" + this.elementOrTag(tagEntry));
        }

        return list;
    }
}
