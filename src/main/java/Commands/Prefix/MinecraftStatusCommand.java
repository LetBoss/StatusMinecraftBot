package Commands.Prefix;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

public class MinecraftStatusCommand extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] message = event.getMessage().getContentRaw().split(" ");
        if (message[0].equals(".ping")) {
            handlePingCommand(event, message);
        }
    }

    private void handlePingCommand(MessageReceivedEvent event, String[] message) {
        if (message.length != 2) {
            sendErrorMessage(event, "Пожалуйста, укажите IP-адрес сервера. Использование: `.ping <IP-адрес>`");
            return;
        }

        String ip = message[1];
        try {
            MinecraftServerStatus serverStatus = new MinecraftServerStatus(ip);
            EmbedBuilder embed = serverStatus.getServerStatusEmbed(event.getAuthor());

            event.getChannel().sendMessageEmbeds(embed.build()).queue();

            File motdFile = serverStatus.createMotdFile();
            event.getChannel().sendFiles(FileUpload.fromData(motdFile)).queue();

        } catch (Exception e) {
            sendErrorMessage(event, "Сервер не отвечает, попробуйте чуть позже");
        }
    }

    private void sendErrorMessage(MessageReceivedEvent event, String description) {
        EmbedBuilder noArgEmbed = new EmbedBuilder()
                .setTitle("Ошибка ❌")
                .setDescription(description)
                .setColor(Color.red);
        event.getChannel().sendMessageEmbeds(noArgEmbed.build()).queue();
    }

    class MinecraftServerStatus {
        private final String ipAddress;

        public MinecraftServerStatus(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        public EmbedBuilder getServerStatusEmbed(User user) throws Exception {
            JSONObject json = fetchServerData();
            return createEmbed(json, user);
        }

        public File createMotdFile() throws Exception {
            JSONObject json = fetchServerData();
            String motdRaw = json.getJSONObject("motd").optString("raw", "N/A");

            File motdFile = new File("motd.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(motdFile))) {
                writer.write(motdRaw);
            }
            return motdFile;
        }

        private JSONObject fetchServerData() throws IOException {
            String url = "https://api.mcsrvstat.us/3/" + this.ipAddress;
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            StringBuilder content;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
            }

            return new JSONObject(content.toString());
        }

        private EmbedBuilder createEmbed(JSONObject json, User user) {
            EmbedBuilder embed = new EmbedBuilder();
           /*
                    .setColor(Color.green)
                    .setTitle("🟢 " + ipAddress + " Информация о сервере:")
                    .addField("IP or Port", "```" + json.optString("ip", "N/A") + ":" + json.optString("port", "N/A") + "```", false)
                    .addField("Players", "```" + json.getJSONObject("players").optInt("online", 0) + "/" + json.getJSONObject("players").optInt("max", 0) + "```", true)
                    .addField("Version", "```" + json.optString("version", "N/A") + "```", true)
                    .addField("Software", "```" + json.optString("software", "None") + "```", true)
                    .addField("Ping", "```" + getPing(ipAddress) + "ms```", true)


            */


            embed.setColor(Color.RED)
                    .setAuthor(user.getName(), null, user.getAvatarUrl())
                    .setTitle("Информация о сервере " + ipAddress)
                    .setDescription("**IP:** " + json.optString("ip", "N/A") + ":" + json.optString("port", "N/A") + "\n" +
                            "**SRV:** " + json.optString("hostname", "N/A") + "\n" +
                            "**Ping:** " + getPing(ipAddress) + "ms\n" +
                            "**Players:** " + json.getJSONObject("players").optInt("online", 0) + "/" + json.getJSONObject("players").optInt("max", 0) + "\n" +
                            "**Version:** " + json.optString("version", "N/A") + "\n" +
                            "**Software:** " + json.optString("software", "None") + "\n" +
                            "**MOTD:** В файле")
                    .setThumbnail("https://api.mcsrvstat.us/icon/" + ipAddress);
            return embed;
        }

        private int getPing(String ipAddress) {
            try {
                String ip = ipAddress.split(":")[0];
                InetAddress address = InetAddress.getByName(ip);
                long startTime = System.currentTimeMillis();
                if (address.isReachable(400)) {
                    return (int) (System.currentTimeMillis() - startTime);
                } else {
                    return -1;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        }
    }
}