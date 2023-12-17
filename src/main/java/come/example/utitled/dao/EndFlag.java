package come.example.utitled.dao;

public class EndFlag {

    private static boolean value = true;

    public static void changeFlag() {
        value = !value;
    }

    public static boolean isEnd() {
        return !value;
    }
}
