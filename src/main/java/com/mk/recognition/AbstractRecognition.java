package com.mk.recognition;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.BinaryOperator;

public abstract class AbstractRecognition<T> implements Callable<T> {
    private FutureTask<T> futureTask;

    public AbstractRecognition<T> start() {
        futureTask = new FutureTask<>(this);
        new Thread(futureTask).start();
        return this;
    }

    public T get()  {
        try {
            return futureTask.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public T startThenGet() throws ExecutionException, InterruptedException {
        start();
        return get();
    }


    protected static <T> BinaryOperator<T> throwingMerger() {
        return (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        };
    }

}
