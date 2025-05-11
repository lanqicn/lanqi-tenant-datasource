package io.lanqi.plus.tenant.datasource;

import io.lanqi.plus.tenant.TenantContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 动态数据源切面逻辑
 * 请注意：这里order一定要小于tx:annotation-driven的order，即先执行DynamicDataSourceAspectAdvice切面，再执行事务切面，才能获取到最终的数据源（请特别关注seata的order）
 */
@Aspect
@Component
@Order(-100)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class DynamicDataSourceAspect {
    private final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

    private static final int INTERNAL_SERVER_ERROR = 500;

    @Pointcut("@within(TenantDataSource) || @annotation(TenantDataSource)")
    public void tenantDataSource() {

    }

    @Around("tenantDataSource()")
    public Object doAround(ProceedingJoinPoint jp) throws Throwable {
        Object result = null;
        Long tenantId = TenantContextHolder.getTenantId();
        logger.info("===TenantDataSourceAspect===当前租户Id:{}", tenantId);
        try {
            if (Objects.nonNull(tenantId)) {
                DynamicDataSourceContextHolder.setDataSourceKey(String.valueOf(tenantId));
                result = jp.proceed();
            } else {
                throw new RuntimeException("当前租户信息未取到，请联系管理员");
            }
        } catch (Exception e) {
            logger.info("===TenantDataSourceAspect===切换租户数据源失败，租户id:{}", tenantId, e);
            throw new RuntimeException("切换租户数据源失败，请联系管理员");
        } finally {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
        return result;
    }
}
