package com.example.application.views.aichat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.vaadin.webspeech.SpeechSynthesis;
import org.vaadin.webspeech.Utterance;
import org.vaadin.webspeech.ui.DictationButton;
import org.vaadin.webspeech.ui.LanguageSelect;
import org.vaadin.webspeech.ui.ReadAloudButton;
import org.vaadin.webspeech.ui.VoiceSelect;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.clipboard.Clipboard;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;
import jakarta.annotation.security.PermitAll;

@Route("aichat")
@Menu(title = "Chat AI")
@PermitAll
public class AiChatView extends VerticalLayout {

    private final TextField textField = new TextField();
    private final Button button = new Button("Ask");
    private final TextArea textArea = new TextArea();

    private final ValueSignal<String> question = new ValueSignal<>("");
    private final ValueSignal<String> response = new ValueSignal<>("");
    private final ValueSignal<Boolean> streaming = new ValueSignal<>(false);

    public AiChatView(ChatClient.Builder chatClientBuilder, @Value("${spring.ai.openai.apikey}") String apikey) {
        ChatClient chatClient = chatClientBuilder.build();
        DictationButton mic = new DictationButton(textField);
        mic.setContinuous(false);
        mic.setAppend(false);
        mic.setIconOnly(true);
        mic.setTexts("Ask by voice", "Listening");
        mic.addThemeVariants(ButtonVariant.TERTIARY);
        textField.setSuffixComponent(mic);

        ReadAloudButton readAloud = new ReadAloudButton(textArea);
        LanguageSelect languageSelect = new LanguageSelect();
        VoiceSelect voiceSelect = new VoiceSelect();
        languageSelect.addValueChangeListener(e -> {
            voiceSelect.setLanguage(e.getValue());
            mic.setLang(e.getValue());
            readAloud.setLang(e.getValue());
        });
        voiceSelect.addValueChangeListener(e -> readAloud.setVoice(e.getValue()));
        // iOS Safari only speaks later if speech was started in a user gesture first
        SpeechSynthesis.prime(button);
        SpeechSynthesis.prime(mic);

        boolean apikeyPresent = apikey != null;

        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.bindValue(question, question::set);
        textArea.bindValue(response, ignored -> {});
        textArea.bindReadOnly(streaming);
        button.bindEnabled(Signal.computed(() -> apikeyPresent && !question.get().isBlank() && !streaming.get()));

        HorizontalLayout questionRow = new HorizontalLayout(textField, button);
        UI ui = UI.getCurrent();
        button.addClickListener(e -> {
            response.set("");
            streaming.set(true);
            chatClient.prompt().user(question.peek()).stream().content().subscribe(
                    token -> ui.access(() -> response.update(r -> r + token)),
                    error -> ui.access(() -> streaming.set(false)),
                    () -> ui.access(() -> {
                        streaming.set(false);
                        SpeechSynthesis.speak(Utterance.builder(response.peek())
                                .lang(languageSelect.getValue()).voice(voiceSelect.getValue()).build());
                    }));
        });

        button.addClickShortcut(Key.ENTER);

        mic.getRecognition().finishBefore(button);
        mic.getRecognition().addEndListener(text -> {
            if (!text.isBlank()) {
                question.set(textField.getValue());
                button.click();
            }
        });
        this.setSizeFull();
        textArea.setSizeFull();
        questionRow.setWidthFull();
        textField.setWidthFull();

        if (!apikeyPresent) {
            response.set("$OPENAI_API_KEY environment variable is not properly set.");
        }

        Button copy = new Button(new Icon(VaadinIcon.COPY));
        copy.setAriaLabel("Copy reply to clipboard");
        copy.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        copy.bindEnabled(response.map(r -> !r.isEmpty()));
        Clipboard.onClick(copy).writeText(textArea,
                value -> Notification.show("Reply copied"),
                error -> Notification.show("Could not copy: " + error.message()));

        HorizontalLayout responseHeader = new HorizontalLayout(languageSelect, voiceSelect, readAloud, copy);
        responseHeader.setWidthFull();
        responseHeader.setWrap(true);
        responseHeader.setAlignItems(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.END);
        responseHeader.setJustifyContentMode(com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.END);

        add(questionRow, responseHeader, textArea);
    }
}
