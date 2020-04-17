package jp.ac.hosei.media.educast.web.controller;

import org.imsglobal.aspect.Lti;
import org.imsglobal.lti.launch.LtiLaunch;
import org.imsglobal.lti.launch.LtiVerificationResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LTILaunchController {

    @Lti
    @PostMapping(path = "/launch")
    public String launch(final HttpServletRequest request, final LtiVerificationResult result, final Model model) {
        if (result.getSuccess()) {
            final LtiLaunch launch = result.getLtiLaunchResult();
            model.addAttribute("message", "success!");
        } else {
            // Error
            model.addAttribute("message", result.getMessage());
        }
        return "launch";
    }

}
