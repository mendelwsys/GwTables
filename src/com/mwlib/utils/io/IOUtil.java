/*
 * IOUtil.java
 *
 */
package com.mwlib.utils.io;

import java.io.*;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import java.util.zip.*;

/**
 *
 * @author  SCimbalov
 */
public final class IOUtil{
    //    
    private static final String DEFAULT_CHARACTER_SET = "Cp1251";
    public static final String  CP1251                = "Cp1251";    
    // 
    private static final String ALLOWED_CHARS         = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.!~*'()";
    
    /** Creates a new instance of IOUtil */
    private IOUtil() {}
    //
    public static String readFile(InputStream input, String charSet) throws IOException{
        
        String data = null;
        BufferedReader bReader = null;
        try {
            bReader = new BufferedReader(new InputStreamReader(input, charSet));
            StringBuffer sBuf = new StringBuffer();
            char[] cbuf = new char[1024];
            int len = 0;
            while((len  = bReader.read(cbuf, 0, cbuf.length))!= -1){
                  sBuf.append(cbuf, 0, len);
            }
            data = sBuf.toString();
        }finally{
            try{
                bReader.close();
            }catch(IOException exc){
                //
            }
        }
        return data;
    }
    //
    public static String readFile(InputStream input) throws IOException{
        
        String data = null;
        BufferedReader bReader = null;
        try {
            bReader = new BufferedReader(new InputStreamReader(input, DEFAULT_CHARACTER_SET));
            StringBuffer sBuf = new StringBuffer();
            char[] cbuf = new char[1024];
            int len = 0;
            while((len  = bReader.read(cbuf, 0, cbuf.length))!= -1){
                  sBuf.append(cbuf, 0, len);
            }
            data = sBuf.toString();
        }finally{
            try{
                bReader.close();
            }catch(IOException exc){
                //
            }
        }
        return data;
    }
    
