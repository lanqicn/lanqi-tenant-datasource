package io.lanqi.plus.tenant.datasource;

import com.alibaba.ttl.TransmittableThreadLocal;
import io.lanqi.plus.tenant.constant.TenantConstant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class DynamicDataSourceContextHolder {

    private static final String EMPTY = "";

    private static final TransmittableThreadLocal<String> contextHolder = new TransmittableThreadLocal<>() {
        /**
         * 将 master 数据源的 key作为默认数据源的 key
         */
        @Override
        protected String initialValue() {
            return TenantConstant.MASTER_DATASOURCE;
        }
    };

    /**
     * 数据源的 key集合，用于切换时判断数据源是否存在
     */
    public static List<String> dataSourceKeys = new ArrayList<>();


    /**
     * 切换数据源
     *
     * @param key 数据源
     */
    public static void setDataSourceKey(String key) {
        if (Objects.nonNull(key) && !Objects.equals(EMPTY, key)) {
            contextHolder.set(key);
        }
    }


    /**
     * 获取数据源
     *
     * @return
     */
    public static String getDataSourceKey() {
        return contextHolder.get();
    }


    /**
     * 重置数据源
     */
    public static void clearDataSourceKey() {
        contextHolder.remove();
    }


    /**
     * 判断是否包含数据源
     *
     * @param key 数据源
     * @return
     */
    public static boolean containDataSourceKey(String key) {
        return dataSourceKeys.contains(key);
    }


    /**
     * 添加数据源Keys
     *
     * @param keys
     * @return
     */
    public static boolean addDataSourceKeys(Collection<String> keys) {
        return dataSourceKeys.addAll(keys);
    }
}
