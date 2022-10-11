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
	function delParam(ev) {
		event.preventDefault();
		$(ev).parent().parent().remove();
	}
	function addParam(ev){
		event.preventDefault();
		var tbody = $(ev).parent().parent().parent();
		var index = tbody.children().size()-1;
		var tr = '<tr class="ng-scope">'+
			'<td><input class="form-control" name="routingConfig.params['+index+'].feParamName"></td>'+
			'<td><select class="form-control" name="routingConfig.params['+index+'].feParamType">'+
					'<option value="PATH">PATH</option>'+
					'<option value="QUERY">QUERY</option>'+
					'<option value="HEADER">HEADER</option></select></td>'+
			'<td><input class="form-control" name="routingConfig.params['+index+'].beParamName"></td>'+
			'<td><select class="form-control" name="routingConfig.params['+index+'].beParamType">'+
					'<option value="PATH">PATH</option>'+
					'<option value="QUERY">QUERY</option>'+
					'<option value="HEADER">HEADER</option></select></td>'+
			'<td><button class="btn btn-primary" onclick="delParam(this)"><i class="glyphicon glyphicon-trash"></i> Delete</button></td></tr>';
		$(ev).parent().parent().parent().append(tr);
	}
	function delUrl(ev) {
		event.preventDefault();
		$(ev).parent().parent().remove();
	}
	function addUrl(ev){
		event.preventDefault();
		var tr = `<tr>
			<td><input type="text" class="form-control" name="routingConfig.urls"></td>
			<td>
				<button class="btn btn-primary" onclick="delUrl(this)">
					<i class="glyphicon glyphicon-trash"></i> Delete
				</button>
			</td>
		</tr>`;
		$(ev).parent().parent().parent().append(tr);
	}
	
	function addPlugin(ev){
		event.preventDefault();
		var tbody = $("#plugins");
		var index = tbody.children().size();
		var tr = '<tr><td><label>Plugin Type</label>'+
			'<select class="form-control" name="pluginConfigs['+index+'].type">'+
			'<option value="-1">Before</option><option value="1">After</option><option value="0">Failure</option>'+
			'</select></td>'+'<td><label>Plugin Name</label><input type="text" class="form-control" name="pluginConfigs['+index+'].plugin" value="${pluginConfig.plugin}"></td>'+
			'<td><label>Plugin Order</label><input type="text" class="form-control" name="pluginConfigs['+index+'].order"></td>'+
			'<td><label>Plugin Config</label> <textarea class="form-control" name="pluginConfigs['+index+'].config"></textarea></td>'+
			'<td><button class="btn btn-primary" onclick="delPlugin(this)"><i class="glyphicon glyphicon-trash"></i> Delete Plugin</button></td></tr>';
		tbody.append(tr);
	}
	function delPlugin(ev) {
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
						<li><a href="${contextPath}/view/route/list">Routes</a></li>
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
							<input name="action" value="${action}" type="hidden"> <input name="arId" value="${api.arId}" type="hidden">
							<div class="panel panel-default">
								<div class="panel-heading">
									<h4>
										<i class="glyphicon glyphicon-th"></i> Basic
									</h4>
								</div>
								<div class="panel-body">
									<div class="form-group">
										<label>Gateway <i class="glyphicon glyphicon-star red"></i></label> <select name="gwId" class="form-control">
											<c:forEach items="${gateways}" var="gateway">
												<option value="${gateway.id}" <c:if test="${gateway.id==api.gwId}">selected</c:if>>${gateway.name}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-group">
										<label>Name (Globe Uniqueness) <i class="glyphicon glyphicon-star red"></i></label> <input type="text" class="form-control" name="name" value="${api.name}">
									</div>
									<div class="form-group">
										<label>Remark</label>
										<textarea class="form-control" name="remark">${api.remark}</textarea>
									</div>
								</div>
							</div>
							<div class="panel panel-default">
								<div class="panel-heading">
									<h4>
										<i class="glyphicon glyphicon-th"></i> Request
									</h4>
								</div>
								<div class="panel-body">
									<div class="form-group">
										<label>Request Path (Path start with '/') <i class="glyphicon glyphicon-star red"></i></label> <input type="text" class="form-control" name="requestConfig.path" value="${api.requestConfig.path}">
									</div>
									<div class="form-group">
										<label>Request Method (Http Method)</label> <select name="requestConfig.method" class="form-control">
											<option></option>
											<option value="GET">GET</option>
											<option value="POST">POST</option>
											<option value="PUT">PUT</option>
											<option value="DELETE">DELETE</option>
										</select>
									</div>
									<div class="form-group">
										<label>Request Consumes (consume type and split by ',')</label> <input type="text" class="form-control" name="requestConfig.consumes" value="${api.requestConfig.consumes}" placeholder="please input consume type and split by ','">
									</div>
									<div class="form-group">
										<label>Request Produces (produce type and split by ',')</label> <input type="text" class="form-control" name="requestConfig.produces" value="${api.requestConfig.produces}" placeholder="please input produce type and split by ','">
									</div>
								</div>
							</div>
							<div class="panel panel-default">
								<div class="panel-heading">
									<h4>
										<i class="glyphicon glyphicon-th"></i> Routing
									</h4>
								</div>
								<div class="panel-body">
									<div class="form-group">
										<label>Routing Type</label>
										<div class="input-group">
											<c:choose>
												<c:when test="${api.routingConfig.type==0}">
													<input type="radio" value="0" name="routingConfig.type" checked="checked"> Http Reverse Proxy 
													<input type="radio" value="1" name="routingConfig.type"> Http Service Discovery
												</c:when>
												<c:otherwise>
													<input type="radio" value="0" name="routingConfig.type"> Http Reverse Proxy 
													<input type="radio" value="1" name="routingConfig.type" checked="checked"> Http Service Discovery
												</c:otherwise>
											</c:choose>
										</div>
									</div>
									<div class="form-group">
										<label>Routing Method (Http Method)</label> <select name="routingConfig.method" class="form-control">
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
											<input type="text" class="form-control" name="routingConfig.timeout" value="${api.routingConfig.timeout}"><span class="input-group-addon">ms</span>
										</div>
									</div>
									<div class="form-group">
										<label>Routing URLs <i class="glyphicon glyphicon-star red"></i></label>
										<table class="table table-striped bootstrap-datatable datatable responsive dataTable">
											<tbody>
												<c:if test="${empty api.routingConfig.urls}">
													<tr>
														<td><input type="text" class="form-control" name="routingConfig.urls" placeholder="example: http://127.0.0.1/test"></td>
														<td>
															<button class="btn btn-primary" onclick="addUrl(this)">
																<i class="glyphicon glyphicon-th-list"></i> Create
															</button>
														</td>
													</tr>
												</c:if>
												<c:forEach var="url" items="${api.routingConfig.urls}" varStatus="status">
													<c:choose>
														<c:when test="${status.index==0}">
															<tr>
																<td><input type="text" class="form-control" name="routingConfig.urls" value="${url}" placeholder="example: http://127.0.0.1/test"></td>
																<td>
																	<button class="btn btn-primary" onclick="addUrl(this)">
																		<i class="glyphicon glyphicon-th-list"></i> Create
																	</button>
																</td>
															</tr>
														</c:when>
														<c:otherwise>
															<tr>
																<td><input type="text" class="form-control" name="routingConfig.urls" value="${url}"></td>
																<td>
																	<button class="btn btn-primary" onclick="delUrl(this)">
																		<i class="glyphicon glyphicon-trash"></i> Delete
																	</button>
																</td>
															</tr>
														</c:otherwise>
													</c:choose>
												</c:forEach>
											</tbody>
										</table>
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
													<th width="10%" class="ng-binding"><button class="btn btn-primary" onclick="addParam(this)">
															<i class="glyphicon glyphicon-th-list"></i> Create
														</button></th>
												</tr>
												<c:forEach var="paramConfig" items="${api.routingConfig.params}" varStatus="status">
													<tr class="ng-scope">
														<td><input class="form-control" name="routingConfig.params[${status.index}].feParamName" value="${paramConfig.feParamName}"></td>
														<td><select class="form-control" name="routingConfig.params[${status.index}].feParamType">
																<option value="PATH">PATH</option>
																<option value="QUERY">QUERY</option>
																<option value="HEADER">HEADER</option>
														</select></td>
														<td><input class="form-control" name="routingConfig.params[${status.index}].beParamName" value="${paramConfig.beParamName}"></td>
														<td><select class="form-control" name="routingConfig.params[${status.index}].beParamType">
																<option value="PATH">PATH</option>
																<option value="QUERY">QUERY</option>
																<option value="HEADER">HEADER</option>
														</select></td>
														<td>
															<button class="btn btn-primary" onclick="delParam(this)">
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
									<div class="row">
										<div class="col-md-9">
											<h4>
												<i class="glyphicon glyphicon-th"></i> Plugin Config
											</h4>
										</div>
										<div class="col-md-3">
											<button class="btn btn-primary" onclick="addPlugin(this)">
												<i class="glyphicon glyphicon-th-list"></i> Add Plugin
											</button>
										</div>
									</div>
								</div>
								<div class="panel-body">
									<div class="form-group">
										<table class="table table-striped bootstrap-datatable datatable responsive dataTable">
											<tbody id="plugins">
												<c:forEach var="pluginConfig" items="${api.pluginConfigs}" varStatus="status">
													<tr>
														<td><label>Plugin Type</label><select class="form-control" name="pluginConfigs[${status.index}].type" value="${pluginConfig.type}">
																<option value="-1" <c:if test="${pluginConfig.type==-1}">selected</c:if>>Before</option>
																<option value="1" <c:if test="${pluginConfig.type==1}">selected</c:if>>After</option>
																<option value="0" <c:if test="${pluginConfig.type==0}">selected</c:if>>Failure</option>
														</select></td>
														<td><label>Plugin Name</label><input type="text" class="form-control" name="pluginConfigs[${status.index}].plugin" value="${pluginConfig.plugin}"></td>
														<td><label>Plugin Order</label><input type="text" class="form-control" name="pluginConfigs[${status.index}].order" value="${pluginConfig.order}"></td>
														<td><label>Plugin Config</label> <textarea class="form-control" name="pluginConfigs[${status.index}].config">${pluginConfig.config}</textarea></td>
														<td>
															<button class="btn btn-primary" onclick="delPlugin(this)">
																<i class="glyphicon glyphicon-trash"></i> Delete Plugin
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