package es.daw.pixaymvc.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Configuration;

/**
 * Clase técnica para resolver el conflicto entre Web y WebFlux
 * sin tocar archivos de configuración externos.
 */
@Configuration
public class ErrorFixConfig implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (beanFactory instanceof BeanDefinitionRegistry) {
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;

            // Si Spring ha detectado dos componentes con el mismo nombre (el de Web y el de WebFlux)
            if (registry.containsBeanDefinition("conventionErrorViewResolver")) {
                // Borramos uno de la lista.
                // Spring luego usará el otro automáticamente y la aplicación arrancará.
                registry.removeBeanDefinition("conventionErrorViewResolver");
            }
        }
    }
}