//package com.team1.etcommon.aop;
//
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.stereotype.Component;
//
//@Aspect
//@Component
//@Slf4j
//public class LoggingAspect {
//    /**
//     * 포인트컷(execution(* com.team1..*Service.*(..)))에 해당하는 메서드 실행 전/후 시간을 측정합니다.
//     * @param joinPoint 대상 메서드 실행 정보
//     * @return 대상 메서드의 원래 반환값
//     * @throws Throwable 대상 메서드에서 발생할 수 있는 예외
//     */
//    @Around("execution(* com.team1..*Service.*(..))")
//    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
//        long startTime = System.currentTimeMillis();
//        try {
//            return joinPoint.proceed();
//        } finally {
//            long endTime = System.currentTimeMillis();
//            log.info("{} 메서드 실행 시: {} ms가 소요되었습니다.", joinPoint.getSignature(), (endTime - startTime));
//        }
//    }
//}
