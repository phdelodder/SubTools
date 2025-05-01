package org.lodder.subtools.multisubdownloader.gui.panels.preference;

import static org.lodder.subtools.multisubdownloader.Messages.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.Serial;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.lodder.subtools.multisubdownloader.gui.dialog.StructureBuilderDialog;
import org.lodder.subtools.multisubdownloader.gui.extra.BoxModelProperties;
import org.lodder.subtools.multisubdownloader.gui.extra.PanelCheckBox;
import org.lodder.subtools.multisubdownloader.gui.extra.TitlePanel;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield.MyTextFieldString;
import org.lodder.subtools.multisubdownloader.lib.library.FilenameLibraryBuilder;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;

@Slf4j
public class StructureFilePanel extends JPanel {

    @Serial private static final long serialVersionUID = -5458593307643063563L;

    private final LibrarySettings librarySettings;
    private final MyTextFieldString txtFileStructure;
    private final JCheckBox chkReplaceSpace;
    private final JComboBox<Character> cbxReplaceSpaceChar;
    private final JCheckBox chkIncludeLanguageCode;
    private final Supplier<LanguageComponents> addLanguageSupplier;
    private final LanguageMapping languageMapping = new LanguageMapping();

    public StructureFilePanel(LibrarySettings librarySettings, VideoType videoType, Manager manager,
        UserInteractionHandler userInteractionHandler) {
        super(new MigLayout("insets 0, fill, nogrid"));
        this.librarySettings = librarySettings;

        JPanel titlePanel = new TitlePanel(
            title:getText("PreferenceDialog.RenameFiles"),
            margin:new BoxModelProperties(0, 20, 0, 0),
            padding:new BoxModelProperties(0, 20, 0, 0))
            .addToPanel(this, "span, grow");

        new JLabel(getText("PreferenceDialog.Structure")).addTo(titlePanel, "shrink");
        this.txtFileStructure =
            MyTextFieldString.builder().requireValue().build().columns(20).addTo(titlePanel, "grow");
        new JButton(getText("StructureBuilderDialog.Structure"))
            .actionListener(() -> {
                StructureBuilderDialog sDialog =
                    new StructureBuilderDialog(null, getText("PreferenceDialog.StructureBuilderTitle"),
                        true, videoType, StructureBuilderDialog.StructureType.FILE, manager,
                        userInteractionHandler, getLibraryStructureBuilder());
                String value = sDialog.showDialog(txtFileStructure.getText());
                if (!value.isEmpty()) {
                    txtFileStructure.setText(value);
                }

            }).addTo(titlePanel, "shrink, wrap");

        this.chkReplaceSpace = new JCheckBox(getText("PreferenceDialog.ReplaceSpaceWith"));

        new PanelCheckBox(checkbox:chkReplaceSpace, panelOnNewLine:false)
            .addToPanel(titlePanel, "wrap")
            .addComponent("width pref+10px, wrap",
                this.cbxReplaceSpaceChar = JComboBox.create('-', '.', '_'));

        this.chkIncludeLanguageCode =
            new JCheckBox(getText("PreferenceDialog.IncludeLanguageInFileName"))
                .selectedListener(languageMapping::refreshState).addTo(titlePanel, "wrap");

        JPanel languagePanelRoot = new PanelCheckBox(
            checkbox:chkIncludeLanguageCode,
            panelOnNewLine:true,
            panelLayout:new MigLayout("insets 0, novisualpadding", "[][][]"))
            .addToPanel(titlePanel, "span, growx");
        {
            JPanel languagePanel = new JPanel(new MigLayout("insets 0, novisualpadding", "[][][][20px]"));
            JScrollPane languageScrollPane =
                new JScrollPane(languagePanel).addTo(languagePanelRoot, "span, growx, wrap, hidemode 3");
            languageScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
            languageScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            languageScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            languageScrollPane.setVisible(false);

            AtomicInteger langId = new AtomicInteger();
            addLanguageSupplier = () -> {
                int id = langId.getAndIncrement();
                JComboBox<Language> cmbLanguage = new JComboBox<>(Language.values())
                    .toMessageStringRenderer(Language::getMsgCode).addTo(languagePanel);
                MyTextFieldString txtLanguage = MyTextFieldString.builder().build().columns(20).addTo(languagePanel);
                JButton btnDelete = new JButton(getText("StructureFilePanel.Delete"))
                    .actionListenerSelf(delBtn -> {
                        languagePanel.remove(cmbLanguage);
                        languagePanel.remove(txtLanguage);
                        languagePanel.remove(delBtn);
                        languageMapping.remove(id);
                        languageScrollPane.setVisible(!languageMapping.isEmpty());
                        languagePanelRoot.repaint();
                        languagePanelRoot.revalidate();
                    }).addTo(languagePanel, "wrap");
                LanguageComponents languageComponents = new LanguageComponents(cmbLanguage, txtLanguage, btnDelete);
                languageMapping.put(id, languageComponents);

                languageScrollPane.setVisible(true);
                languagePanelRoot.repaint();
                languagePanelRoot.revalidate();
                return languageComponents;
            };
            new JButton(getText("StructureFilePanel.AddLanguage")).actionListener(
                addLanguageSupplier::get).addTo(languagePanelRoot);
        }

        loadPreferenceSettings();
    }

