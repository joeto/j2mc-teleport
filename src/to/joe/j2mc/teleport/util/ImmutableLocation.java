package to.joe.j2mc.teleport.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class ImmutableLocation {

    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final UUID worldUID;

    public ImmutableLocation(double x, double y, double z, float yaw, float pitch, World world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.worldUID = world.getUID();
    }

    public static ImmutableLocation fromLocation(Location loc) {
        return new ImmutableLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(), loc.getWorld().getUID());
    }

    private ImmutableLocation(double x, double y, double z, float yaw, float pitch, UUID worldUID) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.worldUID = worldUID;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public World getWorld() {
        return Bukkit.getServer().getWorld(worldUID);
    }

    public ImmutableLocation setX(double x) {
        return new ImmutableLocation(x, this.y, this.z, this.yaw, this.pitch, this.worldUID);
    }

    public ImmutableLocation setY(double y) {
        return new ImmutableLocation(this.x, y, this.z, this.yaw, this.pitch, this.worldUID);
    }

    public ImmutableLocation setYaw(float yaw) {
        return new ImmutableLocation(this.x, this.y, this.z, yaw, this.pitch, this.worldUID);
    }

    public ImmutableLocation setZ(double z) {
        return new ImmutableLocation(this.x, this.y, z, this.yaw, this.pitch, this.worldUID);
    }

    public ImmutableLocation setPitch(float pitch) {
        return new ImmutableLocation(this.x, this.y, this.z, this.yaw, pitch, this.worldUID);
    }

    public ImmutableLocation setWorld(World world) {
        return new ImmutableLocation(this.x, this.y, this.z, this.yaw, this.pitch, world);
    }
}
