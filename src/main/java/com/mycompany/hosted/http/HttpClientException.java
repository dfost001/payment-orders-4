/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.hosted.http;

/**
 *
 * @author Dinah3
 */
@SuppressWarnings("serial")
public class HttpClientException extends HttpException{
    
   
   
    private String textMessage = "";
    private Object errObj = null;   
    
    public HttpClientException(Throwable cause, String message, String friendly, String method){
        
        super(cause, message, friendly, method);        
    }  

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public Object getErrObj() {
        return errObj;
    }

    public void setErrObj(Object errObj) {
        this.errObj = errObj;
    }    
    
} //end class
