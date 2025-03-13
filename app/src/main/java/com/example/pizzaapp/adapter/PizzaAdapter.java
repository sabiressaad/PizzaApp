package com.example.pizzaapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.example.pizzaapp.DetailPizzaActivity;
import com.example.pizzaapp.R;
import com.example.pizzaapp.classes.Produit;
import com.example.pizzaapp.service.ProduitService;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;


public class PizzaAdapter extends BaseAdapter {
    private List<Produit> produits;
    private LayoutInflater inflater;
    private Context context;
    private ProduitService produitService;
    private int lastPosition = -1;


    public PizzaAdapter(Context context, List<Produit> produits) {
        this.context = context;
        this.produits = produits;
        this.inflater = LayoutInflater.from(context);
        this.produitService = ProduitService.getInstance();
    }

    @Override
    public int getCount() {
        return produits.size();
    }

    @Override
    public Object getItem(int position) {
        return produits.get(position);
    }

    @Override
    public long getItemId(int position) {
        return produits.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_pizza, null);
            holder = new ViewHolder();

            holder.nomPizza = convertView.findViewById(R.id.nomPizza);
            holder.imagePizza = convertView.findViewById(R.id.imagePizza);
            holder.nbrIngredients = convertView.findViewById(R.id.nbrIngredients);
            holder.duree = convertView.findViewById(R.id.duree);
            holder.favoriteButton = convertView.findViewById(R.id.favoriteButton);
            holder.shareButton = convertView.findViewById(R.id.shareButton);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Produit currentProduit = produits.get(position);

        holder.nomPizza.setText(currentProduit.getNom());
        holder.imagePizza.setImageResource(currentProduit.getPhoto());
        holder.nbrIngredients.setText(String.valueOf(currentProduit.getNbrIngredients()));
        holder.duree.setText(currentProduit.getDuree());

        updateFavoriteButton(holder.favoriteButton, currentProduit);

        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(v, "scaleX", 1f, 1.2f, 1f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(v, "scaleY", 1f, 1.2f, 1f);
                ObjectAnimator rotation = ObjectAnimator.ofFloat(v, "rotation", 0f, 360f);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(scaleX, scaleY, rotation);
                animatorSet.setDuration(500);
                animatorSet.start();

                // Basculer l'état favori et mettre à jour l'affichage
                toggleFavorite(currentProduit);
                updateFavoriteButton(holder.favoriteButton, currentProduit);
                notifyDataSetChanged();

                Toast.makeText(context,
                        currentProduit.isFavori() ?
                                currentProduit.getNom() + " ajouté aux favoris" :
                                currentProduit.getNom() + " retiré des favoris",
                        Toast.LENGTH_SHORT).show();
            }
        });


        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharePizza(currentProduit);
            }
        });

        holder.imagePizza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorSet animatorSet = new AnimatorSet();
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(v, "scaleX", 1f, 1.1f, 1f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(v, "scaleY", 1f, 1.1f, 1f);
                animatorSet.playTogether(scaleX, scaleY);
                animatorSet.setDuration(300);
                animatorSet.start();
            }
        });


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailPizzaActivity.class);

                intent.putExtra("PIZZA_ID", currentProduit.getId());

                context.startActivity(intent);
            }
        });

        Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView;
    }

    private void updateFavoriteButton(ImageButton button, Produit produit) {
        if (produit.isFavori()) {
            button.setColorFilter(0xFFFFD700);
        } else {
            button.setColorFilter(0xFFCCCCCC);
        }
    }

    private void toggleFavorite(Produit produit) {
        produit.setFavori(!produit.isFavori());
        produitService.update(produit);
    }

    private void sharePizza(Produit produit) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Recette de " + produit.getNom());
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                produit.getNom() + "\n\n" +
                        "Temps de préparation: " + produit.getDuree() + "\n\n" +
                        "Ingrédients: " + produit.getDetailsIngred() + "\n\n" +
                        "Préparation: " + produit.getPreparation()
        );
        context.startActivity(Intent.createChooser(shareIntent, "Partager la recette via"));
    }

    private class ViewHolder {
        TextView nomPizza;
        ImageView imagePizza;
        TextView nbrIngredients;
        TextView duree;
        ImageButton favoriteButton;
        ImageButton shareButton;
    }
}