import a from"./Write-c8a2769a.js";import{_ as e}from"./ContentDetailWrap.vue_vue_type_script_setup_true_lang-2c9e1659.js";import{d as t,u as s,Z as o,r,o as i,i as l,w as m,e as n,J as p,t as u,a as d,O as c,k as f}from"./index-9d34d1b9.js";/* empty css                  */import{a as _}from"./route-37daf1fa.js";import{u as y}from"./useEmitt-312128b0.js";/* empty css                */import"./index-fd6339dd.js";const j=t({__name:"Add",setup(t){const{t:j}=f(),{emitter:v}=y(),{push:k,go:g}=s(),{query:x}=o(),C=r(),h=r(!1),w=async()=>{const a=d(C),e=await(null==a?void 0:a.submitForm());if(e){h.value=!0,e.appId=x.app;await _(e).catch((()=>{})).finally((()=>{h.value=!1}))&&(v.emit("changeEvent","add"),g(-1))}},b=async()=>{const a=d(C);null==a||a.resetForm()};return(t,s)=>(i(),l(d(e),{title:d(j)("exampleDemo.add"),onBack:s[1]||(s[1]=a=>d(k)("/app/route/list"))},{header:m((()=>[n(d(c),{onClick:s[0]||(s[0]=a=>d(g)(-1))},{default:m((()=>[p(u(d(j)("common.back")),1)])),_:1}),n(d(c),{type:"primary",onClick:b},{default:m((()=>[p(u(d(j)("common.reset")),1)])),_:1}),n(d(c),{type:"primary",loading:h.value,onClick:w},{default:m((()=>[p(u(d(j)("exampleDemo.save")),1)])),_:1},8,["loading"])])),default:m((()=>[n(a,{ref_key:"writeRef",ref:C},null,512)])),_:1},8,["title"]))}});export{j as default};
