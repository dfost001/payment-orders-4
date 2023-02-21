package com.mycompany.hosted.http;

/**
 *
 * @author Dinah3
 */
@SuppressWarnings("serial")
public class HttpException extends Exception{
    
    private String friendly = "";
    private String method = "";
    private String debug = "";
    private Integer responseStatus = -1;
   
    
    public HttpException(Throwable cause, String message, String friendly, String method){
        super(message,cause,true,true);
        this.friendly = friendly;
        this.method = method;
    }
    
    public String getDebug() {
        return debug;
    }

    public void setDebug(String debug) {
        this.debug = debug;
    }
    
    public Integer getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(Integer responseStatus) {
		this.responseStatus = responseStatus;
	}

    public String getFriendly() {
        return friendly;
    }

    public String getMethod() {
        return method;
    }

    public void setFriendly(String friendly) {
        this.friendly = friendly;
    }

    public void setMethod(String method) {
        this.method = method;
    }    
} //end class
