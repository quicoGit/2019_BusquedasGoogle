package com.plumbaria.e_10_1_busquedasengoogle

import android.app.ProgressDialog
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun buscar(view: View){
        var palabras= EditText01.text.toString()
        TextView01.append(palabras + "--")
        BuscarGoogle().execute(palabras)
    }

    fun resultadosGoogle(palabras : String): String {
        var pagina = ""
        var devuelve = ""

        var url = URL("https://www.google.es/search?hl=es&q=\""
                + URLEncoder.encode(palabras, "UTF-8") + "\"")

        var conexion = url.openConnection() as HttpURLConnection
        conexion.setRequestProperty("User-Agent",
            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)")
        if(conexion.responseCode==HttpURLConnection.HTTP_OK){
            var reader = BufferedReader(InputStreamReader(conexion.getInputStream()))
            var linea = reader.readLine()
            while(linea !=null){
                pagina += linea
                Log.d("222",linea)
                linea = reader.readLine()

            }
            reader.close()
            var ini = pagina.indexOf("Aproximadamente")
            if(ini != -1){
                var fin = pagina.indexOf("",ini +16)
                devuelve = pagina.substring(ini+16,fin)
            } else {
                devuelve = "no encontrado"
            }
        } else {
            TextView01.append("Error: "+conexion.responseMessage + "\n")
        }
        conexion.disconnect()
        return devuelve
    }


    inner class BuscarGoogle : AsyncTask<String,Void,String>() {


        lateinit var progreso: ProgressDialog

        override fun onPreExecute() {
            progreso = ProgressDialog(this@MainActivity)
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            progreso.setMessage("Accediendo a Google...")
            progreso.setCancelable(false)
            progreso.show()
        }

        override fun doInBackground(vararg p0: String?): String {
            try {
                return resultadosGoogle(p0[0].toString())
            } catch (e: Exception) {
                cancel(true)
                Log.e("HTTP", e.message, e)
                return null.toString()
            }
        }

        override fun onPostExecute(result: String?) {
            progreso.dismiss()
            TextView01.append(result + "\n")
        }

        override fun onCancelled() {
            progreso.dismiss()
            TextView01.append("Error al conectar\n")
        }
    }
}
