package com.mycompany.hosted.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class CacheControlFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		 System.out.println("CacheControlFilter#doFilter executing");
		 
		 setCacheControlHeaders((HttpServletResponse)response);
		 
		 chain.doFilter(request,response);	
		
	}
	
	private void setCacheControlHeaders(HttpServletResponse response) {
		
		response.setHeader("Cache-Control", "no-cache,no-store");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Thu, 01 Jan 1970 00:00:00 GMT");
		
	}
	

}
