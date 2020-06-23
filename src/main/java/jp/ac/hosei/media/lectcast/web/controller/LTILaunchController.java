package jp.ac.hosei.media.lectcast.web.controller;

import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import jp.ac.hosei.media.lectcast.web.component.LectcastSession;
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

@Controller
public class LTILaunchController {

  private static final Logger logger = LoggerFactory.getLogger(LTILaunchController.class);

  @Autowired
  protected LectcastSession lectcastSession;

  @Lti
  @PostMapping(path = "/launch")
  public String launch(final HttpServletRequest request, final LtiVerificationResult result,
      final Model model,
      final HttpSession httpSession, final UriComponentsBuilder builder) {
    if (!result.getSuccess()) {
      // LTI error
      model.addAttribute("error", "LTI Error");
      model.addAttribute("message", result.getMessage());
      return "error";
    }

    final LtiLaunch ltiLaunch = result.getLtiLaunchResult();
    lectcastSession.setUserId(ltiLaunch.getUser().getId());
    lectcastSession.setUserRoles(ltiLaunch.getUser().getRoles());
    lectcastSession.setContextId(ltiLaunch.getContextId());
    lectcastSession.setResourceLinkId(ltiLaunch.getResourceLinkId());

    final String contextTitle = request.getParameter("context_title");
    lectcastSession.setContextTitle(contextTitle);

    httpSession.setAttribute("lectcast", lectcastSession);

    final URI location = builder.path("/channels").build().toUri();
    return "redirect:" + location.toString();
  }

}
