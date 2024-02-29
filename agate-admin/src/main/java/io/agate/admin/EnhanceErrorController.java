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
package io.agate.admin;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import io.agate.admin.business.BusinessException;

public class EnhanceErrorController extends BasicErrorController {

	public EnhanceErrorController(ErrorAttributes errorAttributes, ErrorProperties errorProperties) {
		super(errorAttributes, errorProperties);
	}

	public EnhanceErrorController(ErrorAttributes errorAttributes, ErrorProperties errorProperties,
			List<ErrorViewResolver> errorViewResolvers) {
		super(errorAttributes, errorProperties, errorViewResolvers);
	}

	@Override
	@RequestMapping
	public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
		Map<String, Object> errorAttributes = getErrorAttributes(request);

		HttpStatus status = globalErrorHandle(request, errorAttributes);

		return new ResponseEntity<>(errorAttributes, status);
	}

	private Throwable getError(HttpServletRequest request) {
		return (Throwable) request.getAttribute(ErrorAttributes.ERROR_ATTRIBUTE);
	}

	@Override
	@RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> errorAttributes = getErrorAttributes(request);

		HttpStatus status = globalErrorHandle(request, errorAttributes);

		response.setStatus(status.value());

		Map<String, Object> model = Collections.unmodifiableMap(errorAttributes);
		ModelAndView modelAndView = resolveErrorView(request, response, status, model);
		return (modelAndView != null) ? modelAndView : new ModelAndView("error", model);
	}

	private HttpStatus globalErrorHandle(HttpServletRequest request, Map<String, Object> errorAttributes) {
		Throwable error = getError(request);

		int code = 9999;
		HttpStatus status = getStatus(request);
		if (error instanceof BusinessException) {
			status = HttpStatus.BAD_REQUEST;
			code = ((BusinessException) error).getCode();
			
			errorAttributes.put("status", status.value());
			errorAttributes.put("error", status.getReasonPhrase());
		}
		errorAttributes.put("code", code);

		return status;
	}

	private Map<String, Object> getErrorAttributes(HttpServletRequest request) {
		return getErrorAttributes(request, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE));
	}

}