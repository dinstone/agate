package io.agate.admin.business.param;

import io.agate.admin.business.model.AppDefinition;
import io.agate.admin.business.model.GatewayDefinition;

public class AppDetail {

    private AppDefinition app;

    private GatewayDefinition gateway;

    public AppDetail() {
    }

    public AppDetail(AppDefinition app, GatewayDefinition gateway) {
        this.app = app;
        this.gateway = gateway;
    }

    public AppDefinition getApp() {
        return app;
    }

    public void setApp(AppDefinition app) {
        this.app = app;
    }

    public GatewayDefinition getGateway() {
        return gateway;
    }

    public void setGateway(GatewayDefinition gateway) {
        this.gateway = gateway;
    }
}
