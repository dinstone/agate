package io.agate.gateway;

import java.util.ArrayList;
import java.util.List;

import io.agate.gateway.handler.OrderedHandler;
import io.vertx.ext.web.RoutingContext;

public class OrderedTest {

    public static void main(String[] args) {
        List<OrderedHandler> hs = new ArrayList<>();
        hs.add(new OrderedHandler(600) {
            @Override
            public void handle(RoutingContext event) {

            }
        });
        hs.add(new OrderedHandler(500) {
            @Override
            public void handle(RoutingContext event) {

            }
        });
        hs.add(new OrderedHandler(Integer.MIN_VALUE) {
            @Override
            public void handle(RoutingContext event) {

            }
        });

        hs.forEach(h -> {
            System.out.println(h.getOrder());
        });
        hs.sort(null);
        hs.forEach(h -> {
            System.out.println(h.getOrder());
        });
    }
}
