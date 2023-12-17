package come.example.utitled.utils;

import come.example.utitled.system.Context;
import come.example.utitled.dao.RefRegister;
import come.example.utitled.dao.Register;
import come.example.utitled.dao.RegisterName;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class OperationsRealization {

    /**
     * Метод для XOR команды
     */
    public static BiConsumer<Register, Register> XOR_REGISTER = (reg1, reg2) -> {
        int value = (Integer) reg1.getValue() ^ (Integer) reg2.getValue();
        reg1.setValue(reg1.getRegisterType(), value);
    };

    /**
     * Метод для ADD команды
     */
    public static BiConsumer<Register, Register> ADD_REGISTER = (reg1, reg2) -> {
        int value;
        if (reg2 instanceof RefRegister) {
            RefRegister ref = (RefRegister) reg2;
            value = (Integer) reg1.getValue() + Integer.valueOf((String) Context.arraysHolder.get(ref.getValue().getFirstPosition() + ref.getPosition()));
        } else {
            value = (Integer) reg1.getValue() + (Integer) reg2.getValue();
        }
        reg1.setValue(reg1.getRegisterType(), value);
    };

    /**
     * Метод для MOV (присвоить) команды
     */
    public static BiFunction<Register, Register, Register> MOV_REGISTER = (reg1, reg2) -> {
        int value = (Integer) reg2.getValue();
        reg1.setValue(reg1.getRegisterType(), value);
        return reg1;
    };

    /**
     * Метод для MOV (присвоить) команды
     */
    public static BiFunction<Register, Integer, Register> MOV_VALUE = (reg, value) -> {
        reg.setValue(reg.getRegisterType(), value);
        return reg;
    };

    /**
     * Метод для ADD команды
     */
    public static BiFunction<Register, Integer, Register> ADD_VALUE = (reg, value) -> {
        reg.setValue(reg.getRegisterType(), (Integer)reg.getValue() + value);
        return reg;
    };

    /**
     * Метод для MOV команды с массивом
     * <pre>
     *     array dw 12,15,43,11
     *     mov esi, array
     *     Будет присвоен адрес числа 12
     *     add esi, 2
     *     Будет уже адрес числа 15
     * </pre>
     */
    public static BiFunction<Register, Register, Register> MOV_REF_REGISTER = (reg1, reg2) -> {
        RegisterName name = reg1.getRegisterName();
        if (RegisterName.DX.equals(name)) {
            RefRegister ref = (RefRegister) reg2;
            reg1.setValue(reg1.getRegisterType(), Integer.valueOf((String) Context.arraysHolder.get(ref.getValue().getFirstPosition() + ref.getPosition())));
            return reg1;
        }
        reg2.setRegisterName(name);
        return reg2;
    };

    /**
     * Метод для DEC (декремент) команды
     */
    public static Function<Register, Register> DEC_VALUE = (reg) -> {
        reg.setValue(reg.getRegisterType(), (Integer) reg.getValue() - 1);
        return reg;
    };

    /**
     * Метод для IMUL (знаковое умножение) команды
     */
    public static BiConsumer<Register, Register> IMUL_REGISTER = (reg1, reg2) -> {
        Integer val1 = -1;
        Integer val2 = -1;
        if (reg1 instanceof RefRegister) {
            RefRegister ref1 = (RefRegister) reg1;
            val1 = Integer.valueOf((String) Context.arraysHolder.get(ref1.getValue().getFirstPosition() + ref1.getPosition()));
        } else {
            val1 = (Integer) reg1.getValue();
        }
        if (reg2 instanceof RefRegister) {
            RefRegister ref2 = (RefRegister) reg2;
            val2 = Integer.valueOf((String) Context.arraysHolder.get(ref2.getValue().getFirstPosition() + ref2.getPosition()));
        }

        int value = val1 * val2;
        reg1.setValue(reg1.getRegisterType(), value);
    };
}
