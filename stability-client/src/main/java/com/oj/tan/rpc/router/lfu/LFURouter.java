package com.oj.tan.rpc.router.lfu;

import com.oj.tan.rpc.router.AbstractRouter;
import com.oj.tan.rpc.model.ServiceMetaInfo;
import com.oj.tan.rpc.model.ServiceURL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LFURouter extends AbstractRouter {
    private final Map<String, Map<String, Integer>> frequencyMap = new HashMap<>();

    @Override
    public ServiceMetaInfo doRoute(ServiceURL serviceURL, List<ServiceMetaInfo> serviceMetaInfos) {
        String serviceKey = serviceURL.toString();
        Map<String, Integer> freq = frequencyMap.computeIfAbsent(serviceKey, k -> new HashMap<>());

        ServiceMetaInfo selected = null;
        int minFreq = Integer.MAX_VALUE;
        for (ServiceMetaInfo meta : serviceMetaInfos) {
            int f = freq.getOrDefault(meta.getServiceAddress(), 0);
            if (f < minFreq) {
                minFreq = f;
                selected = meta;
            }
        }

        if (selected != null) {
            freq.put(selected.getServiceAddress(), minFreq + 1);
        }
        return selected != null ? selected : serviceMetaInfos.get(0);
    }
}
