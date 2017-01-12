package edu.self.twitter.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.self.twitter.model.LongTuple;

@Controller
public class JsonData {

    private static final String OSB = "[";
    private static final String COMMA = ",";
    private static final String CSB = "]";
    @Autowired
    UsersService usersService;

    @RequestMapping(value = "/data", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String data(@RequestParam("callback") String callback, @RequestParam("name") String name) {

        StringBuilder sb = new StringBuilder(callback + "([");
        List<LongTuple> history = usersService.getUserHistory(name);

        history.forEach(tuple -> {
            sb.append(OSB).append(tuple.x).append(COMMA).append(tuple.y).append(CSB);
            if (history.indexOf(tuple) != history.size() - 1) {
                sb.append(COMMA);
            }
        });

        return sb.append("]);").toString();
    }
}
