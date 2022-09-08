package org.lodder.subtools.multisubdownloader.subtitleproviders.subscene;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.exception.SubsceneException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.model.SubsceneSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.ManagerSetupException;
import org.lodder.subtools.sublibrary.data.Html;
import org.lodder.subtools.sublibrary.model.Subtitle.SubtitleSource;
import org.lodder.subtools.sublibrary.util.http.HttpClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.icu.text.RuleBasedNumberFormat;
import com.pivovarit.function.ThrowingSupplier;

public class SubsceneApi extends Html {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubsceneApi.class);
    private static final String IDENTIFIER = SubtitleSource.SUBSCENE.name();
    private static final RuleBasedNumberFormat NUMBER_FORMAT = new RuleBasedNumberFormat(Locale.UK, RuleBasedNumberFormat.SPELLOUT);
    private static final long RATEDURATION = 1; // seconds
    private LocalDateTime lastRequest = LocalDateTime.now();

    public SubsceneApi(Manager manager) {
        super(manager, "Mozilla/5.25 Netscape/5.0 (Windows; I; Win95)");
    }

    public List<SubsceneSubtitleDescriptor> getSubtilteDescriptors(String serieName, int season) throws SubsceneException {
        try {
            Optional<String> urlForSerie = getUrlForSerie(serieName, season);
            if (urlForSerie.isEmpty()) {
                return List.of();
            }
            return Jsoup.parse(getHtml(urlForSerie.get()))
                    .select("td.a1").stream().map(Element::parent)
                    .map(row -> new SubsceneSubtitleDescriptor()
                            .setLanguage(row.select(".a1 span.l").text().trim())
                            .setUrlSupplier(() -> getDownloadUrl("https://subscene.com" + row.select(".a1 > a").attr("href").trim()))
                            .setName(row.select(".a1 span:not(.l)").text().trim())
                            .setHearingImpaired(!row.select(".a41").isEmpty())
                            .setUploader(row.select(".a5 > a").text().trim())
                            .setComment(row.select(".a6 > div").text().trim()))
                    .toList();
        } catch (IOException | HttpClientException | ManagerSetupException | ManagerException e) {
            throw new SubsceneException(e);
        }
    }

    private String getDownloadUrl(String seriePageUrl) throws ManagerException {
        try {
            return "https://subscene.com" + Jsoup.parse(getHtml(seriePageUrl)).selectFirst("#downloadButton").attr("href");
        } catch (IOException | HttpClientException | ManagerSetupException e) {
            throw new ManagerException(e);
        }
    }

    private Optional<String> getUrlForSerie(String serieName, int season) throws ManagerSetupException, SubsceneException {
        ThrowingSupplier<Optional<String>, SubsceneException> valueSupplier = () -> {
            try {
                String url = "https://subscene.com/subtitles/searchbytitle?query=" + URLEncoder.encode(serieName, StandardCharsets.UTF_8);
                Element searchResultElement = Jsoup.parse(getHtml(url)).selectFirst(".search-result");
                if (searchResultElement == null) {
                    return null;
                }
                return searchResultElement.select("h2").stream()
                        .filter(element -> "TV-Series".equals(element.text())).findFirst()
                        .map(Element::nextElementSibling)
                        .map(element -> element.selectFirst(".title a"))
                        .filter(element -> {
                            String[] split = element.text().trim().split(" - ");
                            return split.length > 1 && (NUMBER_FORMAT.format(season, "%spellout-ordinal") + " Season")
                                    .equalsIgnoreCase(split[split.length - 1].trim());
                        })
                        .map(element -> "https://subscene.com" + element.attr("href"));
            } catch (ManagerSetupException | HttpClientException | IOException | ManagerException e) {
                throw new SubsceneException(e);
            }
        };
        return getOptionalValueDisk(IDENTIFIER + serieName + "_SEASON:" + season, valueSupplier);
    }

    @Override
    public String getHtml(String url) throws IOException, HttpClientException, ManagerSetupException, ManagerException {
        while (ChronoUnit.SECONDS.between(lastRequest, LocalDateTime.now()) < RATEDURATION) {
            try {
                // Pause for 1 seconds
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                // restore interrupted status
                Thread.currentThread().interrupt();
            }
        }
        lastRequest = LocalDateTime.now();
        return super.getHtml(url);
    }
}
