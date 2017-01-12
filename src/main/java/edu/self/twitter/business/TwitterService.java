package edu.self.twitter.business;

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

    public int getFollowersCount(String screenName) {
        try {
            User user = TwitterFactory.getSingleton().users().showUser(screenName);
            //printRateLimitStatus(user);
            return user.getFollowersCount();
        }
        catch (TwitterException e) {
            if (e.getErrorCode() == 50) {
                LogUtil.info("User not found: " + screenName);
            }
            else {
                LogUtil.severe(e);
            }
            return -1;
        }
    }

    @SuppressWarnings("unused")
    private void printRateLimitStatus(TwitterResponse user) {
        LogUtil.info(
            "Remaining %s out of %s. %s seconds until reset.",
            user.getRateLimitStatus().getRemaining(),
            user.getRateLimitStatus().getLimit(),
            user.getRateLimitStatus().getSecondsUntilReset());
    }
}
