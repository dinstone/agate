import{D as e}from"./Descriptions-4884b6e3.js";import{d as a,s,e as l,J as t,B as n,o as i,i as r,a as o,k as f}from"./index-9d34d1b9.js";const d=a({__name:"Detail",props:{currentRow:{type:Object,default:()=>null}},setup(a){const{t:d}=f(),p=s([{field:"cname",label:d("集群")},{field:"name",label:d("网关")},{field:"port",label:d("端口")},{field:"status",label:d("状态"),slots:{default:e=>0===e.status?l(n("el-tag"),{type:"info"},{default:()=>[t("已停止")]}):l(n("el-tag"),{type:"success"},{default:()=>[t("已启动")]})}},{field:"remark",label:d("备注"),span:24},{field:"serverConfig",label:d("服务端配置"),span:24},{field:"clientConfig",label:d("客户端配置"),span:24}]);return(s,l)=>(i(),r(o(e),{schema:p,data:a.currentRow||{}},null,8,["schema","data"]))}});export{d as _};
