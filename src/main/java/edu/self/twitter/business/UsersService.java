package edu.self.twitter.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import ch.nyg.java.util.LogUtil;
import edu.self.twitter.model.LongTuple;

@Service
public class UsersService {

    private static final String ALL_USERS = "SELECT screen_name FROM users";
    private static final String USER_HISTORY = "SELECT * FROM followers WHERE screen_name = ? ORDER BY timestamp";
    private static final String CURRENT_FOLLOWERS_COUNT = "SELECT followers FROM followers WHERE screen_name = ? ORDER BY timestamp DESC LIMIT 1";
    private static final String UPDATE_FOLLOWERS_COUNT = "INSERT INTO followers VALUES(?, ?, now())";
    private static final String LOG_UPDATE_RESULT = "Updated %s from %s to %s";

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    TwitterService twitterService;

    /**
     * Retrieves all Twitter screen names stored in the USERS table.
     *
     * @return a list of screen name
     */
    public List<String> getAllUsers() {
        return jdbc.queryForList(ALL_USERS, String.class);
    }

    /**
     * Retrieves the followers count history for a given Twitter screen name.
     *
     * @param screenName the Twitter screen name
     * @return a list of {@link LongTuple} objects
     */
    public List<LongTuple> getUserHistory(String screenName) {
        return jdbc.query(
            USER_HISTORY,
            (RowMapper<LongTuple>) (rs, rowNum) -> new LongTuple(rs.getTimestamp("timestamp").getTime(), rs.getInt("followers")),
            screenName);
    }

    /**
     * Update the followers count for the given Twitter screen name.
     */
    public void updateFollowersCount(String screenName) {

        int followersCount = twitterService.getFollowersCount(screenName);
        int currentFollowersCount;

        try {
            currentFollowersCount = jdbc.queryForObject(CURRENT_FOLLOWERS_COUNT, Integer.class, screenName);
        }
        catch (EmptyResultDataAccessException e) {
            currentFollowersCount = -1;
        }

        // only make insert if new value is different than last value and > 0
        if (followersCount != currentFollowersCount && followersCount > 0) {
            jdbc.update(UPDATE_FOLLOWERS_COUNT, screenName, followersCount);
            LogUtil.info(LOG_UPDATE_RESULT, screenName, currentFollowersCount, followersCount);
        }
    }

    /**
     * Update the followers count for all Twitter screen names of the USERS
     * table.
     */
    public void updateAllFollowersCount() {
        getAllUsers().forEach(this::updateFollowersCount);
    }
}
