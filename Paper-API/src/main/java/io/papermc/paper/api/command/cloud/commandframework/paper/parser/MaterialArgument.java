package io.papermc.paper.api.command.cloud.commandframework.paper.parser;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import io.papermc.paper.api.command.cloud.commandframework.paper.PaperCaptionKeys;
import io.papermc.paper.api.material.Material;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

/**
 * cloud argument type that parses Bukkit {@link Material materials}
 *
 * @param <C> Command sender type
 */
public class MaterialArgument<C> extends CommandArgument<C, Material> {

    protected MaterialArgument(
            final boolean required,
            final @NonNull String name,
            final @NonNull String defaultValue,
            final @Nullable BiFunction<@NonNull CommandContext<C>, @NonNull String,
                    @NonNull List<@NonNull String>> suggestionsProvider,
            final @NonNull ArgumentDescription defaultDescription
    ) {
        super(required, name, new MaterialParser<>(), defaultValue, Material.class, suggestionsProvider, defaultDescription);
    }

    /**
     * Create a new {@link Builder}.
     *
     * @param name argument name
     * @param <C>  sender type
     * @return new {@link Builder}
     * @since 1.8.0
     */
    @API(status = API.Status.STABLE, since = "1.8.0")
    public static <C> @NonNull Builder<C> builder(final @NonNull String name) {
        return new Builder<>(name);
    }

    /**
     * Create a new builder
     *
     * @param name Name of the argument
     * @param <C>  Command sender type
     * @return Created builder
     * @deprecated prefer {@link #builder(String)}
     */
    @API(status = API.Status.DEPRECATED, since = "1.8.0")
    @Deprecated
    public static <C> @NonNull Builder<C> newBuilder(final @NonNull String name) {
        return builder(name);
    }

    /**
     * Create a new required argument
     *
     * @param name Argument name
     * @param <C>  Command sender type
     * @return Created argument
     */
    public static <C> @NonNull CommandArgument<C, Material> of(final @NonNull String name) {
        return MaterialArgument.<C>builder(name).asRequired().build();
    }

    /**
     * Create a new optional argument
     *
     * @param name Argument name
     * @param <C>  Command sender type
     * @return Created argument
     */
    public static <C> @NonNull CommandArgument<C, Material> optional(final @NonNull String name) {
        return MaterialArgument.<C>builder(name).asOptional().build();
    }

    /**
     * Create a new optional argument with a default value
     *
     * @param name     Argument name
     * @param material Default value
     * @param <C>      Command sender type
     * @return Created argument
     */
    public static <C> @NonNull CommandArgument<C, Material> optional(
            final @NonNull String name,
            final @NonNull Material material
    ) {
        return MaterialArgument.<C>builder(name).asOptionalWithDefault(material.name().toLowerCase()).build();
    }

    public static final class Builder<C> extends CommandArgument.Builder<C, Material> {

        private Builder(final @NonNull String name) {
            super(Material.class, name);
        }

        @Override
        public @NonNull CommandArgument<C, Material> build() {
            return new MaterialArgument<>(
                    this.isRequired(),
                    this.getName(),
                    this.getDefaultValue(),
                    this.getSuggestionsProvider(),
                    this.getDefaultDescription()
            );
        }
    }

    public static final class MaterialParser<C> implements ArgumentParser<C, Material> {

        @Override
        public @NonNull ArgumentParseResult<Material> parse(
                final @NonNull CommandContext<C> commandContext,
                final @NonNull Queue<@NonNull String> inputQueue
        ) {
            String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(
                        MaterialParser.class,
                        commandContext
                ));
            }

            try {
                final Material material = Material.valueOf(input.toUpperCase());
                inputQueue.remove();
                return ArgumentParseResult.success(material);
            } catch (final IllegalArgumentException exception) {
                return ArgumentParseResult.failure(new MaterialParseException(input, commandContext));
            }
        }

        @Override
        public @NonNull List<@NonNull String> suggestions(
                final @NonNull CommandContext<C> commandContext,
                final @NonNull String input
        ) {
            final List<String> completions = new ArrayList<>();
            for (Material value : Material.values()) {
                completions.add(value.name().toLowerCase());
            }
            return completions;
        }
    }


    public static final class MaterialParseException extends ParserException {

        private static final long serialVersionUID = 1615554107385965610L;
        private final String input;

        /**
         * Construct a new MaterialParseException
         *
         * @param input   Input
         * @param context Command context
         */
        public MaterialParseException(
                final @NonNull String input,
                final @NonNull CommandContext<?> context
        ) {
            super(
                    MaterialParser.class,
                    context,
                    PaperCaptionKeys.ARGUMENT_PARSE_FAILURE_MATERIAL,
                    CaptionVariable.of("input", input)
            );
            this.input = input;
        }

        /**
         * Get the input
         *
         * @return Input
         */
        public @NonNull String getInput() {
            return this.input;
        }
    }
}
