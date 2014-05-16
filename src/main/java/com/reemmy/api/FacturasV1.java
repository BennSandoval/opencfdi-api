package com.reemmy.api;

import com.ecodex.cancela.wsdl.CancelacionesCancelaMultipleFallaServicioFaultFaultMessage;
import com.ecodex.cancela.wsdl.CancelacionesCancelaMultipleFallaSesionFaultFaultMessage;
import com.ecodex.repositorio.wsdl.RepositorioCancelaComprobanteFallaServicioFaultFaultMessage;
import com.ecodex.repositorio.wsdl.RepositorioCancelaComprobanteFallaSesionFaultFaultMessage;
import com.ecodex.repositorio.wsdl.RepositorioCancelaComprobanteFallaValidacionFaultFaultMessage;
import com.ecodex.seguridad.wsdl.SeguridadObtenerTokenFallaServicioFaultFaultMessage;
import com.ecodex.seguridad.wsdl.SeguridadObtenerTokenFallaSesionFaultFaultMessage;
import com.ecodex.timbrado.wsdl.*;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.google.appengine.repackaged.com.google.api.client.util.StringUtils;
import com.google.appengine.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.appengine.repackaged.org.codehaus.jackson.annotate.JsonAutoDetect;
import com.google.appengine.repackaged.org.codehaus.jackson.annotate.JsonMethod;
import com.google.appengine.repackaged.org.codehaus.jackson.map.DeserializationConfig;
import com.google.appengine.repackaged.org.codehaus.jackson.map.ObjectMapper;
import com.google.appengine.repackaged.org.codehaus.jackson.map.annotate.JsonSerialize;
import com.reemmy.api.models.*;
import com.reemmy.api.models.database.Factura;
import com.reemmy.api.models.database.PMF;
import com.reemmy.api.models.database.Usuario;
import com.reemmy.common.Constant;
import com.reemmy.common.Seguridad;
import com.reemmy.common.Timbra;
import mx.bigdata.sat.cfdi.CFDv32;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante;
import mx.bigdata.sat.security.KeyLoaderEnumeration;
import mx.bigdata.sat.security.factory.KeyLoaderFactory;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.xml.transform.TransformerFactory;
import java.io.*;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.*;
/**
 * Created with IntelliJ IDEA.
 * User: benn_sandoval
 * To change this template use File | Settings | File Templates.
 */
@Api(
        name = "facturas",
        version = "v1",
        description = "Facturacion y cancelacion de CFDI",
        clientIds = {Constant.WEB_CLIENT_ID, Constant.ANDROID_CLIENT_ID, Constant.IOS_CLIENT_ID, Constant.EXPLORE_CLIENT_ID},
        audiences = {Constant.ANDROID_AUDIENCE}
)
public class FacturasV1 {

