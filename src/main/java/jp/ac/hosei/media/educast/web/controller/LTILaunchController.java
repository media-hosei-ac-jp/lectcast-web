package jp.ac.hosei.media.educast.web.controller;

import jp.ac.hosei.media.educast.web.data.Channel;
import jp.ac.hosei.media.educast.web.repository.ChannelRepository;
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

        // Get a channel
        Channel channel = channelRepository.findByContextIdAndResourceLinkId(launch.getContextId(), launch.getResourceLinkId());
        if (null == channel) {
            if (isInstructor) {
                channel = new Channel();
                channel.setContextId(launch.getContextId());
                channel.setResourceLinkId(launch.getResourceLinkId());
                channel.setTitle("");   // Set default value
                channelRepository.save(channel);
            }
        }

        model.addAttribute("isInstructor", isInstructor);
        model.addAttribute("channel", channel);

        return "launch";
    }

}
