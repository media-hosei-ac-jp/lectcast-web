package jp.ac.hosei.media.lectcast.web.controller;

import jp.ac.hosei.media.lectcast.web.component.LectcastSession;
import jp.ac.hosei.media.lectcast.web.data.Channel;
import jp.ac.hosei.media.lectcast.web.data.Feed;
import jp.ac.hosei.media.lectcast.web.data.Item;
import jp.ac.hosei.media.lectcast.web.form.ChannelForm;
import jp.ac.hosei.media.lectcast.web.form.ItemForm;
import jp.ac.hosei.media.lectcast.web.repository.ChannelRepository;
import jp.ac.hosei.media.lectcast.web.repository.FeedRepository;
import jp.ac.hosei.media.lectcast.web.repository.ItemRepository;
import jp.ac.hosei.media.lectcast.web.service.AmazonS3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

    private static final String[] AVAILABLE_EXTENSION = {"mp3", "m4a"};

    private static final String INSTRUCTOR_NAME = "Instructor";

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
            model.addAttribute("error", "Forbidden");
            model.addAttribute("message", "Authentication required");
            return "error";
        }

        final boolean isInstructor = lectcastSession.getUserRoles().contains(INSTRUCTOR_NAME);
        model.addAttribute("isInstructor", isInstructor);

        // Get a channel
        Channel channel = channelRepository.findByLtiContextIdAndLtiResourceLinkId(lectcastSession.getContextId(),
                                                                                   lectcastSession.getResourceLinkId());
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

    @PostMapping("")
    public String handleChannelUpdate(final ChannelForm channelForm, final HttpSession httpSession,
                                      final UriComponentsBuilder builder, final Model model) {
        final LectcastSession lectcastSession = (LectcastSession) httpSession.getAttribute("lectcast");
        if (null == lectcastSession || null == lectcastSession.getChannel() || ! lectcastSession.getUserRoles().contains(INSTRUCTOR_NAME)) {
            model.addAttribute("error", "Forbidden");
            model.addAttribute("message", "Authorization required");
            return "error";
        }

        // Update the channel object
        final Channel channel = channelRepository.findById(lectcastSession.getChannel().getId());
        channel.setTitle(channelForm.getTitle());
        channel.setDescription(channelForm.getDescription());
        channelRepository.save(channel);

        final URI location = builder.path("/channels").build().toUri();
        return "redirect:" + location.toString();
    }

    @PostMapping("item")
    public String handleItemUpload(final ItemForm itemForm, final HttpSession httpSession,
                                   final UriComponentsBuilder builder, final Model model) {
        final LectcastSession lectcastSession = (LectcastSession) httpSession.getAttribute("lectcast");
        if (null == lectcastSession || null == lectcastSession.getChannel() || ! lectcastSession.getUserRoles().contains(INSTRUCTOR_NAME)) {
            model.addAttribute("error", "Forbidden");
            model.addAttribute("message", "Authorization required");
            return "error";
        }

        final String originalFileName = itemForm.getAudioFile().getOriginalFilename();
        final String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        if (! containsExtension(extension)) {
            model.addAttribute("error", "Unsupported Filetype");
            model.addAttribute("message", "Only " + String.join(", ", AVAILABLE_EXTENSION) + " is supported");
            return "error";
        }

        final Item item = new Item();

        File file = null;
        try {
            // Create a temporary file
            final Path tmpPath = Files.createTempFile(Paths.get("/tmp"), "lectcast_", "." + extension);
            file = tmpPath.toFile();

            // Write the temporary audio file
            final byte[] bytes = itemForm.getAudioFile().getBytes();
            final BufferedOutputStream uploadFileStream = new BufferedOutputStream(new FileOutputStream(file));
            uploadFileStream.write(bytes);
            uploadFileStream.close();

            // Put an audio object
            final String key = amazonS3Service.putObject(file, KEY_PREFIX, itemForm.getAudioFile().getContentType(),
                    itemForm.getAudioFile().getSize(), 600);

            // Persist an item object
            item.setChannel(lectcastSession.getChannel());
            item.setS3Key(key);
            item.setTitle(itemForm.getTitle());
            item.setDescription(itemForm.getDescription());
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

    @DeleteMapping("item/{id}")
    @ResponseBody
    public String deleteItem(@PathVariable final String id,
        final HttpSession httpSession, final UriComponentsBuilder builder, final Model model) {
        final LectcastSession lectcastSession = (LectcastSession) httpSession.getAttribute("lectcast");
        if (null == lectcastSession || null == lectcastSession.getChannel() || ! lectcastSession.getUserRoles().contains(INSTRUCTOR_NAME)) {
            model.addAttribute("error", "Forbidden");
            model.addAttribute("message", "Authorization required");
            return "error";
        }

        final Item item = itemRepository.findByIdAndChannel(id, lectcastSession.getChannel());
        if (null == item) {
            model.addAttribute("error", "Not found");
            model.addAttribute("message", "Item not found");
        }

        item.setIsDeleted(1);
        itemRepository.save(item);

        final URI location = builder.path("/channels").build().toUri();
        return "redirect:" + location.toString();
    }

    @GetMapping("item/download")
    @ResponseBody
    public ResponseEntity<InputStreamResource> serveFile(@RequestParam("key") final String key, final HttpSession httpSession) {
        final LectcastSession lectcastSession = (LectcastSession) httpSession.getAttribute("lectcast");
        if (null == lectcastSession || null == lectcastSession.getChannel()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return amazonS3Service.getObject(key, KEY_PREFIX);
    }

    @ModelAttribute(name = "channelForm")
    public ChannelForm initChannelForm(){
        return new ChannelForm();
    }

    @ModelAttribute(name = "itemForm")
    public ItemForm initItemForm(){
        return new ItemForm();
    }

    private boolean containsExtension(final String extension){
        for (final String availableExtension : AVAILABLE_EXTENSION) {
            if (availableExtension.equalsIgnoreCase(extension)){
                return true;
            }
        }
        return false;
    }

}
