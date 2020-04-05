<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Agate Manager APP</title>
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
						<li><a href="/view/app/list">APPs</a></li>
						<c:if test="${action == 'create'}">
							<li>APP Create</li>
						</c:if>
						<c:if test="${action == 'update'}">
							<li>APP Update</li>
						</c:if>
					</ul>
				</div>
				<div class="row">
					<div class="box col-md-12">
						<div class="box-inner">
							<div class="box-header well">
								<h2>
									<i class="glyphicon glyphicon-th"></i> APP Config
								</h2>
							</div>
							<div class="box-content">
								<c:if test="${ !empty error}">
									<div id="tip" class="alert alert-info">${error}</div>
								</c:if>
								<form role="form" action="/view/app/save" method="post">
									<input name="action" value="${action}" type="hidden"> <input name="id" value="${app.id}" type="hidden">
									<div class="form-group">
										<label>Cluster (Global Uniqueness) <i class="glyphicon glyphicon-star red"></i></label> <input type="text" class="form-control" name="cluster" value="${app.cluster}">
									</div>
									<div class="form-group">
										<label>Name (Cluster Uniqueness) <i class="glyphicon glyphicon-star red"></i></label> <input type="text" class="form-control" name="name" value="${app.name}">
									</div>
									<div class="form-group">
										<label>Prefix (API path prefix) <i class="glyphicon glyphicon-star red"></i></label> <input type="text" class="form-control" name="prefix" value="${app.prefix}">
									</div>
									<div class="form-group">
										<label>Port (Server listen port) <i class="glyphicon glyphicon-star red"></i></label> <input type="text" class="form-control" name="port" value="${app.port}">
									</div>
									<div class="form-group">
										<label>Host (Server listen host)</label> <input type="text" class="form-control" name="host" value="${app.host}">
									</div>
									<div class="form-group">
										<label>Remark</label>
										<textarea class="form-control" name="remark">${app.remark}</textarea>
									</div>
									<div class="form-group">
										<label>Server Config (Json Format for Vertx's ServerOption)</label>
										<textarea class="form-control" name="serverConfig">${app.serverConfig}</textarea>
									</div>
									<div class="form-group">
										<label>Client Config (Json Format for Vertx's ClientOption)</label>
										<textarea class="form-control" name="clientConfig">${app.clientConfig}</textarea>
									</div>
									<button type="reset" class="btn btn-default">Reset</button>
									<button type="submit" class="btn btn-default">Submit</button>
								</form>
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