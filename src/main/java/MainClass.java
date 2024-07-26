import Commands.Prefix.HelpCommand;
import Commands.Prefix.IPCheckerCommand;
import Commands.Prefix.MinecraftStatusCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class MainClass {
    public static void main(String[] args) throws Exception {
        JDA jda = JDABuilder.createDefault("YOUR_TOKEN_THERE")
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new MinecraftStatusCommand(), new IPCheckerCommand(), new HelpCommand())
                .setMemberCachePolicy(MemberCachePolicy.ALL).build().awaitReady();
    }
}

