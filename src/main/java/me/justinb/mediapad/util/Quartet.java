package me.justinb.mediapad.util;

/**
 * Created by Justin Baldwin on 10/16/2014.
 */
public class Quartet<T> {
    private T value1, value2, value3, value4;
    public Quartet(T value1, T value2, T value3, T value4) {
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
        this.value4 = value4;
    }

    public T getValue1() {
        return value1;
    }

    public T getValue2() {
        return value2;
    }

    public T getValue3() {
        return value3;
    }

    public T getValue4() {
        return value4;
    }
}
