package com.test.jacoco;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author txgj
 * @date 2023/11/8
 */
public class SimpleClassForJacoco {
    private static final Map<String, String> WORKER_MAP = new ConcurrentHashMap(2);

    public static String getWorker(String id){
        return WORKER_MAP.get(id);
    }

    public static String removeWorker(String id){
        return WORKER_MAP.remove(id);
    }

    public static void addWorker(String id, String worker){
        WORKER_MAP.put(id, worker);
    }
}
