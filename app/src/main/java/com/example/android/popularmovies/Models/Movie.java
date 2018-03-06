package com.example.android.popularmovies.Models;


import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable{

    private int id;

    private String title;

    private String synopsis;

    private float rating;

    private String imageURL;

    private String releaseDate;

    public static final Parcelable.Creator CREATOR = new  Creator<Movie>(){

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Movie(int id, String title, String synopsis, float rating, String imageURL, String releaseDate){
        this.id = id;
        this.title = title;
        this.synopsis = synopsis;
        this.rating = rating;
        this.imageURL = imageURL;
        this.releaseDate = releaseDate;
    }

    public Movie(Parcel source) {
        this.id = source.readInt();
        this.title = source.readString();
        this.synopsis = source.readString();
        this.rating = source.readFloat();
        this.imageURL = source.readString();
        this.releaseDate = source.readString();
    }


    public int getId(){ return id;}

    public String getTitle(){ return title;}

    public String getSynopsis(){ return synopsis;}

    public float getRating(){ return rating;}

    public String getImageURL(){ return imageURL;}

    public String getReleaseDate(){ return releaseDate;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(synopsis);
        dest.writeFloat(rating);
        dest.writeString(imageURL);
        dest.writeString(releaseDate);
    }
}
