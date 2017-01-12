package edu.self.twitter.mvp.chart;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.self.twitter.business.UsersService;

@Component
public class ChartPresenter {

    @Autowired
    UsersService usersService;

    public List<String> getAllUsers() {
        return usersService.getAllUsers();
    }
}
