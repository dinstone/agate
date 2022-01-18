/*
 * Copyright (C) 2020~2022 dinstone<dinstone@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dinstone.agate.gateway;

import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;

@ExtendWith(VertxExtension.class)
class CircuitBreakerTest {

    private static final Logger LOG = LoggerFactory.getLogger(CircuitBreakerTest.class);

    private static int count = 0;

    public static void main(String[] args) {
        CircuitBreakerOptions breakerOptions = new CircuitBreakerOptions();
        // cbOptions.setFailuresRollingWindow(10000);
        breakerOptions.setMaxFailures(10);
        // If an action is not completed before this timeout, the action is considered as a failure.
        breakerOptions.setTimeout(1000);
        // does not succeed in time
        breakerOptions.setFallbackOnFailure(false);
        // time spent in open state before attempting to re-try
        breakerOptions.setResetTimeout(10000);

        String breakerName = "test";

        Vertx vertx = Vertx.vertx();
        CircuitBreaker circuitBreaker = createCircuitBreaker(vertx, breakerName, breakerOptions);

        for (int i = 0; i < 100; i++) {
            vertx.executeBlocking(p -> {

                Future<Void> uf = circuitBreaker.<Void> executeWithFallback(promise -> {
                    dosomething().onComplete(promise);
                }, t -> {
                    LOG.warn("fallback trigger by :", t);
                    return null;
                });
                uf.onComplete(ar -> {
                    if (ar.failed()) {
                        LOG.error("user future error :", ar.cause());
                    } else {
                        LOG.info("user future result: {}", ar.result());
                    }
                });

            }, false);

        }

    }

    private static Future<Void> dosomething() {
        Promise<Void> promise = Promise.promise();
        LOG.info("dosomething {}", count++);
        if (count % 2 == 0) {
            try {
                Thread.sleep(1100);
            } catch (InterruptedException e) {
                promise.fail(e);
            }
        } else {
            promise.complete();
        }
        return promise.future();
    }

    private static CircuitBreaker createCircuitBreaker(Vertx vertx, String breakerName,
            CircuitBreakerOptions cbOptions) {
        return CircuitBreaker.create(breakerName, vertx, cbOptions).openHandler(v -> {
            LOG.debug("circuit breaker {} open", breakerName);
        }).closeHandler(v -> {
            LOG.debug("circuit breaker {} close", breakerName);
        }).halfOpenHandler(v -> {
            LOG.debug("circuit breaker {} half", breakerName);
        });
    }

}
