package jp.ac.hosei.media.educast.web.component;

import jp.ac.hosei.media.educast.web.data.Channel;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class EducastSession implements Serializable  {

    private static final long serialVersionUID = 1L;

    private Channel channel;

    private String resourceLinkId;

    private String contextId;

    private String contextTitle;

    private String userId;

    private List<String> userRoles;

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getResourceLinkId() {
        return resourceLinkId;
    }

    public void setResourceLinkId(String resourceLinkId) {
        this.resourceLinkId = resourceLinkId;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public String getContextTitle() {
        return contextTitle;
    }

    public void setContextTitle(String contextTitle) {
        this.contextTitle = contextTitle;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<String> userRoles) {
        this.userRoles = userRoles;
    }

}
