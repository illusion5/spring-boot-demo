package top.yangyl.learn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import top.yangyl.learn.config.ImportXmlResource;

@SpringBootApplication
//@ImportResource({"classpath:my-bean.xml"})
@ImportXmlResource({"my-bean.xml"})
public class SpringBootDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootDemoApplication.class, args);
	}
}
