package com.reemmy.api.models;

/**
 * Created with IntelliJ IDEA.
 * User: benn_sandoval
 * To change this template use File | Settings | File Templates.
 */
import mx.bigdata.sat.cfdi.v32.schema.Comprobante;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: benn_sandoval
 * Date: 3/22/14
 * Time: 12:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class RespuestaComprobante {

    private Comprobante comprobante;
    private String comprobanteXML;
    private String cadenaOriginal;
    private boolean valid=false;
    private List<String> errores = new ArrayList<String>();

    private Comprobante comprobanteTimbrado;
    private String comprobanteXMLTimbrado;

    //private RespuestaTimbraXML timbraEcodex;
    //private RespuestaSellaTimbraXML sellaTimbraEcodex;

    private String codeQrBmpBase64;
    private byte[] codeQrBmpByte;

    public Comprobante getComprobante() {
        return comprobante;
    }

    public void setComprobante(Comprobante comprobante) {
        this.comprobante = comprobante;
    }

    public String getComprobanteXML() {
        return comprobanteXML;
    }

    public void setComprobanteXML(String comprobanteXML) {
        this.comprobanteXML = comprobanteXML;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<String> getErrores() {
        return errores;
    }

    public void setErrores(List<String> errores) {
        this.errores = errores;
    }

    public String getCadenaOriginal() {
        return cadenaOriginal;
    }

    public void setCadenaOriginal(String cadenaOriginal) {
        this.cadenaOriginal = cadenaOriginal;
    }

    public String getComprobanteXMLTimbrado() {
        return comprobanteXMLTimbrado;
    }

    public void setComprobanteXMLTimbrado(String comprobanteXMLTimbrado) {
        this.comprobanteXMLTimbrado = comprobanteXMLTimbrado;
    }

    public Comprobante getComprobanteTimbrado() {
        return comprobanteTimbrado;
    }

    public void setComprobanteTimbrado(Comprobante comprobanteTimbrado) {
        this.comprobanteTimbrado = comprobanteTimbrado;
    }

    public String getCodeQrBmpBase64() {
        return codeQrBmpBase64;
    }

    public void setCodeQrBmpBase64(String codeQrBmpBase64) {
        this.codeQrBmpBase64 = codeQrBmpBase64;
    }

    public byte[] getCodeQrBmpByte() {
        return codeQrBmpByte;
    }

    public void setCodeQrBmpByte(byte[] codeQrBmpByte) {
        this.codeQrBmpByte = codeQrBmpByte;
    }
}