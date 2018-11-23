package com.zhizheng.Aspect;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

@Component
@Aspect
public class ModelAspect {

    private static final Logger logger = LoggerFactory.getLogger(ModelAspect.class);

    /**
     * 切面点
     */
    private final String POINT_CUT = "execution(public * com.zhizheng..*.*Mapper.insert*(..))";

    @Pointcut(POINT_CUT)
    private void pointCut() {
    }


    /**
     * 前置通知，方法调用前被调用
     *
     * @param model
     */
    @Before(value = POINT_CUT)
    public void before(JoinPoint joinPoint) throws InvocationTargetException, IllegalAccessException {

        Object[] args = joinPoint.getArgs();
        if (args==null || args.length<1) return;
        Object model = args[0];
        if (model == null) return ;
        Method method = null;
        try {
            method = model.getClass().getMethod("setCreateDate", Date.class);
            method.invoke(model, new Date());
            logger.error("时间注入成功");
        } catch (NoSuchMethodException e) {
            logger.error("没有找到｛｝setCreateDate 方法");
        }

    }


}
