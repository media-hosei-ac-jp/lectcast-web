package jp.ac.hosei.media.educast.web.controller;

import jp.ac.hosei.media.educast.web.data.Channel;
import jp.ac.hosei.media.educast.web.data.Feed;
import jp.ac.hosei.media.educast.web.repository.ChannelRepository;
import jp.ac.hosei.media.educast.web.repository.FeedRepository;
import org.imsglobal.aspect.Lti;
import org.imsglobal.lti.launch.LtiLaunch;
import org.imsglobal.lti.launch.LtiVerificationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LTILaunchController {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private FeedRepository feedRepository;

    @Lti
    @PostMapping(path = "/launch")
    public String launch(final HttpServletRequest request, final LtiVerificationResult result, final Model model) {
        if (!result.getSuccess()) {
            // LTI error
            model.addAttribute("message", result.getMessage());
            return "error";
        }

        final LtiLaunch launch = result.getLtiLaunchResult();
        final boolean isInstructor = launch.getUser().getRoles().contains("Instructor");
        model.addAttribute("isInstructor", isInstructor);

        // Get a channel
        Channel channel = channelRepository.findByLtiContextIdAndLtiResourceLinkId(launch.getContextId(), launch.getResourceLinkId());
        if (null == channel) {
            if (isInstructor) {
                channel = new Channel();
                channel.setLtiContextId(launch.getContextId());
                channel.setLtiResourceLinkId(launch.getResourceLinkId());
                channel.setTitle(request.getParameter("context_title"));   // Set default value
                channelRepository.save(channel);
            }
        }
        model.addAttribute("channel", channel);

        // Set a feed
        final String ltiUserId = request.getParameter("user_id");
        Feed feed = feedRepository.findByChannelAndLtiUserId(channel, ltiUserId);
        if (null == feed) {
            feed = new Feed();
            feed.setChannel(channel);
            feed.setLtiUserId(ltiUserId);
            feed.setActive(1);
            feedRepository.save(feed);
        }
        model.addAttribute("feed", feed);

        return "launch";
    }

}
