package edu.self.twitter.vp.chart;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public List<User> getAllUsers() {
        return twitterService.getUsers(usersService.getAllUsers());
    }

    public Optional<User> doesUserExists(String screenName) {
        boolean existsInDb = usersService.doesUserExists(screenName);
        return existsInDb ? twitterService.getUser(screenName) : Optional.empty();
    }

    public List<Tuple<String, Integer>> getUserFollowersStatistics(String screenName) {
        return usersService.getUserFollowersStatistics(screenName);
    }
}
