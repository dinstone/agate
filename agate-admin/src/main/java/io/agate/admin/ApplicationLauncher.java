
package io.agate.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.agate.admin.bootstrap.ApplicationBootstrap;

public class ApplicationLauncher {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationLauncher.class);

    public static void main(String[] args) {
        ApplicationBootstrap bootstrap = new ApplicationBootstrap();
        try {
            String implementationVersion = ApplicationLauncher.class.getPackage().getImplementationVersion();
            LOG.info("agate admin server {} is starting...", implementationVersion);
            bootstrap.start();
            // add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread("ShutdownHook") {

                @Override
                public void run() {
                    bootstrap.stop();
                }
            });
        } catch (Exception e) {
            LOG.error("agate admin server start error", e);
            bootstrap.stop();
        }
    }

}
