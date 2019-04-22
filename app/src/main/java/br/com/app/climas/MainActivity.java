package br.com.app.climas;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static android.os.StrictMode.*;

public class MainActivity extends AppCompatActivity {

    // Array de Clima(s)
    private ArrayList<Clima> climas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setThreadPolicy(new ThreadPolicy.Builder().permitAll().build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        climas = new ArrayList<>();

        try {

            // Lê arquivo local "city.list.min.json" ( Paises e Cidades do OpenWeatherMap [ http://bulk.openweathermap.org/sample/city.list.min.json.gz ] )
            InputStream is = this.getAssets().open("city.list.min.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, "UTF-8");

            //Array com nome de Paises e Cidades
            final JSONArray jsonCity = new JSONArray(jsonString);

            // List Países para Spinner de Países
            final List<String> paises = new ArrayList<>();
            // List Cidades para Spinner de Cidades
            final List<String> cidades = new ArrayList<>();

            for (int i = 0; i < jsonCity.length(); i++) {
                String country = jsonCity.getJSONObject(i).getString("country");
                if (!paises.contains(country)) {
                    paises.add(country);
                }
            }

            Collections.sort(paises);

            final ArrayAdapter<String> adapterCidades = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cidades);
            adapterCidades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            final Spinner spinnerCidades = findViewById(R.id.spinnerCidade);

            ArrayAdapter<String> adapterPaises = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paises);
            adapterPaises.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            final Spinner spinnerPaises = findViewById(R.id.spinnerPais);

            spinnerPaises.setAdapter(adapterPaises);
            spinnerPaises.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int index, long id) {
                    try {
                        String country = paises.get(index);
                        cidades.clear();

                        for (int i = 0; i < jsonCity.length() && country.length() > 0; i++) {
                            JSONObject obj = jsonCity.getJSONObject(i);
                            String cidade = obj.getString("name");
                            if (obj.getString("country").equals(country) && !cidades.contains(cidade)) {
                                cidades.add(cidade);
                            }
                        }

                        Collections.sort(cidades);
                        cidades.add(0, country.length() > 0 ? "Selecione uma cidade" : "Selecione um país"); //Primeiro Item

                        spinnerCidades.setAdapter(adapterCidades);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                }
            });

            ListView listClimas = findViewById(R.id.listClimas);
            final CustomAdapter customAdapter = new CustomAdapter();
            listClimas.setAdapter(customAdapter);

            // Adicionando 5 modelos padrões
            adicionarClima("BR", "Recife", customAdapter);
            adicionarClima("BR", "Rio de Janeiro", customAdapter);
            adicionarClima("BR", "Curitiba", customAdapter);
            adicionarClima("BR", "Sao Paulo", customAdapter);
            adicionarClima("BR", "Caruaru", customAdapter);
            // ------------------

            listClimas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> a, View v, final int position, long id) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                    adb.setTitle("Deletar?");
                    adb.setMessage("Você tem certeza que deseja deletar este item?");
                    adb.setNegativeButton("Cancelar", null);
                    adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            climas.remove(position);
                            customAdapter.notifyDataSetChanged();
                        }
                    });
                    adb.show();
                }
            });

            Button button = findViewById(R.id.button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (spinnerPaises.getSelectedItemPosition() > 0 && spinnerCidades.getSelectedItemPosition() > 0) {
                        String pais = spinnerPaises.getSelectedItem().toString();
                        String cidade = spinnerCidades.getSelectedItem().toString();

                        adicionarClima(pais, cidade, customAdapter);
                    }
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return MainActivity.this.climas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {
            view = getLayoutInflater().inflate(R.layout.customlayout, null);

            Clima climaAtual = MainActivity.this.climas.get(i);

            ImageView imagemClima = view.findViewById(R.id.imagemClima);
            imagemClima.setImageBitmap(climaAtual.getImagemClima());

            TextView cidade = view.findViewById(R.id.cidade);
            cidade.setText(climaAtual.getCidade());

            TextView pais = view.findViewById(R.id.pais);
            pais.setText(climaAtual.getPais());

            ImageView imagemBandeira = view.findViewById(R.id.bandeira);
            imagemBandeira.setImageBitmap(climaAtual.getImagemPais());

            TextView temp = view.findViewById(R.id.temperatura);
            temp.setText(climaAtual.getTemperatura().concat(" °C"));

            TextView u = view.findViewById(R.id.umidade);
            u.setText(String.format("Umidade: %s%%", climaAtual.getUmidade()));

            TextView v = view.findViewById(R.id.vento);
            v.setText(String.format("Vento: %s m/s", climaAtual.getVento()));

            TextView n = view.findViewById(R.id.nuvens);
            n.setText(String.format("Nuvens: %s%%", climaAtual.getNuvem()));

            return view;
        }
    }


    /**
     * Método para acessar os dados da cidade do país alvo
     * Retorna o JSON com os dados em formato de String
     * @param pais
     * @param cidade
     * */
    public String getClima(String pais, String cidade) {
        final String URL = "https://api.openweathermap.org/data/2.5/weather?q=" + cidade + "," + pais + "&appid=d91415cb9dc1f39c0153d04eca192982&units=metric";
        HttpsURLConnection con = null;
        try {
            URL u = new URL(URL);
            con = (HttpsURLConnection) u.openConnection();

            con.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            return sb.toString();


        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Adiciona um Clima ao ArrayList "climas" e atualiza o ListView com o novo clima
     *
     * @param pais
     * @param cidade
     * @param customAdapter
     */
    public void adicionarClima(final String pais, final String cidade, final CustomAdapter customAdapter){
        String climaJSON = getClima(pais, cidade);

        try {
            final JSONObject json = new JSONObject(climaJSON);

            final Clima c = new Clima();

            c.setCidade(cidade);
            c.setPais(pais);
            c.setNuvem(json.getJSONObject("clouds").getString("all"));
            c.setTemperatura(json.getJSONObject("main").getString("temp"));
            c.setUmidade(json.getJSONObject("main").getString("humidity"));
            c.setVento(json.getJSONObject("wind").getString("speed"));

            new Thread() {
                public void run() {
                    try {
                        // Obtem a imagem do clima a partir da URL
                        final Bitmap image = BitmapFactory.decodeStream(new URL("http://openweathermap.org/img/w/" + json.getJSONArray("weather").getJSONObject(0).getString("icon") + ".png").openStream());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                c.setImagemClima(image);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        // Obtem a bandeira do País a partir da URL
                        final Bitmap image = BitmapFactory.decodeStream(new URL("https://www.countryflags.io/" + pais + "/shiny/24.png").openStream());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                c.setImagemPais(image);
                                climas.add(c);
                                customAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
