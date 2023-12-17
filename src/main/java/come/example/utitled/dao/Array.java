package come.example.utitled.dao;

import java.util.List;

public class Array extends BaseData {
    private List<Object> array;

    public Array(String name, List<Object> array) {
        this.name = name;
        this.array = array;
    }

    public List<Object> getArray() {
        return array;
    }
}
