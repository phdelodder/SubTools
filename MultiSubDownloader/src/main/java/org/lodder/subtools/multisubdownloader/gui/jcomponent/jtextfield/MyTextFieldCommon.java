package org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.io.Serial;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.sublibrary.util.BooleanConsumer;

public abstract class MyTextFieldCommon<T, R extends MyTextFieldCommon<T, R>> extends JTextField implements
        MyTextFieldToStringMapperIntf<T, R>,
        MyTextFieldToObjectMapperIntf<T, R>,
        MyTextFieldOthersIntf<T, R> {

    @Serial
    private static final long serialVersionUID = -393882042554264226L;
    private static final String DEFAULT_BORDER_PROPERTY = "DefaultBorder";
    private static final Border ERROR_BORDER = new LineBorder(Color.RED, 1);

    private Function<T, String> toStringMapper;
    private Function<String, T> toObjectMapper;
    private Predicate<String> valueVerifier;
    private boolean requireValue;
    private Consumer<T> valueChangedCalbackListener;
    private BooleanConsumer[] validityChangedCalbackListeners;

    private final ObjectWrapper<T> valueWrapper = new ObjectWrapper<>();
    private final ObjectWrapper<Boolean> validWrapper = new ObjectWrapper<>();
    private Predicate<String> completeValueVerifier;

    MyTextFieldCommon() {
        putClientProperty(DEFAULT_BORDER_PROPERTY, getBorder());
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
    public R withToStringMapper(Function<T, String> toStringMapper) {
        this.toStringMapper = toStringMapper;
        return self();
    }

    @Override
    public R withToObjectMapper(Function<String, T> toObjectMapper) {
        this.toObjectMapper = toObjectMapper;
        return self();
    }

    @Override
    public R withValueVerifier(Predicate<String> verifier) {
        this.valueVerifier = verifier;
        return self();
    }

    @Override
    public R requireValue(boolean requireValue) {
        this.requireValue = requireValue;
        return self();
    }

    @Override
    public R withValueChangedCallback(Consumer<T> valueChangedCalbackListener) {
        this.valueChangedCalbackListener = valueChangedCalbackListener;
        return self();
    }

    @Override
    public final R withValidityChangedCallback(BooleanConsumer... validityChangedCalbackListeners) {
        this.validityChangedCalbackListeners = validityChangedCalbackListeners;
        return self();
    }

    private static class ObjectWrapper<S> {
        private S value;

        public boolean setValue(S value) {
            boolean changed = this.value != value;
            this.value = value;
            return changed;
        }

        public S getValue() {
            return value;
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
        } else if (!completeValueVerifier.test(getText())) {
            setSuperBorder(ERROR_BORDER);
        }
    }

    private static Border getDefaultBorder(JComponent thisTextField) {
        return (Border) thisTextField.getClientProperty(DEFAULT_BORDER_PROPERTY);
    }

    @Override
    public R build() {
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
            checkValidity(getText());
            getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent e) {
                    checkValidity(getText());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    checkValidity(getText());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    checkValidity(getText());
                }

            });
        }
        return self();
    }

    private void checkValidity(String text) {
        boolean valid = completeValueVerifier.test(text);
        setSuperBorder(valid ? MyTextFieldCommon.getDefaultBorder(self()) : ERROR_BORDER);

        boolean changedValidity = validWrapper.setValue(valid);
        if (changedValidity && validityChangedCalbackListeners != null) {
            Arrays.stream(validityChangedCalbackListeners).forEach(listener -> listener.accept(valid));
        }

        if (valueChangedCalbackListener != null) {
            T value = toObjectMapper.apply(text);
            boolean valueChanged = valueWrapper.setValue(toObjectMapper.apply(text));
            if (valueChanged) {
                valueChangedCalbackListener.accept(value);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private R self() {
        return (R) this;
    }

    public T getObject() {
        String text = super.getText();
        return completeValueVerifier.test(text) ? toObjectMapper.apply(text) : null;
    }

    public Optional<T> getOptionalObject() {
        return Optional.ofNullable(getObject());
    }

    public void setObject(T object) {
        super.setText(object == null ? null : toStringMapper.apply(object));
        valueWrapper.setValue(object);
        validWrapper.setValue(completeValueVerifier.test(object == null ? null : toStringMapper.apply(object)));
    }

    public boolean hasValidValue() {
        return !isEnabled() || completeValueVerifier.test(getText());
    }
}
