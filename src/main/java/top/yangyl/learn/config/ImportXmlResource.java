package top.yangyl.learn.config;


import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ImportXmlResource {

    /**
     * Alias for {@link #locations}.
     * @see #locations
     * @see #reader
     */
    @AliasFor("locations")
    String[] value() default {};

    /**
     * Resource locations from which to import.
     * <p>Supports resource-loading prefixes such as {@code classpath:},
     * {@code file:}, etc.
     * <p>Consult the Javadoc for {@link #reader} for details on how resources
     * will be processed.
     * @since 4.2
     * @see #value
     * @see #reader
     */
    @AliasFor("value")
    String[] locations() default {};

    /**
     * {@link BeanDefinitionReader} implementation to use when processing
     * resources specified via the {@link #value} attribute.
     * <p>By default, the reader will be adapted to the resource path specified:
     * {@code ".groovy"} files will be processed with a
     * {@link org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader GroovyBeanDefinitionReader};
     * whereas, all other resources will be processed with an
     * {@link org.springframework.beans.factory.xml.XmlBeanDefinitionReader XmlBeanDefinitionReader}.
     * @see #value
     */
    Class<? extends BeanDefinitionReader> reader() default BeanDefinitionReader.class;
}
