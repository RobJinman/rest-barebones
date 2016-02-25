package com.recursiveloop.webcommon.config;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.inject.Qualifier;
import javax.enterprise.util.Nonbinding;


@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface ConfigParam {
  @Nonbinding String key() default "[unassigned]";
  @Nonbinding boolean mandatory() default false;
  @Nonbinding String defaultValue() default "";
}
