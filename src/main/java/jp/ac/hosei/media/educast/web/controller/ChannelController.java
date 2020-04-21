package jp.ac.hosei.media.educast.web.controller;

import jp.ac.hosei.media.educast.web.component.EducastSession;
import jp.ac.hosei.media.educast.web.data.Channel;
import jp.ac.hosei.media.educast.web.data.Feed;
import jp.ac.hosei.media.educast.web.data.Item;
import jp.ac.hosei.media.educast.web.repository.ChannelRepository;
import jp.ac.hosei.media.educast.web.repository.FeedRepository;
import jp.ac.hosei.media.educast.web.repository.ItemRepository;
import jp.ac.hosei.media.educast.web.service.AmazonS3Service;
import org.imsglobal.lti.launch.LtiLaunch;
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

    @Autowired
    protected EducastSession educastSession;

    @Autowired
    private AmazonS3Service amazonS3Service;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private FeedRepository feedRepository;

    @GetMapping
    public String index(final Model model) {
        final LtiLaunch ltiLaunch = educastSession.getLtiLaunch();
        if (null == ltiLaunch) {
            return "error";
        }

        final boolean isInstructor = ltiLaunch.getUser().getRoles().contains("Instructor");
        model.addAttribute("isInstructor", isInstructor);

        // Get a channel
        Channel channel = channelRepository.findByLtiContextIdAndLtiResourceLinkId(ltiLaunch.getContextId(), ltiLaunch.getResourceLinkId());
        if (null == channel) {
            if (isInstructor) {
                channel = new Channel();
                channel.setLtiContextId(ltiLaunch.getContextId());
                channel.setLtiResourceLinkId(ltiLaunch.getResourceLinkId());
                channel.setTitle(educastSession.getContextTitle());   // Set default value
                channelRepository.save(channel);
            }
        }
        model.addAttribute("channel", channel);
        educastSession.setChannel(channel);

        // Set a feed
        final String ltiUserId = educastSession.getUserId();
        Feed feed = feedRepository.findByChannelAndLtiUserId(channel, ltiUserId);
        if (null == feed) {
            feed = new Feed();
            feed.setChannel(channel);
            feed.setLtiUserId(ltiUserId);
            feed.setActive(1);
            feedRepository.save(feed);
        }
        model.addAttribute("feed", feed);

        return "channel/index";
    }

    @GetMapping(path = "new")
    public String uploadForm() {
        return "channel/uploadFile";
    }

    @PostMapping
    public String handleFileUpload(@RequestParam final MultipartFile multipartFile, @RequestParam final String title, final UriComponentsBuilder builder) {
        final Item item = new Item();

        File file = null;
        try {
            // Create a temporary file
            final Path tmpPath = Files.createTempFile(Paths.get("/tmp"), "educast_", ".tmp");
            file = tmpPath.toFile();

            final byte[] bytes = multipartFile.getBytes();
            final BufferedOutputStream uploadFileStream = new BufferedOutputStream(new FileOutputStream(file));
            uploadFileStream.write(bytes);
            uploadFileStream.close();

            // Put an audio object
            final String key = amazonS3Service.putObject(file, "files/", multipartFile.getContentType(), multipartFile.getSize(), 600);

            // Persist item object
            item.setChannel(educastSession.getChannel());
            item.setS3Key(key);
            item.setTitle(title);
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
        return amazonS3Service.getObject("files/" + key);
    }

}
