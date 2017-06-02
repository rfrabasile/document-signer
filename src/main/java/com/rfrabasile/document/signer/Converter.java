/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rfrabasile.document.signer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 *
 * @author rfrabasile@gmail.com
 */
public class Converter {
    
    public static String fromFileToString(String filePath) {
        
        try {
            Reader input = new FileReader(filePath);
            StringWriter output = new StringWriter();
            try {
                IOUtils.copy(input, output);
            } finally {
                input.close();
            }
            return output.toString();
        } catch (IOException ex) {
            throw new RuntimeException("Error reading file from: " + filePath 
                                        + " IOException : " + ex.getMessage());
        }
        
    }
    
    public static void fromStringToFile( String text, String fileName ){
        
	try {		 
            File file = new File( fileName );
            // if file doesnt exists, then create it 
            if ( ! file.exists( ) )
                file.createNewFile( );
            FileWriter fw = new FileWriter( file.getAbsoluteFile( ) );
            BufferedWriter bw = new BufferedWriter( fw );
            bw.write( text );
            bw.close( );	    
	} catch( IOException e ) {
            System.out.println("Error converting from string to file: " + e);
            e.printStackTrace( );
	}
        
    }
    
    public static Document fromStringToDocument(String definition) {
        DocumentBuilder documentBuilder;
        Document document;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                    .newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(definition));
            document = documentBuilder.parse(inputSource);

            return document;

        } catch (Exception e) {
            throw new RuntimeException("Error parsing file: ", e);
        }
    }
    
    public static String fromNodeToString(Node node) {
	TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
	StringWriter sw = new StringWriter();
	try {
            transformer = tf.newTransformer();
            // below code to remove XML declaration
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(new DOMSource(node), new StreamResult(sw));
            return sw.toString();
        } catch (TransformerException e) {
            System.out.println("Error converting from node to string: " + e.getMessage());
        }
        return null;
    }
    
}
