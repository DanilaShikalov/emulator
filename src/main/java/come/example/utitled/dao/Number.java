package come.example.utitled.dao;

public class Number extends BaseData {
    private Object value;

    public Number(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
