package tn.esprit.microservice2.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


/**
 * Aspect for logging course access attempts
 */
@Aspect
@Component
public class CourseAccessAspect {

    private static final Logger logger = LoggerFactory.getLogger(CourseAccessAspect.class);

    /**
     * Log course access check attempts
     */
    @Around("execution(* tn.esprit.microservice2.service.CourseAccessService.hasAccessToCourse(..))")
    public Object logCourseAccessCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Long userId = (Long) args[0];
        Long courseId = (Long) args[1];
        
        HttpServletRequest request = null;
        String ipAddress = "unknown";
        String userAgent = "unknown";
        
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                request = attributes.getRequest();
                ipAddress = getClientIp(request);
                userAgent = request.getHeader("User-Agent");
            }
        } catch (Exception e) {
            logger.warn("Could not get request details: {}", e.getMessage());
        }
        
        LocalDateTime timestamp = LocalDateTime.now();
        
        // Execute the original method
        boolean hasAccess = false;
        try {
            hasAccess = (boolean) joinPoint.proceed();
        } catch (Throwable t) {
            logger.error("Error checking course access: {}", t.getMessage());
            throw t;
        }
        
        // Log access attempt
        String accessStatus = hasAccess ? "GRANTED" : "DENIED";
        logger.info("COURSE_ACCESS_LOG: timestamp={}, userId={}, courseId={}, ip={}, userAgent={}, access={}",
                timestamp, userId, courseId, ipAddress, userAgent, accessStatus);
        
        return hasAccess;
    }
    
    /**
     * Get client IP address from request
     */
    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
} 