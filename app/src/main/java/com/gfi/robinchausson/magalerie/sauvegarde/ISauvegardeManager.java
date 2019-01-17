package com.gfi.robinchausson.magalerie.sauvegarde;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by Alexandre MASSON on 16/01/2019.
 */

//Tous ceux qui voudront gérer la sauvegarde de nos images,
//seront obligés de faire ça
public interface ISauvegardeManager {

    //dois sauvegarder les photos, quelquesoit la méthode choisie
    void sauvegarderPhotos(List<Bitmap> photos);
    //setPhotosSauvegardees

    //dois charger et renvoyer une liste de Bitmap
    //quelquesoit la méthode choisie
    List<Bitmap> chargerPhotos();
    //getPhotosSauvegardees
}