    public static byte[] readFully(InputStream in) throws IOException{
        
        ByteArrayOutputStream out = null;
        final byte[] buf = new byte[1024]; 
        try {        
            out = new ByteArrayOutputStream(1024);
            int len = 0;  
            //while(in.available()>0 && (len = in.read(buf))!=-1){ //System.out.println("len=" + len);
            while((len = in.read(buf))!=-1){ //System.out.println("len=" + len);    
                   out.write(buf, 0, len);
            }
            out.flush();
            return out.toByteArray();
        }finally{
            try{
                if(in!=null) in.close();
                if(out!=null) out.close();
            }catch(Exception exc){
            }
        }
    }
    //
    public static void writeTo(final String fileFullPath, final byte[] buf) throws IOException{
        //
        OutputStream out = null;
        try{
            out = new FileOutputStream(fileFullPath);
            out.write(buf);
        }finally{
            try{if(out!=null)out.close();}catch(Exception exc1){}
        }
    }
    // 
    // object serialization util 
    public static byte[] serializeObject(final Object obj) throws IOException{
        // 
        final java.io.ByteArrayOutputStream byteArrOs = new java.io.ByteArrayOutputStream(7000);
        final java.io.ObjectOutputStream objOs = new java.io.ObjectOutputStream(byteArrOs);                        
        objOs.writeObject(obj);        
        objOs.close();                            
        return byteArrOs.toByteArray();         
    }               
    //                      
    public static Object deserializeObject(final byte[] b) throws IOException, ClassNotFoundException{
        //                                 
        final ObjectInputStream objOs = new ObjectInputStream(new java.io.ByteArrayInputStream(b));
        try{            
            return objOs.readObject();                
        }finally{
            objOs.close();
        }
    }
    
    
    // 
    public static String quote(final String string){
        // 
        if(string == null || string.length() == 0){return "\"\"";}
        //    
        char         b;
        char         c = 0;
        int          i;
        int          len = string.length();
        StringBuffer sb = new StringBuffer(len + 4);
        String       t;

        sb.append('"');
        for (i = 0; i < len; i += 1) {
            b = c;
            c = string.charAt(i);
            switch (c) {
            case '\\':
            case '"':
                sb.append('\\');
                sb.append(c);
                break;
            case '/':
                if (b == '<') {
                    sb.append('\\');
                }
                sb.append(c);
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\t':
                sb.append("\\t");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\r':
                sb.append("\\r");
                break;
            default:
                if (c < ' ') {
                    t = "000" + Integer.toHexString(c);
                    sb.append("\\u" + t.substring(t.length() - 4));
                } else {
                    sb.append(c);
                }
            }
        }
        sb.append('"');
        return sb.toString();
    }        
    // 
    public static String forXml(String text) throws IOException{ 
        // 
        if(text==null || (text!=null && text.length()==0)){return "";}     
        //
        final StringWriter out = new StringWriter(text.length()); 
        int start = 0, last = 0;
        char[] data = text.toCharArray();         
	while(last < data.length){
	      char c = data[last];
              //
	      // escape markup delimiters only ... and do bulk
	      // writes wherever possible, for best performance
	      //
	      // note that character data can't have the CDATA
	      // termination "]]>"; escaping ">" suffices, and
	      // doing it very generally helps simple parsers
	      // that may not be quite correct.
	      //
              if(c == '<'){			// not legal in char data
		 out.write (data, start, last - start);
		 start = last + 1;
		 out.write ("&lt;");
	      }else if(c == '>'){		// see above
		out.write (data, start, last - start);
		start = last + 1;
		out.write ("&gt;");
	      }else if (c == '&'){		// not legal in char data
		out.write (data, start, last - start);
		start = last + 1;
		out.write ("&amp;");
	      }
	      last++;
	}
	out.write (data, start, last - start);       
        return out.toString();
    }    
    /**
     * Escape characters for text appearing in HTML markup.
     * 
     * <P>This method exists as a defence against Cross Site Scripting (XSS) hacks.
     * This method escapes all characters recommended by the Open Web App
     * Security Project - 
     * <a href='http://www.owasp.org/index.php/Cross_Site_Scripting'>link</a>.  
     * 
     * <P>The following characters are replaced with corresponding HTML 
     * character entities : 
     * <table border='1' cellpadding='3' cellspacing='0'>
     * <tr><th> Character </th><th> Encoding </th></tr>
     * <tr><td> < </td><td> &lt; </td></tr>
     * <tr><td> > </td><td> &gt; </td></tr>
     * <tr><td> & </td><td> &amp; </td></tr>
     * <tr><td> " </td><td> &quot;</td></tr>
     * <tr><td> ' </td><td> &#039;</td></tr>
     * <tr><td> ( </td><td> &#040;</td></tr> 
     * <tr><td> ) </td><td> &#041;</td></tr>
     * <tr><td> # </td><td> &#035;</td></tr>
     * <tr><td> % </td><td> &#037;</td></tr>
     * <tr><td> ; </td><td> &#059;</td></tr>
     * <tr><td> + </td><td> &#043; </td></tr>
     * <tr><td> - </td><td> &#045; </td></tr>
     * </table>
     * 
     * <P>Note that JSTL's {@code <c:out>} escapes <em>only the first 
     * five</em> of the above characters.
     */
    public static String forHTML(String aText){       
        // 
        if(aText==null || (aText!=null && aText.length()==0)){return "";}
        //
        final StringBuffer result = new StringBuffer(aText.length());
        final StringCharacterIterator iterator = new StringCharacterIterator(aText);
        char character =  iterator.current();
        while(character != CharacterIterator.DONE){
              if(character == '<'){
                 result.append("&lt;");
              }else if(character == '>'){
                 result.append("&gt;");
              }else if(character == '&'){
                 result.append("&amp;");
              }else if(character == '\"'){
                 result.append("&quot;");
              }else if(character == '\''){
                 result.append("&#039;");
              }else if(character == '('){
                 result.append("&#040;");
              }else if(character == ')'){
                 result.append("&#041;");
              }else if(character == '#'){
                 result.append("&#035;");
              }else if(character == '%'){
                 result.append("&#037;");
              }else if(character == ';'){
                 result.append("&#059;");
              }else if(character == '+'){
                 result.append("&#043;");
              }else if (character == '-'){
                 result.append("&#045;");
              }else{
                //the char is not a special one
                //add it to the result as is
                result.append(character);
              }
              character = iterator.next();
        }
        return result.toString();
    }
    
