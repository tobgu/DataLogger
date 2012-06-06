package datalogger;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

@Aspect
public class Logging {
   Gson gson = new Gson();
   Configuration config = new Configuration();

   @Around("execution(@datalogger.DoLog * *.*(..))")
   public Object loggerError(final ProceedingJoinPoint jp) throws Throwable {
      LogLevel level = getLogLevel(jp);
      final Logger logger = getLoggerForClass(jp);
      boolean loggable = isLoggable(logger, level);
      
      if(loggable) {
         return executeLogging(jp, level, logger, loggable);
      } else {
         return jp.proceed();
      }
   }

   private Object executeLogging(final ProceedingJoinPoint jp, LogLevel level,
         final Logger logger, boolean loggable) throws Throwable {
      String message = buildEntryLogging(jp);
      logMessage(logger, message, level);
      try {
         Object retVal = jp.proceed();
         String returnMessage = buildExitLogging(jp, retVal);
         logMessage(logger, returnMessage, level);
         return retVal;
      } catch(Throwable t) {
         String throwMessage = buildExceptionLogging(jp, t);
         logException(logger, throwMessage, level, t);
         throw t;
      }
   }

   private void logException(Logger logger, String message, LogLevel level, Throwable t) {
      switch(level) {
      case TRACE: logger.trace(message, t); break;
      case DEBUG: logger.debug(message, t); break;
      case INFO: logger.info(message, t); break;
      case WARNING: logger.error(message, t); break;         
      case ERROR: default: logger.error(message, t); break;
      }
   }

   private void logMessage(Logger logger, String message, LogLevel level) {
      switch(level) {
      case TRACE: logger.trace(message); break;
      case DEBUG: logger.debug(message); break;
      case INFO: logger.info(message); break;
      case WARNING: logger.error(message); break;         
      case ERROR: default: logger.error(message); break;
      }
   }

   private LogLevel getLogLevel(final JoinPoint jp) {
      MethodSignature signature = (MethodSignature) jp.getSignature();
      Method method = signature.getMethod();
      DoLog annotation = method.getAnnotation(DoLog.class);
      LogLevel level = annotation.level();
      return level;
   }

   private boolean isLoggable(final Logger logger, LogLevel level) {
      switch(level) {
      case TRACE: return logger.isTraceEnabled();
      case DEBUG: return logger.isDebugEnabled();
      case INFO: return logger.isInfoEnabled();
      case WARNING: return logger.isWarnEnabled();
      case ERROR: default: return logger.isErrorEnabled();
      }
   }

   private Logger getLoggerForClass(final JoinPoint jp) {
      return LoggerFactory.getLogger(jp.getTarget().getClass());
   }

   private String buildEntryLogging(final JoinPoint jp) {
      final StringBuilder message = new StringBuilder();
      message.append("Method Invocation=[")
      .append(jp.getTarget().getClass().getName())
      .append(".")
      .append(jp.getSignature().getName())
      .append("]")
      .append(" - Args: ");
      if(config.isVerbose()) {
         for(Object arg : jp.getArgs()) {
            //toLog(message, arg);
            toJson(message, arg);
         }
      } else {
         message.append(Arrays.toString(jp.getArgs()));         
      }

      return message.toString();
   }

   private void toJson(StringBuilder message, Object arg) {
      Class<? extends Object> clazz = arg.getClass();
      message.append(clazz.getName());
      message.append(":");
      message.append(gson.toJson(arg));
   }

   private String buildExitLogging(final JoinPoint jp, Object retVal) {
      final StringBuilder message = new StringBuilder();
      message.append("Method Return=[")
      .append(jp.getTarget().getClass().getName())
      .append(".")
      .append(jp.getSignature().getName())
      .append("]")
      .append(" - Return Value: ");
      if(config.isVerbose()) {
         toJson(message, retVal);
      } else {
         message.append(retVal);         
      }

      return message.toString();
   }

   private String buildExceptionLogging(ProceedingJoinPoint jp, Throwable t) {
      final StringBuilder message = new StringBuilder();
      message.append("Method Exception=[")
      .append(jp.getSignature().getName())
      .append("]")
      .append(" - Exception: ")
      .append(t.getMessage());
      return message.toString();
   }
}


// Dynamic logging level
//def serverUrl = "service:jmx:rmi:///jndi/rmi://${args[2]}:${args[3]}/jmxrmi"
//def server = JMXConnectorFactory.connect(new JMXServiceURL(serverUrl)).MBeanServerConnection
//def mbeanName = new ObjectName(LogManager.LOGGING_MXBEAN_NAME)
//LoggingMXBean mxbeanProxy =
//JMX.newMXBeanProxy(server, mbeanName, LoggingMXBean.class);
//mxbeanProxy.setLoggerLevel(args[0], args[1].toUpperCase())