    private record LanguageComponents(JComboBox<Language> cmbLanguage, MyTextFieldString txtLanguage,
        JButton btnDelete) {

        public void setValue(Language language, String langCode) {
            cmbLanguage.setSelectedItem(language);
            txtLanguage.setText(langCode);
        }

        public boolean hasValidValue() {
            return txtLanguage.hasValidValue();
        }

        Language getLanguage() {
            return cmbLanguage.getSelectedValue();
        }

    }

    private void addLanguage(Language lang, String langCode) {
        addLanguageSupplier.get().setValue(lang, langCode);

    }

    private Function<String, FilenameLibraryBuilder> getLibraryStructureBuilder() {
        return structure -> new FilenameLibraryBuilder(
            structure:structure,
            replaceSpace:chkReplaceSpace.isSelected(),
            replacingSpaceChar:cbxReplaceSpaceChar.getSelectedValue(),
            includeLanguageCode:chkIncludeLanguageCode.isSelected(),
            languageTags:languageMapping.toSettingsMap(),
            rename:true);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        txtFileStructure.refreshState();
    }

    public void loadPreferenceSettings() {
        txtFileStructure.setText(librarySettings.filenameStructure);
        chkReplaceSpace.setSelected(librarySettings.filenameReplaceSpace);
        cbxReplaceSpaceChar.setSelectedItem(librarySettings.filenameReplacingSpaceChar);
        chkIncludeLanguageCode.setSelected(librarySettings.includeLanguageCode);
        librarySettings.langCodeMap.forEach(this::addLanguage);
    }

    public void savePreferenceSettings() {
        librarySettings.filenameStructure = txtFileStructure.getText();
        librarySettings.filenameReplaceSpace = chkReplaceSpace.isSelected();
        librarySettings.filenameReplacingSpaceChar = cbxReplaceSpaceChar.getSelectedValue();
        librarySettings.langCodeMap = languageMapping.toSettingsMap();
    }

    private static class LanguageMapping {
        private final Map<Integer, LanguageComponents> languageComponentsMap = new LinkedHashMap<>();
        private static final String DEFAULT_BORDER_PROPERTY = "DefaultBorder";
        private static final Border ERROR_BORDER = new LineBorder(Color.RED, 1);

        public void remove(int id) {
            updateBorder(languageComponentsMap.remove(id).getLanguage());
        }

        public void put(int id, LanguageComponents languageComponents) {
            languageComponentsMap.put(id, languageComponents);

            JComboBox<Language> cmbLanguage = languageComponents.cmbLanguage();
            cmbLanguage.putClientProperty(DEFAULT_BORDER_PROPERTY, cmbLanguage.getBorder());
            cmbLanguage.selectedItemConsumer(this::updateBorder);
            cmbLanguage.addItemListener(e -> updateBorder((Language) e.getItem()));
            updateBorder(cmbLanguage.getSelectedValue());
        }

        private void updateBorder(Language lang) {
            List<LanguageComponents> componentList = getLanguageComponentsForLanguageStream(lang).toList();
            if (componentList.isEmpty()) {
                return;
            }

            Border border = componentList.size() > 1 ? ERROR_BORDER : getDefaultBorder(componentList.first);
            componentList.forEach(components -> components.cmbLanguage.setBorder(border));
        }

        public boolean hasValidSettings() {
            return languageComponentsMap.values().stream().allMatch(LanguageComponents::hasValidValue) &&
                languageComponentsMap.values().stream().map(LanguageComponents::getLanguage).distinct().count() ==
                    languageComponentsMap.size();
        }

        private Stream<LanguageComponents> getLanguageComponentsForLanguageStream(Language language) {
            return languageComponentsMap.values().stream().filter(langComps -> langComps.getLanguage() == language);
        }

        public Optional<LanguageComponents> getLanguageComponentsForLanguage(Language language) {
            return getLanguageComponentsForLanguageStream(language).findAny();
        }

        public Map<Language, String> toSettingsMap() {
            return languageComponentsMap.values()
                .stream()
                .collect(Collectors.toMap(langComps -> langComps.cmbLanguage().getSelectedValue(),
                    langComps -> langComps.txtLanguage().getText(), (v1, _) -> v1, LinkedHashMap::new));
        }

        public void refreshState(boolean enabled) {
            if (enabled) {
                languageComponentsMap.values()
                    .stream()
                    .map(langComp -> langComp.cmbLanguage.getSelectedValue())
                    .distinct()
                    .forEach(this::updateBorder);
            } else {
                languageComponentsMap.values()
                    .forEach(langComps -> langComps.cmbLanguage.setBorder(getDefaultBorder(langComps)));
            }
        }

        private Border getDefaultBorder(LanguageComponents languageComponents) {
            return (Border) languageComponents.cmbLanguage.getClientProperty(DEFAULT_BORDER_PROPERTY);
        }

        public boolean isEmpty() {
            return languageComponentsMap.isEmpty();
        }
    }

    public boolean hasValidSettings() {
        return !isVisible() || (txtFileStructure.hasValidValue() &&
            (!chkIncludeLanguageCode.isSelected() || languageMapping.hasValidSettings()));
    }
}
