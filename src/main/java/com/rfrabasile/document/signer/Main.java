/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rfrabasile.document.signer;

import org.w3c.dom.Document;

/**
 *
 * @author rfrabasile@gmail.com
 */
public class Main {
    
    public static void main(String[] args) {
        
        String jksPath = args[0];
        String jksPassword = args[1]; 
	String jksKeyAlias = args[2];
        String filePathInput = args[3];
        String includeCertificateInput = args[4];
        
        boolean includeCertificate = false;
        if(includeCertificateInput.equalsIgnoreCase("true"))
            includeCertificate = true;
                
        if(args.length < 5)
            throw new RuntimeException("Incorrect input args: " + printParams());
        
        String documentDefinition = Converter.fromFileToString(filePathInput);
        
        if (documentDefinition == null)
            throw new RuntimeException("Doc definition can't be null: " + filePathInput);
        
        System.out.println("Starting signing process.");        
        
        Document document = Converter.fromStringToDocument(documentDefinition);
	
        System.out.println("File converted. Document obtained.");
        
        Signer.initialize(jksPath, jksPassword, jksKeyAlias);
        
        Signer.signDocument(document, includeCertificate);
        
        System.out.println("XML signed.");
        
        String xml = Converter.fromNodeToString(document);
        
        String fileOutput = filePathInput + ".output";
        
        Converter.fromStringToFile(xml, fileOutput);
        
        System.out.println("Signer ended. Output is in: " + fileOutput);
        
    }
    
    private static String printParams(){
        return "Params: "
                + "\n [0]: jksPath "
                + "\n [1]: jksPassword "
                + "\n [2]: jksKeyAlias "
                + "\n [3]: xml input"
                + "\n [4]: include certificate (true/false)";
    }
    
    
    
    
    
}
