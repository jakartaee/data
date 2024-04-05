package ee.jakarta.tck.data.common.cdi;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This is a custom lifecycle annotation only supported by the Jakarta Data provider present
 * in this TCK and is used to verify the <code>@Repository</code> provider attribute is honored by 
 * any other Jakarta Data providers present on the tests classpath.
 * 
 * This lifecycle annotation is named in such a way that it should not conflict with any other 
 * custom lifecycle annotations from other Jakarta Data providers.
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface PutTCKLifecyleMethod {

}
