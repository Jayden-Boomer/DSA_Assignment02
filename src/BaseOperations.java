public interface BaseOperations<T> {
    public void insert(T element);
    public T delete(T element);
    public T search(T element);
}
