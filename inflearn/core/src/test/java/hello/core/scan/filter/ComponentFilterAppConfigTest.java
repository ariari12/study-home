package hello.core.scan.filter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import static org.springframework.context.annotation.ComponentScan.*;

public class ComponentFilterAppConfigTest {
    @Test
    void filterScan(){
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ComponentFilterAppConfig.class);
        BeanA bean = ac.getBean(BeanA.class);
        Assertions.assertThat(bean).isNotNull();
        org.junit.jupiter.api.Assertions.assertThrows(NoSuchBeanDefinitionException.class, () -> ac.getBean(BeanB.class));


    }
    @Configuration //type = FilterType.ANNOTATION 은 기본 값이다 없어도 상관없음
    @ComponentScan(includeFilters = @Filter(type = FilterType.ANNOTATION, classes = MyIncludeComponent.class))
    @ComponentScan(excludeFilters = @Filter(classes = MyExcludeComponent.class))
    static class ComponentFilterAppConfig {

    }
}
