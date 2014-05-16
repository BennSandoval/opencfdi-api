package com.reemmy.api;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.google.appengine.repackaged.org.codehaus.jackson.map.ObjectMapper;
import com.reemmy.api.models.database.PMF;
import com.reemmy.api.models.database.Receptor;
import com.reemmy.common.Constant;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: benn_sandoval
 * To change this template use File | Settings | File Templates.
 */
@Api(
        name = "receptores",
        version = "v1",
        description = "Ingresa y obtiene todos los emisores utilizados en facturas por usuario.",
        clientIds = {Constant.WEB_CLIENT_ID, Constant.ANDROID_CLIENT_ID, Constant.IOS_CLIENT_ID, Constant.EXPLORE_CLIENT_ID},
        audiences = {Constant.ANDROID_AUDIENCE}
)
public class ReceptorV1 {

    @ApiMethod(name = "obtiene", httpMethod = "GET")
    public List<Comprobante.Receptor> obtiene(@Nullable @Named("origen") String origen,
                                              @Nullable @Named("limite") String limite,
                                              User user) throws OAuthRequestException, IOException {

        if (user != null) {

            PersistenceManager pm = getPersistenceManager();
            Query query = pm.newQuery(Receptor.class);
            if (user != null) {
                query.setFilter("user == userParam");
                query.declareParameters("com.google.appengine.api.users.User userParam");
            } else {
                throw new OAuthRequestException("usuario invalido.");
            }

            if (origen == null) {
                origen = "0";
            }
            if (limite == null) {
                limite = "10";
            }
            query.setRange(new Long(origen), new Long(limite));

            List<Receptor> receptores = (List<Receptor>) pm.newQuery(query).execute(user);
            List<Comprobante.Receptor> resultado = new ArrayList<Comprobante.Receptor>();

            for(Receptor receptor:receptores){
                ObjectMapper mapper = new ObjectMapper();
                Comprobante.Receptor entry  = mapper.readValue(receptor.getEstructura().getValue(), Comprobante.Receptor.class);
                resultado.add(entry);
            }
            pm.close();
            return resultado;

        } else {
            throw new OAuthRequestException("usuario invalido.");
        }
    }

    @ApiMethod(name = "ingresa", httpMethod = "POST")
    public Comprobante.Receptor ingresa(Comprobante.Receptor receptor, User user) throws OAuthRequestException, IOException {

        if (user != null) {

            PersistenceManager pm = getPersistenceManager();
            Query query = pm.newQuery(Receptor.class);
            query.setFilter("rfc == rfcParam && user == userParam");
            query.declareParameters("String rfcParam, com.google.appengine.api.users.User userParam");
            query.setRange(0, new Long(1));
            List<Receptor> receptoresDatabase = (List<Receptor>) pm.newQuery(query).execute(receptor.getRfc(),user);

            if (receptoresDatabase.size()==0) {
                ObjectMapper mapper = new ObjectMapper();
                Receptor entryReceptor = new Receptor();

                entryReceptor.setRfc(receptor.getRfc());
                entryReceptor.setNombre(receptor.getNombre());
                entryReceptor.setUser(user);
                entryReceptor.setEstructura(new Text(mapper.writeValueAsString(receptor)));

                pm = getPersistenceManager();
                pm.makePersistent(entryReceptor);
                pm.close();

                return receptor;
            } else {
                pm.close();
                throw new NullPointerException("cliente ya ingresado");
            }
        } else {
            throw new OAuthRequestException("usuario invalido.");
        }
    }

    private static PersistenceManager getPersistenceManager() {
        return PMF.get().getPersistenceManager();
    }
}
