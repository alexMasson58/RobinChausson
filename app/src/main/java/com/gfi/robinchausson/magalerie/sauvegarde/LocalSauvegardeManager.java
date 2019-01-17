package com.gfi.robinchausson.magalerie.sauvegarde;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexandre MASSON on 16/01/2019.
 * Manager poiur stocker en local nos photos
 * On ne peut stocker que des objets simples : String, int , float (nombre à virgule), etc...
 * Les image Bitmap n'en font pas partie, nous allons devoir les transformer avant de la sauvegarder
 * puis les "détransformer" quand on les charge de la mémoire, avant de pouvoir les utiliser
 */

public class LocalSauvegardeManager implements ISauvegardeManager {

    //la clé
    static final String CLE_SAUVEGARDE_SHARED_PREFS = "CLE_SAUVEGARDE_LISTE_PHOTOS";

    //On a besoin pour les sharedPref de connaitre l'activity qui veus 'en servir (le cerveau)
    private Activity activity;

    //permet n'importe où ailleurs dans le code, de créer un nouvel objet : LocalSauvegardeManager
    public LocalSauvegardeManager(Activity activity) {
        super();
        this.activity = activity;
    }

    //transforme une image bitmap en chaine (string)
    private String transformeBitmapEnString(Bitmap imageBitmap) {
        //tableau spécial pour une suite de bytes (des 1 et des 0)
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //compresse l'image dans le tableau spécial, au format png, avec la 100% de qualité
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        //renvoie le résultats : transforme le tableau spécial en chaine (string) spéciale
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }

    //transforme une chaine (string) en image Bitmap
    private Bitmap transformeStringEnBitmap(String chaine) {
        //décode la chaine spéciale, en la mettant dans un tableau de 1 et de 0
        byte[] imageEncodee = Base64.decode(chaine, Base64.DEFAULT);
        //transforme le tableau de 1 et de 0 en image bitmap
        //pour ça j'utilise une "boite noire" déjà faite
        return BitmapFactory.decodeByteArray(imageEncodee, 0, imageEncodee.length);
    }

    //sauvegarde dans les sharedpref une liste de "string"
    private void sauvegardeListeStringDansSharedPrefs(List<String> chaines) {
        //met moi dans sharedprefs, la zone mémoire que "activity" à la droit d'utiliser
        SharedPreferences sharedprefs = activity.getPreferences(Context.MODE_PRIVATE);
        //créer moi un objet pour éditer cette zone méméoire, parce que moi je veux écrire dedans
        SharedPreferences.Editor editor = sharedprefs.edit();
        //objet capable d'écrire, écrit moi la valeur 2 avec la clé valeur 1
        //editor.putString(valeur1, valeur2);
        editor.putString(CLE_SAUVEGARDE_SHARED_PREFS, new Gson().toJson(chaines));
        //sauvegarde les modifications en mémoire
        editor.apply();
    }

    //charge depuis les shared prefs une liste de "string"
    private List<String> chargeListeStringDepuisSharedPrefs() {
        //met moi dans sharedprefs, la zone mémoire que "activity" à la droit d'utiliser
        SharedPreferences sharedprefs = activity.getPreferences(Context.MODE_PRIVATE);
        //récupére moi la zoné mémoire associé à la clé que je te donne
        //et si tu trouve rien, renvoie moi la valeur par défaut que je te donne aussi
        String valeurEnMémoire = sharedprefs.getString(CLE_SAUVEGARDE_SHARED_PREFS, "");

        //si la valeur en mémoire est vide, on retourne une liste vide
        if (valeurEnMémoire.length() == 0) {
            return new ArrayList<>();
        }
        //sinon on retourne la liste transformée
        else {
            //on transforme le JSON en liste de String
            Type type = new TypeToken<List<String>>() {
            }.getType();
            return new Gson().fromJson(valeurEnMémoire, type);
        }
    }


    @Override
    public void sauvegarderPhotos(List<Bitmap> photos) {
        //créer une liste de "string" vide
        //Boucle : pour chaque image Bitmap de la liste "photos" :
        //      la transformer en "String"
        //      L'ajouter dans la liste de "String"
        //fin boucle
        //sauvegarde de la liste dans les sharedprefs (fonction)
        List<String> imagesTransformeesEnString = new ArrayList<>();
        for (Bitmap image : photos) {
            String imageTransformee = transformeBitmapEnString(image);
            imagesTransformeesEnString.add(imageTransformee);
        }
        sauvegardeListeStringDansSharedPrefs(imagesTransformeesEnString);

        //i = 0; // maintenant i vaux 0
        //i = foisDeux(4); // i vaux maintenant le résultat de la fonction foisDeux, à qui on a demandé de travailler sur le chiffre 4
        //i = new List<T>(); //  i vaux maintenant un nouvelle liste de "T" qui est vide (où qui contient 0 "T").
        //private int additionne(int a, int b){ return a+b;} //code d'une fonction qui additionne 2 entiers, pour ça elle a besoin quon lui donne a et b, sinon elle peux pas travailler
        //additionne(10,35) // addtionne 10 et 35, et ça te retourne 10+35 = 45, mais là le resultat est stocké nul part
        //i = additionne(10,35) //i vaux maintenant le résultat de la fonction additionne, avec 10 et 35 en entrée
    }

    //faire une boucle sur une liste d'objet de type T
    //(indice : remplace T par le type que tu est en train d'utiliser)
    //for(T mavariable : maListeD'objet à parcourir){
    //      ... ici faire le boulot
    //}
    // ex
    // parcourir une liste de "Personne" : personnes et pour chaque personne p
    // prendre le nom de p, et changer le nom de p par le meme nom, mais tout en majuscules
    //for(Personne p : personnes){
    //      //on imagine que la fonction EnMajuscules() renvoie une chaine en majuscule
    //      p.setNom(p.getNom().EnMajuscules());
    //}


    @Override
    public List<Bitmap> chargerPhotos() {
        //créer une liste "resultat" de Bitmap vide
        //charger la mémoire depuis les sharedprefs
        //Boucle : pour chaque élément de la liste renvoyée par les sharedPrefs :
        //      transformer l'élément chaine en Bitmap
        //      ajouter cet élément dans notre liste résultat
        //fin boucle
        //renvoyer la liste resultat
        List<Bitmap> resultat = new ArrayList<>();
        List<String> ListeMemoire = chargeListeStringDepuisSharedPrefs();
        for (String photoMemoire : ListeMemoire) {
            resultat.add(transformeStringEnBitmap(photoMemoire));
        }
        return resultat;


    }
}
