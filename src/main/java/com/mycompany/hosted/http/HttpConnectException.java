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
public class HttpConnectException extends HttpException{
      
    
    public HttpConnectException(Throwable cause, String message, String friendly, String method){
        
        super(cause,  message, friendly,  method);
       
    }  
    
} //end class
