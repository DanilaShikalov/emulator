package come.example.utitled.dao;

public class RefRegister extends Register<ArrayReference> {

    private int position;

    public RefRegister(RegisterName registerName, ArrayReference full, ArrayReference young) {
        super(registerName, full, young);
        position = 0;
    }

    @Override
    public ArrayReference getValue() {
        return full;
    }

    public void iterate(int delta) {
        position += delta;
    }

    @Override
    void addValue(RegisterType registerType, Object value) {
        // TODO
    }

    public int getPosition() {
        return position;
    }
}
