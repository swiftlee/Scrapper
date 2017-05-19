package net.jmdev.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

/*************************************************************************
 *
 * J&M CONFIDENTIAL - @author Jon - 05/16/2017 | 21:28
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
public class TextUtils {

    public static String formatText(String txt) {

        if (txt.equalsIgnoreCase(""))
            return "null";

        return ChatColor.translateAlternateColorCodes('&', txt);

    }

    public static Location parseLocation(String loc) {

        try {

            World world = Bukkit.getWorld(loc.split(",")[0]);
            double x = Double.valueOf(loc.split(",")[1]);
            double y = Double.valueOf(loc.split(",")[2]);
            double z = Double.valueOf(loc.split(",")[3]);
            float yaw = Float.valueOf(loc.split(",")[4]);
            float pitch = Float.valueOf(loc.split(",")[5]);

            return new Location(world, x, y, z, yaw, pitch);

        } catch (IndexOutOfBoundsException e) {

            World world = Bukkit.getWorld(loc.split(",")[0]);
            double x = Double.valueOf(loc.split(",")[1]);
            double y = Double.valueOf(loc.split(",")[2]);
            double z = Double.valueOf(loc.split(",")[3]);

            return new Location(world, x, y, z);

        }

    }

}
