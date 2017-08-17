package edu.self.twitter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import edu.self.twitter.business.UsersService;

@SpringBootApplication
public class TwitterApplication {

    @Configuration
    @EnableScheduling
    public class AppConfig {}

    public static void main(String[] args) {
        SpringApplication.run(TwitterApplication.class, args);
    }

    @Autowired
    UsersService usersService;

    //@Scheduled(fixedRate = 5 * 60 * 1000)
    public void updateFollowersCount() {
        usersService.updateAllFollowersCount();
    }
}
