package tn.esprit.microservice2.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tn.esprit.microservice2.Model.Course;
import tn.esprit.microservice2.Model.Subscription;
import tn.esprit.microservice2.Model.SubscriptionStatus;
import tn.esprit.microservice2.Model.User;
import tn.esprit.microservice2.repo.ICourseRepository;
import tn.esprit.microservice2.repo.ISubscriptionRepository;
import tn.esprit.microservice2.repo.UserRepository;

import java.util.Optional;

@Service
public class CourseAccessService {
    
    private static final Logger logger = LoggerFactory.getLogger(CourseAccessService.class);
    
    private final ICourseRepository courseRepository;
    private final ISubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    
    public CourseAccessService(ICourseRepository courseRepository, 
                              ISubscriptionRepository subscriptionRepository,
                              UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * Check if a user has access to a course
     * @param userId The user's ID
     * @param courseId The course's ID
     * @return true if the user has access, false otherwise
     */
    public boolean hasAccessToCourse(Long userId, Long courseId) {
        logger.debug("Checking access for user {} to course {}", userId, courseId);
        
        // TEMPORARY FOR TESTING: Allow access to all courses
        // Remove this line in production
        return true;
        
        /* 
        // Get the course
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            logger.debug("Course {} not found", courseId);
            return false; // Course doesn't exist
        }
        
        Course course = courseOpt.get();
        logger.debug("Course {} found, free: {}", courseId, course.isFree());
        
        // If course is free, allow access
        if (course.isFree()) {
            logger.debug("Course {} is free, granting access", courseId);
            return true;
        }
        
        // If course is not free, check if user has an active subscription
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            logger.debug("User {} not found", userId);
            return false; // User doesn't exist
        }
        
        User user = userOpt.get();
        logger.debug("User {} found", userId);
        
        // Check if user has an active subscription for this course
        boolean hasSubscription = subscriptionRepository.findByUserIdAndCourseId(userId, courseId)
                .stream()
                .anyMatch(Subscription::isActive);
        
        logger.debug("User {} has active subscription for course {}: {}", userId, courseId, hasSubscription);
        return hasSubscription;
        */
    }
    
    /**
     * Check if a course is accessible publicly (free course)
     * @param courseId The course's ID
     * @return true if the course is free, false otherwise
     */
    public boolean isCoursePubliclyAccessible(Long courseId) {
        logger.debug("Checking if course {} is publicly accessible", courseId);
        
        // TEMPORARY FOR TESTING
        return true;
        
        /*
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            logger.debug("Course {} not found", courseId);
            return false; // Course doesn't exist
        }
        
        boolean isFree = courseOpt.get().isFree();
        logger.debug("Course {} is free: {}", courseId, isFree);
        return isFree;
        */
    }
    
    /**
     * For development testing: always allow access
     * @param userId The user ID
     * @param courseId The course ID
     * @return Always true during development
     */
    public boolean developmentAccess(Long userId, Long courseId) {
        logger.info("TEST MODE: Granting access to user {} for course {}", userId, courseId);
        return true;
    }
} 