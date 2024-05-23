/*
 * Copyright (C) 2020~2024 dinstone<dinstone@163.com>
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
package io.agate.gateway.handler;

public abstract class OrderedHandler implements RouteHandler, Comparable<OrderedHandler> {

    private final int order;

    public OrderedHandler(int order) {
        this.order = order;
    }

    /**
     * Get the order value of this object.
     * <p>
     * Higher values are interpreted as lower priority. As a consequence, the object
     * with the lowest value has the highest priority (somewhat analogous to Servlet
     * {@code load-on-startup} values).
     * <p>
     * Same order values will result in arbitrary sort positions for the affected
     * objects.
     *
     * @return the order value
     */
    public int getOrder() {
        return order;
    }

    @Override
    public int compareTo(OrderedHandler other) {
        if (other == null) {
            return -1;
        }

        return this.order >= other.order ? 1 : -1;
    }
}
