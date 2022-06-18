package com.drace.shared;

import java.util.function.Supplier;
import static java.util.Objects.*;

public class Lazy<T> {

    private volatile T value;

    public T getOrCompute(Supplier<T> supplier) {
        final T result = value; // ler um valor volátil apenas.

        if(result == null)
            return maybeCompute(supplier);

        return result;
    }

    private synchronized T maybeCompute(Supplier<T> supplier) {
        if (value == null)
            value = requireNonNull(supplier.get());

        return value;
    }

}
