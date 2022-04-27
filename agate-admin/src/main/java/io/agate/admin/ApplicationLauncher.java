
package io.agate.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import io.agate.admin.bootstrap.ApplicationBootstrap;

@SpringBootApplication
public class ApplicationLauncher implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationLauncher.class);

    @Autowired
    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = null;
        try {
            String implementationVersion = ApplicationLauncher.class.getPackage().getImplementationVersion();
            LOG.info("agate admin application {} is starting...", implementationVersion);

            context = SpringApplication.run(ApplicationLauncher.class, args);
        } catch (Exception e) {
            LOG.error("agate admin application start error", e);
            if (context != null) {
                SpringApplication.exit(context, new ExitCodeGenerator() {

                    @Override
                    public int getExitCode() {
                        return -1;
                    }
                });
            }
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ApplicationBootstrap bootstrap = new ApplicationBootstrap(applicationContext);
        try {
            bootstrap.start();
            // add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread("ShutdownHook") {

                @Override
                public void run() {
                    bootstrap.stop();
                }
            });
        } catch (Exception e) {
            bootstrap.stop();

            throw e;
        }
    }

}
