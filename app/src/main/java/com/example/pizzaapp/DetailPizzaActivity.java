package com.example.pizzaapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pizzaapp.classes.Produit;
import com.example.pizzaapp.service.ProduitService;

public class DetailPizzaActivity extends AppCompatActivity {

    private ProduitService produitService;
    private Produit produit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pizza);

        ImageView imageView = findViewById(R.id.imageView);
        TextView nomTextView = findViewById(R.id.nomTextView);
        TextView dureeTextView = findViewById(R.id.dureeTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        TextView ingredientsTextView = findViewById(R.id.ingredientsTextView);
        TextView preparationTextView = findViewById(R.id.preparationTextView);

        produitService = ProduitService.getInstance();

        int pizzaId = getIntent().getIntExtra("PIZZA_ID", -1);

        Log.d("DetailPizzaActivity", "ID reçu: " + pizzaId);

        if (pizzaId != -1) {
            produit = produitService.findById(pizzaId);

            if (produit != null) {
                Log.d("DetailPizzaActivity", "Pizza trouvée: " + produit.getNom());
                imageView.setImageResource(produit.getPhoto());
                nomTextView.setText(produit.getNom());
                dureeTextView.setText("Temps de préparation: " + produit.getDuree());
                descriptionTextView.setText(produit.getDescription());
                ingredientsTextView.setText(produit.getDetailsIngred());
                preparationTextView.setText(produit.getPreparation());
            } else {
                Log.e("DetailPizzaActivity", "Pizza non trouvée pour l'ID: " + pizzaId);
            }
        }

        findViewById(R.id.shareButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partagerPizza();
            }
        });

        findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmerSuppression();
            }
        });
    }

    private void partagerPizza() {
        if (produit != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Recette de " + produit.getNom());
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    produit.getNom() + "\n\n" +
                            "Description: " + produit.getDescription() + "\n\n" +
                            "Ingrédients: " + produit.getDetailsIngred() + "\n\n" +
                            "Préparation: " + produit.getPreparation()
            );
            startActivity(Intent.createChooser(shareIntent, "Partager la recette via"));
        }
    }

    private void confirmerSuppression() {
        if (produit != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Supprimer");
            builder.setMessage("Voulez-vous vraiment supprimer cette pizza?");
            builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    boolean success = produitService.delete(produit);

                    Log.d("DetailPizzaActivity", "Suppression: " + success);

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("DELETED", true);
                    setResult(RESULT_OK, resultIntent);

                    finish();
                }
            });
            builder.setNegativeButton("Non", null);
            builder.show();
        }
    }
}