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
						<li><a href="/view/app/list">Applications</a></li>
						<li>APP Detail</li>
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
								<c:if test="${!empty error}">
									<div id="tip" class="alert alert-info">${error}</div>
								</c:if>
								<div class="panel-body">
									<div class="form-group">
										<label>Cluster<i class="glyphicon glyphicon-star red"></i></label> <input type="text" class="form-control" name="name" value="${gateway.cluster}" disabled="disabled">
									</div>
									<div class="form-group">
										<label>Gateway <i class="glyphicon glyphicon-star red"></i></label> <input type="text" class="form-control" name="cluster" value="${gateway.name}" disabled="disabled">
									</div>
									<div class="form-group">
										<label>Application <i class="glyphicon glyphicon-star red"></i></label> <input type="text" class="form-control" value="${app.name}" disabled="disabled">
									</div>
									<div class="form-group">
										<label>Domain <i class="glyphicon glyphicon-star red"></i></label> <input type="text" class="form-control" name="name" value="${app.domain}" disabled="disabled">
									</div>
									<div class="form-group">
                                        <label>Prefix <i class="glyphicon glyphicon-star red"></i></label> <input type="text" class="form-control" name="name" value="${app.prefix}" disabled="disabled">
                                    </div>
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