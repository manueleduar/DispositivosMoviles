package mx.itesm.proyectofinal

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class ResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        this.title = "Resultados"
    }
}
