package net.jmdev.command;

import net.jmdev.Scrapper;
import net.jmdev.database.ScrapperDatabase;
import net.jmdev.utils.TextUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

/*************************************************************************
 *
 * J&M CONFIDENTIAL - @author Jon - 05/16/2017 | 21:15
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
public class CommandScrapper implements CommandExecutor {

    public static HashMap<UUID, String> scrapperUsers = new HashMap<>();
    ScrapperDatabase scrapperDatabase;
    private Scrapper plugin;
    private FileConfiguration config;

    public CommandScrapper(Scrapper plugin, ScrapperDatabase scrapperDatabase) {

        this.plugin = plugin;
        config = plugin.getConfig();
        this.scrapperDatabase = scrapperDatabase;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("scrapper")) {

            if (sender instanceof Player) {

                Player p = (Player) sender;
                String scrapperName;

                if (args.length == 0) {


                } else if (args.length == 1) {

                    scrapperName = args[0];

                    if (!scrapperName.equalsIgnoreCase("add") && !scrapperName.equalsIgnoreCase("rm")) {

                        if (scrapperDatabase.getYML().get(scrapperName) != null) {

                            for (String names : scrapperUsers.values()) {

                                if (names.equalsIgnoreCase(scrapperName)) {

                                    p.sendMessage(TextUtils.formatText("&cSomeone is already using that!"));
                                    return true;

                                }

                            }

                            Inventory inv = Bukkit.createInventory(null, 27, TextUtils.formatText(config.getString("scrapper.gui.name")));
                            scrapperUsers.put(p.getUniqueId(), scrapperName);
                            p.openInventory(inv);

                        } else {

                            p.sendMessage(TextUtils.formatText("&cThat scrapper is no longer available or doesn't exist."));

                        }

                    } else {

                        p.sendMessage(TextUtils.formatText("&cInvalid argument length, please provide one more argument!"));

                    }

                } else if (args.length == 2) {

                    if (sender.hasPermission("scrapper")) {

                        scrapperName = args[1];

                        if (args[0].equalsIgnoreCase("add")) {

                            Location blockLookingLoc = p.getTargetBlock((Set<Material>) null, 10).getLocation();

                            if (blockLookingLoc.getBlock().getType() != Material.IRON_BLOCK) {

                                p.sendMessage(TextUtils.formatText("&cYou must select an IRON_BLOCK for this to work."));
                                return true;

                            }

                            if (scrapperDatabase.getYML().get(scrapperName) == null) {

                                scrapperDatabase.addScrapper(scrapperName, blockLookingLoc);
                                p.sendMessage(TextUtils.formatText("&aYou have added a Scrapper to the database!"));

                            } else {

                                p.sendMessage(TextUtils.formatText("&cThat Scrapper already exists!"));

                            }

                        } else if (args[0].equalsIgnoreCase("rm")) {

                            if (scrapperDatabase.getYML().get(scrapperName) != null) {

                                scrapperDatabase.getYML().set(scrapperName, null);
                                p.sendMessage(TextUtils.formatText("&aYou have removed a Scrapper from the database!"));

                            } else {

                                p.sendMessage(TextUtils.formatText("&cThat Scrapper doesn't exist!"));

                            }

                        } else {

                            p.sendMessage(TextUtils.formatText("&cInvalid argument '" + args[0] + "'. Try 'add' or 'rm'."));

                        }

                    } else {

                        sender.sendMessage(TextUtils.formatText("&cYou do not have permission to use this command!"));

                    }

                } else {

                    p.sendMessage(TextUtils.formatText("&cInvalid argument length!"));

                }

            } else {

                sender.sendMessage(TextUtils.formatText("&cThe console cannot execute this command!"));

            }

            return true;


        }

        return false;
    }

}
