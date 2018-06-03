package br.com.newtechs.er.dao;

/**
 * Created by Paulo on 20/05/2016.
 */
public class UserLogin {

    private Integer ID;
    private String user;
    private String password;
    private String email;
    private String dataUltAcesso;
    private String companyName;

    public UserLogin () {

    }

    public Integer getID () { return ID; }

    public void setID (Integer ID) { this.ID = ID; }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() { return password; }

    public void setPassword (String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail (String email) {
        this.email = email;
    }

    public String getDataUltAcesso() {
        return dataUltAcesso;
    }

    public void setDataUltAcesso(String dataUltAcesso) {
        this.dataUltAcesso = dataUltAcesso;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}