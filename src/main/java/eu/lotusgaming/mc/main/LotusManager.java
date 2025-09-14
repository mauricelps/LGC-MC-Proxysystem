package eu.lotusgaming.mc.main;

import java.io.File;
import java.io.IOException;

import org.simpleyaml.configuration.file.YamlFile;

import eu.lotusgaming.mc.bot.command.MC_Verify;
import eu.lotusgaming.mc.bot.event.ChatBridgeToDiscord;
import eu.lotusgaming.mc.game.command.HubCommand;
import eu.lotusgaming.mc.game.command.MC_VerifyIG;
import eu.lotusgaming.mc.game.command.punishments.WarnCommand;
import eu.lotusgaming.mc.game.event.ChatBridgeInfoReceiver;
import eu.lotusgaming.mc.game.event.MaintenanceHandler;
import eu.lotusgaming.mc.game.event.UserLogEventsDCB;
import eu.lotusgaming.mc.misc.MySQL;
import eu.lotusgaming.mc.misc.Serverupdater;
import net.dv8tion.jda.api.JDA;

public class LotusManager {
	
	public static File mainFolder = new File("plugins/LotusGaming");
	public static File mainConfig = new File("plugins/LotusGaming/config.yml");
	
	public void preInit() {
		long current = System.currentTimeMillis();
		
		if(!mainFolder.exists()) mainFolder.mkdirs();
		if(!mainConfig.exists()) try { mainConfig.createNewFile(); } catch (Exception ex) { };
		
		try {
			YamlFile cfg = YamlFile.loadConfiguration(mainConfig);
			cfg.addDefault("MySQL.Host", "127.0.0.1");
			cfg.addDefault("MySQL.Port", "3306");
			cfg.addDefault("MySQL.Database", "TheDataBaseTM");
			cfg.addDefault("MySQL.Username", "user");
			cfg.addDefault("MySQL.Password", "pass");
			cfg.addDefault("System.Bottoken", "The Bottoken goes in here.");
			cfg.addDefault("System.HashPass", "SomeRandompassword");
			cfg.options().copyDefaults(true);
			cfg.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			YamlFile cfg = YamlFile.loadConfiguration(mainConfig);
			if(!cfg.getString("MySQL.Password").equalsIgnoreCase("pass")) {
				MySQL.connect(cfg.getString("MySQL.Host"), cfg.getString("MySQL.Port"), cfg.getString("MySQL.Database"), cfg.getString("MySQL.Username"), cfg.getString("MySQL.Password"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Main.logger.info("Pre-Initialisation took " + (System.currentTimeMillis() - current) + "ms.");
	}
	
	public void init() {
		long current = System.currentTimeMillis();
		
		Main.main.getProxy().getPluginManager().registerCommand(Main.main, new HubCommand("hub"));
		Main.main.getProxy().getPluginManager().registerCommand(Main.main, new HubCommand("lobby"));
		Main.main.getProxy().getPluginManager().registerCommand(Main.main, new HubCommand("l"));
		Main.main.getProxy().getPluginManager().registerCommand(Main.main, new MC_VerifyIG("verify"));
		
		Main.main.getProxy().getPluginManager().registerListener(Main.main, new MaintenanceHandler());
		
		Main.logger.info("Initialisation took " + (System.currentTimeMillis() - current) + "ms.");
	}
	
	public void init(JDA jda) {
		long current = System.currentTimeMillis();
		
		jda.addEventListener(new MC_Verify(jda));
		
		Main.main.getProxy().getPluginManager().registerCommand(Main.main, new WarnCommand("warn", jda));
		
		Main.main.getProxy().getPluginManager().registerListener(Main.main, new ChatBridgeToDiscord(jda));
		Main.main.getProxy().getPluginManager().registerListener(Main.main, new UserLogEventsDCB(jda));
		Main.main.getProxy().getPluginManager().registerListener(Main.main, new ChatBridgeInfoReceiver(jda));

		Main.logger.info("Initialisation with JDA took " + (System.currentTimeMillis() - current) + "ms.");
	}
	
	public void postInit() {
		long current = System.currentTimeMillis();
		
		BotMain.startBot();
		Serverupdater.startScheduler();
		LotusController lc = new LotusController();
		lc.initPrefixSystem();
		lc.initLanguageSystem();
		lc.initPlayerLanguages();
		
		Main.logger.info("Post-Initialisation took " + (System.currentTimeMillis() - current) + "ms.");
	}

}
