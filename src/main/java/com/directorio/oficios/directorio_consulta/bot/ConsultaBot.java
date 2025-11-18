package com.directorio.oficios.directorio_consulta.bot;

import com.directorio.oficios.directorio_consulta.model.Profesional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.List;

@Component
public class ConsultaBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;


    private final RestTemplate restTemplate;

    private static final String BASE_URL = "http://127.0.0.1:8081/api/consulta";

    public ConsultaBot(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                long chatId = update.getMessage().getChatId();

                if (messageText.startsWith("/buscar")) {
                    handleSearchCommand(chatId, messageText);
                } else if (messageText.equals("/start")) {
                    sendMessage(chatId, "¡Hola! Soy el Bot de consulta de profesionales. Para buscar, usa el formato: /buscar [Oficio] en [Ciudad]");
                }
            }
        }
    }

    private void handleSearchCommand(long chatId, String message) {
        String oficio = null;
        String ciudad = null;

        String[] parts = message.split("\\s+");

        if (parts.length >= 2) {
            oficio = parts[1];
        }

        for (int i = 2; i < parts.length - 1; i++) {
            if (parts[i].equalsIgnoreCase("en") && i + 1 < parts.length) {
                ciudad = parts[i + 1];
                break;
            }
        }

        if (oficio == null && ciudad == null) {
            sendMessage(chatId, "La búsqueda requiere al menos un filtro (oficio o ciudad).");
            return;
        }

        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BASE_URL);

            if (oficio != null) {
                builder.queryParam("oficio", oficio);
            }
            if (ciudad != null) {
                builder.queryParam("ciudad", ciudad);
            }

            ResponseEntity<Profesional[]> response = restTemplate.getForEntity(
                    builder.toUriString(),
                    Profesional[].class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Profesional> resultados = Arrays.asList(response.getBody());
                String responseText = formatResults(resultados, oficio, ciudad);
                sendMessage(chatId, responseText);
            }

        } catch (HttpClientErrorException httpEx) {
            if (httpEx.getStatusCode().value() == 400) {
                sendMessage(chatId, "La búsqueda requiere al menos un filtro válido (oficio o ciudad).");
            } else {
                sendMessage(chatId, "Error HTTP al consultar el servicio. Código: " + httpEx.getStatusCode().value());
            }
            httpEx.printStackTrace();

        } catch (Exception e) {
            sendMessage(chatId, "Error grave de conexión o procesamiento. Verifica la consola para el stack trace.");
            e.printStackTrace();
        }
    }


    private String formatResults(List<Profesional> resultados, String oficio, String ciudad) {
        if (resultados == null || resultados.isEmpty()) {
            return String.format("No se encontraron profesionales de **%s** en **%s**.",
                    (oficio != null ? oficio : "cualquier oficio"),
                    (ciudad != null ? ciudad : "cualquier ciudad"));
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Encontrados **%d** profesionales de **%s** en **%s**:\n\n",
                resultados.size(),
                (oficio != null ? oficio : "varios oficios"),
                (ciudad != null ? ciudad : "varias ciudades")));

        for (Profesional p : resultados) {
            sb.append(String.format("**%s** (%s)\n📍 %s\n",
                    p.getNombreProfesional(),
                    p.getTipoOficio(),
                    p.getCiudadLocalidad()));
        }
        return sb.toString();
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setParseMode("Markdown");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}