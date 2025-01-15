package com.zerock.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Log4j2
public class customerServiceController {

    @GetMapping(value = "/notic")
    public  String notic(){

        return "Custom/notic";
    }

    @GetMapping(value = "/inquity")
    public  String inquity(){

        return "Custom/inquity";
    }

    @GetMapping(value = "/eventList")
    public  String eventList(){

        log.info("이벤트 리스트 이동 완료!");

        return "Custom/eventList";
    }


    @GetMapping(value = "/eventDetail")
    public  String eventDetail(){

        log.info("eventDetail Page Success");

        return "Custom/eventDetail";
    }


}
