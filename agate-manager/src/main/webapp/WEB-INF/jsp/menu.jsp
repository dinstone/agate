<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div id="menu" class="col-sm-2 col-lg-2">
	<div class="sidebar-nav">
		<div class="nav-canvas">
			<div class="nav-sm nav nav-stacked"></div>
			<ul class="nav nav-pills nav-stacked main-menu">
				<li><a class="ajax-link" href="${contextPath}/view/dashboard/show"><i class="glyphicon glyphicon-home"></i><span> Dashboard</span></a></li>
				<li><a class="ajax-link" href="${contextPath}/view/app/list"><i class="glyphicon glyphicon-list"></i><span> Applications</span></a></li>
				<li><a class="ajax-link" href="${contextPath}/view/gateway/list"><i class="glyphicon glyphicon-globe"></i><span> Gateways</span></a></li>
				<li><a class="ajax-link" href="${contextPath}/view/cluster/list"><i class="glyphicon glyphicon-th"></i><span> Clusters</span></a></li>
			</ul>
		</div>
	</div>
</div>