package edu.self.twitter.vp.chart;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;

import edu.self.twitter.business.TwitterService;
import edu.self.twitter.business.UsersService;
import edu.self.twitter.model.Tuple;
import twitter4j.User;

@Component
public class ChartPresenter {

    @Autowired
    private UsersService usersService;

    @Autowired
    private TwitterService twitterService;

    public BeanContainer<String, User> getAllUsers() {
        BeanContainer<String, User> container = new BeanContainer<>(User.class);
        container.setBeanIdResolver(bean -> bean.getScreenName());
        container.addAll(twitterService.getUsers(usersService.getAllUsers()));
        return container;
    }

    public boolean doesUserExists(String screenName) {
        return usersService.doesUserExists(screenName) && twitterService.getUser(screenName).isPresent();
    }

    public boolean deleteUser(String screenName) {
        return usersService.deleteUser(screenName);
    }

    public List<String> addUsers(String[] screenNames) {
        return usersService.addUsers(screenNames);
    }

    public BeanItemContainer<Tuple<String, Integer>> getUserFollowersStatistics(String screenName) {
        return new BeanItemContainer<>(Tuple.class, usersService.getUserFollowersStatistics(screenName));
    }
}
