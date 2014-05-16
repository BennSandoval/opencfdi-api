/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.reemmy.common;

import com.ecodex.cancela.wsdl.*;
import com.ecodex.repositorio.wsdl.*;
import com.ecodex.timbrado.wsdl.*;
import com.ecodex.timbrado.wsdl.ComprobanteXML;
import com.sun.xml.ws.client.BindingProviderProperties;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.util.*;


public class Timbra{
    

    public RespuestaTimbraXML TimbraXML(String rfc,String token, String comprobanteXML) throws TimbradoTimbraXMLFallaSesionFaultFaultMessage, TimbradoTimbraXMLFallaServicioFaultFaultMessage, TimbradoTimbraXMLFallaValidacionFaultFaultMessage {

        Random random = new Random();
        long transactionID = random.nextLong();
        Timbrado_Service servicioX = new Timbrado_Service();
        Timbrado puertoX = servicioX.getPuertoTimbrado();

        ((BindingProvider)puertoX).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, 60000);
        
        JAXBElement<String> Rfc = new JAXBElement (new QName("http://Ecodex.WS.Model/2011/CFDI","RFC"), JAXBElement.class, rfc);
        JAXBElement<String> Token = new JAXBElement(new QName("http://Ecodex.WS.Model/2011/CFDI","Token"),JAXBElement.class,token);
        ComprobanteXML comprobante =new ComprobanteXML();
        JAXBElement xml=new JAXBElement(new QName("http://Ecodex.WS.Model/2011/CFDI","DatosXML"),JAXBElement.class,comprobanteXML);
        comprobante.setDatosXML(xml);
        JAXBElement<ComprobanteXML> xml2=new JAXBElement(new QName("http://Ecodex.WS.Model/2011/CFDI","ComprobanteXML"),JAXBElement.class,comprobante);
        
        SolicitudTimbraXML solicitud = new SolicitudTimbraXML();
        solicitud.setRFC(Rfc);
        solicitud.setToken(Token);
        solicitud.setTransaccionID(transactionID);
        solicitud.setComprobanteXML(xml2);
        RespuestaTimbraXML respuesta = puertoX.timbraXML(solicitud);

