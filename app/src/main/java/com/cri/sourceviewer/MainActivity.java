package com.cri.sourceviewer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.webkit.WebView;
import android.app.AlertDialog;
import java.io.File;
import java.io.FileOutputStream;
import android.webkit.JavascriptInterface;
import java.util.UUID;
import android.webkit.WebViewClient;
import android.view.MenuInflater;
import android.os.Build;
import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.app.ProgressDialog;
import android.graphics.Bitmap;

public class MainActivity extends AppCompatActivity {
	ProgressDialog progressdlg;
    WebView web;
	String url="https://www.github.com/kuttahaitu";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		progressdlg=new ProgressDialog(this);
		web = (WebView) findViewById(R.id.activity_mainWebView);
		web.getSettings().setJavaScriptEnabled(true);
		web.loadUrl(url);
		web.setWebViewClient(new WebViewClient(){
				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon) {
					progressdlg.show();
					super.onPageStarted(view,url,favicon);
				}
				@Override
				public void onPageFinished(WebView view,String url) {
					progressdlg.dismiss();
					super.onPageFinished(view,url);
				}

			});
		EnableJavascript();
    }
	private static final int MY_PERMISSION_REQUEST_CODE = 123;
    protected void checkPermission() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage("Write external storage permission is required.");
					builder.setTitle("Please grant permission");
					builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
						ActivityCompat.requestPermissions(MainActivity.this,
					  new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
		              MY_PERMISSION_REQUEST_CODE);
							}
						});
					builder.setNeutralButton("Cancel", null);
					AlertDialog dialog = builder.create();
					dialog.show();
				} else {
					ActivityCompat.requestPermissions(MainActivity.this,
			new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, MY_PERMISSION_REQUEST_CODE);
				}
			} 
		}
	}
	@Override
	protected void onStart() {
		super.onStart();
		checkPermission();
	}
	@Override
	public void onBackPressed() {
		if (web.canGoBack())
			web.goBack();
		else
			super.onBackPressed();
	}




	public void EnableJavascript() {
		web.addJavascriptInterface(new WebJS(), "crijs");

	}
	private class WebJS {
		@JavascriptInterface
		public void setData(int tag, String data) {
			onReceiveValue(tag, data);
		}
	}
	public void onReceiveValue(int tag, String data) {
		try {
			File directory = new File("sdcard/MatrixCri/Offline Page/");
			if (!directory.exists()) {
				directory.mkdirs();
			}
			String filename="matrix-- " + System.currentTimeMillis() + ".html";
			FileOutputStream fout=new FileOutputStream(new File(directory, filename));
			String s=data;
			byte b[]=s.getBytes();
			fout.write(b);
			fout.close();
			Toast.makeText(getApplicationContext(), filename +" Saved in "+directory.getAbsolutePath(),
						   Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(),
						   e.getMessage().toString(), Toast.LENGTH_LONG).show();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.download) {
			web.loadUrl("javascript:crijs.setData(0, document.documentElement.outerHTML);");
		}
		return super.onOptionsItemSelected(item);
	}

}
