package top.yangyl.learn.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader;
import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.*;

public class ImportXmlPostProcessor implements BeanDefinitionRegistryPostProcessor,PriorityOrdered,ResourceLoaderAware, EnvironmentAware {

    private final Log logger = LogFactory.getLog(getClass());

    Map<Class<?>, BeanDefinitionReader> readerInstanceCache = new HashMap<Class<?>, BeanDefinitionReader>();

    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    private Environment environment;

    private Set<String> cacheSet=new HashSet<>();

    private String[] defaultRootPath={"file:./config/","file:./","classpath:/config/","classpath:/"};
    
    private PathMatchingResourcePatternResolver pathResolver=new PathMatchingResourcePatternResolver();

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        String[] candidateNames = registry.getBeanDefinitionNames();
        for (String beanName : candidateNames) {
            BeanDefinition beanDef = registry.getBeanDefinition(beanName);
            if (beanDef instanceof AnnotatedBeanDefinition) {
                AnnotatedBeanDefinition definition = (AnnotatedBeanDefinition) beanDef;
                if (beanDef.getBeanClassName() != null && definition.getMetadata() != null &&
                        beanDef.getBeanClassName().equals(definition.getMetadata().getClassName()) &&
                            definition.getMetadata().hasAnnotation(ImportXmlResource.class.getName())) {
                    parseXml(definition,registry);
                }
            }
        }

    }

    private void parseXml(AnnotatedBeanDefinition definition,BeanDefinitionRegistry registry){
        AnnotationMetadata metadata = definition.getMetadata();
        MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(ImportXmlResource.class.getName());
        List<Object> locations = attributes.get("locations");
        if(!CollectionUtils.isEmpty(locations)){
            String[] xmls = (String[])locations.get(0);
            for (String xml:xmls){
                if(!cacheSet.contains(xml)){
                    logger.info("ImportXmlResource加载配置文件:"+xml);
                    loadBeanDefinitionsFromImportedResources(getXmlPath(xml),registry);
                }

            }

        }
    }

    private String getXmlPath(String xml){
        for (String path:defaultRootPath){
            String realPath = path + xml;
            try {
                Resource resource = pathResolver.getResource(realPath);
                resource.getInputStream();
            }catch (Exception e){
                continue;
            }
            logger.info("ImportXmlResource实际加载配置文件:"+realPath);
            return realPath;
        }
        throw new RuntimeException("ImportXmlResource加载配置文件:"+xml+"不存在");
    }

    private void loadBeanDefinitionsFromImportedResources(String resource,BeanDefinitionRegistry registry){
        Class<? extends BeanDefinitionReader> readerClass = BeanDefinitionReader.class;
        // Default reader selection necessary?
        if (BeanDefinitionReader.class == readerClass) {
            if (StringUtils.endsWithIgnoreCase(resource, ".groovy")) {
                // When clearly asking for Groovy, that's what they'll get...
                readerClass = GroovyBeanDefinitionReader.class;
            }
            else {
                // Primarily ".xml" files but for any other extension as well
                readerClass = XmlBeanDefinitionReader.class;
            }
        }

        BeanDefinitionReader reader = readerInstanceCache.get(readerClass);
        if (reader == null) {
            try {
                // Instantiate the specified BeanDefinitionReader
                reader = readerClass.getConstructor(BeanDefinitionRegistry.class).newInstance(registry);
                // Delegate the current ResourceLoader to it if possible
                if (reader instanceof AbstractBeanDefinitionReader) {
                    AbstractBeanDefinitionReader abdr = ((AbstractBeanDefinitionReader) reader);
                    abdr.setResourceLoader(resourceLoader);
                    abdr.setEnvironment(this.environment);
                }
                readerInstanceCache.put(readerClass, reader);
            }
            catch (Throwable ex) {
                throw new IllegalStateException(
                        "Could not instantiate BeanDefinitionReader class [" + readerClass.getName() + "]");
            }
        }

        // TODO SPR-6310: qualify relative path locations as done in AbstractContextLoader.modifyLocations
        reader.loadBeanDefinitions(resource);
    }

    @Override
    public void setEnvironment(Environment environment) {
        Assert.notNull(environment, "Environment must not be null");
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");
        this.resourceLoader = resourceLoader;
    }
}
