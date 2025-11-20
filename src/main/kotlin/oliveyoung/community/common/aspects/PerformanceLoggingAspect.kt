package oliveyoung.community.common.aspects

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * 성능 측정 AOP
 *
 * Controller, Service, Repository 메서드 실행 시간 측정
 */
@Aspect
@Component
class PerformanceLoggingAspect {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Controller 메서드 실행 시간 측정
     */
    @Around("execution(* oliveyoung.community.domain..controller.*.*(..))")
    fun logControllerPerformance(joinPoint: ProceedingJoinPoint): Any? = measureExecutionTime(joinPoint, "CONTROLLER")

    /**
     * Service 메서드 실행 시간 측정
     */
    @Around("execution(* oliveyoung.community.domain..service.impl.*.*(..))")
    fun logServicePerformance(joinPoint: ProceedingJoinPoint): Any? = measureExecutionTime(joinPoint, "SERVICE")

    /**
     * Repository 메서드 실행 시간 측정
     */
    @Around("execution(* oliveyoung.community.domain..repository.impl.*.*(..))")
    fun logRepositoryPerformance(joinPoint: ProceedingJoinPoint): Any? = measureExecutionTime(joinPoint, "REPOSITORY")

    /**
     * 실행 시간 측정 및 로깅
     */
    private fun measureExecutionTime(
        joinPoint: ProceedingJoinPoint,
        layer: String,
    ): Any? {
        val startTime = System.currentTimeMillis()

        return try {
            val result = joinPoint.proceed()
            val executionTime = System.currentTimeMillis() - startTime

            val className = joinPoint.target.javaClass.simpleName
            val methodName = joinPoint.signature.name

            // 100ms 이상 걸린 경우만 로깅 (또는 항상 로깅)
            if (executionTime > 100) {
                logger.warn("⚠️ [$layer] $className.$methodName() took ${executionTime}ms")
            } else {
                logger.info("✅ [$layer] $className.$methodName() took ${executionTime}ms")
            }

            result
        } catch (e: Throwable) {
            val executionTime = System.currentTimeMillis() - startTime
            logger.error("[$layer] ${joinPoint.signature.name}() failed after ${executionTime}ms", e)
            throw e
        }
    }
}
