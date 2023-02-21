/*
 * 
 * Copied from AddressValidationRs to PictureRsClient2
 * Util cannot be used for JSON unless content-type response header is set
 */
package com.mycompany.hosted.http;

//import java.lang.reflect.Field;


import java.io.StringReader;
import java.nio.charset.CharacterCodingException;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import java.io.StringWriter;

import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamResult;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Gson;
import com.google.gson.FieldNamingPolicy;


/**
 *
 * @author Dinah3
 * Not thread safe -- construct for each use
 * To Do: Static method for return header value
 */

public class ResponseUtil {
    
        
    private Class<?> errType = null;
    private Class<?> resultType = null;
    private int respCode = -1;
    private Map<String,List<String>> headers = null;
    private byte[] response = null;
    private String jaxbPackage = "";
    private String contentType = "";
    
    @SuppressWarnings("rawtypes")
	private Class[] jaxbContext = null;
    
    private String decodedEntity = "";
    private boolean isXML = true;
    //private boolean debugging = false;
    private com.google.gson.FieldNamingPolicy namingPolicy;
    
    public ResponseUtil(int respCode, Map<String,List<String>> headers, byte[] httpEntity){
        this.respCode = respCode;
        this.headers = headers;
        this.response = httpEntity;
    }

       
    public Object processResponse_XML(Class<?>responseType, Class<?> perrType,
            String pjaxbPackage) throws HttpClientException {
        this.errType = perrType;
        this.resultType = responseType;
        this.jaxbPackage = pjaxbPackage;
        this.isXML = true;
        Object o = this.processResponseCode();
        return o;
        
    }
    
    @SuppressWarnings("rawtypes")
	public Object processResponse_XML(Class<?>responseType, Class<?> perrType,
            Class[] pjaxbContext) throws HttpClientException {
        this.errType = perrType;
        this.resultType = responseType;
        this.jaxbContext = pjaxbContext;
        this.isXML = true;
        Object o = this.processResponseCode();
        return o;
        
    }
    
    public Object processResponse_JSON(Class<?>responseType, 
            Class<?>perrType,
            FieldNamingPolicy pnamingPolicy) throws HttpClientException {
        this.errType = perrType;
        this.resultType = responseType;
        this.isXML = false;
        this.namingPolicy = pnamingPolicy;
        Object o = processResponseCode();
        return o;
    }

    public void processResponse_NoContent() throws HttpClientException {
        processResponseCode();
    }
    
    public String processResponse() throws HttpClientException {
        Object o = processResponseCode();
        return (String)o;
    }
    
  
   
   private Object processResponseCode() throws HttpClientException {
       decodedEntity = "";
       Object err = null;
       if(response == null || response.length == 0 )
           decodedEntity = "";
       else {
           this.decodeHttpEntity();      
           if(this.decodedEntity.isEmpty())
              decodedEntity = new String(this.response);
       }   
       if(this.respCode == 204)
           return null;
       
       String type = this.findContentType();
       System.out.println("ResponseUtil#processResponseCode: contentType=" + this.contentType);
       String edited = "";
       
       if(this.respCode == 200 || this.respCode == 201) {
           System.out.println("ResponseUtil#status is OK");
           if(isXML && this.resultType != null) {
               edited = this.normalizeXmlDoc();
               return this.unmarshall(this.resultType,edited);
           }
           else if(this.resultType != null)  
               return this.fromJSON(this.resultType);
           
           else return decodedEntity;
       }
       else if(this.respCode >= 400) {  //unmarshallers will throw syntax errors if wrong contentType
           System.out.println("ResponseUtil#status is " + respCode);
           if(isXML && this.errType != null && type.equalsIgnoreCase("xml")) {
               err = this.unmarshall(this.errType,decodedEntity);
               throw this.initResponseEx("processResponseCode", err);
           }
           else if(this.errType != null && type.equalsIgnoreCase("json")) {
        		err = this.fromJSON(this.errType);
                throw this.initResponseEx("processResponseCode", err);
           }
           else throw(this.initResponseEx("processResponseCode", null));
       }
       else {
           throw(this.initResponseEx("processResponseCode", null));
       }
      
   }


private void decodeHttpEntity() {
       if(response == null || response.length == 0){
           this.decodedEntity = "";
           return;
       }
       try {
           decodedEntity = new DecoderUtil2().decode(response);
       }
       catch(CharacterCodingException ex) {
          // throw this.initApplicationEx(ex,"decodeHttpEntity");
           
       }
   }
   
