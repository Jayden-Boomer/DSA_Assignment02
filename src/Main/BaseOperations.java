package Main;

/**
 * A generic interface defining the basic operations required by all data structures in the project.
 *
 * @param <T> The type of elements handled by the data structure.
 */
public interface BaseOperations<T> {

    /**
     * Inserts the specified element into the data structure.
     *
     * @param element The element to be inserted.
     * @return {@code true} if the element was successfully inserted; {@code false} otherwise.
     */
    boolean insert(T element);

    /**
     * Deletes the specified element from the data structure.
     *
     * @param element The element to be deleted.
     * @return The deleted element if it was found and removed; {@code null} otherwise.
     */
    T delete(T element);

    /**
     * Searches for the specified element in the data structure.
     *
     * @param element The element to search for.
     * @return The element if found; {@code null} otherwise.
     */
    T search(T element);
}

