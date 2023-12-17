package come.example.utitled.system;

import com.google.common.collect.Lists;
import come.example.utitled.dao.*;
import come.example.utitled.dao.Number;

import java.util.*;

public class Context {
    /**
     * Все массивы регистров
     */
    public static List<Object> arraysHolder = new ArrayList<>();
    /**
     * Мапа для хранения массивов из блока инициализации
     */
    private static final Map<String, ArrayReference> arraysInit = new HashMap<>();
    /**
     * Лист для обычных переменных
     */
    private static final List<Number> NUMBER_LIST = new ArrayList<>();
    /**
     * Мапа для команд и названий функций
     */
    private final Map<String, List<Command>> commands = new LinkedHashMap<>();

    public Iterator<Map.Entry<String, List<Command>>> getFunctionCommandIterator() {
        return commands.entrySet().iterator();
    }

    /**
     * Сохранить массив из блока инициализации
     */
    public void addArray(Array asmArray) {
        arraysInit.put(asmArray.getName(), new ArrayReference(arraysHolder.size()));
        arraysHolder.addAll(asmArray.getArray());
    }

    /**
     * Получить массив из блока инициализации
     */
    public static ArrayReference getArrayReferenceByName(String name) {
        return arraysInit.get(name);
    }

    /**
     * Сохранить переменную
     */
    public void addData(BaseData baseData) {
        if (baseData instanceof Array) {
            addArray((Array) baseData);
        } else {
            NUMBER_LIST.add((Number) baseData);
        }
    }

    /**
     * Получить переменную по имени
     */
    public static Number getDataByName(String name) {
        return NUMBER_LIST.stream()
                .filter(el -> el.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }

    /**
     * Получить объект из общего хранилища
     */
    public static Object getRefRegValue(ArrayReference arrayReference, RefRegister refRegister) {
        if (arraysHolder.size() <= arrayReference.getFirstPosition() + refRegister.getPosition()) {
            return arraysHolder.get(arraysHolder.size() - 1);
        }
        return arraysHolder.get(arrayReference.getFirstPosition() + refRegister.getPosition());
    }

    public static boolean hasArray(String name) {
        return arraysInit.containsKey(name);
    }

    /**
     * Добавить команду для функции
     */
    public void addCommands(String functionName, Command command) {
        if (this.commands.containsKey(functionName)) {
            this.commands.get(functionName).add(command);
        } else {
            List<Command> commands = Lists.newLinkedList();
            commands.add(command);
            this.commands.put(functionName, commands);
        }
    }

    public Map<String, List<Command>> getCommands() {
        return commands;
    }
}
