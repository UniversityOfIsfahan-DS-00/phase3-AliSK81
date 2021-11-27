public class MyStack <T> {
    private final int maxSize;
    private final T[] data;
    private int top;

    @SuppressWarnings("unchecked")
    public MyStack(int size) {
        maxSize = size;
        data = (T[]) new Object[size];
        top = -1;
    }

    public void push(T val) {
        data[++top] = val;
    }

    public T pop() {
        T temp = data[top];
        data[top--] = null;
        return temp;
    }

    public T peek() {
        return data[top];
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public boolean isFull() {
        return top == maxSize - 1;
    }

    public int size() {
        return top + 1;
    }
}
