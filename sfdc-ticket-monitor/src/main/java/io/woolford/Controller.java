package io.woolford;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.woolford.database.entity.BundleEnriched;
import io.woolford.database.entity.Ticket;
import io.woolford.database.mapper.DbMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@EnableAutoConfiguration
public class Controller {

    private final Configuration ftlConfig = new Configuration(Configuration.VERSION_2_3_26);

    private final
    DbMapper dbMapper;

    // TODO: make stats call that returns counts for recent runs, etc...
    // TODO: make 'ignore ticket' endpoint and button so user can exclude tickets from the open-tickets page

    @Autowired
    private Controller(DbMapper dbMapper){
        ftlConfig.setClassForTemplateLoading(Controller.class, "/templates");
        ftlConfig.setDefaultEncoding("UTF-8");
        this.dbMapper = dbMapper;
    }

    //TODO: do better healthcheck (e.g. check MySQL, any recent exceptions, etc...)
    @RequestMapping("/healthcheck")
    String healtcheck() {
        return "{\"status\": \"ok\"}";
    }

    @RequestMapping("/open-tickets")
    String openTickets() throws IOException, TemplateException {

        List<Ticket> openTickets = dbMapper.getOpenTickets();
        Template openTicketTemplate = ftlConfig.getTemplate("open-tickets.ftl");

        Map<String, Object> openTicketMap = new HashMap<String, Object>();
        openTicketMap.put("openTickets", openTickets);

        return renderTemplate(openTicketTemplate, openTicketMap);
    }

    @RequestMapping("/recent-bundles")
    String recentBundles() throws IOException, TemplateException {

        List<BundleEnriched> recentBundles = dbMapper.getMostRecentBundles();
        Template recentBundleTemplate = ftlConfig.getTemplate("most-recent-bundles.ftl");

        Map<String, Object> recentBundleMap = new HashMap<String, Object>();
        recentBundleMap.put("recentBundles", recentBundles);

        return renderTemplate(recentBundleTemplate, recentBundleMap);
    }

    // TODO: this code is consolidated
    private String renderTemplate(Template template, Map map) throws IOException, TemplateException {

        StringWriter stringWriter = new StringWriter();
        template.process(map, stringWriter);
        return stringWriter.toString();

    }

}
