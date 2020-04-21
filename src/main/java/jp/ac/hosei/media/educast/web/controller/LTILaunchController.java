package jp.ac.hosei.media.educast.web.controller;

import jp.ac.hosei.media.educast.web.component.EducastSession;
import org.imsglobal.aspect.Lti;
import org.imsglobal.lti.launch.LtiLaunch;
import org.imsglobal.lti.launch.LtiVerificationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@Controller
public class LTILaunchController {

    private static final Logger logger = LoggerFactory.getLogger(LTILaunchController.class);

    @Autowired
    protected EducastSession educastSession;

    @Lti
    @PostMapping(path = "/launch")
    public String launch(final HttpServletRequest request, final LtiVerificationResult result, final Model model, final UriComponentsBuilder builder) {
        if (!result.getSuccess()) {
            // LTI error
            model.addAttribute("message", result.getMessage());
            return "error";
        }

        final LtiLaunch ltiLaunch = result.getLtiLaunchResult();
        educastSession.setLtiLaunch(ltiLaunch);

        final String contextTitle = request.getParameter("context_title");
        educastSession.setContextTitle(contextTitle);

        final String userId = request.getParameter("user_id");
        educastSession.setUserId(userId);

        final URI location = builder.path("/channels").build().toUri();
        return "redirect:" + location.toString();
    }

}
