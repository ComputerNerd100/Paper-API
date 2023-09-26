package io.papermc.paper.api.command.cloud.commandframework.paper.parser.selector;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.context.CommandContext;
import com.google.common.collect.ImmutableList;
import io.papermc.paper.api.Paper;
import io.papermc.paper.api.command.cloud.commandframework.paper.arguments.selector.SinglePlayerSelector;
import io.papermc.paper.api.command.cloud.commandframework.paper.parser.PlayerArgument;
import io.papermc.paper.api.entity.Player;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

/**
 * Argument type for parsing {@link SinglePlayerSelector}. On Minecraft 1.13+
 * this argument uses Minecraft's built-in entity selector argument for parsing
 * and suggestions. On prior versions, this argument behaves similarly to
 * {@link PlayerArgument}.
 *
 * @param <C> sender type
 */
public final class SinglePlayerSelectorArgument<C> extends CommandArgument<C, SinglePlayerSelector> {

    private SinglePlayerSelectorArgument(
            final boolean required,
            final @NonNull String name,
            final @NonNull String defaultValue,
            final @Nullable BiFunction<@NonNull CommandContext<C>, @NonNull String,
                    @NonNull List<@NonNull String>> suggestionsProvider,
            final @NonNull ArgumentDescription defaultDescription
    ) {
        super(
                required,
                name,
                new SinglePlayerSelectorParser<>(),
                defaultValue,
                SinglePlayerSelector.class,
                suggestionsProvider,
                defaultDescription
        );
    }

    /**
     * Create a new {@link Builder}.
     *
     * @param name argument name
     * @param <C>  sender type
     * @return new builder
     * @since 1.8.0
     */
    @API(status = API.Status.STABLE, since = "1.8.0")
    public static <C> @NonNull Builder<C> builder(final @NonNull String name) {
        return new Builder<>(name);
    }

    /**
     * Create a new required command argument
     *
     * @param name Argument name
     * @param <C>  Command sender type
     * @return Created argument
     */
    public static <C> @NonNull SinglePlayerSelectorArgument<C> of(final @NonNull String name) {
        return SinglePlayerSelectorArgument.<C>builder(name).asRequired().build();
    }

    /**
     * Create a new optional command argument
     *
     * @param name Argument name
     * @param <C>  Command sender type
     * @return Created argument
     */
    public static <C> @NonNull SinglePlayerSelectorArgument<C> optional(final @NonNull String name) {
        return SinglePlayerSelectorArgument.<C>builder(name).asOptional().build();
    }

    /**
     * Create a new required command argument with a default value
     *
     * @param name                  Argument name
     * @param defaultEntitySelector Default player
     * @param <C>                   Command sender type
     * @return Created argument
     */
    public static <C> @NonNull SinglePlayerSelectorArgument<C> optional(
            final @NonNull String name,
            final @NonNull String defaultEntitySelector
    ) {
        return SinglePlayerSelectorArgument.<C>builder(name).asOptionalWithDefault(defaultEntitySelector).build();
    }


    public static final class Builder<C> extends CommandArgument.TypedBuilder<C, SinglePlayerSelector, Builder<C>> {

        private Builder(final @NonNull String name) {
            super(SinglePlayerSelector.class, name);
        }

        /**
         * Builder a new argument
         *
         * @return Constructed argument
         */
        @Override
        public @NonNull SinglePlayerSelectorArgument<C> build() {
            return new SinglePlayerSelectorArgument<>(
                    this.isRequired(),
                    this.getName(),
                    this.getDefaultValue(),
                    this.getSuggestionsProvider(),
                    this.getDefaultDescription()
            );
        }
    }


    public static final class SinglePlayerSelectorParser<C> extends SelectorUtils.PlayerSelectorParser<C, SinglePlayerSelector> {

        /**
         * Creates a new {@link SinglePlayerSelectorParser}.
         */
        public SinglePlayerSelectorParser() {
            super(true);
        }

        @Override
        public SinglePlayerSelector mapResult(
                final @NonNull String input,
                final SelectorUtils.@NonNull EntitySelectorWrapper wrapper
        ) {
            return new SinglePlayerSelector(input, Collections.singletonList(wrapper.singlePlayer()));
        }

        @Override
        protected @NonNull ArgumentParseResult<SinglePlayerSelector> legacyParse(
                final @NonNull CommandContext<C> commandContext,
                final @NonNull Queue<@NonNull String> inputQueue
        ) {
            final String input = inputQueue.peek();
            @SuppressWarnings("deprecation") final @Nullable Player player = Paper.getPlayer(input);

            if (player == null) {
                return ArgumentParseResult.failure(new PlayerArgument.PlayerParseException(input, commandContext));
            }
            inputQueue.remove();
            return ArgumentParseResult.success(new SinglePlayerSelector(input, ImmutableList.of(player)));
        }

    }
}
