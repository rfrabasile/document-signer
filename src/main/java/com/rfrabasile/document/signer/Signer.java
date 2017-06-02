/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rfrabasile.document.signer;

import java.io.FileInputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import org.w3c.dom.Document;

/**
 *
 * @author rfrabasile@gmail.com
 */
public class Signer {
    
    private static KeyStore keyStore;
    private static X509Certificate certificate;
    private static KeyStore.PrivateKeyEntry privateKeyEntry;
    
    public static void initialize(String jksPath, String jksPassword, String jksKeyAlias){
        try {
            keyStore = KeyStore.getInstance("jks");
            keyStore.load(new FileInputStream(jksPath),
                            jksPassword.toCharArray());            
            privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(jksKeyAlias,
                    new KeyStore.PasswordProtection(jksPassword.toCharArray()));
            certificate = (X509Certificate) privateKeyEntry.getCertificate();
            if(!isDateValid(certificate.getNotBefore(),
                        certificate.getNotAfter())){
                System.out.println("WARNING: certificate may be expired! ");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading JKS", e);
        }

    }
    
    private static boolean isDateValid(Date dateFrom, Date dateTo){
        Date now = new Date();
        if(now.before(dateFrom) || now.after(dateTo))
            return false;
        return true;
    }
    
    public static void signDocument(Document document, boolean includeCertificate){
        if(keyStore == null || certificate == null)
            throw new RuntimeException("You need to set keyStore and certificate first. Use initialize()");
        
        try {
            signDocument(document,
                        certificate, 
                        privateKeyEntry.getPrivateKey(), 
                        includeCertificate);

        } catch (Exception e) {
            throw new RuntimeException("Error obtaining keypair from JKS", e);
        }
    }
    
    private static void signDocument(Document document,
			X509Certificate x509Certificate, 
                        PrivateKey privateKey,
			boolean includeCertificate) {
        try {
            // Create a DOM XMLSignatureFactory that will be used to
            // generate the enveloped signature.
            XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory
                            .getInstance();

            // Create a Reference to the enveloped document (in this case,
            // you are signing the whole document, so a URI of "" signifies
            // that, and also specify the SHA1 digest algorithm and
            // the ENVELOPED Transform.
            Reference ref = xmlSignatureFactory
                            .newReference("", xmlSignatureFactory.newDigestMethod(
                                            DigestMethod.SHA1, null), Collections
                                            .singletonList(xmlSignatureFactory.newTransform(
                                                            Transform.ENVELOPED,
                                                            (TransformParameterSpec) null)),
                                                            null, 
                                                            null);

            // Create the SignedInfo.
            SignedInfo signedInfo = xmlSignatureFactory.newSignedInfo(
                            xmlSignatureFactory.newCanonicalizationMethod(
                                            CanonicalizationMethod.INCLUSIVE,
                                            (C14NMethodParameterSpec) null),
                            xmlSignatureFactory.newSignatureMethod(
                                            SignatureMethod.RSA_SHA1, null), 
                                            Collections.singletonList(ref));

            // Create the KeyInfo containing the X509Data.
            KeyInfoFactory keyInfoFactory = xmlSignatureFactory
                            .getKeyInfoFactory();

            List<Object> x509Content = new ArrayList<Object>();

            // X509SubjectName
            x509Content.add(x509Certificate.getSubjectX500Principal().getName());
            // X509IssuerSerial
            X509IssuerSerial issuerSerial = keyInfoFactory.newX509IssuerSerial(
                            x509Certificate.getIssuerX500Principal().getName(),
                            x509Certificate.getSerialNumber());
            x509Content.add(issuerSerial);

            // X509Certificate
            if (includeCertificate) {
                    x509Content.add(x509Certificate);
            }

            X509Data x509Data = keyInfoFactory.newX509Data(x509Content);
            KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections
                            .singletonList(x509Data));

            // Create a DOMSignContext and specify the RSA PrivateKey and
            // location of the resulting XMLSignature's parent element.
            DOMSignContext domSignContext = new DOMSignContext(privateKey,
                            document.getDocumentElement());

            // Create the XMLSignature, but don't sign it yet.
            XMLSignature signature = xmlSignatureFactory.newXMLSignature(
                            signedInfo, keyInfo);

            // Marshal, generate, and sign the enveloped signature.
            signature.sign(domSignContext);
        } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
                throw new RuntimeException(e);
        } catch (MarshalException e) {
                throw new RuntimeException(e);
        } catch (XMLSignatureException e) {
                throw new RuntimeException(e);
        }
    }
    
}
