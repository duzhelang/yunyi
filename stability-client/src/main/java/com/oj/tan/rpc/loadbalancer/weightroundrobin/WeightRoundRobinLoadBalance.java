package com.oj.tan.rpc.loadbalancer.weightroundrobin;

import com.oj.tan.rpc.loadbalancer.AbstractLoadBalancer;
import com.oj.tan.rpc.model.ServiceMetaInfo;
import com.oj.tan.rpc.model.ServiceURL;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class WeightRoundRobinLoadBalance extends AbstractLoadBalancer {
    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    protected ServiceMetaInfo doSelect(ServiceURL serviceURL, List<ServiceMetaInfo> serviceMetaInfos) {
        int size = serviceMetaInfos.size();
        if (size == 1) {
            return serviceMetaInfos.get(0);
        }
        double totalWeight = 0;
        for (ServiceMetaInfo meta : serviceMetaInfos) {
            totalWeight += meta.getWeight();
        }
        double random = (index.getAndIncrement() & Integer.MAX_VALUE) % totalWeight;
        double current = 0;
        for (ServiceMetaInfo meta : serviceMetaInfos) {
            current += meta.getWeight();
            if (random < current) {
                return meta;
            }
        }
        return serviceMetaInfos.get(0);
    }
}
