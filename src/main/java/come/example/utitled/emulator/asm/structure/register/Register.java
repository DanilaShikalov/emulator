package come.example.utitled.emulator.asm.structure.register;

import come.example.utitled.emulator.ArrayReference;

import java.util.Objects;

public abstract class Register<T> {

    protected RegisterName registerName;

    protected RegisterType registerType;

    /** Весь регистр **/
    protected T full;

    /** Младший регистр **/
    protected T young;

    public Register(RegisterName registerName, T full, T young) {
        this.registerName = registerName;
        if (Objects.isNull(full) && !Objects.isNull(young)) {
            this.young = young;
            registerType = RegisterType.YOUNG;
        } else if (!Objects.isNull(full) && Objects.isNull(young)) {
            this.full = full;
            registerType = RegisterType.FULL;
        } else {
            throw new RuntimeException("Ошибка добавления регистра в контекст!");
        }
    }

    public void setValue(RegisterType registerType, T value) {
       if (RegisterType.FULL.equals(registerType) || value instanceof ArrayReference) {
           this.full = value;
           this.young = null;
       } else {
           this.young = value;
           this.full = null;
       }
    }

    public RegisterType getRegisterType() {
        return this.registerType;
    }

    public abstract T getValue();

    public RegisterName getRegisterName() {
        return registerName;
    }

    public void setRegisterName(RegisterName registerName) {
        this.registerName = registerName;
    }

    abstract void addValue(RegisterType registerType, Object value);
}
