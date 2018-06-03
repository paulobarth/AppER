package br.com.newtechs.er;

/**
 * Created by Paulo on 12/05/2016.
 */
public class JsonDocER {

    private String doc = "";
    private Boolean bErro = false;
    private String sErro = "";
    // Tipo da origem do dado, se CSV , Google Drive, EndPoint
    private String tipoOrigem;
    // Identificação direta da origem, como caminho do arquivo, ID da planilha do GoogleDrive
    private String origemDados;
    // A origem pode ter vários formatos, como bloco de notas, planilha excel.
    private String formatoOrigem;

    public String getTipoOrigem() {
        return tipoOrigem;
    }

    public void setTipoOrigem(String tipoOrigem) {
        this.tipoOrigem = tipoOrigem;
    }

    public String getOrigemDados() {
        return origemDados;
    }

    public void setOrigemDados(String origemDados) {
        this.origemDados = origemDados;
    }

    public String getFormatoOrigem() {
        return formatoOrigem;
    }

    public void setFormatoOrigem(String formatoOrigem) {
        this.formatoOrigem = formatoOrigem;
    }

    public void setErro (String erro) {
        sErro = erro;
        bErro = true;
    }

    public Boolean hasErro () {
        return bErro;
    }

    public String getErro () {
        return sErro;
    }

    public void write (String dado) {
        doc += dado;
    }

    public String getJsonDoc () {
        return doc;
    }

    public void newLine() {
        doc += "\n";
    }

}
