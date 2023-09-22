package io.papermc.paper.api.event.events.server;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import io.papermc.paper.api.Server;
import io.papermc.paper.api.event.Event;
import io.papermc.paper.api.event.util.Param;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * This event is fired if server is getting queried over GS4 Query protocol
 */
public interface GS4QueryEvent extends Event {

    /**
     * Get query type
     * @return query type
     */
    @Param(0)
    QueryType queryType();

    /**
     * Get querier address
     * @return querier address
     */
    @Param(1)
    InetAddress querierAddress();

    /**
     * Get query response
     * @return query response
     */
    @Param(2)
    QueryResponse response();

    /**
     * The type of query
     */
    enum QueryType {
        /**
         * Basic query asks only a subset of information, such as motd, game type (hardcoded to <pre>MINECRAFT</pre>), map,
         * current players, max players, server port and server motd
         */
        BASIC,

        /**
         * Full query asks pretty much everything present on this event (only hardcoded values cannot be modified here).
         */
        FULL
    }

    final class QueryResponse {
        private final String motd;
        private final String gameVersion;
        private final String map;
        private final int currentPlayers;
        private final int maxPlayers;
        private final String hostname;
        private final int port;
        private final Collection<String> players;
        private final String serverVersion;
        private final Collection<PluginInformation> plugins;

        private QueryResponse(String motd, String gameVersion, String map, int currentPlayers, int maxPlayers, String hostname, int port, Collection<String> players, String serverVersion, Collection<PluginInformation> plugins) {
            this.motd = motd;
            this.gameVersion = gameVersion;
            this.map = map;
            this.currentPlayers = currentPlayers;
            this.maxPlayers = maxPlayers;
            this.hostname = hostname;
            this.port = port;
            this.players = players;
            this.serverVersion = serverVersion;
            this.plugins = plugins;
        }

        /**
         * Get motd which will be used to reply to the query. By default it is {@link Server#getMotd()}.
         * @return motd
         */
        @NotNull
        public String getMotd() {
            return motd;
        }

        /**
         * Get game version which will be used to reply to the query. By default supported Minecraft versions range is sent.
         * @return game version
         */
        @NotNull
        public String getGameVersion() {
            return gameVersion;
        }

        /**
         * Get map name which will be used to reply to the query. By default {@code world} is sent.
         * @return map name
         */
        @NotNull
        public String getMap() {
            return map;
        }

        /**
         * Get current online player count which will be used to reply to the query.
         * @return online player count
         */
        public int getCurrentPlayers() {
            return currentPlayers;
        }

        /**
         * Get max player count which will be used to reply to the query.
         * @return max player count
         */
        public int getMaxPlayers() {
            return maxPlayers;
        }

        /**
         * Get server (public facing) hostname
         * @return server hostname
         */
        @NotNull
        public String getHostname() {
            return hostname;
        }

        /**
         * Get server (public facing) port
         * @return server port
         */
        public int getPort() {
            return port;
        }

        /**
         * Get collection of players which will be used to reply to the query.
         * @return collection of players
         */
        @NotNull
        public Collection<String> getPlayers() {
            return players;
        }

        /**
         * Get server software (name and version) which will be used to reply to the query.
         * @return server software
         */
        @NotNull
        public String getServerVersion() {
            return serverVersion;
        }

        /**
         * Get list of plugins which will be used to reply to the query.
         * @return collection of plugins
         */
        @NotNull
        public Collection<PluginInformation> getPlugins() {
            return plugins;
        }


        /**
         * Creates a new {@link Builder} instance from data represented by this response
         * @return {@link QueryResponse} builder
         */
        @NotNull
        public Builder toBuilder() {
            return QueryResponse.builder()
                    .motd(getMotd())
                    .gameVersion(getGameVersion())
                    .map(getMap())
                    .currentPlayers(getCurrentPlayers())
                    .maxPlayers(getMaxPlayers())
                    .hostname(getHostname())
                    .port(getPort())
                    .players(getPlayers())
                    .serverVersion(getServerVersion())
                    .plugins(getPlugins());
        }

        /**
         * Creates a new {@link Builder} instance
         * @return {@link QueryResponse} builder
         */
        @NotNull
        public static Builder builder() {
            return new Builder();
        }

