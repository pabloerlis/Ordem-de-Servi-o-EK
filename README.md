# Checklist OS EK
Android v1.1.0 — experimental. HTML original preservado em web/.
## Funcionalidades
- Base inicial versionada em `data/checklist-os-inicial.json`, incluída no APK no build, carregada apenas no primeiro uso. Configurações > Base de dados inicial permite baixar uma cópia offline sem modificar os registros atuais.
- Filtro Somente marcados compacto, sem halo de seleção por toque.
- Catálogo e formato original dos relatórios.
- Busca por problema, ação e categoria, sem acentos; filtro de marcados.
- Número da OS, equipamento e observações opcionais; histórico local com relatórios independentes.
- Compartilhamento, cópia, exportação e importação com o seletor Android. Backup TXT/JSON legado; importação substitui dados, incluindo histórico (backups antigos não têm histórico).
- Offline, sem permissão de internet. Dados locais; exporte backups antes de desinstalar.
## Build
Instale JDK 17, Android SDK Platform 35, Build Tools 35.0.0 e zip. Execute node tests/checklist.test.cjs e bash android/build.sh. O APK sai em build/out. Android 6+ (API 23); target 34.
GitHub Actions compila e disponibiliza o APK como artifact. Assinatura de teste; runners novos podem gerar chaves diferentes. Para atualização local preserve build/debug.keystore. Não publique dados de OS nem chaves privadas.
## Validação
Testes automatizados de lógica não substituem testes no Android real. Importação, compartilhamento e restauração precisam de validação em dispositivo.
