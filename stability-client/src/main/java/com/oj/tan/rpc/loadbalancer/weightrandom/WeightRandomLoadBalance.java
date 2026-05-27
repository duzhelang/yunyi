package com.oj.tan.rpc.loadbalancer.weightrandom;

import com.oj.tan.rpc.loadbalancer.AbstractLoadBalancer;
import com.oj.tan.rpc.model.ServiceMetaInfo;
import com.oj.tan.rpc.model.ServiceURL;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WeightRandomLoadBalance extends AbstractLoadBalancer {
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
        double random = ThreadLocalRandom.current().nextDouble() * totalWeight;
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
