package com.willzcode;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by willz on 2018/4/4.
 * A bukkit plugin record online player
 */
public class OnlineRecorder extends JavaPlugin {
    private BukkitTask task;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp() && args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                initialize();
            }
        }
        return true;
    }

    @Override
    public void onEnable() {
        initialize();
    }

    private void initialize() {
        saveDefaultConfig();
        reloadConfig();
        FileConfiguration cfg = getConfig();
        long interval = cfg.getLong("recording-interval", 60L);

        if (task != null) {
            task.cancel();
        }

        task = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            Date now = new Date();
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(now);

            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
            String day = ft.format(now);
            ft = new SimpleDateFormat("HH:mm:ss");
            String key = ft.format(now);
            int value = Bukkit.getServer().getOnlinePlayers().size();

            File datafile = new File(getDataFolder().getAbsolutePath() + "/", day + ".yml");

            if (!datafile.exists()) {
                try {
                    if (!datafile.createNewFile())
                        return;
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }

            YamlConfiguration datayml = YamlConfiguration.loadConfiguration(datafile);
            datayml.set(key, value);
            try {
                datayml.save(datafile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0L, interval * 20L);

    }
}
