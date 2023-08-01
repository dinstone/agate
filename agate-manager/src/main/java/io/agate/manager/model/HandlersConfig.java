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

package io.agate.manager.model;

import java.util.List;

public class HandlersConfig {

    private List<PluginDefination> befores;

    private List<PluginDefination> afters;

    private List<PluginDefination> failures;

    public List<PluginDefination> getBefores() {
        return befores;
    }

    public void setBefores(List<PluginDefination> befores) {
        this.befores = befores;
    }

    public List<PluginDefination> getAfters() {
        return afters;
    }

    public void setAfters(List<PluginDefination> afters) {
        this.afters = afters;
    }

    public List<PluginDefination> getFailures() {
        return failures;
    }

    public void setFailures(List<PluginDefination> failures) {
        this.failures = failures;
    }

    @Override
    public String toString() {
        return "{\"befores\":" + befores + ", \"afters\":" + afters + ", \"failures\":" + failures + "}";
    }

}
