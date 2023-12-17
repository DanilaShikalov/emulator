package come.example.utitled.syntax;

import java.util.List;

public class AsmArray extends AsmData {
    private List<Object> array;

    public AsmArray(AssemblerType assemblerType, String name, List<Object> array) {
        this.assemblerType = assemblerType;
        this.name = name;
        this.array = array;
    }


    public List<Object> getArray() {
        return array;
    }
}
