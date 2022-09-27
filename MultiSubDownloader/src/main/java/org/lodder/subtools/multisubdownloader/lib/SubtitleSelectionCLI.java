package org.lodder.subtools.multisubdownloader.lib;

import java.io.Console;
import java.util.Arrays;
import java.util.List;

import org.lodder.subtools.multisubdownloader.gui.extra.table.SubtitleTableColumnName;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.model.Release;

import dnl.utils.text.table.TextTable;

public class SubtitleSelectionCLI extends SubtitleSelection {

    public SubtitleSelectionCLI(Settings settings) {
        super(settings);
    }

    @Override
    public List<Integer> getUserInput(Release release) {
        System.out.println("\nSelect subtitle(s) for : " + release.getFileName());
        generateSubtitleSelectionOutput(release);

        System.out.println("(-1) To skip download and/or move!");
        Console c = System.console();
        String selectedSubtitles = c.readLine("Enter comma separated list of numbers of selected subtitle: ");
        System.out.println("");
        if ("-1".equals(selectedSubtitles)) {
            return List.of();
        }
        try {
            return Arrays.stream(selectedSubtitles.split(",")).map(Integer::parseInt).toList();
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, try again.");
            return getUserInput(release);
        }
    }

    @Override
    public void dryRunOutput(Release release) {
        generateSubtitleSelectionOutput(release);
    }

    private void generateSubtitleSelectionOutput(Release release) {
        System.out.println("\nAvailable subtitles for : " + release.getFileName());
        String[] columnNames =
                { SubtitleTableColumnName.SCORE.getColumnName(),
                        SubtitleTableColumnName.FILENAME.getColumnName(),
                        SubtitleTableColumnName.RELEASEGROUP.getColumnName(),
                        SubtitleTableColumnName.QUALITY.getColumnName(),
                        SubtitleTableColumnName.SOURCE.getColumnName(),
                        SubtitleTableColumnName.UPLOADER.getColumnName(),
                        SubtitleTableColumnName.HEARINGIMPAIRED.getColumnName() };

        Object[][] dataTable = new Object[release.getMatchingSubs().size()][columnNames.length];
        for (int i = 0; i < release.getMatchingSubs().size(); i++) {
            dataTable[i] =
                    new Object[] { release.getMatchingSubs().get(i).getScore(),
                            release.getMatchingSubs().get(i).getFileName(),
                            release.getMatchingSubs().get(i).getReleaseGroup(),
                            release.getMatchingSubs().get(i).getQuality(),
                            release.getMatchingSubs().get(i).getSubtitleSource(),
                            release.getMatchingSubs().get(i).getUploader(),
                            release.getMatchingSubs().get(i).isHearingImpaired() };
        }

        TextTable tt = new TextTable(columnNames, dataTable);
        // this adds the numbering on the left
        tt.setAddRowNumbering(true);
        tt.printTable();
    }
}
