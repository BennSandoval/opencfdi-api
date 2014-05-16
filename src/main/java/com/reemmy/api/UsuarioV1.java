package com.reemmy.api;

import com.ecodex.cliente.wsdl.*;
import com.ecodex.seguridad.wsdl.SeguridadObtenerTokenFallaServicioFaultFaultMessage;
import com.ecodex.seguridad.wsdl.SeguridadObtenerTokenFallaSesionFaultFaultMessage;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.reemmy.api.models.database.GoogleCloudMessage;
import com.reemmy.api.models.database.PMF;
import com.reemmy.api.models.database.Usuario;
import com.reemmy.common.*;
import com.reemmy.api.models.*;
import com.reemmy.api.models.RespuestaEstatusCuenta;

import javax.annotation.Nullable;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: benn_sandoval
 * To change this template use File | Settings | File Templates.
 */
@Api(
        name = "usuarios",
        version = "v1",
        description = "Ingresa y obtiene el estatus de los usuarios con el PAC Ecodex.",
        clientIds = {Constant.WEB_CLIENT_ID, Constant.ANDROID_CLIENT_ID, Constant.IOS_CLIENT_ID, Constant.EXPLORE_CLIENT_ID},
        audiences = {Constant.ANDROID_AUDIENCE}
)
public class UsuarioV1 {

    @ApiMethod(name = "ingresa", httpMethod = "POST")
    public RespuestaEstatusCuenta registrar(PeticionUsuario datos, User potencialUser) throws OAuthRequestException, UnsupportedEncodingException, ClientesRegistrarFallaServicioFaultFaultMessage, SeguridadObtenerTokenFallaServicioFaultFaultMessage, SeguridadObtenerTokenFallaSesionFaultFaultMessage, ClientesRegistrarFallaSesionFaultFaultMessage, ClientesEstatusCuentaFallaServicioFaultFaultMessage, ClientesEstatusCuentaFallaSesionFaultFaultMessage {
        if (potencialUser != null) {
            PersistenceManager pm = getPersistenceManager();
            try {
                Query query = pm.newQuery(Usuario.class);
                query.setFilter("user == userParam");
                query.declareParameters("com.google.appengine.api.users.User userParam");
                query.setRange(0, new Long(1));

                List<Usuario> users = (List<Usuario>) pm.newQuery(query).execute(potencialUser);
                Usuario user =  new Usuario();
                if(users.size()==0){
                    Cliente cliente= new Cliente();
                    Random random = new Random();
                    Integer trsID = random.nextInt();
                    Long ID = Long.parseLong(String.valueOf(trsID));
                    com.ecodex.cliente.wsdl.RespuestaRegistro respuestaService = null;

                    try {
                        respuestaService = cliente.registrar(ID, Constant.integrador, Constant.rfcIntegrador, Constant.idAlta, datos.getRfc(), datos.getRazon(), potencialUser.getEmail());
                    } catch (ClientesRegistrarFallaValidacionFaultFaultMessage clientesRegistrarFallaValidacionFaultFaultMessage) {
                        if(clientesRegistrarFallaValidacionFaultFaultMessage.getMessage().contains("El emisor ya se encuentra dado de alta con un integrador.")){
                            user.setUser(potencialUser);
                            user.setRfc(datos.getRfc());
                            user.setRazon(datos.getRazon());
                            user.setEstatus("el emisor ya se encuentra dado de alta con un integrador, se dara de alta en el servidor");
                        } else {
                            throw new NullPointerException(clientesRegistrarFallaValidacionFaultFaultMessage.getMessage());
                        }
                    }

                    if(respuestaService!=null && respuestaService.getRespuesta()!=null){

                        user.setUser(potencialUser);
                        user.setRfc(datos.getRfc());
                        user.setRazon(datos.getRazon());
                        user.setEstatus("nuevo usuario dado de alta.");
                        pm.makePersistent(user);

                    }

                } else {
                    user = users.get(0);
                }

                if(datos.getGcmKey()!=null && datos.getGcmKey().length()>0){
                    boolean exist=false;
                    for(GoogleCloudMessage gcm:user.getGcmKeys()){
                        if(gcm.getGcmKey().equals(datos.getGcmKey())){
                            exist=true;
                        }
                    }
                    if(!exist){
                        GoogleCloudMessage gcm = new GoogleCloudMessage();
                        gcm.setGcmKey(datos.getGcmKey());
                        user.getGcmKeys().add(gcm);
                    }
                }

                RespuestaEstatusCuenta respuesta = estatus(null, user.getUser());
                if(user.getGcmKeys()!=null && user.getGcmKeys().size()>0){
                    List<Map.Entry<String,String>> mensajes = new ArrayList<Map.Entry<String, String>>();
                    mensajes.add(new AbstractMap.SimpleEntry<String,String>("Accion","Registro"));
                    Constant.sendMessageToDevice("Nuevo elemento de tu organizacion facturando: "+user.getUser().getEmail(),user.getGcmKeys());
                }

                return respuesta;
            } finally {
                pm.close();
            }
        } else {
            throw new OAuthRequestException("usuario invalido.");
        }
    }

