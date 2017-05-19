package net.jmdev.database;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.FileNotFoundException;

/*************************************************************************
 *
 * J&M CONFIDENTIAL - @author Jon - 05/16/2017 | 18:32
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
public class ScrapperDatabase {

    public static YamlConfiguration scrapperDatabase = new YamlConfiguration();

    public YamlConfiguration getYML() {

        return scrapperDatabase;

    }

    public void save() {

        try {

            scrapperDatabase.save("plugins/Scrapper/scrapperDatabase.yml");

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void load() {

        if (scrapperDatabase == null) {

            scrapperDatabase = new YamlConfiguration();

        }

        try {

            scrapperDatabase.load("plugins/Scrapper/scrapperDatabase.yml");

        } catch (FileNotFoundException e1) {

            save();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void reload() {

        save();
        load();

    }

    public void addScrapper(String name, Location location) {

        scrapperDatabase.set(name, location.getWorld().getName() + "," + (int) location.getX() + "," + (int) location.getY() + "," + (int) location.getZ());
        reload();

    }

}
