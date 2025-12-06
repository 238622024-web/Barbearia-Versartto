package com.example.n2app_ex3_;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfissionalDetailActivity extends AppCompatActivity {

    public static final String PROFESSIONAL_NAME_KEY = "PROFESSIONAL_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profissional_detail);

        // Configura o botão de voltar na barra de ferramentas
        findViewById(R.id.toolbarProfessionalDetail).setOnClickListener(v -> finish());

        // Encontra os componentes da tela
        TextView nameToolbar = findViewById(R.id.professional_name_toolbar);
        ImageView professionalImage = findViewById(R.id.professional_image);
        TextView professionalDescription = findViewById(R.id.professional_description);

        // Pega o nome da PROFISSÃO enviado pela tela anterior
        String professionalName = getIntent().getStringExtra(PROFESSIONAL_NAME_KEY);

        // Verifica se o nome foi recebido e carrega as informações corretas
        if (professionalName != null && !professionalName.isEmpty()) {
            nameToolbar.setText(professionalName);

            switch (professionalName) {
                case "Cabeleireiro":
                    professionalImage.setImageResource(R.drawable.profissional1);
                    professionalDescription.setText("Especialista em cortes modernos e coloração. Com 10 anos de experiência, transforma cabelos com precisão e arte.");
                    break;

                case "Maquiadora":
                    professionalImage.setImageResource(R.drawable.maqueadora);
                    professionalDescription.setText("Artista da maquiagem, realçando a beleza natural para eventos e ocasiões especiais. Expert em contorno e pele iluminada.");
                    break;

                case "Barbeiro":
                    professionalImage.setImageResource(R.drawable.lucas);
                    professionalDescription.setText("Mestre das tesouras e navalhas. Oferece cortes clássicos, modernos e um barbear impecável, sempre com um toque de estilo.");
                    break;

                case "Manicure":
                    professionalImage.setImageResource(R.drawable.isabella);
                    professionalDescription.setText("Cuida das suas unhas com perfeição. Dos tratamentos de spa às últimas tendências em nail art, suas mãos estarão em boas mãos.");
                    break;

                default:
                    // Caso padrão se o nome não corresponder a nenhum caso conhecido
                    professionalImage.setImageResource(R.drawable.ic_person); // Um ícone genérico
                    professionalDescription.setText("Informações do profissional não encontradas.");
                    break;
            }
        }
    }
}