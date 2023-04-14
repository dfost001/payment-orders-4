/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.hosted.http;


import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.net.ssl.SSLException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.context.annotation.Scope;
//import org.springframework.context.annotation.Scope;
//import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
//import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author Dinah3
 */
@SuppressWarnings("serial")
@Component
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class ApacheConnectBean2 implements Serializable {
    
    
    public static final String AcceptDefault = 
            "application/xml,application/json,text/plain,"
            + "text/xml,text/json,"
            + "text/html,image/gif,image/jpeg,*;q=.2,*/*;q=.2";
    
    private String accept = "";
    
    private String authProp = "";
    
    private int responseCode = -1;
    
    private String responseText = "";
    
    private Map<String, List<String>> headers = null;
    
    private Map<String, String> customHeaders = null;
    
    private final String SPC = Character.valueOf((char)32).toString();
    
    private String module = "";
    
    private CloseableHttpClient client = null;
    
    private RequestConfig config = null;

    /** Creates a new instance of ApacheConnectBean */
    public ApacheConnectBean2() {
        module = this.getClass().getName();
        config = RequestConfig.custom().setRedirectsEnabled(true)
                .setConnectionRequestTimeout(10000)
                .setConnectTimeout(10000)
                .setSocketTimeout(15000)
                .build();
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public void setAuthProp(String user, String secret) {
        String toEncode = user + ":" + secret;
        byte[] bytes = toEncode.getBytes();
        byte[] encoded = Base64.encodeBase64(bytes);
        String sEncoded = new String(encoded);
        this.authProp = "Basic" + SPC + sEncoded;
    }
    
    public void setBearerAuthProp(String token){
        this.authProp = "Bearer" + SPC + token;
    } 
    
    public void setCustomRequestHeaders(Map<String, String> requestHeaders) {
    	
    	this.customHeaders = requestHeaders;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseText() {
        return responseText;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }
    
      private void initProperties(){
         
         responseCode = -1;
         responseText = "";
         headers = null;
         
    }
    
    public byte[] doUrlEncodedFormPost(String path, Map<String,String> pair)
                  throws HttpConnectException {
        
        byte[] result = null;
        HttpPost post = null;
        this.initProperties();
        post = new HttpPost(path);
        post.setConfig(config);
        List<NameValuePair> nv = new ArrayList<NameValuePair>();
        for(String key : pair.keySet()) {
            nv.add(new BasicNameValuePair(key, pair.get(key)));
        }
        try {
            post.setEntity(new UrlEncodedFormEntity(nv));
        }
        catch(UnsupportedEncodingException unex) {
           String display = "Application Exception (UnsupportedEncodingException)" ;
           throw new HttpConnectException(unex, unex.getMessage(),
                   display, module + "#doUrlEncodedFormPost");
           
        }
        this.setRequestHeaders(post, "application/x-www-form-urlencoded");
        CloseableHttpResponse resp = this.doExecute(post, "doUrlFormEncodedPost");
        result = this.getBinaryResult(resp);
        try {
          resp.close();
        }
        catch(IOException ioex){
            client = null;
        }
        return result; 
        
        
    }
    
    public byte[] doConnectPost(String path, byte[] entityBody, String contentType)
         throws HttpConnectException {
       byte[] result = null;
        HttpPost post = null;
        this.initProperties();
        post = new HttpPost(path);
        HttpEntity  entity = null;
        if(entityBody != null) {
            entity = EntityBuilder.create().setBinary(entityBody).build();
            post.setEntity(entity);  
        }
        this.setRequestHeaders(post, contentType);   
        CloseableHttpResponse resp = this.doExecute(post,"doConnectPost");
        result = this.getBinaryResult(resp);   
        try {
          resp.close();
        }
        catch(IOException ioex){client = null;}
        return result; 
        
    }
    
      public byte[] doConnectDelete(String path) throws HttpConnectException {
       byte[] result = null;
       HttpDelete delete = null;
        this.initProperties();
        delete = new HttpDelete(path);
        this.setRequestHeaders(delete, null);   
        CloseableHttpResponse resp = this.doExecute(delete,"doConnectDelete");
        result = this.getBinaryResult(resp);   
        try {
          resp.close();
        }
        catch(IOException ioex){client = null;}
        return result; 
        
    }
    
  

	public byte[] doConnectGet(String path) throws HttpConnectException{
        byte[] result = null;
        HttpGet get = null;
        this.initProperties();
        get = new HttpGet(path);
        this.setRequestHeaders(get, null);   
        CloseableHttpResponse resp = this.doExecute(get,"doConnectGet");
        result = this.getBinaryResult(resp);   
        try {
          resp.close();
        }
        catch(IOException ioex){client = null;}
        return result;
    }
    
    
  
   
   private void setRequestHeaders(HttpRequestBase request, String contentType){
        if(!this.authProp.isEmpty())
            request.setHeader("Authorization", authProp);
        
        if(this.accept.isEmpty())
            this.accept = AcceptDefault;
        
        request.setHeader("Accept", this.accept);
        
        if(contentType != null && !contentType.isEmpty())
            request.setHeader("Content-Type",contentType);
        
        request.setHeader("Accept-Language", "en_US");
        
        if(this.customHeaders == null) return;
        
        for(Entry<String, String> entry : this.customHeaders.entrySet())
        	request.addHeader(entry.getKey(), entry.getValue());
    }
   
   /*
    * Note: Connect errors are thrown as UnknownHost
    */
   private CloseableHttpResponse doExecute
           (HttpUriRequest request, String method ) throws HttpConnectException {
       if(this.client == null)
            client = HttpClients.createDefault();
        CloseableHttpResponse resp = null;
       
        try {
            resp = client.execute(request);
            this.responseCode = resp.getStatusLine().getStatusCode();
            this.responseText = resp.getStatusLine().getReasonPhrase();
            this.getHeaderMap2(resp);
        }
        
        catch(SocketTimeoutException timeEx ){
            String display = "Temporary  error: Please retry. (SocketTimeoutException)";
            client = null;
            throw new HttpConnectException(timeEx,
                    timeEx.getMessage(),
                    display,
                    this.module + "#" + method);
        }
        catch(ConnectTimeoutException timeEx){
            String display = "Temporary  error: Please retry. (ConnectTimeoutException)";
            client = null;
            throw new HttpConnectException(timeEx,
                    timeEx.getMessage(),
                    display,
                    this.module + "#" + method);
        }
        catch(UnknownHostException hostEx){ //java.net
            String display = "May be possible to retry. (UnknownHostException)";
            client = null;
            throw new HttpConnectException(hostEx,
                    hostEx.getMessage(),
                    display, 
                    this.module + "#" + method);
        }
        catch(SSLException ssl){ //java.net
            String display = "Application Error (SSLException)";
            client = null;
            throw new HttpConnectException(ssl,
                    ssl.getMessage(),
                    display, 
                    this.module + "#" + method);
        }
        catch(ClientProtocolException protEx){
            String display = "Application Error (ClientProtocolException)";
            client = null;
            throw new HttpConnectException(protEx,
                    protEx.getMessage(),
                    display, 
                    this.module + "#" + method);
        }
        catch(IOException ioEx){
            String display = "Temporary error: Please retry. (IOException)";
            client = null;
            throw new HttpConnectException(ioEx,
                    ioEx.getMessage(),
                    display, 
                   this.module + "#" + method);
        }
        catch (Exception ex){
            String display = "Application Error (Exception: Null Response)";
            client = null;
            throw new HttpConnectException(ex,
                    ex.getMessage(),
                    display, 
                    this.module + "#" + method);
        }
        return resp;
   }
   
   private byte[] getBinaryResult(CloseableHttpResponse resp) throws HttpConnectException {
       byte[]result = null;
       HttpEntity entity = resp.getEntity();
       if(entity == null)
           return null;
       try {
           result = EntityUtils.toByteArray(entity);
           EntityUtils.consume(entity);
       }
       catch(IOException ioex){
           
               String display = "Temporary error: Please retry. (IOException)";
               client = null;
               throw new HttpConnectException(ioex,ioex.getMessage(),
                    display, this.module + "#getBinaryResult");
           
       }
       return result;
   }
   
   private void getHeaderMap2(HttpResponse resp){
        this.headers = new HashMap<String,List<String>>();
        Header[] clientHeaders = resp.getAllHeaders();
        for (Header h : clientHeaders){
           ArrayList<String> list = new ArrayList<String>();
           String name = h.getName();
           String value = h.getValue();
           list.add(value);
           headers.put(name,list);
        }
        ArrayList<String> status = new ArrayList<>();
        status.add(resp.getStatusLine().getStatusCode() + ":" 
                + resp.getStatusLine().getReasonPhrase());
        headers.put(null, status);
        
    }
}
