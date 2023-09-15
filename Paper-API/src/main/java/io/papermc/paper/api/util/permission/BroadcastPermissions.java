package io.papermc.paper.api.util.permission;

import io.papermc.paper.api.permisson.Permission;
import io.papermc.paper.api.permisson.PermissionDefault;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class BroadcastPermissions {
    private static final String ROOT = "bukkit.broadcast";
    private static final String PREFIX = ROOT + ".";

    private BroadcastPermissions() {}

    @NonNull
    public static Permission registerPermissions(@NonNull Permission parent) {
        Permission broadcasts = DefaultPermissions.registerPermission(ROOT, "Allows the user to receive all broadcast messages", parent);

        DefaultPermissions.registerPermission(PREFIX + "admin", "Allows the user to receive administrative broadcasts", PermissionDefault.OP, broadcasts);
        DefaultPermissions.registerPermission(PREFIX + "user", "Allows the user to receive user broadcasts", PermissionDefault.TRUE, broadcasts);

        broadcasts.recalculatePermissibles();

        return broadcasts;
    }
}

