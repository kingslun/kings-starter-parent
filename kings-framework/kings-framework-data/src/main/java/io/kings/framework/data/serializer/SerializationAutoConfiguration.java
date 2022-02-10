package io.kings.framework.data.serializer;

import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;

/**
 * 序列化组件自动装配 根据配置初始化个性化序列化组件 目前支持三种序列化功能 详细可查阅{@link Serializer} 暂时只开放序列化器IOC管理
 *
 * @author lun.kings
 * @date 2020/8/1 6:19 下午
 * @since v2.0
 */
@ConditionOnSerialize
@ConditionalOnMissingBean(Serializer.class)
public class SerializationAutoConfiguration implements ApplicationContextAware {

    /**
     * 根据配置装配个性化序列化器
     */
    @Override
    public void setApplicationContext(@NonNull ApplicationContext ctx) throws BeansException {
        if (ctx instanceof ConfigurableApplicationContext) {
            ConfigurableApplicationContext cac = (ConfigurableApplicationContext) ctx;
            // 获取bean工厂并转换为DefaultListableBeanFactory
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) cac.getBeanFactory();
            ServiceLoader<Serializer> serializers = ServiceLoader.load(Serializer.class);
            AtomicBoolean def = new AtomicBoolean(true);
            serializers.forEach(serializer -> {
                def.set(false);
                // 通过BeanDefinitionBuilder创建bean定义
                BeanDefinitionBuilder definition =
                    BeanDefinitionBuilder.genericBeanDefinition(serializer.getClass());
                // 注册bean
                beanFactory.registerBeanDefinition(serializer.getBeanName(),
                    definition.getRawBeanDefinition());
            });
            if (def.get()) {
                Serializer serializer = Serializer.DEFAULT_;
                // 通过BeanDefinitionBuilder创建bean定义
                BeanDefinition definition =
                    BeanDefinitionBuilder.genericBeanDefinition(serializer.getClass())
                        .getBeanDefinition();
                // 注册bean
                beanFactory.registerBeanDefinition(serializer.getBeanName(), definition);
            }
        }
    }
}