   private String normalizeXmlDoc(){
       String edited = "";
       int pos = decodedEntity.indexOf("<?xml");
       if(pos == -1)
           return decodedEntity;
       edited = decodedEntity.substring(pos);
       return edited;
   }
    
    
    private  String makeDebuggingMsg(){
        String msg = "";
      
              
        if(decodedEntity == null || decodedEntity.length() == 0)
            msg = "Empty http response";
        else msg = decodedEntity;
      
            
        msg = headers.get(null).get(0) + ": " + msg;
       
        return msg;
        
    }
    
    private  String makeErrorMsg_ParseHtml() {
        String msg = "";
              
        if(decodedEntity==null || decodedEntity.length() == 0){
            return "Empty http response entity";
        }   
             
                
        String type = this.findContentType();
        if(type.equals("html"))
            msg = ResponseUtil.parseHtmlBody(decodedEntity);
        else msg = this.makeErrorText();
        return msg;
    }
    
    /*
     * To do: look at content-type to init message only if not unknown and not an object
     * Otherwise, almost the same as the debugging message except for Html message
     */
    private String makeErrorText() {
            
        //String msg = headers.get(null).get(0);
        
        String msg = "";
        
        if(this.response == null || this.response.length == 0)
            return  " Empty http response entity";
              
        if(this.decodedEntity.isEmpty()){
            return  " Response could not be decoded";
        }   
                  
        String type = this.findContentType();
        
        
        if(type.equals("html"))
            msg =  "Internal error report from service (html)";
        else if(type.equals("xml"))
            msg = "Custom application content (xml)";
        else if(type.equals("json"))
            msg = "Custom application content (json)";
        else if(type.equals("text"))
            msg =  this.decodedEntity;
        else if(type.equals("unknown")) {
            msg = "Undetermined content type from server"; //could be text, json, or other        }
        }
                             
        return msg;
      
    }
    /*
     * End-user message
     * To do: get Retry-After if present on 503
     */
    private String makeReasonPhrase(){
        String phrase = headers.get(null).get(0);
        if(respCode >= 400 && respCode < 500)
            phrase  = "Internal application error " + phrase;
        else if(respCode == 503) {
            phrase = "Temporary service error " + phrase;
        }
        else if(respCode >= 500 && respCode < 600)
            phrase = "Service application error may be temporary " + phrase;
       
        return phrase;
    }
    
      
    /*
     * To do: get list of mime types, and iterate
     * 
     */
    public  String findContentType(){
       
        if(decodedEntity == null || decodedEntity.length() == 0)
            return "No Content";
        List<String> content = headers.get("Content-Type");
        if(content != null){
        	this.contentType = content.get(0);
            if(content.get(0).equals("text/plain"))
                return "text";
            else if(content.get(0).equals("application/xml") ||
                    content.get(0).equals("text/xml"))
                return "xml";
            else if(content.get(0).equals("application/json") ||
                    content.get(0).equals("text/json"))
                return "json";
            else if(content.get(0).equals("text/html"))
                return "html";
       }
        
        return "unknown";
    }
   
      
    /*
     * To do: Scan text until body tag is found using "<" as delimiter
     * Keep scanning until </body> is found
     * 
     */
    public static String parseHtmlBody(String result){
        String body = "";
        int posStart = result.indexOf("<body>");
        int posEnd = result.indexOf("</body>");
      
        if(posStart > -1){
          
           body = result.substring(posStart + 6, posEnd );
        }
        else body = result;
        return body;
    }
    
