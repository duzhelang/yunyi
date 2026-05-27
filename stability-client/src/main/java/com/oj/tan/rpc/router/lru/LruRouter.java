package com.oj.tan.rpc.router.lru;

import com.oj.tan.rpc.router.AbstractRouter;
import com.oj.tan.rpc.model.ServiceMetaInfo;
import com.oj.tan.rpc.model.ServiceURL;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LruRouter extends AbstractRouter {
    private final LinkedHashMap<String, ServiceMetaInfo> lruCache = new LinkedHashMap<>(16, 0.75f, true);

    @Override
    public ServiceMetaInfo doRoute(ServiceURL serviceURL, List<ServiceMetaInfo> serviceMetaInfos) {
        String serviceKey = serviceURL.toString();
        ServiceMetaInfo cached = lruCache.get(serviceKey);
        if (cached != null && serviceMetaInfos.contains(cached)) {
            return cached;
        }
        ServiceMetaInfo selected = serviceMetaInfos.get(0);
        lruCache.put(serviceKey, selected);
        return selected;
    }
}