        /**
         * A builder for {@link QueryResponse} objects.
         */
        public static final class Builder {
            private String motd;
            private String gameVersion;
            private String map;
            private String hostname;
            private String serverVersion;

            private int currentPlayers;
            private int maxPlayers;
            private int port;

            private final List<String> players = new ArrayList<>();
            private final List<PluginInformation> plugins = new ArrayList<>();

            private Builder() {}

            @NotNull
            public Builder motd(@NotNull String motd) {
                this.motd = Preconditions.checkNotNull(motd, "motd");
                return this;
            }

            @NotNull
            public Builder gameVersion(@NotNull String gameVersion) {
                this.gameVersion = Preconditions.checkNotNull(gameVersion, "gameVersion");
                return this;
            }

            @NotNull
            public Builder map(@NotNull String map) {
                this.map = Preconditions.checkNotNull(map, "map");
                return this;
            }

            @NotNull
            public Builder currentPlayers(int currentPlayers) {
                Preconditions.checkArgument(currentPlayers >= 0, "currentPlayers cannot be negative");
                this.currentPlayers = currentPlayers;
                return this;
            }

            @NotNull
            public Builder maxPlayers(int maxPlayers) {
                Preconditions.checkArgument(maxPlayers >= 0, "maxPlayers cannot be negative");
                this.maxPlayers = maxPlayers;
                return this;
            }

            @NotNull
            public Builder hostname(@NotNull String hostname) {
                this.hostname = Preconditions.checkNotNull(hostname, "hostname");
                return this;
            }

            @NotNull
            public Builder port(int port) {
                Preconditions.checkArgument(port >= 1 && port <= 65535, "port must be between 1-65535");
                this.port = port;
                return this;
            }

            @NotNull
            public Builder players(@NotNull Collection<String> players) {
                this.players.addAll(Preconditions.checkNotNull(players, "players"));
                return this;
            }

            @NotNull
            public Builder players(@NotNull String... players) {
                this.players.addAll(Arrays.asList(Preconditions.checkNotNull(players, "players")));
                return this;
            }

            @NotNull
            public Builder clearPlayers() {
                this.players.clear();
                return this;
            }

            @NotNull
            public Builder serverVersion(@NotNull String serverVersion) {
                this.serverVersion = Preconditions.checkNotNull(serverVersion, "serverVersion");
                return this;
            }

            @NotNull
            public Builder plugins(@NotNull Collection<PluginInformation> plugins) {
                this.plugins.addAll(Preconditions.checkNotNull(plugins, "plugins"));
                return this;
            }

            @NotNull
            public Builder plugins(@NotNull PluginInformation... plugins) {
                this.plugins.addAll(Arrays.asList(Preconditions.checkNotNull(plugins, "plugins")));
                return this;
            }

            @NotNull
            public Builder clearPlugins() {
                this.plugins.clear();
                return this;
            }

            /**
             * Builds new {@link QueryResponse} with supplied data
             * @return response
             */
            @NotNull
            public QueryResponse build() {
                return new QueryResponse(
                        Preconditions.checkNotNull(motd, "motd"),
                        Preconditions.checkNotNull(gameVersion, "gameVersion"),
                        Preconditions.checkNotNull(map, "map"),
                        currentPlayers,
                        maxPlayers,
                        Preconditions.checkNotNull(hostname, "hostname"),
                        port,
                        ImmutableList.copyOf(players),
                        Preconditions.checkNotNull(serverVersion, "serverVersion"),
                        ImmutableList.copyOf(plugins)
                );
            }
        }

        /**
         * Plugin information
         */
        public static class PluginInformation {
            private String name;
            private String version;

            public PluginInformation(@NotNull String name, @NotNull String version) {
                this.name = Preconditions.checkNotNull(name, "name");
                this.version = Preconditions.checkNotNull(version, "version");
            }

            @NotNull
            public String getName() {
                return name;
            }

            public void setName(@NotNull String name) {
                this.name = name;
            }

            public void setVersion(@NotNull String version) {
                this.version = version;
            }

            @NotNull
            public String getVersion() {
                return version;
            }

            @NotNull
            public static PluginInformation of(@NotNull String name, @NotNull String version) {
                return new PluginInformation(name, version);
            }
        }
    }
}
