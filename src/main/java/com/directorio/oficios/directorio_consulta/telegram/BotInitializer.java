package com.directorio.oficios.directorio_consulta.telegram;
import com.directorio.oficios.directorio_consulta.bot.ConsultaBot;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class BotInitializer {

    private final ConsultaBot consultaBot;

    public BotInitializer(ConsultaBot consultaBot) {
        this.consultaBot = consultaBot;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        try {

            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(consultaBot);

            System.out.println("Bot de Consulta registrado y en línea. Se forzó la inicialización por EventListener.");

        } catch (TelegramApiException e) {
            System.err.println("Error CRÍTICO al iniciar el Polling del Bot:");
            e.printStackTrace();
        }
    }
}