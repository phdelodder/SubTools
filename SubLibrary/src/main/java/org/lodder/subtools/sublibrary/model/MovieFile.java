package org.lodder.subtools.sublibrary.model;

import java.io.File;


public class MovieFile extends Release {

    private String title;
    private int year, imdbid;

    public MovieFile() {
        super(VideoType.MOVIE);
        setTitle("");
        setYear(0);
        this.imdbid = 0;
    }

    public MovieFile(String title, Integer year, File file, String extension, String description, String team) {
        super(VideoType.MOVIE, file, extension, description, team);
        this.title = title;
        this.year = year;
        this.imdbid = 0;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public int getImdbid() {
        return imdbid;
    }

    public void setImdbid(int imdbid) {
        this.imdbid = imdbid;
    }

    public String getImdbidAsString(){
        String imdbidstr = String.valueOf(this.getImdbid());
        while (imdbidstr.length() < 7){
            imdbidstr = 0 + imdbidstr;
        }
        return "tt" + imdbidstr;
    }
}