    @ApiMethod(name = "obtiene", httpMethod = "GET")
    public RespuestaEstatusCuenta estatus(@Nullable @Named("gcmKey") String gcmKey, User potencialUser) throws OAuthRequestException, UnsupportedEncodingException, SeguridadObtenerTokenFallaServicioFaultFaultMessage, SeguridadObtenerTokenFallaSesionFaultFaultMessage, ClientesEstatusCuentaFallaServicioFaultFaultMessage, ClientesEstatusCuentaFallaSesionFaultFaultMessage, ClientesRegistrarFallaServicioFaultFaultMessage, ClientesRegistrarFallaSesionFaultFaultMessage {

        if (potencialUser != null) {
            RespuestaEstatusCuenta respuesta = new RespuestaEstatusCuenta();
            respuesta.setEmail(potencialUser.getEmail());
            PersistenceManager pm = getPersistenceManager();
            try {
                Query query = pm.newQuery(Usuario.class);
                query.setFilter("user == userParam");
                query.declareParameters("com.google.appengine.api.users.User userParam");
                query.setRange(0, new Long("10"));

                List<Usuario> users = (List<Usuario>) pm.newQuery(query).execute(potencialUser);

                if(users.size()>=1){
                    Usuario user = users.get(0);

                    if(gcmKey!=null&&gcmKey.length()>0){
                        boolean exist=false;
                        for(GoogleCloudMessage gcm:user.getGcmKeys()){
                            if(gcm.getGcmKey().equals(gcmKey)){
                                exist=true;
                            }
                        }
                        if(!exist){
                            GoogleCloudMessage gcm = new GoogleCloudMessage();
                            gcm.setGcmKey(gcmKey);
                            user.getGcmKeys().add(gcm);
                        }
                    }

                    respuesta.setRfc(user.getRfc());
                    respuesta.setRazon(user.getRazon());

                    Cliente cliente= new Cliente();
                    Random random = new Random();
                    Integer trsID = random.nextInt();
                    Long ID = Long.parseLong(String.valueOf(trsID));
                    Seguridad seguridad = new Seguridad();

                    String token = seguridad.construirToken(ID, user.getRfc(), Constant.integrador);
                    com.ecodex.cliente.wsdl.RespuestaEstatusCuenta respuestaService = cliente.estatus(user.getRfc(), token, ID);
                    if(respuestaService.getEstatus()!=null){

                        respuesta.setRfc(respuestaService.getEstatus().getValue().getRFC().getValue());
                        respuesta.setCodigo(respuestaService.getEstatus().getValue().getCodigo().getValue());
                        respuesta.setDescripcion(respuestaService.getEstatus().getValue().getDescripcion().getValue());
                        respuesta.setTimbresAsignados(Double.valueOf(respuestaService.getEstatus().getValue().getTimbresAsignados()));
                        respuesta.setTimbresDisponibles(Double.valueOf(respuestaService.getEstatus().getValue().getTimbresDisponibles()));
                        user.setTimbresAsignados(respuesta.getTimbresAsignados().intValue());
                        user.setTimbresDisponibles(respuesta.getTimbresDisponibles().intValue());

                    }

                    if(user.getGcmKeys()!=null && user.getGcmKeys().size()>0){
                        List<Map.Entry<String,String>> mensajes = new ArrayList<Map.Entry<String, String>>();
                        mensajes.add(new AbstractMap.SimpleEntry<String,String>("Accion","Registro"));
                        Constant.sendMessageToDevice("Usuario actualizado, folios disponibles: "+respuesta.getTimbresDisponibles(),user.getGcmKeys());
                    }

                } else {
                    throw new OAuthRequestException("Tu cuenta no esta asociado a ningun emisor "+potencialUser.getEmail()+".");
                }
            } finally {
                pm.close();
            }

            return respuesta;
        } else {
            throw new OAuthRequestException("usuario invalido.");
        }
    }

    private static PersistenceManager getPersistenceManager() {
        return PMF.get().getPersistenceManager();
    }

}
