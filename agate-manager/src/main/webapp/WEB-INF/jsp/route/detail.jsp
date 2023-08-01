<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Agate Manager Route</title>
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
						<li><a href="${contextPath}/view/home/welcome">Home</a></li>
						<li><a href="${contextPath}/view/app/list">Applications</a></li>
						<li><a href="${contextPath}/view/route/list?appId=${app.id}">Routes</a></li>
						<li>Route Detail</li>
					</ul>
				</div>
				<div class="row">
					<div class="box col-md-12">
						<c:if test="${!empty error}">
							<div id="tip" class="alert alert-info">${error}</div>
						</c:if>
						<input name="id" value="${route.id}" type="hidden">
						<div class="panel panel-default">
							<div class="panel-heading">
								<h4>
									<i class="glyphicon glyphicon-th"></i> Basic
								</h4>
							</div>
							<div class="panel-body">
								<div class="form-group">
									<label>Route Name (Globe Uniqueness) <i class="glyphicon glyphicon-star red"></i></label> <input type="text" class="form-control" name="name" value="${route.name}" disabled="disabled">
								</div>
								<div class="form-group">
									<label>Remark</label>
									<textarea class="form-control" name="remark" disabled="disabled">${route.remark}</textarea>
								</div>
								<div class="form-group">
									<label>Application Name</label><input type="text" class="form-control" name="app.name" value="${app.name}" disabled>
								</div>
								<div class="form-group">
									<label>Application Domain</label><input type="text" class="form-control" name="app.domain" value="${app.domain}" disabled>
								</div>
								<div class="form-group">
									<label>Application Prefix</label><input type="text" class="form-control" name="appp.refix" value="${app.prefix}" disabled>
								</div>
							</div>
						</div>
						<div class="panel panel-default">
							<div class="panel-heading">
								<h4>
									<i class="glyphicon glyphicon-th"></i> Frontend
								</h4>
							</div>
							<div class="panel-body">
								<div class="form-group">
									<label>Request Path (Path start with '/') <i class="glyphicon glyphicon-star red"></i></label> <input type="text" class="form-control" name="frontend.path" value="${route.frontend.path}" disabled="disabled">
								</div>
								<div class="form-group">
									<label>Request Method (Http Method)</label> <input type="text" class="form-control" name="frontend.consumes" value="${route.frontend.method}" disabled="disabled">
								</div>
								<div class="form-group">
									<label>Request Consumes (consume type and split by ',')</label> <input type="text" class="form-control" name="frontend.consumes" value="${route.frontend.consumes}" disabled="disabled">
								</div>
								<div class="form-group">
									<label>Request Produces (produce type and split by ',')</label> <input type="text" class="form-control" name="frontend.produces" value="${route.frontend.produces}" disabled="disabled">
								</div>
							</div>
						</div>
						<div class="panel panel-default">
							<div class="panel-heading">
								<h4>
									<i class="glyphicon glyphicon-th"></i> Backend
								</h4>
							</div>
							<div class="panel-body">
								<div class="form-group">
									<label>Routing Method (Http Method)</label> <input type="text" class="form-control" value="${route.backend.method}" disabled="disabled">
								</div>
								<div class="form-group has-feedback">
									<label>Routing Timeout</label>
									<div class="input-group">
										<input type="text" class="form-control" name="backend.timeout" value="${route.backend.timeout}" disabled="disabled"><span class="input-group-addon">ms</span>
									</div>
								</div>
								<div class="form-group">
									<label>Routing URLs <i class="glyphicon glyphicon-star red"></i></label>
									<table class="table table-striped bootstrap-datatable datatable responsive dataTable">
										<tbody>
											<c:if test="${empty route.backend.urls}">
												<tr>
													<td><input type="text" class="form-control" name="backend.urls" disabled="disabled"></td>
												</tr>
											</c:if>
											<c:forEach var="url" items="${route.backend.urls}" varStatus="status">
												<tr>
													<td><input type="text" class="form-control" name="backend.urls" value="${url}" disabled="disabled"></td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
								<div class="form-group">
									<label>Routing Path</label><input type="text" class="form-control" name="backend.path" value="${route.backend.path}" disabled>
								</div>
								<div class="form-group">
									<label>Param Mapping</label>
									<table class="table table-striped bootstrap-datatable datatable responsive dataTable">
										<tbody>
											<tr>
												<th width="20%" class="ng-binding">Request ParamName</th>
												<th width="20%" class="ng-binding">Request ParamType</th>
												<th width="20%" class="ng-binding">Routing ParamName</th>
												<th width="20%" class="ng-binding">Routing ParamType</th>
											</tr>
											<c:forEach var="paramConfig" items="${route.backend.params}" varStatus="status">
												<tr class="ng-scope">
													<td><input class="form-control" name="backend.params[${status.index}].feParamName" value="${paramConfig.feParamName}"></td>
													<td><select class="form-control" name="backend.params[${status.index}].feParamType">
															<option value="PATH">PATH</option>
															<option value="QUERY">QUERY</option>
															<option value="HEADER">HEADER</option>
													</select></td>
													<td><input class="form-control" name="backend.params[${status.index}].beParamName" value="${paramConfig.beParamName}"></td>
													<td><select class="form-control" name="backend.params[${status.index}].beParamType">
															<option value="PATH">PATH</option>
															<option value="QUERY">QUERY</option>
															<option value="HEADER">HEADER</option>
													</select></td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
						<div class="panel panel-default">
							<div class="panel-heading">
								<h4>
									<i class="glyphicon glyphicon-th"></i> Response
								</h4>
							</div>
							<div class="panel-body"></div>
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