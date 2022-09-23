package org.lodder.subtools.sublibrary.data.IMDB.model;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: lodder
 * Date: 21/08/11
 * Time: 19:36
 * To change this template use File | Settings | File Templates.
 */

import java.net.URL;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IMDBDetails implements Serializable {
    private static final long serialVersionUID = 2596873770215746380L;
    private String title, rated, released, genre, director, writer, actors, plot, runtime, rating, votes, id;
    private int year;
    private URL poster;

    public IMDBDetails() {
        title = "";
        rated = "";
        released = "";
        genre = "";
        director = "";
        writer = "";
        actors = "";
        plot = "";
        runtime = "";
        rating = "";
        votes = "";
        id = "";
        year = 0;
        poster = null;
    }

}
