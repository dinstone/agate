<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Agate Manager API</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="shortcut icon" href="${contextPath}/img/favicon.ico">
<link id="bs-css" href="${contextPath}/bcs/css/bootstrap.min.css" rel="stylesheet">
<link href="${contextPath}/css/charisma.app.css" rel="stylesheet">
<script src="${contextPath}/bcs/js/jquery.min.js"></script>
<script src="${contextPath}/bcs/js/bootstrap.min.js"></script>
<script src="${contextPath}/js/jquery.cookie.js"></script>
<script src="${contextPath}/js/jquery.history.js"></script>
<script src="${contextPath}/js/autosize.min.js"></script>
<script src="${contextPath}/js/charisma.app.js?ctx=${contextPath}"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$("form").submit(function(event) {
		});
	});
</script>
</head>
<body>
	<jsp:include page="../topbar.jsp" />
	<div class="ch-container">
		<div class="row">
			<div id="content" class="col-lg-12 col-sm-10">
				<div>
					<ul class="breadcrumb">
						<li><a href="${contextPath}/view/api/list?appId=${api.appId}">APIs</a></li>
						<li>API Detail</li>
					</ul>
				</div>
				<div class="row">
					<div class="box col-md-12">
						<div class="box-inner">
							<div class="box-header well">
								<h2>
									<i class="glyphicon glyphicon-th"></i> API Config Detail
								</h2>
							</div>
							<div class="box-content">
								<c:if test="${!empty error}">
									<div id="tip" class="alert alert-info">${error}</div>
								</c:if>
								<input name="action" value="${action}" type="hidden"> <input name="appId" value="${api.appId}" type="hidden"><input name="apiId" value="${api.apiId}" type="hidden">
								<div class="form-group">
									<label>Name (API Uniqueness)</label> <input type="text" class="form-control" disabled="disabled" value="${api.name}">
								</div>
								<div class="form-group">
									<label>Remark</label>
									<textarea class="form-control" disabled="disabled">${api.remark}</textarea>
								</div>
								<div class="form-group">
									<label>Frontend Path (Path start with '/')</label>
									<div class="input-group">
										<span class="input-group-addon">${api.frontendConfig.prefix}</span><input type="text" class="form-control" disabled="disabled" value="${api.frontendConfig.path}">
									</div>
								</div>
								<div class="form-group">
									<label>Frontend Method (Http Method)</label> <input type="text" class="form-control" disabled="disabled" value="${api.frontendConfig.method}">
								</div>
								<div class="form-group">
									<label>Frontend Consumes (consume type and split by ',')</label> <input type="text" class="form-control" disabled="disabled" value="${api.frontendConfig.consumes}"
										placeholder="please input consume type and split by ','">
								</div>
								<div class="form-group">
									<label>Frontend Produces (produce type and split by ',')</label> <input type="text" class="form-control" disabled="disabled" value="${api.frontendConfig.produces}"
										placeholder="please input produce type and split by ','">
								</div>
								<div class="form-group has-feedback">
									<label>Backend Timeout</label>
									<div class="input-group">
										<input type="text" class="form-control" disabled="disabled" value="${api.backendConfig.timeout}"><span class="input-group-addon">ms</span>
									</div>
								</div>
								<div class="form-group">
									<label>Backend URLs</label>
									<table class="table">
										<tbody>
											<c:forEach var="url" items="${api.backendConfig.urls}" varStatus="status">
												<tr>
													<td><input type="text" class="form-control" disabled="disabled" value="${url}"></td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
								<div class="form-group">
									<label>Backend Method (Http Method)</label> <input type="text" class="form-control" disabled="disabled" value="${api.backendConfig.method}">
								</div>
								<div class="form-group">
									<label>Param Mapping</label>
									<table class="table table-striped bootstrap-datatable datatable responsive dataTable">
										<tbody>
											<tr>
												<th width="20%" class="ng-binding">Frontend ParamName</th>
												<th width="20%" class="ng-binding">Frontend ParamType</th>
												<th width="20%" class="ng-binding">Backend ParamName</th>
												<th width="20%" class="ng-binding">Backend ParamType</th>
											</tr>
											<c:forEach var="paramConfig" items="${api.backendConfig.params}">
												<tr class="ng-scope">
													<td><input class="form-control" disabled="disabled" value="${paramConfig.feParamName}"></td>
													<td><input class="form-control" disabled="disabled" value="${paramConfig.feParamType}"></td>
													<td><input class="form-control" disabled="disabled" value="${paramConfig.beParamName}"></td>
													<td><input class="form-control" disabled="disabled" value="${paramConfig.beParamType}"></td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- content ends -->
			<!--/#content.col-md-0-->
		</div>
		<!--/fluid-row-->
		<hr>
		<jsp:include page="../footbar.jsp" />
	</div>
	<!--/.fluid-container-->
</body>
</html>