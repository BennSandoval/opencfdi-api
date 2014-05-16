package com.reemmy.api;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.google.appengine.repackaged.org.codehaus.jackson.map.ObjectMapper;
import com.reemmy.api.models.database.Concepto;
import com.reemmy.api.models.database.PMF;
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
        name = "conceptos",
        version = "v1",
        description = "Ingresa y obtiene todos los conceptos facturados por usuario.",
        clientIds = {Constant.WEB_CLIENT_ID, Constant.ANDROID_CLIENT_ID, Constant.IOS_CLIENT_ID, Constant.EXPLORE_CLIENT_ID},
        audiences = {Constant.ANDROID_AUDIENCE}
)
public class ConceptoV1 {

    @ApiMethod(name = "obtiene", httpMethod = "GET")
    public List<Comprobante.Conceptos.Concepto> obtiene(@Nullable @Named("origen") String origen,
                                              @Nullable @Named("limite") String limite,
                                              User user) throws OAuthRequestException, IOException {

        if (user != null) {
            PersistenceManager pm = getPersistenceManager();
            try {
                Query query = pm.newQuery(Concepto.class);
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

                List<Concepto> conceptos = (List<Concepto>) pm.newQuery(query).execute(user);
                List<Comprobante.Conceptos.Concepto> resultado = new ArrayList<Comprobante.Conceptos.Concepto>();

                for(Concepto concepto:conceptos){
                    ObjectMapper mapper = new ObjectMapper();
                    Comprobante.Conceptos.Concepto entry  = mapper.readValue(concepto.getEstructura().getValue(), Comprobante.Conceptos.Concepto.class);
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

    @ApiMethod(name = "ingresa", httpMethod = "POST")
    public Comprobante.Conceptos.Concepto ingresa(Comprobante.Conceptos.Concepto concepto, User user) throws OAuthRequestException, IOException {

        if (user != null) {
            PersistenceManager pm = getPersistenceManager();
            try {
                Query query = pm.newQuery(Concepto.class);
                query.setFilter("noIdentificacion == noIdentificacionParam && user == userParam");
                query.declareParameters("String noIdentificacionParam, com.google.appengine.api.users.User userParam");
                query.setRange(0, new Long(1));
                List<Concepto> conceptosDatabase = (List<Concepto>) pm.newQuery(query).execute(concepto.getNoIdentificacion(),user);

                if (conceptosDatabase.size()==0) {

                    ObjectMapper mapper = new ObjectMapper();
                    Concepto entryConcepto = new Concepto();

                    entryConcepto.setUser(user);
                    entryConcepto.setNoIdentificacion(concepto.getNoIdentificacion());
                    entryConcepto.setDescripcion(concepto.getDescripcion());
                    entryConcepto.setUnidad(concepto.getUnidad());
                    entryConcepto.setEstructura(new Text(mapper.writeValueAsString(concepto)));

                    pm.makePersistent(entryConcepto);
                    return concepto;

                } else {
                    throw new NullPointerException("Concepto existente.");
                }
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
