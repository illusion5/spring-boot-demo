package top.yangyl.learn.common;

import org.springframework.stereotype.Component;

@Component
public class MyBean {

    private String bean="myBean";

    public String getBean() {
        return bean;
    }

    public void setBean(String bean) {
        this.bean = bean;
    }
}
