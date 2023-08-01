<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Agate Manager Routes</title>
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
</head>
<body>
	<jsp:include page="../topbar.jsp"></jsp:include>
	<div class="ch-container">
		<div class="row">
			<jsp:include page="../menu.jsp"></jsp:include>
			<div id="content" class="col-lg-10 col-sm-10">
				<div>
					<ul class="breadcrumb">
						<li><a href="${contextPath}/">Home</a></li>
						<li><a href="${contextPath}/view/app/list">Applications</a></li>
						<li>Routes</li>
					</ul>
				</div>
				<div class="row">
					<div class="box col-md-12">
						<div class="panel panel-default">
							<div class="panel-heading">
								<div class="row">
									<div class="col-md-9" style="line-height: 30px;">
										<h4>
											<i class="glyphicon glyphicon-th"></i> Route List
										</h4>
									</div>
									<div class="col-md-3">
										<a class="btn btn-default" href="${contextPath}/view/route/create?appId=${app.id}"> <i class="glyphicon glyphicon-edit icon-white"></i> Create Route
										</a>
									</div>
								</div>
							</div>
							<div class="panel-body">
								<table class="table table-striped bootstrap-datatable datatable responsive dataTable">
									<thead>
										<tr>
											<th rowspan="1" colspan="1" style="width: 99px;">Application</th>
											<th rowspan="1" colspan="1" style="width: 99px;">Route Name</th>
											<th rowspan="1" colspan="1" style="width: 99px;">Remark</th>
											<th rowspan="1" colspan="1" style="width: 99px;">Path</th>
											<th rowspan="1" colspan="1" style="width: 99px;">Status</th>
											<th rowspan="1" colspan="1" style="width: 99px;">Actions</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach items="${routes}" var="route">
											<tr class="line">
												<td>${app.name}</td>
												<td><a href="${contextPath}/view/route/detail?id=${route.id}&appId=${app.id}">${route.name}</a></td>
												<td>${route.remark}</td>
												<td>${app.prefix}${route.frontend.path}</td>
												<c:if test="${route.status > 0}">
													<td>Started</td>
													<td><a class="btn btn-default" href="${contextPath}/view/route/close?id=${route.id}&appId=${app.id}"><i class="glyphicon glyphicon-off"></i><span> Close</span></a> <a class="btn btn-default" href="${contextPath}/view/route/update?id=${route.id}&appId=${app.id}"><i class="glyphicon glyphicon-pencil"></i><span>
																Update</span></a> <a class="btn btn-default" href="${contextPath}/view/route/delete?id=${route.id}&appId=${app.id}"><i class="glyphicon glyphicon-trash"></i><span> Delete</span></a></td>
												</c:if>
												<c:if test="${route.status == 0}">
													<td>Closed</td>
													<td><a class="btn btn-default" href="${contextPath}/view/route/start?id=${route.id}&appId=${app.id}"><i class="glyphicon glyphicon-cog"></i><span> Start</span></a> <a class="btn btn-default" href="${contextPath}/view/route/update?id=${route.id}&appId=${app.id}"><i class="glyphicon glyphicon-pencil"></i><span>
																Update</span></a> <a class="btn btn-default" href="${contextPath}/view/route/delete?id=${route.id}&appId=${app.id}"><i class="glyphicon glyphicon-trash"></i><span> Delete</span></a></td>
												</c:if>
											</tr>
										</c:forEach>
									</tbody>
								</table>
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