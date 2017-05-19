package net.jmdev.listener;

import net.jmdev.Scrapper;
import net.jmdev.command.CommandScrapper;
import net.jmdev.database.ScrapperDatabase;
import net.jmdev.utils.TextUtils;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;

/*************************************************************************
 *
 * J&M CONFIDENTIAL - @author Jon - 05/17/2017 | 00:13
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
public class InventoryCloseListener implements Listener {

    FileConfiguration config;
    private Scrapper plugin;
    private ScrapperDatabase scrapperDatabase;
    private Economy econ;

    public InventoryCloseListener(Scrapper plugin, ScrapperDatabase scrapperDatabase, Economy econ) {

        this.plugin = plugin;
        config = plugin.getConfig();
        this.scrapperDatabase = scrapperDatabase;
        this.econ = econ;

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {

        if (e.getInventory() == null)
            return;

        if (e.getInventory().getName().equalsIgnoreCase(TextUtils.formatText(config.getString("scrapper.gui.name")))) {

            Location scrapperLoc = TextUtils.parseLocation(scrapperDatabase.getYML().getString(CommandScrapper.scrapperUsers.get(e.getPlayer().getUniqueId())));
            Location itemsLoc = scrapperLoc.add(new Vector(0.5, 1, 0.5));

            int money = 0;

            ArrayList<Item> droppedItems = new ArrayList<>();

            for (int i = 0; i < e.getInventory().getContents().length; i++) {

                if (e.getInventory().getContents()[i] != null && e.getInventory().getContents()[i].getType() != Material.AIR) {

                    ItemStack stack = e.getInventory().getContents()[i];

                    if (config.getInt("scrapper.items." + stack.getType().toString()) >= 0)
                        money += config.getInt("scrapper.items." + stack.getType().toString());

                    Item item = e.getPlayer().getWorld().dropItem(itemsLoc, stack);
                    item.setVelocity(new Vector(0, 0, 0));
                    item.setMetadata("immobile", new FixedMetadataValue(plugin, ""));
                    droppedItems.add(item);

                }

            }

            if (config.getBoolean("scrapper.blocks.animation")) {

                if (!droppedItems.isEmpty()) {

                    Block block = e.getPlayer().getWorld().getBlockAt(itemsLoc.add(new Vector(0, 1, 0)));
                    Material material = block.getType();
                    BlockState blockState = block.getState();

                    final int finalMoney = money;


                    for (int i = 1; i < 3; i++) {

                        final int j = i;
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {

                            FallingBlock fallingBlock;

                            if (j == 1) {

                                fallingBlock = e.getPlayer().getWorld().spawnFallingBlock(itemsLoc.subtract(new Vector(0, 1, 0)), material, (byte) 0);

                                blockState.setType(Material.AIR);
                                blockState.setData(new MaterialData(Material.AIR));
                                blockState.update(true, false);

                                fallingBlock.setDropItem(false);
                                fallingBlock.setVelocity(new Vector(0, -0.1, 0));

                                for (Item item : droppedItems)
                                    item.remove();

                                droppedItems.clear();

                            } else if (j == 2) {

                                BlockState state = e.getPlayer().getWorld().getBlockAt(itemsLoc).getState();
                                state.setType(Material.AIR);
                                state.setData(new MaterialData(Material.AIR));
                                state.update(true, false);

                                fallingBlock = e.getPlayer().getWorld().spawnFallingBlock(itemsLoc.subtract(new Vector(0, 0, 0)), material, (byte) 0);
                                fallingBlock.setDropItem(false);
                                fallingBlock.setVelocity(new Vector(0, 0.5, 0));
                                fallingBlock.remove();

                                blockState.setType(Material.IRON_BLOCK);
                                blockState.setData(new MaterialData(Material.IRON_BLOCK));
                                blockState.update(true, false);

                            } else {

                            }

                        }, (i == 1 ? 25 : ((long) 1.75 * 20) + 25));

                    }

                    for (int i = 1; i < 3; i++) {

                        final int j = i;

                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {

                            if (j == 1) {

                                Item moneyDisplay = e.getPlayer().getWorld().dropItem(itemsLoc, new ItemStack(Material.valueOf(config.getString("scrapper.blocks.item"))));
                                moneyDisplay.setVelocity(new Vector(0, 0, 0));
                                moneyDisplay.setMetadata("immobile", new FixedMetadataValue(plugin, ""));
                                moneyDisplay.setCustomName(TextUtils.formatText("&a+&e" + finalMoney));
                                moneyDisplay.setCustomNameVisible(true);
                                droppedItems.add(moneyDisplay);
                                econ.depositPlayer((Player) e.getPlayer(), finalMoney);

                            } else {

                                CommandScrapper.scrapperUsers.remove(e.getPlayer().getUniqueId());
                                droppedItems.get(0).remove();
                                droppedItems.clear();

                            }

                        }, (i == 1 ? ((long) 1.75 * 20) + 25 : 100));

                    }

                } else {

                    CommandScrapper.scrapperUsers.remove(e.getPlayer().getUniqueId());

                }

            } else {

                CommandScrapper.scrapperUsers.remove(e.getPlayer().getUniqueId());
                econ.depositPlayer((Player) e.getPlayer(), money);

            }

        }

    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent e) {

        if (e.getItem().hasMetadata("immobile")) {

            e.setCancelled(true);

        }

    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {

        if (e.getInventory() == null)
            return;

        if (e.getInventory().getName().equalsIgnoreCase(TextUtils.formatText(config.getString("scrapper.gui.name")))) {

            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {


            } else if (config.get("scrapper.items." + e.getCurrentItem().getType().toString().toUpperCase()) == null) {

                e.setCancelled(true);
                return;

            }

        }

    }

}
