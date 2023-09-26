package io.papermc.paper.api.command.cloud.commandframework.paper.internal;

import com.google.common.annotations.Beta;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * This is not API, and as such, may break, change, or be removed without any notice.
 */

//TODO: Remove reflection
@Beta
public final class CommandBuildContextSupplier {

    private static final Class<?> COMMAND_BUILD_CONTEXT_CLASS = CraftPaperReflection.needMCClass("commands.CommandBuildContext");
    private static final @Nullable Constructor<?> COMMAND_BUILD_CONTEXT_CTR;
    private static final @Nullable Method CREATE_CONTEXT_METHOD;
    private static final @Nullable Method GET_WORLD_DATA_METHOD;
    private static final @Nullable Method GET_FEATURE_FLAGS_METHOD;
    private static final Class<?> REG_ACC_CLASS;
    private static final Class<?> MC_SERVER_CLASS = CraftPaperReflection.needNMSClassOrElse(
            "MinecraftServer", "net.minecraft.server.MinecraftServer"
    );

    static {
        @Nullable Constructor<?> ctr;
        try {
            ctr = COMMAND_BUILD_CONTEXT_CLASS.getDeclaredConstructors()[0];
        } catch (final Exception ex) {
            ctr = null;
        }
        COMMAND_BUILD_CONTEXT_CTR = ctr;

        if (COMMAND_BUILD_CONTEXT_CTR == null) {
            CREATE_CONTEXT_METHOD = Arrays.stream(COMMAND_BUILD_CONTEXT_CLASS.getDeclaredMethods())
                    .filter(it -> it.getParameterCount() == 2 && COMMAND_BUILD_CONTEXT_CLASS.isAssignableFrom(it
                            .getReturnType()) && Modifier.isStatic(it.getModifiers()))
                    .skip(1)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Could not find CommandBuildContext.configurable"));

            final Class<?> worldDataCls = CraftPaperReflection.firstNonNullOrThrow(
                    () -> "Could not find WorldData class",
                    CraftPaperReflection.findMCClass("world.level.storage.SaveData"),
                    CraftPaperReflection.findMCClass("world.level.storage.WorldData")
            );
            GET_WORLD_DATA_METHOD = Arrays.stream(MC_SERVER_CLASS.getDeclaredMethods())
                    .filter(it -> it.getParameterCount() == 0 && !Modifier.isStatic(it.getModifiers()) && it
                            .getReturnType()
                            .equals(worldDataCls))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Could not find MinecraftServer#getWorldData method"));
            final Class<?> featureFlagSetCls = CraftPaperReflection.needMCClass("world.flag.FeatureFlagSet");
            GET_FEATURE_FLAGS_METHOD = Arrays.stream(worldDataCls.getDeclaredMethods())
                    .filter(it -> it.getParameterCount() == 0 && it
                            .getReturnType()
                            .equals(featureFlagSetCls) && !Modifier.isStatic(it.getModifiers()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Could not find enabledFeatures method"));
        } else {
            CREATE_CONTEXT_METHOD = null;
            GET_WORLD_DATA_METHOD = null;
            GET_FEATURE_FLAGS_METHOD = null;
        }

        REG_ACC_CLASS = COMMAND_BUILD_CONTEXT_CTR != null
                ? COMMAND_BUILD_CONTEXT_CTR.getParameterTypes()[0]
                : CREATE_CONTEXT_METHOD.getParameterTypes()[0];
    }

    private static final Method GET_SERVER_METHOD;
    private static final Method REGISTRY_ACCESS = Arrays.stream(MC_SERVER_CLASS.getDeclaredMethods())
            .filter(m -> REG_ACC_CLASS.isAssignableFrom(m.getReturnType()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Cannot find MinecraftServer#registryAccess"));

    static {
        try {
            GET_SERVER_METHOD = MC_SERVER_CLASS.getDeclaredMethod("getServer");
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private CommandBuildContextSupplier() {
    }

    public static Object commandBuildContext() {
        if (COMMAND_BUILD_CONTEXT_CTR != null) {
            try {
                final Object server = GET_SERVER_METHOD.invoke(null);
                return COMMAND_BUILD_CONTEXT_CTR.newInstance(REGISTRY_ACCESS.invoke(server));
            } catch (final ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        } else if (CREATE_CONTEXT_METHOD != null && GET_WORLD_DATA_METHOD != null && GET_FEATURE_FLAGS_METHOD != null) {
            try {
                final Object server = GET_SERVER_METHOD.invoke(null);
                final Object worldData = GET_WORLD_DATA_METHOD.invoke(server);
                final Object flags = GET_FEATURE_FLAGS_METHOD.invoke(worldData);
                return CREATE_CONTEXT_METHOD.invoke(null, REGISTRY_ACCESS.invoke(server), flags);
            } catch (final ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalStateException();
        }
    }
}