    public static String forHTML1(String aText){       
        // 
        if(aText==null || (aText!=null && aText.length()==0)){return "";}
        //
        final StringBuffer result = new StringBuffer(aText.length());
        final StringCharacterIterator iterator = new StringCharacterIterator(aText);
        char character =  iterator.current();
        while(character != CharacterIterator.DONE){
              if(character == ' '){
                 result.append("&nbsp;");
              }else if(character == '<'){
                 result.append("&lt;");
              }else if(character == '>'){
                 result.append("&gt;");
              }else if(character == '&'){
                 result.append("&amp;");
              }else if(character == '\"'){
                 result.append("&quot;");
              }else if(character == '\''){
                 result.append("&#039;");
              }else if(character == '('){
                 result.append("&#040;");
              }else if(character == ')'){
                 result.append("&#041;");
              }else if(character == '#'){
                 result.append("&#035;");
              }else if(character == '%'){
                 result.append("&#037;");
              }else if(character == ';'){
                 result.append("&#059;");
              }else if(character == '+'){
                 result.append("&#043;");
              }else if (character == '-'){
                 result.append("&#045;");
              }else{
                //the char is not a special one
                //add it to the result as is
                result.append(character);
              }
              character = iterator.next();
        }
        return result.toString();
    }
    
    public static String quoteSpecial1(final String string){
        // 
        if(string == null || string.length() == 0){return "";}
        return string.replaceAll("\"",    "'");        
    }
    // 
    public static String quoteSpecial(final String string){
        // 
        if(string == null || string.length() == 0){return "";}
        return string.replaceAll("\"",    "&quot;");        
    }
    //
        
