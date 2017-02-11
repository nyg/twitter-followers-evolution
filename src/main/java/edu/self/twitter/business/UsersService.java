package edu.self.twitter.business;

import java.util.ArrayList;
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
    private static final String INSERT_USER = "INSERT INTO users VALUES(?)";
    private static final String DELETE_USER = "DELETE FROM users WHERE screen_name = ?";
    private static final String USER_EXISTS = "SELECT COUNT(*) FROM users WHERE screen_name = ?";

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    TwitterService twitterService;

    public boolean doesUserExists(String screenName) {
        return 1 == jdbc.queryForObject(USER_EXISTS, Integer.class, screenName);
    }

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
     * Adds multiple users in the USERS table,
     *
     * @param screenNames a list of screen names to insert into the table
     * @return the list of screen name that were not inserted into the table
     */
    public List<String> addUsers(String[] screenNames) {

        List<String> errors = new ArrayList<>();

        for (String screenName : screenNames) {
            if (!insertUser(screenName)) {
                errors.add(screenName);
            }
            else {
                LogUtil.info("Insert '%s'", screenName);
            }
        }

        return errors;
    }

    /**
     * Update the followers count for all Twitter screen names of the USERS
     * table.
     */
    public void updateAllFollowersCount() {
        getAllUsers().forEach(this::updateFollowersCount);
    }

    /**
     * Update the followers count for the given Twitter screen name.
     */
    public void updateFollowersCount(String screenName) {

        int followersCount = twitterService.getFollowerCount(screenName);
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

    private boolean insertUser(String screenName) {
        return 1 == jdbc.update(INSERT_USER, screenName.trim());
    }

    public boolean deleteUser(String screenName) {
        return 1 == jdbc.update(DELETE_USER, screenName);
    }
}
