package com.rm.common.database.configuration;

import com.rm.common.database.properties.EnableDatabase;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * {@link EnableDatabase} 애노테이션 설정 시 불러 올 Configuration 클래스들을 정의해두는 ImportSelector
 *
 * 데이터베이스 관련 설정 및 빈 생성 등의 전반적인 행위를 담당하고 있으며, 자세한 설명은 각 클래스들을 참고하길 바람
 *
 * @see DataSourceConfiguration
 */

public class DatabaseConfigurationSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{
                DataSourceConfiguration.class.getName()
        };
    }

}
