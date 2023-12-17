package come.example.utitled;

import come.example.utitled.emulator.AssemblerContext;
import come.example.utitled.emulator.Emulator;
import come.example.utitled.emulator.CodeParser;

import java.io.IOException;

public class Compiler {
    public static void main(String[] args) throws IOException {
        var assemblerContext = new AssemblerContext();
        new CodeParser(assemblerContext, "program_2.txt").parse();
        var emulator = new Emulator(assemblerContext);
        emulator.start();
    }
}
