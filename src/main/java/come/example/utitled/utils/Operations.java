package come.example.utitled.utils;

public enum Operations {

    MOV("mov", "Переместить"),
    XOR("XOR", "Исключающее или"),
    ADD("add", "Сложение"),
    IMUL("imul", "Умножение"),
    DEC("dec", "Декремент"),
    JNZ("jnz", "Условный переход");

    private final String name;
    private final String description;

    Operations(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }


    public static Operations readOperation(String operation) {
        return valueOf(operation.toUpperCase());
    }
}
