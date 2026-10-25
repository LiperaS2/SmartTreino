package dao;

public class Usuario {
    private String nome;
    private String telefone;
    private String email;
    private String senha;
    private String termos;
    
    // Gera a capsula
    public Usuario() {
    }
    
    // para armazenamento (Get= transportar)
    public String getNome() {
        return nome;
    }
    
   // para armazenamento (Set= vai guardar)
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTermos() {
        return termos;
    }

    public void setTermos(String termos) {
        this.termos = termos;
    }
   
}
