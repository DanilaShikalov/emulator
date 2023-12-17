package come.example.utitled.emulator.asm.structure.register;

public class NumericRegister extends Register<Integer> {

    public NumericRegister(RegisterName registerName, Integer full, Integer young) {
        super(registerName, full, young);
    }

    @Override
    void addValue(RegisterType registerType, Object value) {
        if (RegisterType.FULL.equals(registerType)) {
            this.full += (Integer) value;
        } else {
            this.young += (Integer) value;
        }
    }

    @Override
    public Integer getValue() {
        return full == null ? young : full;
    }
}
