package datalogger;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DoLog {
	LogLevel level() default LogLevel.DEBUG;
}
