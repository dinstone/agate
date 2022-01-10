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
package com.dinstone.agate.tracing;

import static brave.http.HttpTags.STATUS_CODE;

import brave.SpanCustomizer;
import brave.http.HttpRequest;
import brave.http.HttpRequestParser;
import brave.http.HttpResponse;
import brave.http.HttpResponseParser;
import brave.http.HttpTags;
import brave.internal.Nullable;
import brave.propagation.TraceContext;
import io.netty.handler.codec.http.HttpResponseStatus;

public class HttpRequestResponseParser implements HttpRequestParser, HttpResponseParser {

	@Override
	public void parse(HttpRequest request, TraceContext context, SpanCustomizer span) {
		span.name(request.method() + " " + request.path());

		HttpTags.METHOD.tag(request, context, span);
		HttpTags.PATH.tag(request, context, span);
	}

	@Override
	public void parse(HttpResponse response, TraceContext context, SpanCustomizer span) {
		int statusCode = 0;
		if (response != null) {
			statusCode = response.statusCode();
			if (statusCode < 200 || statusCode > 299) { // not success code
				STATUS_CODE.tag(response, context, span);
			}
			
		}
		error(statusCode, response.error(), span);
	}

	protected void error(int httpStatus, @Nullable Throwable error, SpanCustomizer span) {
		if (error != null)
			return; // the call site used Span.error

		// Instrumentation error should not make span errors. We don't know the
		// difference between a
		// library being unable to get the http status and a bad status (0). We don't
		// classify zero as
		// error in case instrumentation cannot read the status. This prevents tagging
		// every response as
		// error.
		if (httpStatus == 0)
			return;

		// Unlike success path tagging, we only want to indicate something as error if
		// it is not in a
		// success range. 1xx-3xx are not errors. It is endpoint-specific if client
		// codes like 404 are
		// in fact errors. That's why this is overridable.
		if (httpStatus < 100 || httpStatus > 399) {
			span.tag("error", HttpResponseStatus.valueOf(httpStatus).reasonPhrase());
		}
	}

}
