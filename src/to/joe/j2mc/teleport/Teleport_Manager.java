package to.joe.j2mc.teleport;

public class Teleport_Manager {
    private static Teleport_Manager self = new Teleport_Manager();

    private J2MC_Teleport teleport;

    public static void setTeleport(J2MC_Teleport teleport) {
        Teleport_Manager.self.teleport = teleport;
    }

    public static J2MC_Teleport tele() {
        return Teleport_Manager.self.teleport;
    }
}
