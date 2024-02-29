import{u as e,_ as a,a as t}from"./useTable-f05893b6.js";import{_ as l}from"./tree-eadc1c52.js";import{d as i,r,s as o,e as s,K as n,O as d,a as u,o as m,c,w as p,f,J as v,t as g,P as h,i as y,Q as _,R as b,k as x}from"./index-9d34d1b9.js";/* empty css                  */import{_ as j}from"./Write.vue_vue_type_script_setup_true_lang-2ad30c83.js";import{_ as w}from"./Detail.vue_vue_type_script_setup_true_lang-e27ed5f3.js";import{r as D}from"./index-fd6339dd.js";import{u as S}from"./useCrudSchemas-1b1ac3a1.js";/* empty css                *//* empty css                  */import"./tsxHelper-ddf8f698.js";/* empty css                         *//* empty css                       */import"./useForm-1b49b077.js";/* empty css                   */import"./useValidator-4108ca93.js";import"./Descriptions-4884b6e3.js";const k={class:"mb-10px"};function C(e){return"function"==typeof e||"[object Object]"===Object.prototype.toString.call(e)&&!b(e)}const P=i({__name:"Cluster",setup(i){const b=r([]),{tableRegister:P,tableState:z,tableMethods:R}=e({fetchDataApi:async()=>{var e;const{currentPage:a,pageSize:t}=z,l=await(i={pageIndex:u(a),pageSize:u(t)},D.get({url:"/cluster/list",params:i}));var i;return{list:l.data,total:null==(e=l.data)?void 0:e.length}},fetchDelApi:async()=>{const e=await(e=>D.delete({url:"/cluster/delete",data:e}))(u(b));return!!e}}),{loading:A,dataList:E,currentPage:I,pageSize:L}=z,{getList:O,getElTableExpose:V,delList:H}=R,{t:U}=x(),M=o([{field:"index",label:U("tableDemo.index"),type:"index",search:{hidden:!0},form:{hidden:!0},detail:{hidden:!0}},{field:"code",label:U("编码"),search:{hidden:!0},form:{component:"Input",colProps:{span:12}}},{field:"name",label:U("名称"),search:{hidden:!0},form:{component:"Input",colProps:{span:12}}},{field:"instancs",label:U("实例"),search:{hidden:!0},form:{hidden:!0},detail:{hidden:!0},table:{slots:{default:e=>{const{row:a}=e;if(!a.instances)return"";let t="";return a.instances.forEach((e=>{t+=e.manageHost+":"+e.managePort+" "})),s(n,null,[t])}}}},{field:"action",width:"240px",label:U("tableDemo.action"),search:{hidden:!0},form:{hidden:!0},detail:{hidden:!0},table:{fixed:"right",headerAlign:"center",slots:{default:e=>{let a,t,l;return s(n,null,[s(d,{type:"primary",onClick:()=>q(e.row,"edit")},C(a=U("exampleDemo.edit"))?a:{default:()=>[a]}),s(d,{type:"success",onClick:()=>q(e.row,"detail")},C(t=U("exampleDemo.detail"))?t:{default:()=>[t]}),s(d,{type:"danger",onClick:()=>W(e.row)},C(l=U("exampleDemo.del"))?l:{default:()=>[l]})])}}}}]),{allSchemas:Q}=S(M),T=r(!1),B=r(""),F=r(null),J=r(""),K=()=>{B.value=U("exampleDemo.add"),F.value=null,T.value=!0,J.value=""},N=r(!1),W=async e=>{const a=await V();b.value=e?[e.id]:(null==a?void 0:a.getSelectionRows().map((e=>e.id)))||[],N.value=!0,await H(u(b).length).finally((()=>{N.value=!1}))},q=(e,a)=>{B.value=U("edit"===a?"exampleDemo.edit":"exampleDemo.detail"),J.value=a,F.value=e,T.value=!0},G=r(),X=r(!1),Y=async()=>{const e=u(G),a=await(null==e?void 0:e.submit());if(a){X.value=!0;await(t=a,D.post({url:"/cluster/save",data:t})).catch((()=>{})).finally((()=>{X.value=!1}))&&(T.value=!1,I.value=1,O())}var t};return(e,i)=>(m(),c(n,null,[s(u(t),null,{default:p((()=>[f("div",k,[s(u(d),{type:"primary",onClick:K},{default:p((()=>[v(g(u(U)("exampleDemo.add")),1)])),_:1})]),s(u(a),{pageSize:u(L),"onUpdate:pageSize":i[0]||(i[0]=e=>h(L)?L.value=e:null),currentPage:u(I),"onUpdate:currentPage":i[1]||(i[1]=e=>h(I)?I.value=e:null),columns:u(Q).tableColumns,data:u(E),loading:u(A),onRegister:u(P)},null,8,["pageSize","currentPage","columns","data","loading","onRegister"])])),_:1}),s(u(l),{modelValue:T.value,"onUpdate:modelValue":i[3]||(i[3]=e=>T.value=e),title:B.value},{footer:p((()=>["detail"!==J.value?(m(),y(u(d),{key:0,type:"primary",loading:X.value,onClick:Y},{default:p((()=>[v(g(u(U)("exampleDemo.save")),1)])),_:1},8,["loading"])):_("",!0),s(u(d),{onClick:i[2]||(i[2]=e=>T.value=!1)},{default:p((()=>[v(g(u(U)("dialogDemo.close")),1)])),_:1})])),default:p((()=>["detail"!==J.value?(m(),y(j,{key:0,ref_key:"writeRef",ref:G,"form-schema":u(Q).formSchema,"current-row":F.value},null,8,["form-schema","current-row"])):_("",!0),"detail"===J.value?(m(),y(w,{key:1,"detail-schema":u(Q).detailSchema,"current-row":F.value},null,8,["detail-schema","current-row"])):_("",!0)])),_:1},8,["modelValue","title"])],64))}});export{P as default};
