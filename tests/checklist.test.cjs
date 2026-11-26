const assert=require('node:assert/strict'),fs=require('node:fs'),vm=require('node:vm');
const h=fs.readFileSync('android/assets/index.html','utf8');
const source=h.match(/<script>([\s\S]*?)<\/script>/)[1].replace("document.addEventListener('DOMContentLoaded', init);","globalThis.api={generateReportText,normalize,validCatalog,safeHistory,setState:s=>state=s};");
const c={window:{},console};vm.createContext(c);vm.runInContext(source,c);
const a=c.api;let state={categories:[{name:'Fonte',pairs:[{id:'a',problem:'Capacitor',action:'Trocar'},{id:'b',problem:'Fusível',action:'Substituir'}]}],checked:['a','b'],footer:'RODAPÉ',draft:{}};
a.setState(state);assert.equal(a.generateReportText(),'◼ PROBLEMAS DETECTADOS:\nFONTE:\n▪ CAPACITOR;\n▪ FUSÍVEL.\n\n◼ AÇÕES A SEREM REALIZADAS:\nFONTE:\n▪ TROCAR;\n▪ SUBSTITUIR.\n\nRODAPÉ');
state.draft={number:'123',equipment:'Inversor',notes:'Teste'};assert.ok(a.generateReportText().startsWith('OS: 123\nEQUIPAMENTO: Inversor\n\n'));assert.ok(a.generateReportText().includes('OBSERVAÇÕES:\nTeste'));
state.checked=[];assert.equal(a.generateReportText(),'');assert.equal(a.normalize('FUSÍVEL'),'fusivel');assert.equal(a.validCatalog(state),true);assert.equal(a.validCatalog({categories:[null]}),false);assert.equal(a.safeHistory([{text:'relatório'},{foo:'x'}]).length,1);console.log('8 verificações de lógica OK');
