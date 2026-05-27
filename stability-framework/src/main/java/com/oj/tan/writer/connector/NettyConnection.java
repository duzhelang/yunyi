package com.oj.tan.writer.connector;

import com.oj.tan.common.io.IOProvider;
import com.oj.tan.common.io.NioFrameworkContext;
import com.oj.tan.common.io.ProviderFactory;
import com.oj.tan.common.config.Address;
import com.oj.tan.common.config.SocketSession;
import com.oj.tan.writer.Future.TimeOutFuture;
import com.oj.tan.writer.writerCallBack.BaseProcessor;
import com.oj.tan.writer.writerCallBack.DefaultFutureManager;
import com.oj.tan.writer.writerCallBack.ResponseCallBack;
import com.oj.tan.writer.exception.StabilityException;
import org.apache.log4j.Logger;

import java.util.Map;

public class NettyConnection implements Connection {
    private static final Logger logger = Logger.getLogger(NettyConnection.class);
    private NioFrameworkContext context;
    private Address address;

    public NettyConnection(Address address) {
        this.address = address;
    }

    @Override
    public Map<String, Object> invokeSync(Map<String, Object> data, long timeout) throws StabilityException {
        TimeOutFuture<Map<String, Object>> future = new TimeOutFuture<>(timeout);
        DefaultFutureManager.add(future);
        try {
            doSend(data);
        } catch (Exception e) {
            DefaultFutureManager.remove(future);
            throw new StabilityException("发送失败: " + e.getMessage());
        }
        try {
            return future.get(timeout, java.util.concurrent.TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            DefaultFutureManager.remove(future);
            throw new StabilityException("调用超时");
        }
    }

    @Override
    public void invokeAsync(Map<String, Object> data, long timeout, ResponseCallBack<Map<String, Object>> callBack) throws StabilityException {
        TimeOutFuture<Map<String, Object>> future = new TimeOutFuture<>(timeout);
        DefaultFutureManager.add(future);
        try {
            doSend(data);
        } catch (Exception e) {
            DefaultFutureManager.remove(future);
            throw new StabilityException("发送失败: " + e.getMessage());
        }
        CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> result = future.get(timeout, java.util.concurrent.TimeUnit.MILLISECONDS);
                callBack.onResponse(result);
                return result;
            } catch (Exception e) {
                DefaultFutureManager.remove(future);
                throw new RuntimeException("调用超时");
            }
        });
    }

    @Override
    public void invokeCallBack(Map<String, Object> data, long timeout, ResponseCallBack<Map<String, Object>> callBack) throws StabilityException {
        TimeOutFuture<Map<String, Object>> future = new TimeOutFuture<>(timeout);
        DefaultFutureManager.add(future);
        try {
            doSend(data);
        } catch (Exception e) {
            DefaultFutureManager.remove(future);
            throw new StabilityException("发送失败: " + e.getMessage());
        }
        CompletableFuture.runAsync(() -> {
            try {
                Map<String, Object> result = future.get(timeout, java.util.concurrent.TimeUnit.MILLISECONDS);
                callBack.onResponse(result);
            } catch (Exception e) {
                DefaultFutureManager.remove(future);
                callBack.onException(new StabilityException("调用超时"));
            }
        });
    }

    @Override
    public void invokeOneWay(Map<String, Object> data, long timeout) throws StabilityException {
        try {
            doSend(data);
        } catch (Exception e) {
            throw new StabilityException("发送失败: " + e.getMessage());
        }
    }

    private void doSend(Map<String, Object> data) throws Exception {
        initContext();
        SocketSession session = context.getCurrentSession();
        if (session != null) {
            session.send(data);
        }
    }

    private void initContext() throws Exception {
        if (context == null) {
            synchronized (this) {
                if (context == null) {
                    IOProvider provider = ProviderFactory.provider();
                    if (provider instanceof NioFrameworkContext) {
                        context = (NioFrameworkContext) provider;
                        context.bindAddress(address);
                        context.start();
                    }
                }
            }
        }
    }

    @Override
    public Address getRemoteAddress() {
        return address;
    }

    @Override
    public boolean isAlive() {
        return context != null && context.getCurrentSession() != null;
    }

    @Override
    public BaseProcessor getProcessor() {
        return null;
    }
}
