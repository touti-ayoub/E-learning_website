package tn.esprit.tpfoyer17.configurations;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

@Component
@Aspect
@Slf4j
public class AOPConfig {

	



		@AfterThrowing(pointcut = "execution(* tn.esprit..*.*(..))", throwing = "ex")
		public void handleExceptions(Exception ex) {
			logErrorBasedOnExceptionType(ex);
		}

		private void logErrorBasedOnExceptionType(Exception ex) {
			if (ex instanceof IOException ioEx) {
				log.error("IOException occurred: {}", ioEx.getMessage());
			} else if (ex instanceof SQLException sqlEx) {
				log.error("SQLException occurred: {}", sqlEx.getMessage());
			} else {
				log.error("Exception occurred: {}", ex.getMessage());
			}
		}
	}


