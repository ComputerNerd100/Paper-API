package io.papermc.paper.api.ban;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

/**
 * A ban list, containing bans of some {@link Type}.
 *
 * @param <T> The ban target
 */
public interface BanList<T> {

    /**
     * Represents a ban-type that a {@link BanList} may track.
     */
    public enum Type {
        /**
         * Banned player names
         *
         * @deprecated deprecated in favor of {@link #PROFILE}
         */
        @Deprecated
        NAME,
        /**
         * Banned IP addresses
         */
        IP,
        /**
         * Banned player profiles
         */
        PROFILE,
        ;
    }

    /**
     * Gets a {@link BanEntry} by target.
     *
     * @param target entry parameter to search for
     * @return the corresponding entry, or null if none found
     */
    @Nullable
    public BanEntry<T> getBanEntry(@NonNull T target);

    /**
     * Adds a ban to this list. If a previous ban exists, this will
     * update the previous entry.
     *
     * @param target the target of the ban
     * @param reason reason for the ban, null indicates implementation default
     * @param expires date for the ban's expiration (unban), or null to imply
     *     forever
     * @param source source of the ban, null indicates implementation default
     * @return the entry for the newly created ban, or the entry for the
     *     (updated) previous ban
     */
    @Nullable
    public BanEntry<T> addBan(@NonNull T target, @Nullable String reason, @Nullable Date expires, @Nullable String source);

    /**
     * Adds a ban to this list. If a previous ban exists, this will
     * update the previous entry.
     *
     * @param target the target of the ban
     * @param reason reason for the ban, null indicates implementation default
     * @param expires instant for the ban's expiration (unban), or null to imply
     *     forever
     * @param source source of the ban, null indicates implementation default
     * @return the entry for the newly created ban, or the entry for the
     *     (updated) previous ban
     */
    @Nullable
    public BanEntry<T> addBan(@NonNull T target, @Nullable String reason, @Nullable Instant expires, @Nullable String source);

    /**
     * Adds a ban to this list. If a previous ban exists, this will
     * update the previous entry.
     *
     * @param target the target of the ban
     * @param reason reason for the ban, null indicates implementation default
     * @param duration the duration of the ban, or null to imply
     *     forever
     * @param source source of the ban, null indicates implementation default
     * @return the entry for the newly created ban, or the entry for the
     *     (updated) previous ban
     */
    @Nullable
    public BanEntry<T> addBan(@NonNull T target, @Nullable String reason, @Nullable Duration duration, @Nullable String source);

    /**
     * Gets a set containing every {@link BanEntry} in this list.
     *
     * @return an immutable set containing every entry tracked by this list
     */
    @NonNull
    public <E extends BanEntry<? super T>> Set<E> getEntries(); // Paper

    /**
     * Gets if a {@link BanEntry} exists for the target, indicating an active
     * ban status.
     * <p>
     * Bans by name for ban type {@link Type#NAME NAME} are no longer supported.
     * The replacement is bans by UUID.
     *
     * @param target the target to find
     * @return true if a {@link BanEntry} exists for the target, indicating an
     *     active ban status, false otherwise
     */
    public boolean isBanned(@NonNull T target);

    /**
     * Removes the specified target from this list, therefore indicating a
     * "not banned" status.
     * <p>
     * Bans by name for ban type {@link Type#NAME NAME} are no longer supported.
     * The replacement is bans by UUID.
     *
     * @param target the target to remove from this list
     */
    public void pardon(@NonNull T target);

}
