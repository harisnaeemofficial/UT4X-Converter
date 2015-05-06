/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xtx.ut4converter.t3d;

import java.text.DecimalFormat;
import javax.vecmath.Vector3d;

/**
 * 
 * @author XtremeXp
 */
public class T3DUtils {
    
    /**
     *  // Begin Polygon Item=Rise Texture=r-plates-g Link=0
     * @param line
     * @param split
     * @return 
     */
    public static String getString(String line, String split){
        if(!line.contains(split)){
            return null;
        }
        return line.split(split+"=")[1].split("\\ ")[0];
    }
    
    /**
     *
     * @param line
     * @param split
     * @return
     */
    public static Integer getInteger(String line, String split){
        if(!line.contains(split)){
            return null;
        }
        return Integer.valueOf(line.split(split+"=")[1].split("\\ ")[0]);
    }
    
    /**
     *
     * @param line
     * @return
     */
    public static Integer getInteger(String line){
        return Integer.valueOf(line.split("\\=")[1]);
    }
    
    /**
     *
     * @param line
     * @return
     */
    public static Short getShort(String line){
        return Short.valueOf(line.split("\\=")[1]);
    }
    
    /**
     *
     * @param line
     * @return
     */
    public static Double getDouble(String line){
        return Double.valueOf(line.split("\\=")[1]);
    }
    
    /**
     *
     * @param line
     * @param defaut
     * @return
     */
    public static Vector3d getVector3d(String line, Double defaut){
        return getVector3d(line, new String[]{"X","Y","Z"}, defaut);
    }
    
    
    /**
     * Remove double whitespaces and trim to t3d line
     * @param t3dLine
     * @return 
     */
    public static String clean(String t3dLine){
        return t3dLine.replaceAll("\\s+", " ").trim();
    }
    
    /**
     * Transform polygon data into vector
     * E.G: "Normal   -00001.000000,+00000.000000,+00000.000000"
     * @param t3dPolyLine
     * @param polyParam
     * @return 
     */
    public static Vector3d getPolyVector3d(String t3dPolyLine, String polyParam){
        t3dPolyLine = clean(t3dPolyLine);
        String tmp=t3dPolyLine.split(polyParam+" ")[1];
        String tmp2[] = tmp.split("\\,");
        
        Vector3d v = new Vector3d();
        
        v.x = Double.valueOf(tmp2[0]);
        v.y = Double.valueOf(tmp2[1]);
        v.z = Double.valueOf(tmp2[2]);
        
        return v;
    }
    
    
    /**
     * Convert 3d vector of poly data to t3d string format
     * @param v
     * @param df
     * @return // +00001.000000,+00000.000000,+00000.000000
     */
    public static String toPolyStringVector3d(Vector3d v, DecimalFormat df){
        
        return df.format(v.x)+","+df.format(v.y)+","+df.format(v.z);
    }
    
    /**
     * Get vector3d from rotation data in t3d format
     * E.G: "Rotation=(Pitch=848,Yaw=-12928,Roll=1208)"
     * @param line
     * @return 
     */
    public static Vector3d getVector3dRot(String line){
        return getVector3d(line, new String[]{"Pitch","Yaw","Roll"}, null);
    }
    
    
    /**
     * Get Vector3d from line
     * E.G: " Location=(X=3864.000000,Y=-5920.000000,Z=-15776.000000)"
     * E.G: "Rotation=(Yaw=8160)"
     * @param line Current line of T3D Level file being analyzed containing Location info
     * @return Vector 3d
     */
    private static Vector3d getVector3d(String line, String[] s, Double defaut)
    {

        Vector3d v;
        
        if(defaut != null){
            v = new Vector3d(defaut, defaut, defaut);
        } else {
            v = new Vector3d(0D, 0D, 0D);
            defaut = 0D;
        }
        
    
        // Location=(X=5632.000000,Z=384.000000)
        //    Location=(X=3864.000000,Y=-5920.000000,Z=-15776.000000)
        line = line.substring(line.indexOf("(")+1);
        line = line.split("\\)")[0];
        //line = line.replaceAll("\\)","");

        String fields[] = line.split(",");
        if(fields.length==3)
        {
            v.x = Double.valueOf(fields[0].split("\\=")[1]);
            v.y = Double.valueOf(fields[1].split("\\=")[1]);
            v.z = Double.valueOf(fields[2].split("\\=")[1]);
        }
        else if(fields.length==2)
        {
            //    Location=(X=1280.000000,Y=2944.000000)
            if(line.contains(s[0])&&line.contains(s[1]))
            {
                    v.x = Double.valueOf(fields[0].split("\\=")[1]);
                    v.y = Double.valueOf(fields[1].split("\\=")[1]);
                    v.z = defaut;
            }
            //    Location=(X=1280.000000,Y=2944.000000)
            else if(line.contains(s[0])&&line.contains(s[2]))
            {
                    v.x = Double.valueOf(fields[0].split("\\=")[1]);
                    v.y = defaut;
                    v.z = Double.valueOf(fields[1].split("\\=")[1]);
            }
            else if(line.contains(s[1])&&line.contains(s[2]))
            {
                    v.x = defaut;
                    v.y = Double.valueOf(fields[0].split("\\=")[1]);
                    v.z = Double.valueOf(fields[1].split("\\=")[1]);
            }
        }
        else if(fields.length==1)
        {
            if(line.contains(s[0]))
            {
                    v.x = Double.valueOf(fields[0].split("\\=")[1]);
            }
            //    Location=(X=1280.000000,Y=2944.000000)
            else if(line.contains(s[1]))
            {
                    v.y = Double.valueOf(fields[0].split("\\=")[1]);
            }
            else if(line.contains(s[2]))
            {
                    v.z = Double.valueOf(fields[0].split("\\=")[1]);
            }
        }
        
        return v;
    }

    /**
     * Remove bad chars from string
     * (basically unsupported chars for UE4 editor)
     * @param mapName
     * @return 
     */
    public static String filterName(String mapName){
        
        String s = "";
        
        for(char x : mapName.toCharArray()){
            
            int val = (int) x;

            if( (val == 45) // "-" 
                    || (48 <= val && val <= 57) // 0 -> 9
                        || (65 <= val && val <= 90) // A -> Z
                            || (97 <= val && val <= 125)){ // a -> z
                s += (char) val;
            }
        }
        
        return s;
    }
}
