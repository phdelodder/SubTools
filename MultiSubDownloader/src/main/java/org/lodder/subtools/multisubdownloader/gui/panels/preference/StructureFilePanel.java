package org.lodder.subtools.multisubdownloader.gui.panels.preference;

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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.gui.dialog.StructureBuilderDialog;
import org.lodder.subtools.multisubdownloader.gui.extra.PanelCheckBox;
import org.lodder.subtools.multisubdownloader.gui.extra.TitlePanel;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.button.AbstractButtonExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcheckbox.JCheckBoxExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcombobox.MyComboBox;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcomponent.JComponentExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield.JTextFieldExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield.MyTextFieldString;
import org.lodder.subtools.multisubdownloader.lib.library.FilenameLibraryBuilder;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;

import java.awt.Color;

import lombok.experimental.ExtensionMethod;
import net.miginfocom.swing.MigLayout;

@ExtensionMethod({ JTextFieldExtension.class, JComponentExtension.class, JCheckBoxExtension.class, AbstractButtonExtension.class })
public class StructureFilePanel extends JPanel {

    @Serial
    private static final long serialVersionUID = -5458593307643063563L;
    private final LibrarySettings librarySettings;

    private final MyTextFieldString txtFileStructure;
    private final JCheckBox chkReplaceSpace;
    private final MyComboBox<Character> cbxReplaceSpaceChar;
    private final JCheckBox chkIncludeLanguageCode;
    private final Supplier<LanguageComponents> addLanguageSupplier;
    private final LanguageMapping languageMapping = new LanguageMapping();

    public StructureFilePanel(LibrarySettings librarySettings, VideoType videoType, Manager manager,
            UserInteractionHandler userInteractionHandler) {
        super(new MigLayout("insets 0, fill, nogrid"));
        this.librarySettings = librarySettings;

        JPanel titelPanel = TitlePanel.title(Messages.getString("PreferenceDialog.RenameFiles"))
                .margin(0).padding(0).marginLeft(20).paddingLeft(20).addTo(this, "span, grow");

        {
            new JLabel(Messages.getString("PreferenceDialog.Structure")).addTo(titelPanel, "shrink");
            this.txtFileStructure = MyTextFieldString.builder().requireValue().build().withColumns(20).addTo(titelPanel, "grow");
            new JButton(Messages.getString("StructureBuilderDialog.Structure"))
                    .withActionListener(() -> {
                        StructureBuilderDialog sDialog =
                                new StructureBuilderDialog(null, Messages.getString("PreferenceDialog.StructureBuilderTitle"), true, videoType,
                                        StructureBuilderDialog.StructureType.FILE, manager, userInteractionHandler, getLibraryStructureBuilder());
                        String value = sDialog.showDialog(txtFileStructure.getText());
                        if (!value.isEmpty()) {
                            txtFileStructure.setText(value);
                        }

                    })
                    .addTo(titelPanel, "shrink, wrap");

            this.chkReplaceSpace = new JCheckBox(Messages.getString("PreferenceDialog.ReplaceSpaceWith"));

            PanelCheckBox.checkbox(chkReplaceSpace).panelOnSameLine().addTo(titelPanel, "wrap")
                    .addComponent("width pref+10px, wrap", this.cbxReplaceSpaceChar = MyComboBox.ofValues('-', '.', '_'));

            this.chkIncludeLanguageCode = new JCheckBox(Messages.getString("PreferenceDialog.IncludeLanguageInFileName"))
                    .withSelectedListener(languageMapping::refreshState).addTo(titelPanel, "wrap");

            JPanel languagePanelRoot = PanelCheckBox.checkbox(chkIncludeLanguageCode)
                    .panelOnNewLine().panelLayout(new MigLayout("insets 0, novisualpadding", "[][][]"))
                    .addTo(titelPanel, "span, growx");
            {
                JPanel languagePanel = new JPanel(new MigLayout("insets 0, novisualpadding", "[][][][20px]"));
                JScrollPane languageScrollPane = new JScrollPane(languagePanel).addTo(languagePanelRoot, "span, growx, wrap, hidemode 3");
                languageScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
                languageScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                languageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                languageScrollPane.setVisible(false);

                AtomicInteger langId = new AtomicInteger();
                addLanguageSupplier = () -> {
                    int id = langId.getAndIncrement();
                    MyComboBox<Language> cmbLanguage =
                            new MyComboBox<>(Language.values()).withToMessageStringRenderer(Language::getMsgCode).addTo(languagePanel);
                    MyTextFieldString txtLanguage = MyTextFieldString.builder().build().withColumns(20).addTo(languagePanel);
                    JButton btnDelete = new JButton(Messages.getString("StructureFilePanel.Delete"))
                            .withActionListenerSelf(delBtn -> {
                                languagePanel.remove(cmbLanguage);
                                languagePanel.remove(txtLanguage);
                                languagePanel.remove(delBtn);
                                languageMapping.remove(id);
                                languageScrollPane.setVisible(!languageMapping.isEmpty());
                                languagePanelRoot.repaint();
                                languagePanelRoot.revalidate();
                            })
                            .addTo(languagePanel, "wrap");
                    LanguageComponents languageComponents = new LanguageComponents(cmbLanguage, txtLanguage, btnDelete);
                    languageMapping.put(id, languageComponents);

                    languageScrollPane.setVisible(true);
                    languagePanelRoot.repaint();
                    languagePanelRoot.revalidate();
                    return languageComponents;
                };
                new JButton(Messages.getString("StructureFilePanel.AddLanguage"))
                        .withActionListener(addLanguageSupplier::get).addTo(languagePanelRoot);
            }
        }

        loadPreferenceSettings();
    }

