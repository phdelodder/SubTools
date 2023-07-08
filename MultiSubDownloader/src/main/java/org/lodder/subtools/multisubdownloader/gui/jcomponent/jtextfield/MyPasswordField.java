package org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield;

import java.io.Serial;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.swing.JComponent;
import javax.swing.JPasswordField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang3.StringUtils;

import java.awt.Color;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import lombok.Getter;

public class MyPasswordField extends JPasswordField implements MypasswordFieldOthersIntf {

    @Serial
    private static final long serialVersionUID = -3002009544577141751L;
    private static final String DEFAULT_BORDER_PROPERTY = "DefaultBorder";
    private static final Border ERROR_BORDER = new LineBorder(Color.RED, 1);

    public Predicate<String> valueVerifier = StringUtils::isNotEmpty;

    private boolean requireValue;
    private Consumer<String> valueChangedCalbackListener;
    private BooleanConsumer[] validityChangedCalbackListeners;

    private final ObjectWrapper<String> valueWrapper = new ObjectWrapper<>();
    private final ObjectWrapper<Boolean> validWrapper = new ObjectWrapper<>();
    private Predicate<String> completeValueVerifier;

    private MyPasswordField() {
        super();
        putClientProperty(DEFAULT_BORDER_PROPERTY, getBorder());
    }

    public static MyPasswordField builder() {
        return new MyPasswordField();
    }

    @Override
    public void setBorder(Border border) {
        setSuperBorder(border);
        putClientProperty(DEFAULT_BORDER_PROPERTY, border);
    }

    public void setErrorBorder() {
        setBorder(ERROR_BORDER);
    }

    private void setSuperBorder(Border border) {
        super.setBorder(border);
    }

    @Override
    public MyPasswordField withValueVerifier(Predicate<String> verifier) {
        this.valueVerifier = verifier;
        return this;
    }

    @Override
    public MyPasswordField requireValue(boolean requireValue) {
        this.requireValue = requireValue;
        return this;
    }

    @Override
    public MyPasswordField withValueChangedCallback(Consumer<String> valueChangedCalbackListener) {
        this.valueChangedCalbackListener = valueChangedCalbackListener;
        return this;
    }

    @Override
    public MyPasswordField withValidityChangedCallback(BooleanConsumer... validityChangedCalbackListeners) {
        this.validityChangedCalbackListeners = validityChangedCalbackListeners;
        return this;
    }

    private static class ObjectWrapper<S> {
        @Getter
        private S value;

        public boolean setValue(S value) {
            boolean changed = this.value != value;
            this.value = value;
            return changed;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        refreshState();
    }

    public void refreshState() {
        if (!isEnabled()) {
            setSuperBorder(getDefaultBorder(this));
        } else if (!completeValueVerifier.test(getRawText())) {
            setSuperBorder(ERROR_BORDER);
        }
    }

    private static Border getDefaultBorder(JComponent thisTextField) {
        return (Border) thisTextField.getClientProperty(DEFAULT_BORDER_PROPERTY);
    }

    @Override
    public MyPasswordField build() {
        if (valueVerifier != null && requireValue) {
            completeValueVerifier = text -> (StringUtils.isNotEmpty(text) && valueVerifier.test(text));
        } else if (valueVerifier != null) {
            completeValueVerifier = valueVerifier;
        } else if (requireValue) {
            completeValueVerifier = StringUtils::isNotEmpty;
        } else {
            completeValueVerifier = t -> true;
        }

        if (valueVerifier != null || requireValue || valueChangedCalbackListener != null || validityChangedCalbackListeners != null) {
            checkValidity(getRawText());
            getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent e) {
                    checkValidity(getRawText());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    checkValidity(getRawText());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    checkValidity(getRawText());
                }

            });
        }
        return this;
    }

    private void checkValidity(String text) {
        boolean valid = completeValueVerifier.test(text);
        setSuperBorder(valid ? MyPasswordField.getDefaultBorder(this) : ERROR_BORDER);

        boolean changedValidity = validWrapper.setValue(valid);
        if (changedValidity && validityChangedCalbackListeners != null) {
            Arrays.stream(validityChangedCalbackListeners).forEach(listener -> listener.accept(valid));
        }

        if (valueChangedCalbackListener != null) {
            boolean valueChanged = valueWrapper.setValue(text);
            if (valueChanged) {
                valueChangedCalbackListener.accept(text);
            }
        }
    }

    private String getRawText() {
        return new String(getPassword());
    }

    @Override
    public String getText() {
        String text = new String(getPassword());
        return completeValueVerifier.test(text) ? text : null;
    }


    public Optional<String> getOptionalObject() {
        return Optional.ofNullable(getText());
    }

    @Override
    public void setText(String password) {
        super.setText(password == null ? null : password);
        valueWrapper.setValue(password);
        validWrapper.setValue(completeValueVerifier.test(password == null ? null : password));
    }

    public boolean hasValidValue() {
        return !isEnabled() || completeValueVerifier.test(getRawText());
    }
}
