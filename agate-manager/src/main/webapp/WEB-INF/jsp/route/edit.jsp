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

	function addParam(ev){
		event.preventDefault();
		var tbody = $(ev).parent().parent().parent();
		var index = tbody.children().size()-1;
		var tr = '<tr class="ng-scope">'+
			'<td><input class="form-control" name="backend.params['+index+'].feParamName"></td>'+
			'<td><select class="form-control" name="backend.params['+index+'].feParamType">'+
					'<option value="PATH">PATH</option>'+
					'<option value="QUERY">QUERY</option>'+
					'<option value="HEADER">HEADER</option></select></td>'+
			'<td><input class="form-control" name="backend.params['+index+'].beParamName"></td>'+
			'<td><select class="form-control" name="backend.params['+index+'].beParamType">'+
					'<option value="PATH">PATH</option>'+
					'<option value="QUERY">QUERY</option>'+
					'<option value="HEADER">HEADER</option></select></td>'+
			'<td align="right"><button class="btn btn-danger" onclick="delThis(this)"><i class="glyphicon glyphicon-trash"></i> Delete</button></td></tr>';
		$(ev).parent().parent().parent().append(tr);
	}

	function addUrl(ev){
		event.preventDefault();
		var tr = `<tr>
			<td colspan="4"><input type="text" class="form-control" name="backend.urls"></td>
			<td align="right">
				<button class="btn btn-danger" onclick="delThis(this)">
					<i class="glyphicon glyphicon-trash"></i> Delete
				</button>
			</td>
		</tr>`;
		 $("#urls").append(tr);
	}
	
	function addPlugin(ev){
		event.preventDefault();
		var tbody = $("#plugins");
		var index = tbody.children().size();
		var tr = '<tr><td>'+
			'<select class="form-control" name="plugins['+index+'].type">'+
			'<option value="0">Routing</option><option value="1">Failure</option>'+
			'</select></td>'+'<td><input type="text" class="form-control" name="plugins['+index+'].plugin" value="${pluginConfig.plugin}"></td>'+
			'<td><input type="text" class="form-control" name="plugins['+index+'].order"></td>'+
			'<td><textarea class="form-control" name="plugins['+index+'].config"></textarea></td>'+
			'<td align="right"><button class="btn btn-danger" onclick="delThis(this)"><i class="glyphicon glyphicon-trash"></i> Delete</button></td></tr>';
		tbody.append(tr);
	}

	function delThis(ev) {
		event.preventDefault();
		$(ev).parent().parent().remove();
	}
	
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
						<li><a href="${contextPath}/">Home</a></li>
						<li><a href="${contextPath}/view/app/list">Applications</a></li>
						<li><a href="${contextPath}/view/route/list?appId=${app.id}">Routes</a></li>
						<c:if test="${action == 'create'}">
							<li>Route Create</li>
						</c:if>
						<c:if test="${action == 'update'}">
							<li>Route Update</li>
						</c:if>
					</ul>
				</div>
				<div class="row">
					<div class="box col-md-12">
						<c:if test="${!empty error}">
							<div id="tip" class="alert alert-info">${error}</div>
						</c:if>
						<form action="/view/route/save" method="post">
							<input name="action" value="${action}" type="hidden"><input name=appId value="${app.id}" type="hidden"><input name=id value="${route.id}" type="hidden">
							<div class="panel panel-default">
								<div class="panel-heading">
									<h4>
										<i class="glyphicon glyphicon-th"></i> Basic
									</h4>
								</div>
								<div class="panel-body">
									<div class="form-group">
										<label>Route Name (Globe Uniqueness) <i class="glyphicon glyphicon-star red"></i></label> <input type="text" class="form-control" name="name" value="${route.name}">
									</div>
									<div class="form-group">
										<label>Remark</label>
										<textarea class="form-control" name="remark">${route.remark}</textarea>
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
										<label>Request Path (Path start with '/') <i class="glyphicon glyphicon-star red"></i></label> <input type="text" class="form-control" name="frontend.path" value="${route.frontend.path}">
									</div>
									<div class="form-group">
										<label>Request Method (Http Method)</label> <select name="frontend.method" class="form-control">
											<option></option>
											<option value="GET">GET</option>
											<option value="POST">POST</option>
											<option value="PUT">PUT</option>
											<option value="DELETE">DELETE</option>
										</select>
									</div>
									<div class="form-group">
										<label>Request Consumes (consume type and split by ',')</label> <input type="text" class="form-control" name="frontend.consumes" value="${route.frontend.consumes}" placeholder="please input consume type and split by ','">
									</div>
									<div class="form-group">
										<label>Request Produces (produce type and split by ',')</label> <input type="text" class="form-control" name="frontend.produces" value="${route.frontend.produces}" placeholder="please input produce type and split by ','">
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
										<label>Routing Type</label>
										<div class="input-group">
											<c:choose>
												<c:when test="${route.backend.type==0}">
													<input type="radio" value="0" name="backend.type" checked="checked"> Http Reverse Proxy 
													<input type="radio" value="1" name="backend.type"> Http Service Discovery
												</c:when>
												<c:otherwise>
													<input type="radio" value="0" name="backend.type"> Http Reverse Proxy 
													<input type="radio" value="1" name="backend.type" checked="checked"> Http Service Discovery
												</c:otherwise>
											</c:choose>
										</div>
									</div>
									<div class="form-group">
										<label>Routing Method (Http Method)</label> <select name="backend.method" class="form-control">
											<option></option>
											<option value="GET">GET</option>
											<option value="POST">POST</option>
											<option value="PUT">PUT</option>
											<option value="DELETE">DELETE</option>
										</select>
									</div>
									<div class="form-group has-feedback">
										<label>Routing Timeout</label>
										<div class="input-group">
											<input type="text" class="form-control" name="backend.timeout" value="${route.backend.timeout}"><span class="input-group-addon">ms</span>
										</div>
									</div>
									<div class="form-group">
										<label>Routing URLs <i class="glyphicon glyphicon-star red"></i></label>
										<table class="table table-striped bootstrap-datatable datatable responsive dataTable">
											<thead>
												<tr>
													<td colspan="4">Example: https://www.baidu.com or http://user-service, Does not include path.</td>
													<td align="right"><button class="btn btn-primary" onclick="addUrl(this)">
															<i class="glyphicon glyphicon-th-list"></i> Add URL
														</button></td>
												</tr>
											</thead>
											<tbody id="urls">
												<c:forEach var="url" items="${route.backend.urls}" varStatus="status">
													<tr>
														<td colspan="4"><input type="text" class="form-control" name="backend.urls" value="${url}"></td>
														<td align="right">
															<button class="btn btn-danger" onclick="delThis(this)">
																<i class="glyphicon glyphicon-trash"></i> Delete
															</button>
														</td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</div>
									<div class="form-group">
										<label>Routing Path</label> Example: /* or /:path <input type="text" class="form-control" name="backend.path" value="${route.backend.path}">
									</div>
									<div class="form-group">
										<label>Param Mapping</label>
										<table class="table table-striped bootstrap-datatable datatable responsive dataTable">
											<thead>
												<tr>
													<td width="20%" class="ng-binding">Request ParamName</td>
													<td width="20%" class="ng-binding">Request ParamType</td>
													<td width="20%" class="ng-binding">Routing ParamName</td>
													<td width="20%" class="ng-binding">Routing ParamType</td>
													<td align="right" class="ng-binding"><button class="btn btn-primary" onclick="addParam(this)">
															<i class="glyphicon glyphicon-th-list"></i> Add Param
														</button></td>
												</tr>
											</thead>
											<tbody>
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
														<td align="right">
															<button class="btn btn-danger" onclick="delThis(this)">
																<i class="glyphicon glyphicon-trash"></i> Delete
															</button>
														</td>
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
										<i class="glyphicon glyphicon-th"></i> Plugin Config
									</h4>
								</div>
								<div class="panel-body">
									<div class="form-group">
										<table class="table table-striped bootstrap-datatable datatable responsive dataTable">
											<thead>
												<tr>
													<td><label>Plugin Type</label></td>
													<td><label>Plugin Name</label></td>
													<td><label>Plugin Order</label></td>
													<td><label>Plugin Config</label></td>
													<td align="right"><button class="btn btn-primary" onclick="addPlugin(this)">
															<i class="glyphicon glyphicon-th-list"></i> Add Plugin
														</button></td>
												</tr>
											</thead>
											<tbody id="plugins">
												<c:forEach var="pluginConfig" items="${route.plugins}" varStatus="status">
													<tr>
														<td><select class="form-control" name="plugins[${status.index}].type" value="${pluginConfig.type}">
																<option value="0" <c:if test="${pluginConfig.type==0}">selected</c:if>>Routing</option>
																<option value="1" <c:if test="${pluginConfig.type==1}">selected</c:if>>Failure</option>
														</select></td>
														<td><input type="text" class="form-control" name="plugins[${status.index}].plugin" value="${pluginConfig.plugin}"></td>
														<td><input type="text" class="form-control" name="plugins[${status.index}].order" value="${pluginConfig.order}"></td>
														<td><textarea class="form-control" name="plugins[${status.index}].config">${pluginConfig.config}</textarea></td>
														<td>
															<button class="btn btn-danger" onclick="delThis(this)">
																<i class="glyphicon glyphicon-trash"></i> Delete
															</button>
														</td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</div>
								</div>
							</div>
							<div class="form-group">
								<button type="reset" class="btn btn-default">Reset</button>
								<button type="submit" class="btn btn-default">Submit</button>
							</div>
						</form>
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