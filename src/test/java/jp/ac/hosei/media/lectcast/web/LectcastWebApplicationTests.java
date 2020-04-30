package jp.ac.hosei.media.lectcast.web;

import jp.ac.hosei.media.lectcast.web.controller.ChannelController;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LectcastWebApplicationTests {

    @Autowired
    private ChannelController channelController;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(channelController);
    }

}
