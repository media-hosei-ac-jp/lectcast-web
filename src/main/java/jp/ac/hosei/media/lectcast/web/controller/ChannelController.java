package jp.ac.hosei.media.lectcast.web.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.servlet.http.HttpSession;
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
import jp.ac.hosei.media.lectcast.web.service.LocalConvertService;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping(path = "/channels")
public class ChannelController {

  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(ChannelController.class);

  private static final String KEY_PREFIX = "audio";

  private static final String KEY_ORIGINAL_PREFIX = "original";

  private static final String[] AVAILABLE_EXTENSION = {"mp3", "m4a", "wma"};

  private static final String INSTRUCTOR_NAME = "Instructor";

  @Autowired
  private AmazonS3Service amazonS3Service;

  @Autowired
  private LocalConvertService localConvertService;

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
      // 401 Unauthorized
      model.addAttribute("error", "Unauthorized");
      model.addAttribute("additional_message", "lectcast.error.unauthorized");
      return "error";
    }

    final boolean isInstructor = lectcastSession.getUserRoles().contains(INSTRUCTOR_NAME);
    model.addAttribute("isInstructor", isInstructor);

    // Get a channel
    Channel channel = channelRepository
        .findByLtiContextIdAndLtiResourceLinkId(lectcastSession.getContextId(),
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
    if (null == lectcastSession) {
      // 401 Unauthorized
      model.addAttribute("error", "Unauthorized");
      model.addAttribute("additional_message", "lectcast.error.unauthorized");
      return "error";
    }
    if (null == lectcastSession.getChannel() || !lectcastSession.getUserRoles()
        .contains(INSTRUCTOR_NAME)) {
      // 403 Forbidden
      model.addAttribute("error", "Forbidden");
      model.addAttribute("additional_message", "lectcast.error.forbidden");
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
    if (null == lectcastSession) {
      // 401 Unauthorized
      model.addAttribute("error", "Unauthorized");
      model.addAttribute("additional_message", "lectcast.error.unauthorized");
      return "error";
    }
    if (null == lectcastSession.getChannel() || !lectcastSession.getUserRoles()
        .contains(INSTRUCTOR_NAME)) {
      // 403 Forbidden
      model.addAttribute("error", "Forbidden");
      model.addAttribute("additional_message", "lectcast.error.forbidden");
      return "error";
    }

    final String originalFileName = itemForm.getAudioFile().getOriginalFilename();
    assert originalFileName != null : "The original filename must be set.";
    final String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
    final String fileName = originalFileName
        .substring(0, originalFileName.length() - (extension.length() + 1));
    if (!containsExtension(extension)) {
      model.addAttribute("error", "Unsupported FileType");
      model.addAttribute("additional_message", "lectcast.error.file_type_is_not_supported");
      return "error";
    }

    final Item item = new Item();
    final String key = amazonS3Service.generateKey();

    File originalFile = null;
    try {
      // Create a temporary file
      final Path tmpPath = Files.createTempFile(Paths.get("/tmp"), "lectcast_", "." + extension);
      originalFile = tmpPath.toFile();

      // Write the temporary audio file
      final byte[] bytes = itemForm.getAudioFile().getBytes();
      final BufferedOutputStream uploadFileStream = new BufferedOutputStream(
          new FileOutputStream(originalFile));
      uploadFileStream.write(bytes);
      uploadFileStream.close();

      // Check file format
      final FFmpegFormat fileFormat = localConvertService.getFormat(tmpPath.toString());
      final double duration = fileFormat.duration;
      final String formatLongName = fileFormat.format_long_name;

      switch (formatLongName) {
        case "ASF (Advanced / Active Streaming Format)":  // wmapro, wmav2: convert needs
        case "QuickTime / MOV":                           // m4a
        case "MP2/3 (MPEG audio layer 2/3)":              // mp3
          break;
        default:
          model.addAttribute("error", "Unsupported FileFormat");
          model.addAttribute("additional_message", "lectcast.error.file_type_is_not_supported");
          return "error";
      }

      // Put an original audio object
      amazonS3Service.putObject(
          originalFile,
          key,
          fileName + "." + extension,
          KEY_ORIGINAL_PREFIX,
          itemForm.getAudioFile().getContentType(),
          itemForm.getAudioFile().getSize(),
          600);

      // Persist an item object
      item.setChannel(lectcastSession.getChannel());
      item.setS3Key(key);
      item.setTitle(itemForm.getTitle());
      item.setFilename(originalFileName);
      item.setDescription(itemForm.getDescription());
      item.setDuration((int) duration);
      itemRepository.save(item);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      // Delete files if exist
      if (originalFile != null && originalFile.exists()) {
        originalFile.delete();
      }
    }

    final URI location = builder.path("/channels").build().toUri();
    return "redirect:" + location.toString();
  }

  @GetMapping("item/{id}")
  @ResponseBody
  public ResponseEntity<InputStreamResource> getItem(@PathVariable final String id,
      final HttpSession httpSession, final UriComponentsBuilder builder, final Model model) {
    final LectcastSession lectcastSession = (LectcastSession) httpSession.getAttribute("lectcast");
    if (null == lectcastSession) {
      // 401 Unauthorized
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    final Item item = itemRepository.findByIdAndChannel(id, lectcastSession.getChannel());
    if (null == item) {
      // 401 Not Found
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    return amazonS3Service.getObject(item.getS3Key() + "/" + item.getS3Key() + ".m3u8", "hls");
  }

  @DeleteMapping("item/{id}")
  @ResponseBody
  public String deleteItem(@PathVariable final String id,
      final HttpSession httpSession, final UriComponentsBuilder builder, final Model model) {
    final LectcastSession lectcastSession = (LectcastSession) httpSession.getAttribute("lectcast");
    if (null == lectcastSession) {
      // 401 Unauthorized
      model.addAttribute("error", "Unauthorized");
      model.addAttribute("additional_message", "lectcast.error.unauthorized");
      return "error";
    }
    if (null == lectcastSession.getChannel() || !lectcastSession.getUserRoles()
        .contains(INSTRUCTOR_NAME)) {
      // 403 Forbidden
      model.addAttribute("error", "Forbidden");
      model.addAttribute("additional_message", "lectcast.error.forbidden");
      return "error";
    }

    final Item item = itemRepository.findByIdAndChannel(id, lectcastSession.getChannel());
    if (null == item) {
      // 401 Not Found
      model.addAttribute("error", "Not Found");
      model.addAttribute("additional_message", "lectcast.error.not_found");
      return "error";
    }

    item.setIsDeleted(1);
    itemRepository.save(item);

    final URI location = builder.path("/channels").build().toUri();
    return "redirect:" + location.toString();
  }

  @GetMapping("item/download")
  @ResponseBody
  public ResponseEntity<InputStreamResource> serveFile(@RequestParam("key") final String key,
      final HttpSession httpSession) {
    final LectcastSession lectcastSession = (LectcastSession) httpSession.getAttribute("lectcast");
    if (null == lectcastSession || null == lectcastSession.getChannel()) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    return amazonS3Service.getObject(key, KEY_PREFIX);
  }

  @ModelAttribute(name = "channelForm")
  public ChannelForm initChannelForm() {
    return new ChannelForm();
  }

  @ModelAttribute(name = "itemForm")
  public ItemForm initItemForm() {
    return new ItemForm();
  }

  private boolean containsExtension(final String extension) {
    for (final String availableExtension : AVAILABLE_EXTENSION) {
      if (availableExtension.equalsIgnoreCase(extension)) {
        return true;
      }
    }
    return false;
  }

}
