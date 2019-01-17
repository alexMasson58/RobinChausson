package com.gfi.robinchausson.magalerie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.gfi.robinchausson.magalerie.sauvegarde.ISauvegardeManager;
import com.gfi.robinchausson.magalerie.sauvegarde.LocalSauvegardeManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PhotoActivity extends AppCompatActivity {

    FloatingActionButton fab;
    ImageView image;
    ImageView flechedroite;
    ImageView flechegauche;
    ImageView supprime;
    int position = 0;
    List<Bitmap> listephoto;

    ISauvegardeManager sauvegardeManager;

    //fonction appelé quand la vue est crée
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       sauvegardeManager = new LocalSauvegardeManager(this);


        //créer une liste vide, donc avec 0 photos.
        //on demande au sauvegardeManager de nous renvoyer la liste des photos sauvegardées
        //on met cette liste dans notre listephoto
        listephoto = sauvegardeManager.chargerPhotos();

        chargerLeSqueletteSurLEcran();
        creerDesLiensAvecLesObjetsSurLEcran();
        donnerDesValeursANosObjets();
        rajouterDesComportementAuxObjets();
        dessineLaBonnePhoto();
    }

    //dessiner correctement tout l"ecran en fonction des cas
    private void dessineLaBonnePhoto() {
        //premier cas : pas de photos
        //une seule photo
        //plusieurs photos :
        //      premiere:
        //      dernière:
        //      tous les autres cas :
        if (listephoto.size() == 0) {
            supprime.setVisibility(View.INVISIBLE);
            flechegauche.setVisibility(View.INVISIBLE);
            flechedroite.setVisibility(View.INVISIBLE);
            image.setImageDrawable(null);
        }

        if (listephoto.size() == 1) {
            flechegauche.setVisibility(View.INVISIBLE);
            flechedroite.setVisibility(View.INVISIBLE);
            supprime.setVisibility(View.VISIBLE);
            image.setImageBitmap(listephoto.get(position));
        }
        if (listephoto.size() >= 2) {
            //si je le met ici, il est utilisé, dans les 3 sous-cas
            /*supprime.setVisibility(View.VISIBLE);
            image.setImageBitmap(listephoto.get(position));*/
            if (surLaPremirePhoto()) {
                supprime.setVisibility(View.VISIBLE);
                image.setImageBitmap(listephoto.get(position));
                flechedroite.setVisibility(View.VISIBLE);
                flechegauche.setVisibility(View.INVISIBLE);
            } else {
                if (surLaDernierePhoto()) {
                    supprime.setVisibility(View.VISIBLE);
                    image.setImageBitmap(listephoto.get(position));
                    flechedroite.setVisibility(View.INVISIBLE);
                    flechegauche.setVisibility(View.VISIBLE);
                } else {
                    supprime.setVisibility(View.VISIBLE);
                    image.setImageBitmap(listephoto.get(position));
                    flechedroite.setVisibility(View.VISIBLE);
                    flechegauche.setVisibility(View.VISIBLE);
                }
            }

        }

    }

    //indiquer quel xml on veux afficher
    private void chargerLeSqueletteSurLEcran() {
        setContentView(R.layout.activity_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    //permettre à notre cerveau de connaitre les objets affichés
    //ici on a un bouton fab, et une ImageView image
    private void creerDesLiensAvecLesObjetsSurLEcran() {
        //récupère le bouton "fab" dans le squelette(layout xml), et le place dans l'objet fab du cerveau(activity)
        fab = (FloatingActionButton) findViewById(R.id.fab);
        image = (ImageView) findViewById(R.id.image);
        flechedroite = (ImageView) findViewById(R.id.flechedroite);
        flechegauche = (ImageView) findViewById(R.id.flechegauche);
        supprime = (ImageView) findViewById(R.id.supprime);
    }


    //"Remplir" les objets qui doivent afficher des valeurs
    private void donnerDesValeursANosObjets() {
        flechedroite.setVisibility(View.INVISIBLE);
        flechegauche.setVisibility(View.INVISIBLE);
    }

    //rajouter des choses à faire au clic
    //rajouter des chargments depuis le web
    //plein d'autres interactions
    private void rajouterDesComportementAuxObjets() {

        //rajoute un traitment quand on clique sur le bouton "fab"
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                ouvrirAppareilPhoto();
            }

        });
        supprime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supprimeLaBonneImage();
            }
        });

        flechegauche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decalerAGauche();
            }
        });

        flechedroite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decalerADroite();
            }

        });
    }

    //retirer la photo à la positon "position" de la liste des photos
    //décaler correcterment la fleche :
    //      soit on est sur la dernière photo :
    //              soit on avais qu'une seule photo :
    //              soit on avais plusieurs photo :
    //      sinon on est pas sur la derniere photo -> fait rien
    private void supprimeLaBonneImage() {
        int nombrePhotosAvantSuppression = listephoto.size();
        listephoto.remove(position);
        if (surLaDernierePhoto() && position > 0) {
            //tester si avant supression, on avais plus d'une photo
            if (nombrePhotosAvantSuppression > 1) {
                descendrePositionDeUn();
            }
            //sinon on fais rien

        }
        dessineLaBonnePhoto();
        sauvegardeManager.sauvegarderPhotos(listephoto);
    }


    //C'est quoi ?
    //si on est pas sur la derniere photo (la photo à la position : taille de la liste -1)
    //position monte de 1 (on se décale vers la droite)
    //ensuite on se redessine
    //sinon on fais rien
    private void decalerADroite() {
        if (!(surLaDernierePhoto())) {
            monterPositionDeUn();
            dessineLaBonnePhoto();
        }
        //else {...} mais ici nous on fais rien

    }

    //position monte de 1 (on se décale vers la droite)
    private void monterPositionDeUn() {
        position = position + 1;
        //position++;
    }

    //Etre sur la dernier photo c'est
    //(la photo à la position : taille de la liste -1)
    private boolean surLaDernierePhoto() {
        //la position qu'on a stocké est égale à la taille de la liste-1
        //si on a au moins 2 photos
        if (listephoto.size() >= 2) {
            //boolean b = false;
            //if(position == (listephoto.size()-1){
            //      b = true;
            //}
            //return b;
            return position == (listephoto.size() - 1);
        } else {
            return true;
        }
    }

    //C'est quoi ?
    //si on est pas sur la premiere photo (position 0)
    //position descend de 1 (on se décale vers la gauche)
    //ensuite on se redessine
    //sinon on fais rien
    private void decalerAGauche() {
        if (!(surLaPremirePhoto())) {
            descendrePositionDeUn();
            dessineLaBonnePhoto();

        }
    }

    private void descendrePositionDeUn() {
        position = position - 1;
    }

    private boolean surLaPremirePhoto() {
        if (listephoto.size() >= 1) {
            if (position != 0) {

                return false;

            } else {
                return true;
            }
        }
        return true;
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void ouvrirAppareilPhoto() {

        //je demande au téléphone si une autre application est capable de prendre des photos, et de me renvoyer le résultat
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //si au moins une activité sais le faire, alors je demande à android de l'ouvrir
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    //Récupère le résultat d'une autre application
    //ici on doit controler :
    //  qui nous a répondu avec le requestCode
    //  si le traitement c'est bien passé avec le resultCode
    //  les données du résultat sont dans data
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //mImageView.setImageBitmap(imageBitmap);
            //image.setImageBitmap(imageBitmap);
            //ajouter la nouvelle bitmap dans la liste des photos
            //placer position à la fin
            //on appelle dessinemoi la bonne
            listephoto.add(imageBitmap);
        sauvegardeManager.sauvegarderPhotos(listephoto);
            positionALaFin();
            dessineLaBonnePhoto();

        }
    }

    private void positionALaFin() {
        position = listephoto.size() - 1;
    }

    //notre représentation d'une photo
    //pour cet exercice  une photo c'est une date
    //plus des données Bitmap (la photo renvoyée par l'appareil photo)
    class Photo {
        private Bitmap donnees;
        private Date date;

        public Photo() {
        }

        public Bitmap getDonnees() {
            return donnees;
        }

        public void setDonnees(Bitmap donnees) {
            this.donnees = donnees;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }

}
