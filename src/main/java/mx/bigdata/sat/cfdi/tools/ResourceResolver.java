package mx.bigdata.sat.cfdi.tools;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: benn_sandoval
 * Date: 3/20/14
 * Time: 4:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResourceResolver  implements LSResourceResolver {

    public LSInput resolveResource(String type, String namespaceURI,
                                   String publicId, String systemId, String baseURI) {

        // note: in this sample, the XSD's are expected to be in the root of the classpath
        //   /xsd/common/ventavehiculos/ventavehiculos.xsd
        //   ../common/ventavehiculos/ventavehiculos.xsd
        systemId= systemId.replace("../common/","/xsd/common/");
        systemId= systemId.replace("TimbreFiscalDigital.xsd","/xsd/v32/TimbreFiscalDigital.xsd");

        InputStream resourceAsStream = this.getClass().getResourceAsStream(systemId);
        return new Input(publicId, systemId, resourceAsStream);
    }

}