    @ApiMethod(name = "ingresa", httpMethod = "POST")
    public RespuestaComprobante timbrar(PeticionTimbrado peticion, User potencialUser)
            throws OAuthRequestException,
            UnsupportedEncodingException,
            SeguridadObtenerTokenFallaServicioFaultFaultMessage,
            SeguridadObtenerTokenFallaSesionFaultFaultMessage,
            TimbradoTimbraXMLFallaSesionFaultFaultMessage,
            TimbradoTimbraXMLFallaServicioFaultFaultMessage,
            TimbradoTimbraXMLFallaValidacionFaultFaultMessage,
            TimbradoObtenerQRTimbradoFallaSesionFaultFaultMessage,
            TimbradoObtenerQRTimbradoFallaServicioFaultFaultMessage,
            TimbradoObtenerQRTimbradoFallaValidacionFaultFaultMessage {

        PersistenceManager pm = getPersistenceManager();
        try {
            Query query = pm.newQuery(Usuario.class);
            if (potencialUser != null) {
                query.setFilter("user == userParam");
                query.declareParameters("com.google.appengine.api.users.User userParam");
            } else {
                throw new OAuthRequestException("Usuario invalido.");
            }
            query.setRange(0, (long) 1);

            List<Usuario> users = (List<Usuario>) pm.newQuery(query).execute(potencialUser);

            if (users.size()==0) {
                throw new OAuthRequestException("Usuario invalido.");
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
            mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
            mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
            try {
                String estructuraComprobante = mapper.writeValueAsString(peticion.getComprobante());
                estructuraComprobante=estructuraComprobante.replace("{}","null");
                peticion.setComprobante(mapper.readValue(estructuraComprobante, Comprobante.class));
            } catch (IOException e) {
                e.printStackTrace();
            }


            Usuario user = users.get(0);
            RespuestaComprobante respuesta = new RespuestaComprobante();

            Comprobante comprobante = peticion.getComprobante();
            if(comprobante==null){
                throw new NullPointerException("documento vacio.");
            }

            if(comprobante.getEmisor()==null){
                throw new NullPointerException("emisor vacio.");
            }
            comprobante.setAddenda(null);

            CFDv32 cfd;
            try {
                cfd = new CFDv32(comprobante,"mx.bigdata.sat.cfdi.v32.schema");
            } catch (Exception e) {
                e.printStackTrace();
                throw new NullPointerException("no es posible generar el objeto comprobante.");
            }
            TransformerFactory factory = TransformerFactory.newInstance(
                    "org.apache.xalan.processor.TransformerFactoryImpl",
                    Thread.currentThread().getContextClassLoader());
            cfd.setTransformerFactory(factory);

            cfd.addNamespace("http://www.sat.gob.mx/TimbreFiscalDigital","tdf");
            cfd.addNamespace("http://www.sat.gob.mx/ventavehiculos","ventavehiculos");
            cfd.addNamespace("http://www.sat.gob.mx/iedu","iedu");
            cfd.addNamespace("http://www.sat.gob.mx/terceros","terceros");

            cfd.addNamespace("http://www.sat.gob.mx/registrofiscal","registrofiscal");
            cfd.addNamespace("http://www.sat.gob.mx/nomina", "nomina");
            cfd.addNamespace("http://www.sat.gob.mx/ecc","ecc");
            cfd.addNamespace("http://www.sat.gob.mx/donat","donat");
            cfd.addNamespace("http://www.sat.gob.mx/divisas","divisas");
            cfd.addNamespace("http://www.sat.gob.mx/implocal","implocal");
            cfd.addNamespace("http://www.sat.gob.mx/leyendasFiscales","leyendasFisc");
            cfd.addNamespace("http://www.sat.gob.mx/pfic","pfic");
            cfd.addNamespace("http://www.sat.gob.mx/psgcfdsp","psgcfdsp");
            cfd.addNamespace("http://www.sat.gob.mx/TuristaPasajeroExtranjero","tpe");
            cfd.addNamespace("http://www.sat.gob.mx/spei","spei");
            cfd.addNamespace("http://www.sat.gob.mx/psgcfdsp","psgcfdsp");
            cfd.addNamespace("http://www.sat.gob.mx/psgecfd","psgecfd");


            InputStream keyFile = getClass().getResourceAsStream("/certs/SUL010720JN8_1210231422S.key");
            InputStream cerFile = getClass().getResourceAsStream("/certs/SUL010720JN8.cer");

            PrivateKey key = KeyLoaderFactory.createInstance(
                    KeyLoaderEnumeration.PRIVATE_KEY_LOADER,
                    keyFile,
                    "12345678a").getKey();

            X509Certificate cert = KeyLoaderFactory.createInstance(
                    KeyLoaderEnumeration.PUBLIC_KEY_LOADER,
                    cerFile).getKey();

            try {
                cfd.sellar(key, cert);
            } catch (Exception e) {
                e.printStackTrace();
                throw new NullPointerException("no es posible sellar el comprobante.");
            }

            try{
                cfd.validar();
            }catch(Exception e){
                e.printStackTrace();
                throw new NullPointerException("xml invalido.");
            }

            try{
                cfd.verificar();
            }catch(Exception e){
                e.printStackTrace();
                throw new NullPointerException("el xml no ha pasado la verificacion.");
            }


            ByteArrayOutputStream salida = new ByteArrayOutputStream();

            try{
                cfd.guardar(salida);
            }catch(Exception e){
                e.printStackTrace();
                throw new NullPointerException("no se pudo obtener el XML de la factura sin timbrar.");
            }

            String XML;
            try{
                XML = new String(salida.toByteArray(), "UTF-8");
                respuesta.setComprobanteXML(XML);
                respuesta.setCadenaOriginal(cfd.getCadenaOriginal());
            }catch(Exception e){
                e.printStackTrace();
                throw new NullPointerException("no se pudo obtener la cadena original.");
            }

            Random random = new Random();
            Integer trsID = random.nextInt();
            Long ID = Long.parseLong(String.valueOf(trsID));

            Seguridad seguridad = new Seguridad();
            String token = seguridad.construirToken(ID, comprobante.getEmisor().getRfc(), Constant.integrador);

            Timbra timbrar = new Timbra();
            RespuestaTimbraXML respuestaTimbraXML = timbrar.TimbraXML(comprobante.getEmisor().getRfc(), token, XML);

            String XMLTimbrado = respuestaTimbraXML.getComprobanteXML().getValue().getDatosXML().getValue();
            respuesta.setComprobanteXMLTimbrado(XMLTimbrado);

            CFDv32 cfdTimbrado;
            try{
                cfdTimbrado = new CFDv32(new ByteArrayInputStream(XMLTimbrado.getBytes()),"mx.bigdata.sat.cfdi.v32.schema");
                respuesta.setComprobanteTimbrado((Comprobante)cfdTimbrado.getComprobante().getComprobante());
            }catch(Exception e){
                e.printStackTrace();
                throw new NullPointerException("no se pudo obtener el comprobante timbrado. "+e.getMessage());
            }

            respuesta.setComprobante(comprobante);
            respuesta.setValid(true);

            RespuestaObtenerQRTimbrado respuestaQR = timbrar.obtenerQR(respuesta.getComprobanteTimbrado().getEmisor().getRfc(),token,respuesta.getComprobanteTimbrado().getComplemento().getTimbreFiscalDigital().getUUID());

            StringBuilder sb = new StringBuilder();
            sb.append("data:image/bmp;base64,");
            sb.append(StringUtils.newStringUtf8(Base64.encodeBase64(respuestaQR.getQR().getValue().getImagen().getValue(), false)));

            respuesta.setCodeQrBmpBase64(sb.toString());
            respuesta.setCodeQrBmpByte(respuestaQR.getQR().getValue().getImagen().getValue());

            Factura entryComprobante =  new Factura();
            entryComprobante.setUser(potencialUser);
            entryComprobante.setFecha(respuesta.getComprobanteTimbrado().getFecha());
            entryComprobante.setReceptor(respuesta.getComprobanteTimbrado().getReceptor().getRfc());
            entryComprobante.setUUID(respuesta.getComprobanteTimbrado().getComplemento().getTimbreFiscalDigital().getUUID());
            try {
                entryComprobante.setEstructura(new Text(mapper.writeValueAsString(respuesta.getComprobanteTimbrado())));
            } catch (IOException e) {
                e.printStackTrace();
                respuesta.getErrores().add("no se pudo ingresar la factura.");
            }
            entryComprobante.setQr(new Text(respuesta.getCodeQrBmpBase64()));
            entryComprobante.setByteQr(new Blob(respuesta.getCodeQrBmpByte()));
            pm.makePersistent(entryComprobante);

            ReceptorV1 receptorV1 = new ReceptorV1();
            try {
                receptorV1.ingresa(respuesta.getComprobanteTimbrado().getReceptor(),potencialUser);
            } catch (Exception e) {
                e.printStackTrace();
                respuesta.getErrores().add("no se pudo ingresar el receptor.");
            }

            ConceptoV1 conceptoV1 = new ConceptoV1();
            for(Comprobante.Conceptos.Concepto concepto:respuesta.getComprobanteTimbrado().getConceptos().getConcepto()) {
                try {
                    conceptoV1.ingresa(concepto,potencialUser);
                } catch (Exception e) {
                    e.printStackTrace();
                    respuesta.getErrores().add("no se pudo ingresar el concepto.");
                }
            }

            if(user.getGcmKeys().size()>0){
                List<Map.Entry<String,String>> mensajes = new ArrayList<Map.Entry<String, String>>();
                mensajes.add(new AbstractMap.SimpleEntry<String,String>("Accion","Facturacion"));
                mensajes.add(new AbstractMap.SimpleEntry<String,String>("UUID",entryComprobante.getUUID()));
                Constant.sendMessageToDevice("Factura "+entryComprobante.getUUID()+" realizada correctamente.",user.getGcmKeys());
            }

            return respuesta;
        } finally {
            pm.close();
        }
    }

    @ApiMethod(name = "cancela", httpMethod = "DELETE")
    public RespuestaCancela cancela(@Named("UUID") String uuid, User potencialUser) throws OAuthRequestException, UnsupportedEncodingException, SeguridadObtenerTokenFallaServicioFaultFaultMessage, SeguridadObtenerTokenFallaSesionFaultFaultMessage, TimbradoCancelaTimbradoFallaValidacionFaultFaultMessage, TimbradoCancelaTimbradoFallaServicioFaultFaultMessage, TimbradoCancelaTimbradoFallaSesionFaultFaultMessage, RepositorioCancelaComprobanteFallaServicioFaultFaultMessage, RepositorioCancelaComprobanteFallaSesionFaultFaultMessage, RepositorioCancelaComprobanteFallaValidacionFaultFaultMessage, CancelacionesCancelaMultipleFallaServicioFaultFaultMessage, CancelacionesCancelaMultipleFallaSesionFaultFaultMessage {

        PersistenceManager pm = getPersistenceManager();
        try {
            Query query = pm.newQuery(Usuario.class);
            if (potencialUser != null) {
                query.setFilter("user == userParam");
                query.declareParameters("com.google.appengine.api.users.User userParam");
            } else {
                throw new OAuthRequestException("Usuario invalido.");
            }
            query.setRange(0, new Long(1));

            List<Usuario> usersInfo = (List<Usuario>) pm.newQuery(query).execute(potencialUser);
            if (potencialUser != null && usersInfo.size()>0) {

                Usuario user = usersInfo.get(0);

                Random random = new Random();
                Integer trsID = random.nextInt();
                Long ID = Long.parseLong(String.valueOf(trsID));

                Seguridad seguridad = new Seguridad();
                String token = seguridad.construirToken(ID, user.getRfc(), Constant.integrador);

                Timbra timbrar = new Timbra();
                RespuestaCancelaTimbrado respuestaCancela = timbrar.cancela(user.getRfc(), token, uuid);

                RespuestaCancela respuesta = new RespuestaCancela();
                respuesta.setCancelado(respuestaCancela.isCancelada());
                respuesta.setUUID(uuid);

                if(user.getGcmKeys().size()>0){
                    List<Map.Entry<String,String>> mensajes = new ArrayList<Map.Entry<String, String>>();
                    mensajes.add(new AbstractMap.SimpleEntry<String,String>("Accion","Cancelacion"));
                    mensajes.add(new AbstractMap.SimpleEntry<String,String>("UUID",uuid));

                    Constant.sendMessageToDevice("Factura cancelada correctamente "+uuid,user.getGcmKeys(),mensajes);
                }

                return respuesta;

            } else {
                throw new OAuthRequestException("Usuario invalido.");
            }
        } finally {
            pm.close();
        }
    }

    @ApiMethod(name = "obtiene", httpMethod = "GET")   //path = "obtiene/{id}"
    public List<RespuestaComprobante> obtiene(@Nullable  @Named("id") int id,
                                              @Nullable @Named("origen") String origen,
                                              @Nullable @Named("limite") String limite,
                                              User user) throws OAuthRequestException, IOException {

        if (user != null) {

            PersistenceManager pm = getPersistenceManager();
            try {
                Query query = pm.newQuery(Factura.class);
                if (user != null) {
                    query.setFilter("user == userParam");
                    query.declareParameters("com.google.appengine.api.users.User userParam");
                } else {
                    throw new OAuthRequestException("Usuario invalido.");
                }

                if (origen == null) {
                    origen = "0";
                }
                if (limite == null) {
                    limite = "10";
                }
                query.setRange(new Long(origen), new Long(limite));

                List<Factura> facturas = (List<Factura>) pm.newQuery(query).execute(user);
                List<RespuestaComprobante> resultado = new ArrayList<RespuestaComprobante>();

                for(Factura factura:facturas){
                    ObjectMapper mapper = new ObjectMapper();
                    RespuestaComprobante entry = new RespuestaComprobante();
                    Comprobante comprobante  = mapper.readValue(factura.getEstructura().getValue(), Comprobante.class);
                    entry.setComprobanteTimbrado(comprobante);
                    entry.setCodeQrBmpByte(factura.getByteQr().getBytes());
                    entry.setCodeQrBmpBase64(factura.getQr().getValue());
                    resultado.add(entry);
                }
                return resultado;

            } finally {
                pm.close();
            }

        } else {
            throw new OAuthRequestException("Usuario invalido.");
        }
    }

    private static PersistenceManager getPersistenceManager() {
        return PMF.get().getPersistenceManager();
    }
}
