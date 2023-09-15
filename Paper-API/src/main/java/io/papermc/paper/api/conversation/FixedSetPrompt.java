package io.papermc.paper.api.conversation;

import com.google.common.base.Joiner;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.List;

/**
 * FixedSetPrompt is the base class for any prompt that requires a fixed set
 * response from the user.
 */
public abstract class FixedSetPrompt extends ValidatingPrompt {

    protected List<String> fixedSet;

    /**
     * Creates a FixedSetPrompt from a set of strings.
     * <p>
     * foo = new FixedSetPrompt("bar", "cheese", "panda");
     *
     * @param fixedSet A fixed set of strings, one of which the user must
     *     type.
     */
    public FixedSetPrompt(@NonNull String... fixedSet) {
        super();
        this.fixedSet = Arrays.asList(fixedSet);
    }

    private FixedSetPrompt() {}

    @Override
    protected boolean isInputValid(@NonNull ConversationContext context, @NonNull String input) {
        return fixedSet.contains(input);
    }

    /**
     * Utility function to create a formatted string containing all the
     * options declared in the constructor.
     *
     * @return the options formatted like "[bar, cheese, panda]" if bar,
     *     cheese, and panda were the options used
     */
    @NonNull
    protected String formatFixedSet() {
        return "[" + Joiner.on(", ").join(fixedSet) + "]";
    }
}

