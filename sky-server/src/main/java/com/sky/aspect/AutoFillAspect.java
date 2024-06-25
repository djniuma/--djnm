package com.sky.aspect;


import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 公共字段填充切面类
 * 拦截添加有AutoFill的方法，通过反射自动填充字段
 */
@Component//加入到Spring的IoC容器中
@Aspect//代表当前类为切面类
@Slf4j
public class AutoFillAspect {
    //定义切入点 mapper包下所有类的所有方法参数任意&& 包含有@AutoFill注解的方法
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointcut(){

    }

    //定义通知方法
    @Before("autoFillPointcut()")
    public void Before(JoinPoint joinPoint){
        log.info("开始进行字段自动填充");
        //1.获取到被拦截方法上的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();// 获取方法签名
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获取方法上的autofill注解
        OperationType opertionType = autoFill.value();//获取操作类型 INSERT UPDATE

        //2.获取到被拦截方法上的第一个参数 实体对象 （约定好，第一个参数必须是实体对象）
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0){
            return;
        }
        Object entity = args[0];

        //3.准备要赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //4.根据不同的操作类型，利用反射进行赋值
        if (opertionType == OperationType.INSERT){
            //新增的操作类型，需要为4个公共字段赋值
            //通过反射获取到Method
            try {
                //通过反射获取到Method
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //通过反射执行Method进行赋值，调用method方法
                setCreateTime.invoke(entity, now);
                setUpdateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateUser.invoke(entity, currentId);


            } catch (Exception e) {
                log.error("公共字段自动填充失败");
            }
        } else if (opertionType == OperationType.UPDATE){
            //修改的操作类型，需要为2个公共字段赋值
            try {
                //通过反射获取到Method
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //通过反射执行Method进行赋值，调用method方法
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);


            } catch (Exception e) {
                log.error("公共字段自动填充失败");
            }
        }

    }
}
