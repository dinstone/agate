<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Agate Manager Clusters</title>
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
	function createRow(stats) {
		var tsr = '<tr><td><a class="ajax-link" href="chart.html?tube='
				+ stats.tubeName + '">' + stats.tubeName + '</a></td><td>'
				+ stats.totalJobSize + '</td><td>' + stats.finishJobSize
				+ '</td><td>' + stats.delayQueueSize + '</td><td>'
				+ stats.readyQueueSize + '</td><td>' + stats.retainQueueSize
				+ '</td><td>' + stats.failedQueueSize + '</td></tr>';

		$("#tubeStatsList").append(tsr);
	}

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
							<li><a class="ajax-link" href="${contextPath}/view/api/list"><i class="glyphicon glyphicon-align-justify"></i><span> API Routes</span></a></li>
							<li><a class="ajax-link" href="${contextPath}/view/gateway/list"><i class="glyphicon glyphicon-align-justify"></i><span> Gateways</span></a></li>
							<li class="active"><a class="ajax-link" href="${contextPath}/view/cluster/list"><i class="glyphicon glyphicon-align-justify"></i><span> Clusters</span></a></li>
						</ul>
					</div>
				</div>
			</div>
			<div id="content" class="col-lg-10 col-sm-10">
				<div>
					<ul class="breadcrumb">
						<li><a href="${contextPath}/">Home</a></li>
						<li>Clusters</li>
					</ul>
				</div>
				<div class="row">
					<div class="box col-md-12">
						<div class="panel panel-default">
							<div class="panel-heading">
								<div class="row">
									<div class="col-md-9" style="line-height: 30px;">
										<h4>
											<i class="glyphicon glyphicon-th"></i> Cluster List
										</h4>
									</div>
									<div class="col-md-3">
										<a class="btn btn-default" href="${contextPath}/view/cluster/create"> <i class="glyphicon glyphicon-edit icon-white"></i> Create Cluster
										</a>
									</div>
								</div>
							</div>
							<div class="panel-body">
								<table class="table">
									<thead>
										<tr>
											<th rowspan="1" colspan="1" style="width: 99px;">Code</th>
											<th rowspan="1" colspan="1" style="width: 99px;">Name</th>
											<th rowspan="1" colspan="1" style="width: 99px;">Status</th>
											<th rowspan="1" colspan="1" style="width: 99px;">Operation</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach items="${clusters}" var="cluster">
											<tr class="line">
												<td><a href="${contextPath}/view/cluster/detail?id=${cluster.id}">${cluster.code}</a></td>
												<td>${cluster.name}</td>
												<c:if test="${fn:length(cluster.nodes)==0}">
													<td>Down (0)</td>
												</c:if>
												<c:if test="${fn:length(cluster.nodes)>0}">
													<td>Up (${fn:length(cluster.nodes)})</td>
												</c:if>
												<td><a class="btn btn-default" href="${contextPath}/view/cluster/update?id=${cluster.id}"><i class="glyphicon glyphicon-pencil"></i><span> Update</span></a> <a class="btn btn-default" href="${contextPath}/view/cluster/delete?id=${cluster.id}"><i class="glyphicon glyphicon-trash"></i><span>
															Delete</span></a></td>
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