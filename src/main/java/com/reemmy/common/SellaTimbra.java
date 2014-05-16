package com.reemmy.common;


import com.ecodex.comprobantes.wsdl.*;
import com.sun.xml.ws.client.BindingProviderProperties;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;


public class SellaTimbra {

    public RespuestaSellaTimbraXML SellaTimbraXML(String contenido, String rfc, String token, Integer trsID) throws ComprobantesSellaTimbraXMLFallaSesionFaultFaultMessage, ComprobantesSellaTimbraXMLFallaServicioFaultFaultMessage, ComprobantesSellaTimbraXMLFallaValidacionFaultFaultMessage {

        Comprobantes_Service servicioX = new Comprobantes_Service();
        Comprobantes puertoX = servicioX.getPuertoComprobantes();

        ((BindingProvider)puertoX).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, 60000);

        ComprobanteXML c =new ComprobanteXML();

        JAXBElement xml=new JAXBElement(new QName("http://Ecodex.WS.Model/2011/CFDI","DatosXML"),JAXBElement.class,contenido);
        c.setDatosXML(xml);
        JAXBElement<ComprobanteXML> xml2=new JAXBElement(new QName("http://Ecodex.WS.Model/2011/CFDI","ComprobanteXML"),JAXBElement.class,c);

        SolicitudSellaTimbraXML s = new SolicitudSellaTimbraXML();

        JAXBElement<String> Rfc=new JAXBElement(new QName("http://Ecodex.WS.Model/2011/CFDI","RFC"),JAXBElement.class,rfc);
        JAXBElement<String> Token=new JAXBElement(new QName("http://Ecodex.WS.Model/2011/CFDI","Token"),JAXBElement.class,token);

        s.setComprobanteXML(xml2);
        s.setRFC(Rfc);
        s.setToken(Token);
        s.setTransaccionID(trsID.longValue());

        RespuestaSellaTimbraXML resp = puertoX.sellaTimbraXML(s);

        return resp;

    }

}
