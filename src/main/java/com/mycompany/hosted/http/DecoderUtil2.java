/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.hosted.http;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
//import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
//import java.nio.charset.MalformedInputException;
//import java.nio.charset.UnmappableCharacterException;
import java.util.Set;
import java.util.SortedMap;

/**
 *
 * @author Dinah3
 * Http response bytes into 16-bit Unicode char buff
 */
public class DecoderUtil2  {
    
    private SortedMap<String,Charset> charsetMap = Charset.availableCharsets();
   // private final String [] keys = {"US-ASCII", "ISO-8859-1","UTF-8","UTF-16","UTF-16BE", "UTF-16LE"};
    private String decoded = "";
    

    public DecoderUtil2(){
        
    }
    
   /* public String decode(byte[] source){
     //   Set<String> keys = charsetMap.keySet();
       
        for(String key : keys){
            Charset chset = Charset.forName(key);
            if(doDecode(chset, source))
                break;
        }
        
        return decoded;
    }*/
    
    public String decode(byte[] source) throws CharacterCodingException{
        
        if(source == null || source.length == 0)
            return "";
        
        Set<String> keyset = charsetMap.keySet();
       
        for(String key : keyset){
            
            if(doDecode(charsetMap.get(key), source));
                break;
        }
        
        if(decoded.isEmpty())
            throw new CharacterCodingException();
        
        return decoded;
    }
    
 /*   private boolean doDecode(Charset charset, byte[] source) {
        int capacity = source.length * 2;
        CharsetDecoder decoder = charset.newDecoder();
        ByteBuffer byteBuff = ByteBuffer.wrap(source);
        CharBuffer charBuff = CharBuffer.allocate(capacity);
        // decoder.reset();
        decoder.onMalformedInput(CodingErrorAction.REPORT);
        decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        CoderResult result = null;
        do {
            result = decoder.decode(byteBuff, charBuff, false); //endOfInput->false
            if (result.isError()) {
                return false;
            } else if (result.isUnmappable()) {
                return false;
            } else if (result.isMalformed()) {
                return false;
            } else if (result.isOverflow()) {
                decoded = "Insufficient buffer";
                return false;
            }
        } while (!result.isUnderflow());
        decoder.decode(byteBuff, charBuff, true);
        decoder.flush(charBuff);
        this.decoded = charBuff.toString();
        return true;
    }*/
    
     private boolean doDecode(Charset charset, byte[] source) {
     
        CharsetDecoder decoder = charset.newDecoder();
        ByteBuffer byteBuff = ByteBuffer.wrap(source);
        CharBuffer charBuff = null;
        decoder.onMalformedInput(CodingErrorAction.REPORT);
        decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
         charBuff = decoder.decode(byteBuff);
        }
        
        catch(CharacterCodingException encex){
           //throw encex;
            return false;
        }
        
        this.decoded = charBuff.toString();
        return true;
    }
    
}
