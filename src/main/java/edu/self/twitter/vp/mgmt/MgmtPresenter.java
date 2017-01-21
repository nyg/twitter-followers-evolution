package edu.self.twitter.vp.mgmt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.self.twitter.business.UsersService;

@Component
public class MgmtPresenter {

    @Autowired
    UsersService usersService;

    public List<String> insertUsers(String[] screenNames) {
        return usersService.insertUsers(screenNames);
    }
}
