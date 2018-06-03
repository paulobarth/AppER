package br.com.newtechs.er.dao;

/**
 * Created by Paulo on 31/10/2015.
 */
public class Report {

    private Integer ID;
    private String codigo;
    private String userID;
    private String nome;
    // Tipo da origem do dado, se CSV , Google Drive, EndPoint
    private String tipoOrigem;

    // Identificação direta da origem, como caminho do arquivo, ID da planilha do GoogleDrive
    private String origemDados;

    // A origem pode ter vários formatos, como bloco de notas, planilha excel.
    private String formatoOrigem;

    private Boolean shared = false;
    private String tipoAtz;
    private String layout;
    private String visivel;
    private String userLoginCol;

    public Integer getID () { return ID; }

    public void setID (Integer ID) { this.ID = ID; }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo (String codigo) {
        this.codigo = codigo;
    }

    public String getUserID() {return userID; }

    public void setUserID(String userID) { this.userID = userID; }

    public void setTipoOrigem (String tipoOrigem) {
        this.tipoOrigem = tipoOrigem;
    }

    public String getTipoOrigem () {
        return tipoOrigem;
    }

    public void setOrigemDados (String origemDados) {
        this.origemDados = origemDados;
    }

    public String getOrigemDados () {
        return origemDados;
    }

    public String getNome() {
        return nome;
    }

    public void setNome (String nome) {
        this.nome = nome;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout (String layout) {
        this.layout = layout;
    }

    public String getVisivel() {
        return visivel;
    }

    public void setVisivel (String visivel) {
        this.visivel = visivel;
    }

    public String getUserLoginCol() { return userLoginCol; }

    public void setUserLoginCol(String userLoginCol) { this.userLoginCol = userLoginCol; }

    public String getFormatoOrigem() {
        return formatoOrigem;
    }

    public void setFormatoOrigem(String formatoOrigem) {
        this.formatoOrigem = formatoOrigem;
    }

    public String getTipoAtz() {
        return tipoAtz;
    }

    public void setTipoAtz(String tipoAtz) {
        this.tipoAtz = tipoAtz;
    }

    public Boolean getShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }


}
