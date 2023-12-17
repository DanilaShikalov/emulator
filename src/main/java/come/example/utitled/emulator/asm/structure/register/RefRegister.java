package come.example.utitled.emulator.asm.structure.register;

import come.example.utitled.emulator.ArrayReference;

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
        // do nothing
    }

    public int getPosition() {
        return position;
    }
}
