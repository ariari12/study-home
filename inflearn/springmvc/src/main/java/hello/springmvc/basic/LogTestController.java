package hello.springmvc.basic;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LogTestController {
//    private final Logger log = LoggerFactory.getLogger(getClass());
    @RequestMapping("/log-test")
    public String logTest(){
        String name = "Spring";
        System.out.println("name = " + name);

        //로깅 레벨이 debug이상일 때 trace로그는 안나와야함
        //하지만 아래처럼 만들면 trace로그가 안나오지만 + 연산을 하므로
        // cpu, 메모리 등 낭비가 있음
        //log.trace("trace = "+name); 이렇게 사용안함
        log.trace("trace = {}",name);

        log.debug("debug = {}",name);
        log.info("info = {}",name);
        log.warn("warn = {}",name);
        log.error("error = {}",name);

        return "ok";
    }

}
