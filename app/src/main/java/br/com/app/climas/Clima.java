package br.com.app.climas;

import android.graphics.Bitmap;

public class Clima {
    private String cidade, pais, umidade, vento, nuvem, temperatura;
    private Bitmap imagemClima, imagemPais;

    public Clima() {}

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getCidade() {
        return cidade;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getPais() {
        return pais;
    }

    public void setUmidade(String umidade) {
        this.umidade = umidade;
    }

    public String getUmidade() {
        return umidade;
    }

    public void setVento(String vento) {
        this.vento = vento;
    }

    public String getVento() {
        return vento;
    }

    public void setNuvem(String nuvem) {
        this.nuvem = nuvem;
    }

    public String getNuvem() {
        return nuvem;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }

    public String getTemperatura() {
        return temperatura;
    }

    public void setImagemClima(Bitmap imagemClima) {
        this.imagemClima = imagemClima;
    }

    public Bitmap getImagemClima() {
        return imagemClima;
    }

    public void setImagemPais(Bitmap imagemPais) {
        this.imagemPais = imagemPais;
    }

    public Bitmap getImagemPais() {
        return imagemPais;
    }
}
