package com.oj.tan.rpc.router.hashconsistent;

import com.oj.tan.rpc.router.AbstractRouter;
import com.oj.tan.rpc.model.ServiceMetaInfo;
import com.oj.tan.rpc.model.ServiceURL;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class HashConsistentRouter extends AbstractRouter {
    private static final int VIRTUAL_NODE_NUM = 160;
    private final Map<String, TreeMap<Long, ServiceMetaInfo>> ringCache = new ConcurrentHashMap<>();

    @Override
    public ServiceMetaInfo doRoute(ServiceURL serviceURL, List<ServiceMetaInfo> serviceMetaInfos) {
        String serviceKey = serviceURL.getServiceName();
        TreeMap<Long, ServiceMetaInfo> ring = ringCache.computeIfAbsent(serviceKey, k -> {
            TreeMap<Long, ServiceMetaInfo> newRing = new TreeMap<>();
            for (ServiceMetaInfo meta : serviceMetaInfos) {
                for (int i = 0; i < VIRTUAL_NODE_NUM / 4; i++) {
                    byte[] digest = md5(meta.getServiceAddress() + "#" + i);
                    for (int h = 0; h < 4; h++) {
                        long hash = hash(digest, h);
                        newRing.put(hash, meta);
                    }
                }
            }
            return newRing;
        });
        long hash = hash(md5(serviceURL.toString()), 0);
        Map.Entry<Long, ServiceMetaInfo> entry = ring.ceilingEntry(hash);
        if (entry == null) {
            entry = ring.firstEntry();
        }
        return entry.getValue();
    }

    private long hash(byte[] digest, int number) {
        return (((long) (digest[3 + number * 4] & 0xFF) << 24)
                | ((long) (digest[2 + number * 4] & 0xFF) << 16)
                | ((long) (digest[1 + number * 4] & 0xFF) << 8)
                | (digest[number * 4] & 0xFF))
                & 0xFFFFFFFFL;
    }

    private byte[] md5(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not supported", e);
        }
    }
}
