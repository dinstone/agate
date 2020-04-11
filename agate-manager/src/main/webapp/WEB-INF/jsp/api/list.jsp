<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Agate Manager APIS</title>
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
		// load stats

	});
</script>
</head>
<body>
	<jsp:include page="../topbar.jsp"></jsp:include>
	<div class="ch-container">
		<div class="row">
			<div id="menu" class="col-sm-2 col-lg-2">
				<div class="sidebar-nav">
					<div class="nav-canvas">
						<div class="nav-sm nav nav-stacked"></div>
						<ul class="nav nav-pills nav-stacked main-menu">
							<li class="active"><a class="ajax-link" href="${contextPath}/view/app/list"><i class="glyphicon glyphicon-align-justify"></i><span> APPs</span></a></li>
							<li><a class="ajax-link" href="${contextPath}/view/cluster"><i class="glyphicon glyphicon-align-justify"></i><span> Cluster</span></a></li>
						</ul>
					</div>
				</div>
			</div>
			<div id="content" class="col-lg-10 col-sm-10">
				<div>
					<ul class="breadcrumb">
						<li><a href="${contextPath}/view/app/list">APPs</a></li>
						<li>APIs</li>
					</ul>
				</div>
				<div class="row">
					<div class="box col-md-12">
						<div class="box-inner">
							<div class="box-header well">
								<h2>
									<i class="glyphicon glyphicon-th"></i> API List
								</h2>
							</div>
							<div class="box-content" id="tubelist">
								<!-- <div id="container" style="width: 600px; height: 400px;"></div> -->
								<table class="table table-striped bootstrap-datatable datatable responsive dataTable">
									<thead>
										<tr>
											<th rowspan="1" colspan="1" style="width: 99px;">Name</th>
											<th rowspan="1" colspan="1" style="width: 99px;">Path</th>
											<th rowspan="1" colspan="1" style="width: 99px;">Remark</th>
											<th rowspan="1" colspan="1" style="width: 99px;">Status</th>
											<th rowspan="1" colspan="1" style="width: 99px;"><a class="btn btn-default" href="${contextPath}/view/api/create?appId=${app.id}"> <i class="glyphicon glyphicon-edit icon-white"></i>
													Create API
											</a></th>
										</tr>
									</thead>
									<tbody id="appList">
										<c:forEach items="${apis}" var="api">
											<tr class="line">
												<td><a href="${contextPath}/view/api/detail?appId=${api.appId}&apiId=${api.apiId}">${api.name}</a></td>
												<td>${api.frontendConfig.prefix}${api.frontendConfig.path}</td>
												<td>${api.remark}</td>
												<c:if test="${api.status > 0}">
													<td>Started</td>
													<td><a class="btn btn-default" href="${contextPath}/view/api/close?appId=${api.appId}&apiId=${api.apiId}"><i class="glyphicon glyphicon-off"></i><span> Close</span></a> <a
														class="btn btn-default" href="${contextPath}/view/api/update?appId=${api.appId}&apiId=${api.apiId}"><i class="glyphicon glyphicon-pencil"></i><span> Update</span></a> <a
														class="btn btn-default" href="${contextPath}/view/api/delete?appId=${api.appId}&apiId=${api.apiId}"><i class="glyphicon glyphicon-trash"></i><span> Delete</span></a></td>
												</c:if>
												<c:if test="${api.status == 0}">
													<td>Closed</td>
													<td><a class="btn btn-default" href="${contextPath}/view/api/start?appId=${api.appId}&apiId=${api.apiId}"><i class="glyphicon glyphicon-cog"></i><span> Start</span></a> <a
														class="btn btn-default" href="${contextPath}/view/api/update?appId=${api.appId}&apiId=${api.apiId}"><i class="glyphicon glyphicon-pencil"></i><span> Update</span></a> <a
														class="btn btn-default" href="${contextPath}/view/api/delete?appId=${api.appId}&apiId=${api.apiId}"><i class="glyphicon glyphicon-trash"></i><span> Delete</span></a></td>
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