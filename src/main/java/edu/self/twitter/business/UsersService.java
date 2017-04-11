package edu.self.twitter.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import ch.nyg.java.util.LogUtil;
import edu.self.twitter.model.LongTuple;
import edu.self.twitter.model.Tuple;

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
    private static final String USER_HISTORY_COUNT = "SELECT COUNT(*) FROM followers WHERE screen_name = ?";
    private static final String USER_HISTORY_FIRST = "SELECT * FROM followers WHERE screen_name = ? ORDER BY timestamp LIMIT 1";
    private static final String USER_HISTORY_LAST = "SELECT * FROM followers WHERE screen_name = ? ORDER BY timestamp DESC LIMIT 1";
    private static final String USER_HISTORY_LAST_PERIOD = "SELECT * FROM followers WHERE screen_name = ? AND timestamp < ? ORDER BY timestamp DESC LIMIT 1";

    private static final BigDecimal DAY_IN_MS = new BigDecimal(24 * 60 * 60 * 1000);

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    TwitterService twitterService;

    public List<Tuple<String, Integer>> getUserFollowersStatistics(String screenName) {

        List<Tuple<String, Integer>> stats = new ArrayList<>();

        stats.add(new Tuple<>("Number of data points", jdbc.queryForObject(USER_HISTORY_COUNT, Integer.class, screenName)));

        Map<String, Object> first = jdbc.queryForMap(USER_HISTORY_FIRST, screenName);
        Map<String, Object> last = jdbc.queryForMap(USER_HISTORY_LAST, screenName);

        LocalDateTime localDateTimeLast = ((Timestamp) last.get("timestamp")).toLocalDateTime();
        Map<String, Object> lastDay = jdbc.queryForMap(USER_HISTORY_LAST_PERIOD, screenName, localDateTimeLast.minusDays(1));
        Map<String, Object> lastWeek = jdbc.queryForMap(USER_HISTORY_LAST_PERIOD, screenName, localDateTimeLast.minusDays(7));
        Map<String, Object> lastMonth = jdbc.queryForMap(USER_HISTORY_LAST_PERIOD, screenName, localDateTimeLast.minusMonths(1));

        BigDecimal followersFirst = new BigDecimal((Integer) first.get("followers"));
        BigDecimal followersLast = new BigDecimal((Integer) last.get("followers"));
        BigDecimal followersLastDay = new BigDecimal((Integer) lastDay.get("followers"));
        BigDecimal followersLastWeek = new BigDecimal((Integer) lastWeek.get("followers"));
        BigDecimal followersLastMonth = new BigDecimal((Integer) lastMonth.get("followers"));

        BigDecimal timestampFirst = new BigDecimal(((Timestamp) first.get("timestamp")).getTime());
        BigDecimal timestampLast = new BigDecimal(((Timestamp) last.get("timestamp")).getTime());
        BigDecimal timestampLastDay = new BigDecimal(((Timestamp) lastDay.get("timestamp")).getTime());
        BigDecimal timestampLastWeek = new BigDecimal(((Timestamp) lastWeek.get("timestamp")).getTime());
        BigDecimal timestampLastMonth = new BigDecimal(((Timestamp) lastMonth.get("timestamp")).getTime());

        BigDecimal dailyAvgOverall = DAY_IN_MS
            .multiply(followersLast.subtract(followersFirst))
            .divide(timestampLast.subtract(timestampFirst), RoundingMode.HALF_UP);

        BigDecimal dailyAvgLastDay = DAY_IN_MS
            .multiply(followersLast.subtract(followersLastDay))
            .divide(timestampLast.subtract(timestampLastDay), RoundingMode.HALF_UP);

        BigDecimal dailyAvgLastWeek = DAY_IN_MS
            .multiply(followersLast.subtract(followersLastWeek))
            .divide(timestampLast.subtract(timestampLastWeek), RoundingMode.HALF_UP);

        BigDecimal dailyAvgLastMonth = DAY_IN_MS
            .multiply(followersLast.subtract(followersLastMonth))
            .divide(timestampLast.subtract(timestampLastMonth), RoundingMode.HALF_UP);

        stats.add(new Tuple<>("Daily average increase (overall)", dailyAvgOverall.intValue()));
        stats.add(new Tuple<>("Daily average increase (last day)", dailyAvgLastDay.intValue()));
        stats.add(new Tuple<>("Daily average increase (last week)", dailyAvgLastWeek.intValue()));
        stats.add(new Tuple<>("Daily average increase (last month)", dailyAvgLastMonth.intValue()));

        return stats;
    }

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

        List<LongTuple> results = jdbc.query(
            USER_HISTORY,
            (RowMapper<LongTuple>) (rs, rowNum) -> new LongTuple(rs.getTimestamp("timestamp").getTime(), rs.getInt("followers")),
            screenName);

        LogUtil.info("Size: %d", results.size());

        if (results.size() > 1000) {

            Iterator<LongTuple> iterator = results.iterator();
            Instant currentInstant, lastInstant = Instant.ofEpochMilli(iterator.next().x).truncatedTo(ChronoUnit.DAYS);

            while (iterator.hasNext()) {
                currentInstant = Instant.ofEpochMilli(iterator.next().x).truncatedTo(ChronoUnit.DAYS);
                if (lastInstant.equals(currentInstant)) {
                    iterator.remove(); // it's from the same day, remove it
                }
                else {
                    lastInstant = currentInstant;
                }
            }
        }

        LogUtil.info("Size: %d", results.size());
        return results;
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
