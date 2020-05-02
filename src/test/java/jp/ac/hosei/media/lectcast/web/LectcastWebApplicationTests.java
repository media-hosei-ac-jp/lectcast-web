package jp.ac.hosei.media.lectcast.web;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import redis.embedded.RedisServer;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = LectcastWebApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"spring.redis.port=16379"})
class LectcastWebApplicationTests {

  private static RedisServer redisServer;

  @BeforeAll
  static void init() {
    // Start embedded redis server for test
    redisServer = new RedisServer(16379);
    redisServer.start();
  }

  @AfterAll
  static void tearDown() {
    // Stop embedded redis server
    redisServer.stop();
  }

  @Test
  void contextLoads() {

  }

}
