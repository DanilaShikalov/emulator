package come.example.utitled.emulator.asm.structure.flag;

public class ZeroFlag {

    private static boolean value = true;

    public static boolean changeFlag() {
        value = !value;
        return value;
    }

    public static boolean isEnd() {
        return !value;
    }
}