        return respuesta;
    }

    public RespuestaEstatusTimbrado estatus(String rfc, String token, Long trsIdOriginal, String uuid) throws TimbradoEstatusTimbradoFallaServicioFaultFaultMessage, TimbradoEstatusTimbradoFallaSesionFaultFaultMessage, TimbradoEstatusTimbradoFallaValidacionFaultFaultMessage {

        Random random = new Random();
        long transactionNew = random.nextLong();
        Timbrado_Service servicioX = new Timbrado_Service();
        Timbrado puertoX = servicioX.getPuertoTimbrado();

        ((BindingProvider)puertoX).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, 60000);

        JAXBElement<String> Uuid = new JAXBElement(new QName("http://Ecodex.WS.Model/2011/CFDI","UUID"),JAXBElement.class,uuid);
        JAXBElement<String> Rfc = new JAXBElement (new QName("http://Ecodex.WS.Model/2011/CFDI","RFC"), JAXBElement.class, rfc);
        JAXBElement<String> Token = new JAXBElement(new QName("http://Ecodex.WS.Model/2011/CFDI","Token"),JAXBElement.class,token);

        SolicitudEstatusTimbrado solicitud = new SolicitudEstatusTimbrado();
        solicitud.setRFC(Rfc);
        solicitud.setToken(Token);
        solicitud.setTransaccionID(transactionNew);
        solicitud.setTransaccionOriginal(trsIdOriginal);
        solicitud.setUUID(Uuid);

        RespuestaEstatusTimbrado respuesta = puertoX.estatusTimbrado(solicitud);

        return respuesta;

    }

    public RespuestaCancelaTimbrado cancela(String rfc, String token, String uuid) throws TimbradoCancelaTimbradoFallaValidacionFaultFaultMessage, TimbradoCancelaTimbradoFallaServicioFaultFaultMessage, TimbradoCancelaTimbradoFallaSesionFaultFaultMessage, RepositorioCancelaComprobanteFallaServicioFaultFaultMessage, RepositorioCancelaComprobanteFallaSesionFaultFaultMessage, RepositorioCancelaComprobanteFallaValidacionFaultFaultMessage, CancelacionesCancelaMultipleFallaServicioFaultFaultMessage, CancelacionesCancelaMultipleFallaSesionFaultFaultMessage {

        Random random = new Random();
        long transactionID = random.nextLong();

        Cancelaciones_Service servicioX = new Cancelaciones_Service();
        Cancelaciones puertoX = servicioX.getPuertoCancelacion();

        ((BindingProvider)puertoX).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, 60000);

        ListaCancelar lista= new ListaCancelar();
        lista.getGuid().add(uuid);

        JAXBElement<ListaCancelar> UUIDS= new JAXBElement(new QName("http://Ecodex.WS.Model/2011/CFDI","ListaCancelar"), JAXBElement.class,lista);
        JAXBElement<String> Rfc = new JAXBElement (new QName("http://Ecodex.WS.Model/2011/CFDI","RFC"), JAXBElement.class, rfc);
        JAXBElement<String> Token = new JAXBElement(new QName("http://Ecodex.WS.Model/2011/CFDI","Token"),JAXBElement.class,token);

        SolicitudCancelaMultiple solicitud = new SolicitudCancelaMultiple();

        solicitud.setRFC(Rfc);
        solicitud.setToken(Token);
        solicitud.setListaCancelar(UUIDS);
        solicitud.setTransaccionID(transactionID);

        RespuestaCancelaMultiple respuestaX = puertoX.cancelaMultiple(solicitud);
        RespuestaCancelaTimbrado respuesta = new RespuestaCancelaTimbrado();
        if(respuestaX.getResultado().getValue().getResultadoCancelacion().get(0).getEstatus().getValue().equals("Cancelado")){
            respuesta.setCancelada(true);
        } else {
            respuesta.setCancelada(false);
        }
        respuesta.setTransaccionID(Long.valueOf(respuestaX.getTransaccionID().getValue()));

        return respuesta;
    }

   public RespuestaObtenerTimbrado ObtenerTimbre(String rfc, String token, Long trsIdOriginal, String uuid) throws TimbradoObtenerTimbradoFallaValidacionFaultFaultMessage, TimbradoObtenerTimbradoFallaSesionFaultFaultMessage, TimbradoObtenerTimbradoFallaServicioFaultFaultMessage {
        String comprobante = new String();
        Random random = new Random();
        long transactionNew = random.nextLong();

        Timbrado_Service servicioX = new Timbrado_Service();
        Timbrado puertoX = servicioX.getPuertoTimbrado();

        ((BindingProvider)puertoX).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, 60000);

        JAXBElement<String> Uuid = new JAXBElement(new QName("http://Ecodex.WS.Model/2011/CFDI","UUID"),JAXBElement.class,uuid);
        JAXBElement<String> Rfc = new JAXBElement (new QName("http://Ecodex.WS.Model/2011/CFDI","RFC"), JAXBElement.class, rfc);
        JAXBElement<String> Token = new JAXBElement(new QName("http://Ecodex.WS.Model/2011/CFDI","Token"),JAXBElement.class,token);

        SolicitudObtenerTimbrado solicitud = new SolicitudObtenerTimbrado();
        solicitud.setToken(Token);
        solicitud.setRFC(Rfc);
        solicitud.setTransaccionID(transactionNew);
        solicitud.setTransaccionOriginal(trsIdOriginal);
        solicitud.setUUID(Uuid);

        RespuestaObtenerTimbrado respuesta = puertoX.obtenerTimbrado(solicitud);

        return respuesta;
   }
   
   public RespuestaObtenerQRTimbrado obtenerQR(String rfc, String token, String uuid) throws TimbradoObtenerQRTimbradoFallaSesionFaultFaultMessage, TimbradoObtenerQRTimbradoFallaServicioFaultFaultMessage, TimbradoObtenerQRTimbradoFallaValidacionFaultFaultMessage {

        Random random = new Random();
        Long transactionID = random.nextLong();

        Timbrado_Service servicioX = new Timbrado_Service();
        Timbrado puertoX = servicioX.getPuertoTimbrado();

        ((BindingProvider)puertoX).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, 60000);

        JAXBElement<String> Rfc = new JAXBElement (new QName("http://Ecodex.WS.Model/2011/CFDI","RFC"), JAXBElement.class, rfc);
        JAXBElement<String> Token = new JAXBElement(new QName("http://Ecodex.WS.Model/2011/CFDI","Token"),JAXBElement.class,token);
        JAXBElement<String> Uuid = new JAXBElement(new QName("http://Ecodex.WS.Model/2011/CFDI","UUID"),JAXBElement.class,uuid);

        SolicitudObtenerQRTimbrado solicitudqr = new SolicitudObtenerQRTimbrado();
        solicitudqr.setRFC(Rfc);
        solicitudqr.setToken(Token);
        solicitudqr.setTransaccionID(transactionID);
        solicitudqr.setUUID(Uuid);

        RespuestaObtenerQRTimbrado respuestaqr = puertoX.obtenerQRTimbrado(solicitudqr);
        return respuestaqr;
   }


}
