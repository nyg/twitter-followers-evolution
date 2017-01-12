# twitter-followers-evolution

Spring Boot + Vaadin application that tracks the evolution of Twitter users of your choice.

```sh
$ mvn clean install # to build
$ java -jar target/twitter-followers-evolution-1.0-SNAPSHOT.jar # now listening on port 8080
```

You must also create a `twitter4j.properties` file in `src/main/resources` and fill it with your info:

```
debug=true
oauth.consumerKey=
oauth.consumerSecret=
oauth.accessToken=
oauth.accessTokenSecret=
```
