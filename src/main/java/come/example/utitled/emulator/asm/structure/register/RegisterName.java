package come.example.utitled.emulator.asm.structure.register;

public enum RegisterName {
    EAX("1"),
    AX("2"),
    DX("3"),
    ESI("4"),
    EBP("5"),
    ESP("6"),
    EDI("7"),
    ECX("8"),
    REF("9");
    private String number;

    RegisterName(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }
}
