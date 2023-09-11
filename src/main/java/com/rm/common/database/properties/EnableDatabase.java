package com.rm.common.database.properties;

import com.rm.common.database.configuration.DatabaseConfigurationSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 데이터베이스 관련 빈을 활성화시키는 애노테이션
 * 해당 애노테이션에 대한 정보는 {@link DatabaseConfigurationSelector} 클래스를 참고바람
 * @see DatabaseConfigurationSelector
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(DatabaseConfigurationSelector.class)
public @interface EnableDatabase {
}
