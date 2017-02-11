package edu.self.twitter.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ch.nyg.java.util.LogUtil;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterResponse;
import twitter4j.User;

/**
 * Service for Twitter's API.
 */
@Service
public class TwitterService {

    public int getFollowerCount(String screenName) {
        Optional<User> user = getUser(screenName);
        return user.isPresent() ? user.get().getFollowersCount() : -1;
    }

    public List<User> getUsers(List<String> screenNames) {
        List<User> list = new ArrayList<>();
        screenNames.forEach(e -> getUser(e).ifPresent(list::add));
        return list;
    }

    public Optional<User> getUser(String screenName) {

        try {
            Optional<User> user = Optional.of(TwitterFactory.getSingleton().users().showUser(screenName));
            user.ifPresent(this::printRateLimitStatus);
            return user;
        }
        catch (TwitterException e) {

            if (e.isErrorMessageAvailable()) {
                LogUtil.info("Error for '%s': %d, %s", screenName, e.getErrorCode(), e.getErrorMessage());
            }
            else {
                LogUtil.severe("Issue with '%s'", screenName);
                LogUtil.severe(e);
            }

            return Optional.empty();
        }
    }

    private void printRateLimitStatus(TwitterResponse user) {
        LogUtil.info(
            "Remaining %s out of %s. %s seconds until reset.",
            user.getRateLimitStatus().getRemaining(),
            user.getRateLimitStatus().getLimit(),
            user.getRateLimitStatus().getSecondsUntilReset());
    }
}