     private Object unmarshall(Class<?> t, String entity) throws HttpClientException {
    	 
    	 
    	 Object o = null; 
       
       if(entity == null || entity.isEmpty())
            throw initResponseEx("unmarshall", null);
              
       
        StringReader sr = new StringReader(entity);     
       
        
        JAXBContext context = null;
        
        try {
        	if(this.jaxbPackage.isEmpty())
        		context = JAXBContext.newInstance(jaxbContext);
        	else
                context = JAXBContext.newInstance(jaxbPackage);
        	
            Unmarshaller u = context.createUnmarshaller();
           
            o =  u.unmarshal(new StreamSource(sr)); 
            
            if(o.getClass() != t)
            	throw new ClassCastException("Entity cannot be cast to" + t.getClass().getName());
            	
        	return o;
           
        } 
        catch (ClassCastException castEx) {
        	throw initUnmarshalEx(castEx, "unmarshal");
        }
        catch(JAXBException jex) {
        	throw initUnmarshalEx(jex, "unmarshal");
        }
        
    }
    /*
     * ClassCastException code not working to catch json text with field names that are
     * not the same as the Class<?> t parameter.
     * Although the class name of the Object, when printed, is the same.
     *  
     */
    private Object fromJSON (Class<?> t) throws HttpClientException{
        Object o = null;
        
        if(decodedEntity.isEmpty())
            throw new ClassCastException
                        ("Http entity is empty and cannot be cast to " + t.getCanonicalName());
        
        Gson gson = new GsonBuilder()
                .setFieldNamingStrategy(this.namingPolicy).create();
        
        try {
        	
         o = gson.fromJson(decodedEntity, t);
         
         if(o == null) {
        	 
        	 System.out.println("ResponseUtil#fromJson: deserialized class is null" );
                     
        	 throw new ClassCastException("fromJson returned a null object");
         }         
         
         System.out.println("ResponseUtil#fromJson: deserialized class =" 
               + o.getClass().getCanonicalName());
         
        // Following code does not catch Cast exceptions 
         
        if(o.getClass() != t) {
        	 
        	 System.out.println("ResponseUtil#fromJson: deserialized class cannot be cast to " 
                     + t.getClass().getCanonicalName());
        	 
             throw new ClassCastException
                        ("Http entity cannot be cast to " + t.getCanonicalName());
         } 
         
        
         
        } catch(JsonSyntaxException jex){
            throw this.initUnmarshalEx(jex, "fromJSON");
        } catch (ClassCastException clex){
            throw this.initUnmarshalEx(clex, "fromJSON");
        }
        return o;
    } 
    
    public static String  toJson(Object o, FieldNamingPolicy policy)  {
       String json = "";
       Gson gson = new GsonBuilder()
    		   
                .setFieldNamingStrategy(policy)
                
                .addSerializationExclusionStrategy(new GsonExclusionStrategy())
                
                .create();              
                
       json = gson.toJson(o);
       
       return json;
    }
    
    public static String marshal(JAXBElement<?> el, String context, Class<?>[] ctxArr) throws JAXBException{
        
        String xml = "";
        StringWriter sw = new StringWriter();
        JAXBContext ctx = null;
       
       try {
    	    if(ctxArr == null)
              ctx = JAXBContext.newInstance(context);
    	    else
    	    	ctx = JAXBContext.newInstance(ctxArr);
            Marshaller m = ctx.createMarshaller();
            m.marshal(el,
                    new StreamResult(sw));
            xml = sw.toString();
            return xml;
       } catch (JAXBException jaxbex){
           throw jaxbex;
       }

    }
     /*
     * JAXB or ClassCast
     */
     private HttpClientException initUnmarshalEx(Exception e, String method){
         HttpClientException ex = null;
         method = "ResponseUtil#" + method;
         String type = e.getClass().getCanonicalName();
         String message =  type +  ":" + e.getMessage() + ": " + makeDebuggingMsg();
         String friendly = "Application Error";
         ex = new HttpClientException(e,message,friendly, method);
         String debug = this.makeDebuggingMsg();
         ex.setDebug(debug);
         String text = this.makeErrorText();
         ex.setTextMessage(text);
         ex.setResponseStatus(this.respCode);
         System.out.println("ResponseUtil#initUnmarshallEx: message=" + ex.getMessage());
         return ex;
     }
     
     /* 
      * Note: text will duplicate message, if content is not html
      */
   private HttpClientException initResponseEx(String method, Object o)  {
	    System.out.println("ResponseUtil#initResponseEx: entering init");
        HttpClientException ex = null;
        method = "ResponseUtil#" + method;
        String friendly = this.makeReasonPhrase();
        String text = this.makeErrorText();
        String message = this.makeErrorMsg_ParseHtml(); // same as textMessage or html body 
        String debug = this.makeDebuggingMsg();//raw response       
        ex = new HttpClientException(null,message,friendly,method);
        ex.setDebug(debug);
        ex.setTextMessage(text);
        ex.setErrObj(o);
        ex.setResponseStatus(this.respCode);
        System.out.println("ResponseUtil#initResponseEx: exiting: debug=" + debug);
        return ex;
   }
    
    
}//end class
