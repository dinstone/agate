<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Agate Manager Gateways</title>
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
			<jsp:include page="../menu.jsp"></jsp:include>
			<div id="content" class="col-lg-10 col-sm-10">
				<div>
					<ul class="breadcrumb">
						<li><a href="${contextPath}/view/home/welcome">Home</a></li>
						<li>Gateways</li>
					</ul>
				</div>
				<div class="row">
					<div class="box col-md-12">
						<div class="panel panel-default">
							<div class="panel-heading">
								<div class="row">
									<div class="col-md-9" style="line-height: 30px;">
										<h4>
											<i class="glyphicon glyphicon-th"></i> Gateway List
										</h4>
									</div>
									<div class="col-md-3">
										<a class="btn btn-default" href="${contextPath}/view/gateway/create"> <i class="glyphicon glyphicon-edit icon-white"></i> Create Gateway
										</a>
									</div>
								</div>
							</div>
							<div class="panel-body">
								<table class="table">
									<thead>
										<tr>
											<th rowspan="1" colspan="1" style="width: 99px;">Gateway Name</th>
											<th rowspan="1" colspan="1" style="width: 99px;">Cluster Name</th>
											<th rowspan="1" colspan="1" style="width: 99px;">Endpoint</th>
											<th rowspan="1" colspan="1" style="width: 99px;">Remark</th>
											<th rowspan="1" colspan="1" style="width: 99px;">Status</th>
											<th rowspan="1" colspan="1" style="width: 99px;">Actions</th>
										</tr>
									</thead>
									<tbody id="appList">
										<c:forEach items="${gateways}" var="gateway">
											<tr class="line">
												<td><a href="${contextPath}/view/gateway/detail?id=${gateway.id}">${gateway.name}</a></td>
												<td>${gateway.cluster}</td>
												<td>${gateway.host}:${gateway.port}</td>
												<td>${gateway.remark}</td>
												<c:if test="${gateway.status > 0}">
													<td>Started</td>
													<td><a class="btn btn-default" href="${contextPath}/view/gateway/close?id=${gateway.id}"><i class="glyphicon glyphicon-off"></i><span> Close</span></a> <a class="btn btn-default" href="${contextPath}/view/gateway/update?id=${gateway.id}"><i class="glyphicon glyphicon-pencil"></i><span>
																Update</span></a> <a class="btn btn-default" href="${contextPath}/view/gateway/delete?id=${gateway.id}"><i class="glyphicon glyphicon-trash"></i><span> Delete</span></a></td>
												</c:if>
												<c:if test="${gateway.status == 0}">
													<td>Closed</td>
													<td><a class="btn btn-default" href="${contextPath}/view/gateway/start?id=${gateway.id}"><i class="glyphicon glyphicon-cog"></i><span> Start</span></a> <a class="btn btn-default" href="${contextPath}/view/gateway/update?id=${gateway.id}"><i class="glyphicon glyphicon-pencil"></i><span>
																Update</span></a> <a class="btn btn-default" href="${contextPath}/view/gateway/delete?id=${gateway.id}"><i class="glyphicon glyphicon-trash"></i><span> Delete</span></a></td>
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