/* 
 * This file is part of Quelea, free projection software for churches.
 * 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.quelea.services.utils;

import java.util.HashMap;
import java.util.Set;

/**
 * Checks the type of the line.
 *
 * @author Michael
 */
public class LineTypeChecker {

    /**
     * The type of the line.
     */
    public enum Type {

        NORMAL, TITLE, CHORDS, NONBREAK

    }

    private final String line;

    /**
     * Create a new line type checker to check a particular line.
     *
     * @param line the line to check.
     */
    public LineTypeChecker(String line) {
        this.line = line;
    }

    /**
     * Get the line type.
     *
     * @return the type of the line.
     */
    public Type getLineType() {
        if (checkTitle()) {
            return Type.TITLE;
        } else if (checkChords()) {
            return Type.CHORDS;
        } else if (checkNonBreak()) {
            return Type.NONBREAK;
        } else {
            return Type.NORMAL;
        }
    }

    private boolean checkNonBreak() {
        return line.trim().equals("<>");
    }

    /**
     * Check whether this line is a line containing only chords.
     *
     * @return true if it's a chord line, false otherwise.
     */
    private boolean checkChords() {
        if (line.isEmpty()) {
            return false;
        }
        if (line.toLowerCase().endsWith("//chords")) {
            return true;
        }
        if (line.toLowerCase().endsWith("//lyrics")) {
            return false;
        }
        String checkLine = line.replace('-', ' ');
        checkLine = checkLine.replace('(', ' ');
        checkLine = checkLine.replace(')', ' ');
        checkLine = checkLine.replaceAll("[xX][0-9]+", "");
        checkLine = checkLine.replaceAll("[0-9]+[xX]", "");
        for (String s : checkLine.split("\\s")) {
            if (s.trim().isEmpty()) {
                continue;
            }
            if (!s.matches("([a-gA-G](#|b)?[0-9]*((sus|dim|maj|dom|min|m|aug|add)?[0-9]*){3}(#|b)?[0-9]*)(/([a-gA-G](#|b)?[0-9]*((sus|dim|maj|dom|min|m|aug|add)?[0-9]*){3}(#|b)?[0-9]*))?")) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check whether this line is the title of a section.
     *
     * @return true if it's the title of a section, false otherwise.
     */
    private boolean checkTitle() {
        String processedLine = line.toLowerCase().trim().replace("(", "").replace(")", "");
        if (processedLine.endsWith("//title")) {
            return true;
        }
        return processedLine.toLowerCase().startsWith("verse")
                || processedLine.toLowerCase().startsWith("chorus")
                || processedLine.toLowerCase().startsWith("tag")
                || processedLine.toLowerCase().startsWith("pre-chorus")
                || processedLine.toLowerCase().startsWith("pre chorus")
                || processedLine.toLowerCase().startsWith("coda")
                || processedLine.toLowerCase().startsWith("bridge")
                || processedLine.toLowerCase().startsWith("intro")
                || processedLine.toLowerCase().startsWith("outro");
    }

    private static final HashMap<String, String> titleMap = new HashMap<>();

    static {
        titleMap.put("Verse", " ##### ");
        titleMap.put("Chorus", " ###### ");
        titleMap.put("Tag", " ####### ");
        titleMap.put("Pre-chorus", " ######## ");
        titleMap.put("Pre chorus", " ######### ");
        titleMap.put("Coda", " ########## ");
        titleMap.put("Bridge", " ########### ");
        titleMap.put("Intro", " ############ ");
        titleMap.put("Outro", " ############# ");
    }

    public static String[] encodeTitles(String[] toEncode) {
        String[] ret = new String[toEncode.length];
        for (int i = 0; i < ret.length; i++) {
            String line = toEncode[i];
            if (new LineTypeChecker(line).getLineType() == Type.TITLE) {
                for (String key : titleMap.keySet()) {
                    line = line.replaceAll("(?i)" + key, titleMap.get(key));
                }
            }
            ret[i] = line;
        }
        return ret;
    }

    public static String[] decodeTitles(String[] toDecode) {
        String[] ret = new String[toDecode.length];
        for (int i = 0; i < ret.length; i++) {
            String line = toDecode[i];
            for (String entry : titleMap.values()) {
                line = line.replaceAll("(?i)" + entry, getKeyFromEntry(entry));
            }
            ret[i] = line;
        }
        return ret;
    }

    private static String getKeyFromEntry(String entry) {
        for (String key : titleMap.keySet()) {
            if (titleMap.get(key).equals(entry)) {
                return key;
            }
        }
        return "verse";
    }
}
