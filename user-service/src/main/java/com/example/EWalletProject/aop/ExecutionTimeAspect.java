package com.example.EWalletProject.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.logging.Logger;

@Slf4j
@Aspect
@Component
public class ExecutionTimeAspect {


    @Around("execution(* com.example.EWalletProject.UserService.*(..))")
  public Object executionTime(ProceedingJoinPoint joinPoint) throws Throwable {

      long start =  System.currentTimeMillis();
      Object res=joinPoint.proceed();
      long end =  System.currentTimeMillis();

      System.out.println("execution time for "+joinPoint.getSignature().getName()+"  "+ (end - start) + "ms");
      return  res;

  }

}
