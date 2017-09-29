package eu.rsulkowski.jdoocsoup.processor.utils;

/**
 * Created by sulkowsk on 29/09/2017.
 */

public class Pair<T,Z> {

    private T first;
    private Z second;

    public Pair(T first, Z second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public Z getSecond() {
        return second;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public void setSecond(Z second) {
        this.second = second;
    }
}
