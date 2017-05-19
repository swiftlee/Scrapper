package net.jmdev;

import net.jmdev.command.CommandScrapper;
import net.jmdev.database.ScrapperDatabase;
import net.jmdev.listener.InventoryCloseListener;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

/*************************************************************************
 *
 * J&M CONFIDENTIAL - @author Jon - 05/16/2017 | 18:30
 * __________________
 *
 *  [2016] J&M Plugin Development 
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of J&M Plugin Development and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to J&M Plugin Development
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from J&M Plugin Development.
 */
public class Scrapper extends JavaPlugin {

    private ScrapperDatabase scrapperDatabase;
    private Economy econ = null;

    @Override
    public void onEnable() {

        File file = new File("plugins/Scrapper/config.yml");

        if (file.exists())
            reloadConfig();
        else
            saveDefaultConfig();

        scrapperDatabase = new ScrapperDatabase();
        scrapperDatabase.load();

        if (!setupEconomy()) {

            Logger.getLogger("Minecraft").severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;

        }

        getCommand("scrapper").setExecutor(new CommandScrapper(this, scrapperDatabase));
        getServer().getPluginManager().registerEvents(new InventoryCloseListener(this, scrapperDatabase, econ), this);

    }

    @Override
    public void onDisable() {

        scrapperDatabase.save();
        saveConfig();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

}
