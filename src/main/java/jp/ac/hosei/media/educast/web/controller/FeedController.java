package jp.ac.hosei.media.educast.web.controller;

import jp.ac.hosei.media.educast.web.data.Feed;
import jp.ac.hosei.media.educast.web.repository.FeedRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequestMapping(path = "/podcasts")
public class FeedController {

    private static final Logger logger = LoggerFactory.getLogger(FeedController.class);

    @Autowired
    private FeedRepository feedRepository;

    @GetMapping("/{feedId}/feed.xml")
    public void individualFeed(@PathVariable("feedId") final String feedId, final HttpServletResponse response) throws IOException {
        final Feed feed = feedRepository.findById(feedId);

        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(new AnnotationConfigApplicationContext());
        resolver.setPrefix("classpath:/xml/");
        resolver.setSuffix(".xml");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setTemplateMode(TemplateMode.XML);

        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);

        Context ctx = new Context();
        ctx.setVariable("channel", feed.getChannel());
        String xml = engine.process("feed", ctx);

        response.setHeader("Content-Disposition", "attachment; filename=feed.xml");
        response.setContentType("application/xml");
        PrintWriter writer = response.getWriter();
        writer.print(xml);
        writer.close();
    }

}
