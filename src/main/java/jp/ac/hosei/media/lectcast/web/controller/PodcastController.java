package jp.ac.hosei.media.lectcast.web.controller;

import jp.ac.hosei.media.lectcast.web.data.Feed;
import jp.ac.hosei.media.lectcast.web.repository.FeedRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.expression.ThymeleafEvaluationContext;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

@Controller
@RequestMapping(path = "/podcasts")
public class PodcastController {

    private static final Logger logger = LoggerFactory.getLogger(PodcastController.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private FeedRepository feedRepository;

    @GetMapping("/feed.xml")
    public void individualFeed(@RequestParam("feed") final String feedId, final HttpServletResponse response) throws IOException {
        final Feed feed = feedRepository.findById(feedId);

        if (null != feed) {
            SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
            resolver.setApplicationContext(new AnnotationConfigApplicationContext());
            resolver.setPrefix("classpath:/xml/");
            resolver.setSuffix(".xml");
            resolver.setCharacterEncoding("UTF-8");
            resolver.setTemplateMode(TemplateMode.XML);

            SpringTemplateEngine engine = new SpringTemplateEngine();
            engine.setTemplateResolver(resolver);

            Context ctx = new Context();
            ctx.setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME,
                    new ThymeleafEvaluationContext(applicationContext, null));  // for using beans
            ctx.setVariable("channel", feed.getChannel());
            ctx.setLocale(Locale.ENGLISH);  // Set locale for pubDate
            String xml = engine.process("feed", ctx);

            response.setContentType("application/rss+xml");
            PrintWriter writer = response.getWriter();
            writer.print(xml);
            writer.close();
        }
    }

}
