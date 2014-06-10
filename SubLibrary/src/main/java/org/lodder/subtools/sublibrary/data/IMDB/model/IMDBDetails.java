package org.lodder.subtools.sublibrary.data.IMDB.model;

/**
 * Created by IntelliJ IDEA.
 * User: lodder
 * Date: 21/08/11
 * Time: 19:36
 * To change this template use File | Settings | File Templates.
 */

import java.net.URL;

public class IMDBDetails {
    private String title, rated, released, genre, director, writer, actors, plot, runtime, rating, votes, id;
    private int year;
    private URL poster;

    public IMDBDetails(){
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

public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRated() {
        return rated;
    }

    public void setRated(String rated) {
        this.rated = rated;
    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getVotes() {
        return votes;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public URL getPoster() {
        return poster;
    }

    public void setPoster(URL poster) {
        this.poster = poster;
    }

}
