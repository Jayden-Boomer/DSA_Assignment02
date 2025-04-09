package Main;

public interface BaseOperations<T> {
    public boolean insert(T element);
    public T delete(T element);
    public T search(T element);
}
