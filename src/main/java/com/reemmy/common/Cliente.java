package com.reemmy.common;

import com.ecodex.cliente.wsdl.*;
import com.ecodex.seguridad.wsdl.*;
import com.sun.xml.ws.client.BindingProviderProperties;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: benn_sandoval
 * To change this template use File | Settings | File Templates.
 */
public class Cliente {

    public RespuestaRegistro registrar(Long trsID, String integrador, String rfcIntegrador, String idAltaEmisor, String rfcCliente, String razonSocial, String email) throws UnsupportedEncodingException, SeguridadObtenerTokenFallaServicioFaultFaultMessage, SeguridadObtenerTokenFallaSesionFaultFaultMessage, ClientesRegistrarFallaServicioFaultFaultMessage, ClientesRegistrarFallaValidacionFaultFaultMessage, ClientesRegistrarFallaSesionFaultFaultMessage {

        JAXBElement<String> rfcClienteElement=new JAXBElement(new QName("http://Ecodex.WS.Model/2011/CFDI","RFC"),JAXBElement.class,rfcCliente);
        JAXBElement<String> razonSocialElement = new JAXBElement(new QName("http://Ecodex.WS.Model/2011/CFDI","RazonSocial"),JAXBElement.class,razonSocial);
        JAXBElement<String> emailElement = new JAXBElement(new QName("http://Ecodex.WS.Model/2011/CFDI","CorreoElectronico"),JAXBElement.class,email);

        AltaEmisor emisor= new AltaEmisor();
        emisor.setRFC(rfcClienteElement);
        emisor.setRazonSocial(razonSocialElement);
        emisor.setCorreoElectronico(emailElement);

        Seguridad seguridad= new Seguridad();
        SolicitudRegistroCliente registroSolicitud = new SolicitudRegistroCliente();
        String token = seguridad.construirTokenAlta(trsID, integrador, rfcIntegrador, idAltaEmisor);

        JAXBElement<AltaEmisor> altaEmisorElement = new JAXBElement(new QName("http://Ecodex.WS.Model/2011/CFDI","Emisor"),JAXBElement.class,emisor);
        JAXBElement<String> rfcElement=new JAXBElement(new QName("http://Ecodex.WS.Model/2011/CFDI","RfcIntegrador"),JAXBElement.class,rfcIntegrador);
        JAXBElement<String> tokenElement = new JAXBElement(new QName("http://Ecodex.WS.Model/2011/CFDI","Token"),JAXBElement.class,token);

        registroSolicitud.setToken(tokenElement);
        registroSolicitud.setEmisor(altaEmisorElement);
        registroSolicitud.setRfcIntegrador(rfcElement);
        registroSolicitud.setTransaccionID(trsID);

        Clientes_Service servicio = new Clientes_Service();
        Clientes puertoX = servicio.getPuertoClientes();

        ((BindingProvider)puertoX).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, 60000);

        RespuestaRegistro respuesta = puertoX.registrar(registroSolicitud);
        return respuesta;

    }

    public RespuestaEstatusCuenta estatus(String rfc, String token, Long trsID) throws ClientesEstatusCuentaFallaServicioFaultFaultMessage, ClientesEstatusCuentaFallaSesionFaultFaultMessage {

        SolicitudEstatusCuenta estatusSolicitud = new SolicitudEstatusCuenta();
        JAXBElement<String> rfcElement=new JAXBElement(new QName("http://Ecodex.WS.Model/2011/CFDI","RFC"),JAXBElement.class,rfc);
        JAXBElement<String> tokenElement = new JAXBElement(new QName("http://Ecodex.WS.Model/2011/CFDI","Token"),JAXBElement.class,token);

        estatusSolicitud.setToken(tokenElement);
        estatusSolicitud.setRFC(rfcElement);
        estatusSolicitud.setTransaccionID(trsID);

        Clientes_Service servicio = new Clientes_Service();
        Clientes puertoX = servicio.getPuertoClientes();

        ((BindingProvider)puertoX).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, 60000);

        RespuestaEstatusCuenta respuesta = puertoX.estatusCuenta(estatusSolicitud);
        return respuesta;
    }

    public RespuestaAsignacionTimbres asigna(String rfc, String token, int folios,Long trsID) throws ClientesAsignacionTimbresFallaServicioFaultFaultMessage, ClientesAsignacionTimbresFallaValidacionFaultFaultMessage, ClientesAsignacionTimbresFallaSesionFaultFaultMessage {

        SolicitudAsignacionTimbres asignacionSolicitud = new SolicitudAsignacionTimbres();
        JAXBElement<String> rfcElement=new JAXBElement(new QName("http://Ecodex.WS.Model/2011/CFDI","RFC"),JAXBElement.class,rfc);
        JAXBElement<String> tokenElement = new JAXBElement(new QName("http://Ecodex.WS.Model/2011/CFDI","Token"),JAXBElement.class,token);

        asignacionSolicitud.setRFC(rfcElement);
        asignacionSolicitud.setToken(tokenElement);
        asignacionSolicitud.setTransaccionId(trsID);
        asignacionSolicitud.setTimbresAsignar(folios);

        Clientes_Service servicio = new Clientes_Service();
        Clientes puertoX = servicio.getPuertoClientes();

        ((BindingProvider)puertoX).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, 60000);

        RespuestaAsignacionTimbres respuesta = puertoX.asignacionTimbres(asignacionSolicitud);
        return respuesta;

    }
}
