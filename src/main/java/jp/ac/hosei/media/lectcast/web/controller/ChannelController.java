package jp.ac.hosei.media.lectcast.web.controller;

import jp.ac.hosei.media.lectcast.web.component.LectcastSession;
import jp.ac.hosei.media.lectcast.web.data.Channel;
import jp.ac.hosei.media.lectcast.web.data.Feed;
import jp.ac.hosei.media.lectcast.web.data.Item;
import jp.ac.hosei.media.lectcast.web.repository.ChannelRepository;
import jp.ac.hosei.media.lectcast.web.repository.FeedRepository;
import jp.ac.hosei.media.lectcast.web.repository.ItemRepository;
import jp.ac.hosei.media.lectcast.web.service.AmazonS3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping(path = "/channels")
public class ChannelController {

    private static final Logger logger = LoggerFactory.getLogger(ChannelController.class);

    private static final String KEY_PREFIX = "audio";

    @Autowired
    protected LectcastSession lectcastSession;

    @Autowired
    private AmazonS3Service amazonS3Service;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private FeedRepository feedRepository;

    @GetMapping
    public String index(final HttpSession httpSession, final Model model) {
        final LectcastSession lectcastSession = (LectcastSession) httpSession.getAttribute("lectcast");
        if (null == lectcastSession) {
            return "error";
        }

        final boolean isInstructor = lectcastSession.getUserRoles().contains("Instructor");
        model.addAttribute("isInstructor", isInstructor);

        // Get a channel
        Channel channel = channelRepository.findByLtiContextIdAndLtiResourceLinkId(lectcastSession.getContextId(), lectcastSession.getResourceLinkId());
        if (null == channel) {
            if (isInstructor) {
                channel = new Channel();
                channel.setLtiContextId(lectcastSession.getContextId());
                channel.setLtiResourceLinkId(lectcastSession.getResourceLinkId());
                channel.setTitle(lectcastSession.getContextTitle());   // Set default value
                channelRepository.save(channel);
            }
        }
        model.addAttribute("channel", channel);
        lectcastSession.setChannel(channel);

        // Set a feed
        final String ltiUserId = lectcastSession.getUserId();
        Feed feed = feedRepository.findByChannelAndLtiUserId(channel, ltiUserId);
        if (null == feed) {
            feed = new Feed();
            feed.setChannel(channel);
            feed.setLtiUserId(ltiUserId);
            feed.setActive(1);
            feedRepository.save(feed);
        }
        model.addAttribute("feed", feed);

        httpSession.setAttribute("lectcast", lectcastSession);
        return "channel/index";
    }

    @PostMapping
    public String handleFileUpload(@RequestParam final MultipartFile multipartFile, @RequestParam final String title,
                                   @RequestParam final String description,
                                   final HttpSession httpSession, final UriComponentsBuilder builder) {
        final LectcastSession lectcastSession = (LectcastSession) httpSession.getAttribute("lectcast");
        final Item item = new Item();

        File file = null;
        try {
            // Create a temporary file
            final Path tmpPath = Files.createTempFile(Paths.get("/tmp"), "lectcast_", ".tmp");
            file = tmpPath.toFile();

            final byte[] bytes = multipartFile.getBytes();
            final BufferedOutputStream uploadFileStream = new BufferedOutputStream(new FileOutputStream(file));
            uploadFileStream.write(bytes);
            uploadFileStream.close();

            // Put an audio object
            final String key = amazonS3Service.putObject(file, KEY_PREFIX, multipartFile.getContentType(), multipartFile.getSize(), 600);

            // Persist item object
            item.setChannel(lectcastSession.getChannel());
            item.setS3Key(String.join("/", new String[] {KEY_PREFIX, key}));
            item.setTitle(title);
            item.setDescription(description);
            itemRepository.save(item);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (file != null && file.exists()) {
                file.delete();
            }
        }

        final URI location = builder.path("/channels").build().toUri();
        return "redirect:" + location.toString();
    }

    @GetMapping("item/download")
    @ResponseBody
    public ResponseEntity<InputStreamResource> serveFile(@RequestParam("key") final String key) {
        return amazonS3Service.getObject(key, KEY_PREFIX);
    }

}
