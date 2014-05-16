package com.reemmy.common;

import com.ecodex.seguridad.wsdl.*;
import com.google.appengine.repackaged.com.google.common.util.Base64;
import com.sun.xml.ws.client.BindingProviderProperties;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created with IntelliJ IDEA.
 * User: benn_sandoval
 * To change this template use File | Settings | File Templates.
 */
public class Seguridad {

    private static final String KEY_FACTORY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

    public String construirToken(Long trsID, String rfcCliente, String integrador) throws SeguridadObtenerTokenFallaSesionFaultFaultMessage, SeguridadObtenerTokenFallaServicioFaultFaultMessage, UnsupportedEncodingException {
        String token = new String();
        String tokenServicio = obtenerTokenServicio(trsID, rfcCliente);
        String toHash = String.format("%s|%s",integrador,tokenServicio);
        byte[] as = toHash.getBytes("UTF-8");
        String toHash2 = new String(as,"UTF-8");

        SHA1 sha1 = new SHA1();
        try {
            token = sha1.getHash(toHash2);
        } catch (NoSuchAlgorithmException ex) {
            return ex.getMessage();
        }
        return token;
    }

    public String construirTokenAlta(Long trsID, String integrador, String rfcIntegrador, String idAltaEmisor) throws SeguridadObtenerTokenFallaSesionFaultFaultMessage, SeguridadObtenerTokenFallaServicioFaultFaultMessage, UnsupportedEncodingException {
        String token = new String();
        String tokenServicio = obtenerTokenServicio(trsID, rfcIntegrador);
        String toHash = String.format("%s|%s|%s",integrador,idAltaEmisor,tokenServicio);
        byte[] as = toHash.getBytes("UTF-8");
        String toHash2 = new String(as,"UTF-8");

        SHA1 sha1 = new SHA1();
        try {
            token = sha1.getHash(toHash2);
        } catch (NoSuchAlgorithmException ex) {
            return ex.getMessage();
        }
        return token;
    }

    public String obtenerTokenServicio(Long trsID,String rfc) throws SeguridadObtenerTokenFallaSesionFaultFaultMessage, SeguridadObtenerTokenFallaServicioFaultFaultMessage {

        String token = new String();
        SolicitudObtenerToken tokenSolicitud = new SolicitudObtenerToken();

        tokenSolicitud.setTransaccionID(trsID);
        JAXBElement rfcElement=new JAXBElement(new QName("http://Ecodex.WS.Model/2011/CFDI","RFC"),JAXBElement.class,rfc);
        tokenSolicitud.setRFC(rfcElement);

        Seguridad_Service servicio = new Seguridad_Service();
        com.ecodex.seguridad.wsdl.Seguridad puertoX = servicio.getPuertoSeguridad();

        ((BindingProvider)puertoX).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, 60000);

        RespuestaObtenerToken tokenServicio = puertoX.obtenerToken(tokenSolicitud);
        token = tokenServicio.getToken().getValue();

        return token;

    }

    public static boolean verifyPurchase(String base64PublicKey, String signedData, String signature) throws InvalidKeySpecException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {

        if((base64PublicKey==null || base64PublicKey.length()==0)||
                ((signedData==null || signedData.length()==0))||
                ((signature==null || signature.length()==0))){
            return false;
        }
        PublicKey key = generatePublicKey(base64PublicKey);
        return verify(key, signedData, signature);
    }

    public static PublicKey generatePublicKey(String encodedPublicKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] decodedKey = Base64.decode(encodedPublicKey);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
        return keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
    }

    public static boolean verify(PublicKey publicKey, String signedData, String signature) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
        sig.initVerify(publicKey);
        sig.update(signedData.getBytes());
        if (!sig.verify(Base64.decode(signature))) {
            return false;
        }
        return true;
    }
}
