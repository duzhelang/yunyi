package com.oj.tan.rpc.router.roundrobin;

import com.oj.tan.rpc.router.AbstractRouter;
import com.oj.tan.rpc.model.ServiceMetaInfo;
import com.oj.tan.rpc.model.ServiceURL;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinRouter extends AbstractRouter {
    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    public ServiceMetaInfo doRoute(ServiceURL serviceURL, List<ServiceMetaInfo> serviceMetaInfos) {
        int size = serviceMetaInfos.size();
        if (size == 1) {
            return serviceMetaInfos.get(0);
        }
        int i = index.getAndIncrement();
        return serviceMetaInfos.get((i & Integer.MAX_VALUE) % size);
    }
}