    private record LanguageComponents(MyComboBox<Language> cmbLanguage, MyTextFieldString txtLanguage, JButton btnDelete) {

        public void setValue(Language language, String langCode) {
            cmbLanguage.setSelectedItem(language);
            txtLanguage.setText(langCode);
        }

        public boolean hasValidValue() {
            return txtLanguage.hasValidValue();
        }

        Language getLanguage() {
            return cmbLanguage.getSelectedItem();
        }

    }

    private void addLanguage(Language lang, String langCode) {
        addLanguageSupplier.get().setValue(lang, langCode);

    }

    private Function<String, FilenameLibraryBuilder> getLibraryStructureBuilder() {
        return structure -> FilenameLibraryBuilder.builder()
                .structure(structure)
                .replaceSpace(chkReplaceSpace.isSelected())
                .replacingSpaceChar(cbxReplaceSpaceChar.getSelectedItem())
                .includeLanguageCode(chkIncludeLanguageCode.isSelected())
                .languageTags(languageMapping.toSettingsMap())
                .useTvdbName(false)
                .tvdbAdapter(null)
                .rename(true)
                .build();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        txtFileStructure.refreshState();
    }

    public void loadPreferenceSettings() {
        txtFileStructure.setText(librarySettings.getLibraryFilenameStructure());
        chkReplaceSpace.setSelected(librarySettings.isLibraryFilenameReplaceSpace());
        cbxReplaceSpaceChar.setSelectedItem(librarySettings.getLibraryFilenameReplacingSpaceChar());
        chkIncludeLanguageCode.setSelected(librarySettings.isLibraryIncludeLanguageCode());
        librarySettings.getLangCodeMap().forEach(this::addLanguage);
    }

    public void savePreferenceSettings() {
        librarySettings
                .setLibraryFilenameStructure(txtFileStructure.getText())
                .setLibraryFilenameReplaceSpace(chkReplaceSpace.isSelected())
                .setLibraryFilenameReplacingSpaceChar(cbxReplaceSpaceChar.getSelectedItem())
                .setLibraryIncludeLanguageCode(chkIncludeLanguageCode.isSelected())
                .setLangCodeMap(languageMapping.toSettingsMap());
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

            MyComboBox<Language> cmbLanguage = languageComponents.cmbLanguage();
            cmbLanguage.putClientProperty(DEFAULT_BORDER_PROPERTY, cmbLanguage.getBorder());
            cmbLanguage.withSelectedItemConsumer(this::updateBorder);
            cmbLanguage.addItemListener(e -> updateBorder((Language) e.getItem()));
            updateBorder(cmbLanguage.getSelectedItem());
        }

        private void updateBorder(Language lang) {
            List<LanguageComponents> componentList = getLanguageComponentsForLanguageStream(lang).toList();
            if (componentList.isEmpty()) {
                return;
            }

            Border border = componentList.size() > 1 ? ERROR_BORDER : getDefaultBorder(componentList.get(0));
            componentList.forEach(components -> components.cmbLanguage.setBorder(border));
        }

        public boolean hasValidSettings() {
            return languageComponentsMap.values().stream().allMatch(LanguageComponents::hasValidValue)
                    && languageComponentsMap.values().stream().map(LanguageComponents::getLanguage).distinct().count() == languageComponentsMap
                            .size();
        }

        private Stream<LanguageComponents> getLanguageComponentsForLanguageStream(Language language) {
            return languageComponentsMap.values().stream().filter(langComps -> langComps.getLanguage() == language);
        }

        public Optional<LanguageComponents> getLanguageComponentsForLanguage(Language language) {
            return getLanguageComponentsForLanguageStream(language).findAny();
        }

        public Map<Language, String> toSettingsMap() {
            return languageComponentsMap.values().stream().collect(Collectors.toMap(
                    langComps -> langComps.cmbLanguage().getSelectedItem(), langComps -> langComps.txtLanguage().getText(),
                    (v1, v2) -> v1, LinkedHashMap::new));
        }

        public void refreshState(boolean enabled) {
            if (enabled) {
                languageComponentsMap.values().stream().map(langComp -> langComp.cmbLanguage.getSelectedItem()).distinct()
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
        return !isVisible()
                || (txtFileStructure.hasValidValue()
                        && (!chkIncludeLanguageCode.isSelected() || languageMapping.hasValidSettings()));
    }
}
