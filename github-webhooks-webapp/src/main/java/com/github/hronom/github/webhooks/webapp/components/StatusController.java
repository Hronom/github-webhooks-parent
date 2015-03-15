package com.github.hronom.github.webhooks.webapp.components;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class StatusController {
    @Resource(name="EmailManager")
    private EmailManager emailManager;

    @RequestMapping(value = "/status", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String status() {
        return "Good: " + emailManager.workDirectory;
    }
}
