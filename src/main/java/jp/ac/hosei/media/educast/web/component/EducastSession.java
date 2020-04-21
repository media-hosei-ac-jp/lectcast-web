package jp.ac.hosei.media.educast.web.component;

import jp.ac.hosei.media.educast.web.data.Channel;
import org.imsglobal.lti.launch.LtiLaunch;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class EducastSession implements Serializable  {

    private static final long serialVersionUID = 1L;

    private Channel channel;

    private LtiLaunch ltiLaunch;

    private String userId;

    private String contextTitle;

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public LtiLaunch getLtiLaunch() {
        return ltiLaunch;
    }

    public void setLtiLaunch(LtiLaunch ltiLaunch) {
        this.ltiLaunch = ltiLaunch;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContextTitle() {
        return contextTitle;
    }

    public void setContextTitle(String contextTitle) {
        this.contextTitle = contextTitle;
    }

}
