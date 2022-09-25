package org.lodder.subtools.sublibrary.data.imdb.model;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: lodder
 * Date: 21/08/11
 * Time: 19:36
 * To change this template use File | Settings | File Templates.
 */

import java.net.URL;

import org.lodder.subtools.sublibrary.data.ReleaseDBIntf;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImdbDetails implements ReleaseDBIntf, Serializable {
    private static final long serialVersionUID = 2596873770215746380L;
    private String title, rated, released, genre, director, writer, actors, plot, runtime, rating, votes, id;
    private int year;
    private URL poster;

    public ImdbDetails() {
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

    @Override
    public String getName() {
        return title;
    }

}
