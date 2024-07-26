package Commands.Prefix;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONObject;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class IPCheckerCommand extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if (args[0].equalsIgnoreCase(".ip")) {
            if (args.length < 2) {
                sendErrorMessage(event, "Пожалуйста, укажите IP-адрес сервера. Использование: `.ip <IP-адрес>`");
                return;
            }

            String ip = args[1];
            try {
                JSONObject json = getIPInfo(ip);
                if (json != null) {
                    event.getChannel().sendMessageEmbeds(createIPInfoEmbed(json)).queue();
                } else {
                    sendErrorMessage(event, "Пожалуйста, укажите корректный IP-адрес сервера. Использование: `.ip <IP-адрес>`");
                }
            } catch (Exception e) {
                sendErrorMessage(event, "Произошла ошибка с обработкой запроса, возможно вы допустили ошибку в IP-адресе");
            }
        }
    }

    private JSONObject getIPInfo(String ip) {
        try {
            URL url = new URL("http://ip-api.com/json/" + ip);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return new JSONObject(response.toString());
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private MessageEmbed createIPInfoEmbed(JSONObject json) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("Данные о IP")
                .addField("IP", json.getString("query"), false)
                .addField("ISP", json.getString("isp"), false)
                .addField("Country", json.getString("country"), false)
                .addField("Region", json.getString("regionName"), false)
                .addField("City", json.getString("city"), false)
                .setColor(Color.BLUE);

        return embedBuilder.build();
    }

    private void sendErrorMessage(MessageReceivedEvent event, String description) {
        EmbedBuilder noArgEmbed = new EmbedBuilder()
                .setTitle("Ошибка ❌")
                .setDescription(description)
                .setColor(Color.RED);
        event.getChannel().sendMessageEmbeds(noArgEmbed.build()).queue();
    }
}