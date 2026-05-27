package com.oj.tan.rpc.loadbalancer.random;

import com.oj.tan.common.config.Address;
import com.oj.tan.rpc.loadbalancer.AbstractLoadBalancer;
import com.oj.tan.rpc.model.ServiceMetaInfo;
import com.oj.tan.rpc.model.ServiceURL;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomLoadBalance extends AbstractLoadBalancer {
    @Override
    protected ServiceMetaInfo doSelect(ServiceURL serviceURL, List<ServiceMetaInfo> serviceMetaInfos) {
        int size = serviceMetaInfos.size();
        if (size == 1) {
            return serviceMetaInfos.get(0);
        }
        return serviceMetaInfos.get(ThreadLocalRandom.current().nextInt(size));
    }
}
