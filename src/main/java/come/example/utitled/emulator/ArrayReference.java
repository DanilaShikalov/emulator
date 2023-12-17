package come.example.utitled.emulator;

public class ArrayReference {

    private int size;
    private int firstPosition;


    public ArrayReference(int size, int firstPosition) {
        this.size = size;
        this.firstPosition = firstPosition;
    }

    public int getSize() {
        return size;
    }

    public int getFirstPosition() {
        return firstPosition;
    }
}
