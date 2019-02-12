package test.jaeger;

import io.opentracing.Tracer;
import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.opentracing.util.GlobalTracer;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import java.util.logging.Logger;

@WebListener
public class OpenTracingContextInitializer implements javax.servlet.ServletContextListener {
    Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        logger.info("inside contextInitialized method");
        GlobalTracer.register(getTracer());
        logger.info("tracer is initialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

//    private Tracer getTracer() {
//        try {
//            logger.info("inside getTracer method");
//            return new com.uber.jaeger.Configuration(
//                    getName(),
//                    new com.uber.jaeger.Configuration.SamplerConfiguration("const", 1),
//                    new com.uber.jaeger.Configuration.ReporterConfiguration(
//                            true,
//                            "localhost",
//                            6831,
//                            1000,   // flush interval in milliseconds
//                            10000)  /*max buffered Spans*/)
//                    .getTracer();
//        } catch (Exception e) {
//            logger.info("error in getTracer method");
//            e.printStackTrace();
//
//            return NoopTracerFactory.create();
//        }
//    }

    private Tracer getTracer() {
        // -DJAEGER_AGENT_HOST=server2.mycompany.com
        // -DJAEGER_AGENT_PORT=6831
        SamplerConfiguration samplerConfig = SamplerConfiguration.fromEnv().withType("const").withParam(1);
        ReporterConfiguration reporterConfig = ReporterConfiguration.fromEnv().withLogSpans(true);

        Configuration config = new Configuration(getName()).withSampler(samplerConfig).withReporter(reporterConfig);
        return config.getTracer();
    }
    public String getName() {
        return "jaeger-tracing";
    }
}