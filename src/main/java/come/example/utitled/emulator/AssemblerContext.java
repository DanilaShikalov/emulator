package come.example.utitled.emulator;

import com.google.common.collect.Lists;
import come.example.utitled.emulator.asm.structure.Command;
import come.example.utitled.emulator.asm.structure.register.RefRegister;
import come.example.utitled.syntax.AsmArray;
import come.example.utitled.syntax.AsmData;
import come.example.utitled.syntax.AsmNumber;

import java.util.*;

public class AssemblerContext {

    public static List<Object> arraysHolder = new ArrayList<>();

    private static Map<String, ArrayReference> arrayReferenceMap = new HashMap();

    private static List<AsmNumber> asmNumberList = new ArrayList<>();

    private Map<String, List<Command>> commands = new LinkedHashMap<>();

    public Iterator<Map.Entry<String, List<Command>>> getFunctionCommandIterator() {
        return commands.entrySet().iterator();
    }


    public void addArray(AsmArray asmArray) {
        if (arrayReferenceMap.containsKey(asmArray.getName())) {
            throw new RuntimeException(String.format("Массив с именем %s уже существует!", asmArray.getName()));
        } else {
            arrayReferenceMap.put(asmArray.getName(), new ArrayReference(asmArray.getArray().size(), arraysHolder.size()));
            arraysHolder.addAll(asmArray.getArray());
        }
    }


    public static ArrayReference getArrayReferenceByName(String name) {
        return arrayReferenceMap.get(name);
    }

    public void addData(AsmData asmData) {
        if (asmData instanceof AsmArray) {
            addArray((AsmArray) asmData);
        } else {
            this.asmNumberList.add((AsmNumber) asmData);
        }
    }

    public static AsmNumber getDataByName(String name) {
        return asmNumberList.stream()
                .filter(el -> el.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }

    public static Object getRefRegValue(ArrayReference arrayReference, RefRegister refRegister) {
        if (arraysHolder.size() <= arrayReference.getFirstPosition() + refRegister.getPosition()) {
            return arraysHolder.get(arraysHolder.size() - 1);
        }
        return arraysHolder.get(arrayReference.getFirstPosition() + refRegister.getPosition());
    }

    public static boolean hasArray(String name) {
        return arrayReferenceMap.containsKey(name);
    }

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
