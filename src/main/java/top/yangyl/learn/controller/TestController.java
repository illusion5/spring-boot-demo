package top.yangyl.learn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import top.yangyl.learn.common.MyBean;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @Autowired
    private MyBean myBean;

    @RequestMapping(value = "test01",method = RequestMethod.GET)
    public Map test01(){
        Map map=new HashMap();
        map.put("bean",myBean.getBean());
        return map;
    }

}
