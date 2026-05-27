package com.oj.tan.writer.Future;

import com.oj.tan.writer.exception.StabilityException;

import java.util.Map;
import java.util.concurrent.*;

public class TimeOutFuture<V> implements Future<V> {
    private CompletableFuture<V> future = new CompletableFuture<>();
    private long timeout;
    private long startTime;

    public TimeOutFuture(long timeout) {
        this.timeout = timeout;
        this.startTime = System.currentTimeMillis();
    }

    public void success(Map<String, Object> data) {
        future.complete((V) data);
    }

    public void fail(StabilityException e) {
        future.completeExceptionally(e);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return future.get();
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(timeout, unit);
    }

    public boolean isTimeOut() {
        return System.currentTimeMillis() - startTime > timeout;
    }
}
