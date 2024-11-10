package com.y_lab.annotation;

import com.y_lab.aspect.AuditConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Import(AuditConfiguration.class)
public @interface EnableAudit {
}
