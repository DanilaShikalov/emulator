package come.example.utitled.syntax;

public class AsmNumber extends AsmData {
    private Object value;

    public AsmNumber(AssemblerType assemblerType, String name, Object value) {
        this.assemblerType = assemblerType;
        this.name = name;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
