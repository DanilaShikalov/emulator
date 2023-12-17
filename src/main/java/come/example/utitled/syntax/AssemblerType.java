package come.example.utitled.syntax;

import org.apache.commons.lang3.StringUtils;

public enum AssemblerType {

    DB("db"),
    DW("dw");

    private String name;

    AssemblerType(String name) {
        this.name = name;
    }

    public static AssemblerType readAsmType(String type) {
        return valueOf(StringUtils.upperCase(type));
    }
}
