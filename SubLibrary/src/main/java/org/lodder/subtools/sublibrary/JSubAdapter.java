package org.lodder.subtools.sublibrary;

import java.util.List;

import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Subtitle;

public interface JSubAdapter {

    List<Subtitle> searchSubtitles(TvRelease tvRelease, String... sublanguageids);

    List<Subtitle> searchSubtitles(MovieRelease movieRelease, String... sublanguageids);
}
