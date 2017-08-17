package edu.self.twitter.vp.admin;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.self.twitter.business.UsersService;

@Component
public class AdminPresenter {

    @Autowired
    private UsersService usersService;

    public List<String> getAllUsers() {
        return usersService.getAllUsers();
    }

    public void deleteUsers(Set<String> usernames) {
        usernames.forEach(usersService::deleteUser);
    }

    public void addUsers(String screenNames) {
        if (screenNames != null) {
            usersService.addUsers(screenNames.split(","));
        }
    }
}
