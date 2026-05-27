package com.oj.tan.writer;

import java.util.Map;

/**
 * 序列化接口
 * @author tan
 * @version 1.0
 */
public interface MapSerializer {

    /**
     * 序列化
     * @param map 序列化Map
     * @return bytes
     */
    byte[] serialize(Map<String,Object> map);

    /**
     * 反序列化
     * @param bytes bytes
     * @return 序列化Map
     */
    Map<String,Object> deserialize(byte[] bytes);
}
