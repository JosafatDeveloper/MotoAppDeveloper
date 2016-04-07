package developer.onlinesellers.mx.motoappdeveloper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import developer.onlinesellers.mx.motoappdeveloper.servicios.Servicios;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmap);
        Servicios ser = new Servicios(getApplicationContext());
    }
}
