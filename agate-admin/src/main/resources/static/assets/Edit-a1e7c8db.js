import{_ as e}from"./Write.vue_vue_type_script_setup_true_lang-9697c643.js";import{_ as t}from"./ContentDetailWrap.vue_vue_type_script_setup_true_lang-c1e79c36.js";import{d as a,Z as s,u as r,r as i,o,i as l,w as u,e as m,J as p,t as n,a as c,O as d,k as _}from"./index-3c676471.js";/* empty css                  */import{e as f,b as j}from"./gateway-d3be16c0.js";import{u as v}from"./useEmitt-ee4726c6.js";import"./useForm-1109d676.js";/* empty css                   */import"./tsxHelper-101a6832.js";/* empty css                  */import"./useValidator-1da2e1ce.js";/* empty css                */import"./index-5586c822.js";const y=a({__name:"Edit",setup(a){const{t:y}=_(),{query:w}=s(),{emitter:g}=v(),{push:k,go:x}=r(),b=i(null);(async()=>{const e=await f(w.id);e&&(b.value=e.data)})();const h=i(),C=i(!1),D=async()=>{const e=c(h),t=await(null==e?void 0:e.submit());if(t){C.value=!0;await j(t).catch((()=>{})).finally((()=>{C.value=!1}))&&(g.emit("changeEvent","editor"),k("/gateway/list"))}};return(a,s)=>(o(),l(c(t),{title:c(y)("exampleDemo.edit"),onBack:s[1]||(s[1]=e=>c(k)("/gateway/list"))},{header:u((()=>[m(c(d),{onClick:s[0]||(s[0]=e=>c(x)(-1))},{default:u((()=>[p(n(c(y)("common.back")),1)])),_:1}),m(c(d),{type:"primary",loading:C.value,onClick:D},{default:u((()=>[p(n(c(y)("exampleDemo.save")),1)])),_:1},8,["loading"])])),default:u((()=>[m(e,{ref_key:"writeRef",ref:h,"current-row":b.value},null,8,["current-row"])])),_:1},8,["title"]))}});export{y as default};