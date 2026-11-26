package br.com.ek.ordemservico;
import android.app.Activity;
import android.os.Bundle;
import android.content.*;
import android.net.Uri;
import android.webkit.*;
import android.widget.Toast;
import java.io.*;
import java.nio.charset.StandardCharsets;
public class MainActivity extends Activity {
  private static final String HOME="https://appassets.androidplatform.net/assets/index.html";
  private WebView web;
  private ValueCallback<Uri[]> picker;
  private String pendingText;
  @Override public void onCreate(Bundle b){
    super.onCreate(b);
    getWindow().setStatusBarColor(0xff1e293b);getWindow().setNavigationBarColor(0xff1e293b);
    web=new WebView(this);setContentView(web);
    WebSettings s=web.getSettings();s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);
    s.setAllowFileAccess(false);s.setAllowContentAccess(true);s.setBlockNetworkLoads(true);
    web.addJavascriptInterface(new Bridge(),"Android");
    web.setWebViewClient(new WebViewClient(){
      @Override public boolean shouldOverrideUrlLoading(WebView v,WebResourceRequest r){return !HOME.equals(r.getUrl().toString());}
      @Override public WebResourceResponse shouldInterceptRequest(WebView v,WebResourceRequest r){
        if(HOME.equals(r.getUrl().toString()))try{return new WebResourceResponse("text/html","UTF-8",getAssets().open("index.html"));}catch(IOException e){}
        return new WebResourceResponse("text/plain","UTF-8",new ByteArrayInputStream(new byte[0]));
      }
    });
    web.setWebChromeClient(new WebChromeClient(){
      @Override public boolean onShowFileChooser(WebView v,ValueCallback<Uri[]> cb,FileChooserParams p){
        if(picker!=null)picker.onReceiveValue(null);picker=cb;
        Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("*/*");
        try{startActivityForResult(i,10);}catch(Exception e){picker.onReceiveValue(null);picker=null;}
        return true;
      }
    });
    web.loadUrl(HOME);
  }
  final class Bridge {
    @JavascriptInterface public void copyText(String text){runOnUiThread(()->{
      ((android.content.ClipboardManager)getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("Relatório OS",text));toast("Texto copiado");
    });}
    @JavascriptInterface public void shareText(String text){runOnUiThread(()->{
      Intent i=new Intent(Intent.ACTION_SEND);i.setType("text/plain");i.putExtra(Intent.EXTRA_TEXT,text);startActivity(Intent.createChooser(i,"Compartilhar OS"));
    });}
    @JavascriptInterface public void saveText(String name,String text){runOnUiThread(()->{
      if(pendingText!=null){toast("Conclua a exportação anterior");return;}pendingText=text;
      Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("text/plain");i.putExtra(Intent.EXTRA_TITLE,name);
      try{startActivityForResult(i,11);}catch(Exception e){pendingText=null;toast("Não foi possível abrir o seletor");}
    });}
  }
  private void toast(String t){Toast.makeText(this,t,Toast.LENGTH_SHORT).show();}
  @Override protected void onActivityResult(int request,int result,Intent data){
    super.onActivityResult(request,result,data);
    if(request==10&&picker!=null){picker.onReceiveValue(result==RESULT_OK&&data!=null&&data.getData()!=null?new Uri[]{data.getData()}:null);picker=null;}
    if(request==11){
      String text=pendingText;pendingText=null;
      if(result==RESULT_OK&&data!=null&&data.getData()!=null&&text!=null){
        try(OutputStream out=getContentResolver().openOutputStream(data.getData(),"wt")){
          if(out==null)throw new IOException();out.write(text.getBytes(StandardCharsets.UTF_8));toast("Arquivo salvo");
        }catch(Exception e){toast("Erro ao salvar arquivo");}
      }
    }
  }
  @Override public void onBackPressed(){web.evaluateJavascript("window.onAndroidBack && window.onAndroidBack()",v->{if(!"true".equals(v))super.onBackPressed();});}
}