    // zip util !!!    
    public static byte[] zip(final String zipFileName, final String entryFileName, final byte[] entryData) throws IOException{
        //                
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ZipOutputStream zout = new ZipOutputStream(out);
        try{
            zout.putNextEntry(new ZipEntry(entryFileName));
            zout.write(entryData);
            zout.closeEntry();       
        }finally{
            try{if(zout!=null)zout.close();}catch(IOException exc){}
        }
        return out.toByteArray();        
    }
    // warn new - check it before use !!!
    public static byte[] zip(final String zipFileName,
                             final String entryFileName1, final byte[] entryData1,
                             final String entryFileName2, final byte[] entryData2) throws IOException{
        //
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ZipOutputStream zout = new ZipOutputStream(out);
        try{
            zout.putNextEntry(new ZipEntry(entryFileName1));
            zout.write(entryData1);
            zout.closeEntry();
            zout.putNextEntry(new ZipEntry(entryFileName2));
            zout.write(entryData2);
            zout.closeEntry();
        }finally{
            try{if(zout!=null)zout.close();}catch(IOException exc){}
        }
        return out.toByteArray();
    }
    //
    public static byte[] zipContent(final String zipFileName, 
                                    final String entryFileName,
                                    final String content,
                                    final String enc) throws IOException{         
         return zip(zipFileName, entryFileName, content.getBytes(enc));
    }
    // 
    public static byte[] toGZIP(final byte[] content){
        ByteArrayOutputStream baos = null;
        GZIPOutputStream zos = null;
        try{
            baos = new ByteArrayOutputStream(512);
            zos = new GZIPOutputStream(baos);
            zos.write(content);
            zos.flush();
            zos.close();
            return baos.toByteArray();                        
        }catch(Exception e){
//             LogFactory.log.error("error while converting to zip",e);
             try{
                 if(zos!=null){zos.close();}
             }catch(Exception e1){
//                 LogFactory.log.error("error while closing stream",e1);
             }
             try{
                 if(baos!=null){baos.close();}
             }catch(Exception e1){
//                 LogFactory.log.error("error while closing stream",e1);
             }    
             return null;
        }       
    }
    // 
    public static byte[] fromGZIP(final byte[] content){
        GZIPInputStream zis = null;
        try{
            zis = new GZIPInputStream(new ByteArrayInputStream(content), content.length);
            byte data[] = IOUtil.readFully(zis);
            return data;
        }catch(Exception e){
///            LogFactory.log.error("error while reading zip stream",e);
            try{
                if(zis!=null){zis.close();}   
            }catch(Exception e1){
//                LogFactory.log.error("error while closing zip stream",e1);
            }
            return null;
        }
    }               
    // 
    public static String encodeURIComponent(final String input){
        final int l = input.length();
        final StringBuilder o = new StringBuilder(l * 3);
        try{
            for(int i = 0; i < l; i++){
                String e = input.substring(i, i + 1);
                if(ALLOWED_CHARS.indexOf(e) == -1){
                    byte[] b = e.getBytes("utf-8");
                    o.append(getHex(b));
                    continue;
                }
                o.append(e);
            }
            return o.toString();
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return input;
    }
    // 
    private static String getHex(final byte buf[]){
        final StringBuilder o = new StringBuilder(buf.length * 3);
        for(int i = 0; i < buf.length; i++) {
            int n = (int) buf[i] & 0xff;
            o.append("%");
            if(n < 0x10){
                o.append("0");
            }
            o.append(Long.toString(n, 16).toUpperCase());
        }
        return o.toString();
    }  
    // 
    public static String decodeURIComponent(final String encodedURI){
        // 
        char actualChar; 
        final StringBuilder buffer = new StringBuilder();
        int bytePattern, sumb = 0;
        for(int i = 0, more = -1; i < encodedURI.length(); i++){
            actualChar = encodedURI.charAt(i); 
            switch(actualChar){
                case '%': {
                actualChar = encodedURI.charAt(++i);
                int hb = (Character.isDigit(actualChar) ? actualChar - '0'
                : 10 + Character.toLowerCase(actualChar) - 'a') & 0xF;
                actualChar = encodedURI.charAt(++i);
                int lb = (Character.isDigit(actualChar) ? actualChar - '0'
                : 10 + Character.toLowerCase(actualChar) - 'a') & 0xF;
                bytePattern = (hb << 4) | lb;
                break;
                }
                case '+': {
                bytePattern = ' ';
                break;
                }
                default: {
                bytePattern = actualChar;
                }
            } 
            if((bytePattern & 0xc0) == 0x80) { // 10xxxxxx
            sumb = (sumb << 6) | (bytePattern & 0x3f);
            if (--more == 0)
            buffer.append((char) sumb);
            } else if ((bytePattern & 0x80) == 0x00) { // 0xxxxxxx
                buffer.append((char) bytePattern);
            } else if ((bytePattern & 0xe0) == 0xc0) { // 110xxxxx
                sumb = bytePattern & 0x1f;
                more = 1;
            } else if ((bytePattern & 0xf0) == 0xe0) { // 1110xxxx
                sumb = bytePattern & 0x0f;
                more = 2;
            } else if ((bytePattern & 0xf8) == 0xf0) { // 11110xxx
                sumb = bytePattern & 0x07;
                more = 3;
            } else if ((bytePattern & 0xfc) == 0xf8) { // 111110xx
                sumb = bytePattern & 0x03;
                more = 4;
            } else { // 1111110x
                sumb = bytePattern & 0x01;
                more = 5;
            }
        }
        return buffer.toString();
    }
    
    
    public static boolean canUZip(final byte[] zipFileData){
        //                
        //get the zip file content
    	final ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipFileData));    	
        try{
            //get the zipped file list entry
            for(ZipEntry ze;(ze = zis.getNextEntry()) != null;){
                final String fileName = ze.getName();
                //System.out.println("file unzip=" + fileName);               
            }           
            return true; 
        }catch(IOException ex){
            // WARN !!! - log it !!!
        }finally{
            try{if(zis!=null)zis.close();}catch(IOException exc){}
        }
        return false;
    }
    
    /*
    public static void main(String[] strs){
        // 
        String res = quoteSpecial("��� \"������-14\"");  
        System.out.println("res=" + res);         
    }*/ 
}
