package Commands.Prefix;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class HelpCommand extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent event){
        if(event.getMessage().getContentRaw().equals(".help")){
            EmbedBuilder helpEmbed = new EmbedBuilder();
            helpEmbed
                    .setTitle("Список команд:")
                    .addField("Команды:", "**.help** - Список команд бота\n**.ping** `<ip>` - Информация о Minecraft сервере\n**.ip** `<ip>` - Информация о IP адресе", true)
                    .setColor(Color.red);
            event.getChannel().sendMessageEmbeds(helpEmbed.build()).queue();
        }
    }
}
