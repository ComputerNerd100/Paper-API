package io.papermc.paper.api.namespace;

import com.google.common.base.Preconditions;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.UUID;

/**
 * Represents a String based key which consists of two components - a namespace
 * and a key.
 *
 * Namespaces may only contain lowercase alphanumeric characters, periods,
 * underscores, and hyphens.
 * <p>
 * Keys may only contain lowercase alphanumeric characters, periods,
 * underscores, hyphens, and forward slashes.
 *
 */
public final class NamespacedKey implements Key, Namespaced {
    /**
     * The namespace representing all inbuilt keys.
     */
    public static final String MINECRAFT = "minecraft";
    /**
     * The namespace representing all keys generated by Paper for backwards
     * compatibility measures.
     */
    public static final String PAPER = "paper";
    //
    private final String namespace;
    private final String key;

    private static boolean isValidNamespaceChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '.' || c == '_' || c == '-';
    }

    private static boolean isValidKeyChar(char c) {
        return isValidNamespaceChar(c) || c == '/';
    }

    private static boolean isValidNamespace(String namespace) {
        int len = namespace.length();
        if (len == 0) {
            return false;
        }

        for (int i = 0; i < len; i++) {
            if (!isValidNamespaceChar(namespace.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private static boolean isValidKey(String key) {
        int len = key.length();
        if (len == 0) {
            return false;
        }

        for (int i = 0; i < len; i++) {
            if (!isValidKeyChar(key.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Create a key in a specific namespace.
     * <p>
     * For most plugin related code, you should prefer using the
     * {@link NamespacedKey#NamespacedKey(Plugin, String)} constructor.
     *
     * @param namespace namespace
     * @param key key
     * @see #NamespacedKey(Plugin, String)
     */
    public NamespacedKey(@NotNull String namespace, @NotNull String key) {
        Preconditions.checkArgument(namespace != null && isValidNamespace(namespace), "Invalid namespace. Must be [a-z0-9._-]: %s", namespace);
        Preconditions.checkArgument(key != null && isValidKey(key), "Invalid key. Must be [a-z0-9/._-]: %s", key);

        this.namespace = namespace;
        this.key = key;

        String string = toString();
        Preconditions.checkArgument(string.length() < 256, "NamespacedKey must be less than 256 characters", string);
    }

    /**
     * Create a key in the plugin's namespace.
     * <p>
     * Namespaces may only contain lowercase alphanumeric characters, periods,
     * underscores, and hyphens.
     * <p>
     * Keys may only contain lowercase alphanumeric characters, periods,
     * underscores, hyphens, and forward slashes.
     *
     * @param plugin the plugin to use for the namespace
     * @param key the key to create
     */
    public NamespacedKey(@NotNull Plugin plugin, @NotNull String key) {
        Preconditions.checkArgument(plugin != null, "Plugin cannot be null");
        Preconditions.checkArgument(key != null, "Key cannot be null");

        this.namespace = plugin.getName().toLowerCase(Locale.ROOT);
        this.key = key.toLowerCase(Locale.ROOT);

        // Check validity after normalization
        Preconditions.checkArgument(isValidNamespace(this.namespace), "Invalid namespace. Must be [a-z0-9._-]: %s", this.namespace);
        Preconditions.checkArgument(isValidKey(this.key), "Invalid key. Must be [a-z0-9/._-]: %s", this.key);

        String string = toString();
        Preconditions.checkArgument(string.length() < 256, "NamespacedKey must be less than 256 characters (%s)", string);
    }

    @NotNull
    @Override
    public String getNamespace() {
        return namespace;
    }

    @NotNull
    @Override
    public String getKey() {
        return key;
    }

    @Override
    public int hashCode() {
        int result = this.namespace.hashCode();
        result = (31 * result) + this.key.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof net.kyori.adventure.key.Key key)) return false;
        return this.namespace.equals(key.namespace()) && this.key.equals(key.value());
    }

    @Override
    public String toString() {
        return this.namespace + ":" + this.key;
    }

    /**
     * Return a new random key in the {@link #PAPER} namespace.
     *
     * @return new key
     * @deprecated should never be used by plugins, for internal use only!!
     */
    @Deprecated
    @NotNull
    public static NamespacedKey randomKey() {
        return new NamespacedKey(PAPER, UUID.randomUUID().toString());
    }

    /**
     * Get a key in the Minecraft namespace.
     *
     * @param key the key to use
     * @return new key in the Minecraft namespace
     */
    @NotNull
    public static NamespacedKey minecraft(@NotNull String key) {
        return new NamespacedKey(MINECRAFT, key);
    }

    /**
     * Get a NamespacedKey from the supplied string with a default namespace if
     * a namespace is not defined. This is a utility method meant to fetch a
     * NamespacedKey from user input. Please note that casing does matter and
     * any instance of uppercase characters will be considered invalid. The
     * input contract is as follows:
     * <pre>
     * fromString("foo", plugin) -{@literal >} "plugin:foo"
     * fromString("foo:bar", plugin) -{@literal >} "foo:bar"
     * fromString(":foo", null) -{@literal >} "minecraft:foo"
     * fromString("foo", null) -{@literal >} "minecraft:foo"
     * fromString("Foo", plugin) -{@literal >} null
     * fromString(":Foo", plugin) -{@literal >} null
     * fromString("foo:bar:bazz", plugin) -{@literal >} null
     * fromString("", plugin) -{@literal >} null
     * </pre>
     *
     * @param string the string to convert to a NamespacedKey
     * @param defaultNamespace the default namespace to use if none was
     * supplied. If null, the {@code minecraft} namespace
     * ({@link #minecraft(String)}) will be used
     * @return the created NamespacedKey. null if invalid key
     * @see #fromString(String)
     */
    @Nullable
    public static NamespacedKey fromString(@NotNull String string, @Nullable Plugin defaultNamespace) {
        Preconditions.checkArgument(string != null && !string.isEmpty(), "Input string must not be empty or null");

        String[] components = string.split(":", 3);
        if (components.length > 2) {
            return null;
        }

        String key = (components.length == 2) ? components[1] : "";
        if (components.length == 1) {
            String value = components[0];
            if (value.isEmpty() || !isValidKey(value)) {
                return null;
            }

            return (defaultNamespace != null) ? new NamespacedKey(defaultNamespace, value) : minecraft(value);
        } else if (components.length == 2 && !isValidKey(key)) {
            return null;
        }

        String namespace = components[0];
        if (namespace.isEmpty()) {
            return (defaultNamespace != null) ? new NamespacedKey(defaultNamespace, key) : minecraft(key);
        }

        if (!isValidNamespace(namespace)) {
            return null;
        }

        return new NamespacedKey(namespace, key);
    }

    /**
     * Get a NamespacedKey from the supplied string.
     *
     * The default namespace will be Minecraft's (i.e.
     * {@link #minecraft(String)}).
     *
     * @param key the key to convert to a NamespacedKey
     * @return the created NamespacedKey. null if invalid
     * @see #fromString(String, Plugin)
     */
    @Nullable
    public static NamespacedKey fromString(@NotNull String key) {
        return fromString(key, null);
    }

    @NotNull
    @Override
    public String namespace() {
        return this.getNamespace();
    }

    @NotNull
    @Override
    public String value() {
        return this.getKey();
    }

    @NotNull
    @Override
    public String asString() {
        return this.namespace + ':' + this.key;
    }
}