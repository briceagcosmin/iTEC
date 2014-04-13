/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package itec;

import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cosmin
 */
/*
@param PATH the path for the database
@param DRIVER the driver for the database
@param SQL  the sql commands that will be execute
*/
public class DataBaseConnection {
    private final String PATH = "jdbc:sqlite:database.s3db";
    private final String DRIVER = "org.sqlite.JDBC";
    private String SQL;
    private Connection conn;
    private Statement stm;
    private ResultSet rst;
    private int sizeProd = 0;
    private int sizeCat = 0;
    
    public DataBaseConnection (){
        try{
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(PATH);
            conn.setAutoCommit(true);
            stm = conn.createStatement();
         
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*
    @param temp temporary String array
    @param sizeCat find out the number of rows from the categories table
    @function getCategories() return the elements of the table
    @function getSizeProd determinates the number of row from the items table
    @function connectTo checks if both the user and the password are in the database
    */
    public Object[] getCategories(){
        Object[] temp = null;
        int i = 0;
        try {
            SQL = "SELECT name FROM categories";
            rst = stm.executeQuery(SQL);
            while(rst.next()){
                ++sizeCat;
            }
            temp = new String[sizeCat];
            SQL = "SELECT name FROM categories";
            rst = stm.executeQuery(SQL);
            while(rst.next()){
             assert i > sizeCat:i;
             temp[i++] = rst.getString("name");
            }
            
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return temp;
    }
    
    public int getSizeCat(){
        return sizeCat;
    }
    
    public ArrayList<Object> getItems(String name){
        
        ArrayList<Object> array = new ArrayList<Object>();
        int i = 0;
        try {
            SQL = "SELECT cat_id FROM categories WHERE name='"+name+"'";
            rst = stm.executeQuery(SQL);
            while(rst.next()){
                SQL = "SELECT name FROM items WHERE cat_id='"+rst.getString("cat_id")+"'";
                rst = stm.executeQuery(SQL);
                while(rst.next())
                  array.add(rst.getString("name"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return array;
    }
    
    public Object[] getItems(){
        
      Object[] array = null;
        int i = 0,size = 0;
        try {
            SQL = "SELECT name FROM items";
            rst = stm.executeQuery(SQL);
            while(rst.next()){
                 ++size;
            }
            SQL = "SELECT name FROM items";
            rst = stm.executeQuery(SQL);
            array = new Object[size];
            while(rst.next()){
                array[i++] = rst.getString("name");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return array;
    }
    
    public int getSizeProd(){
        return sizeProd;
    }
    
    public boolean connectTo(String name,String password){
        SQL = "SELECT user_id,password FROM users WHERE user_id='"+name+"'";
        String nameV,passwordV;
        try {
            rst = stm.executeQuery(SQL);
            while(rst.next()){
                nameV = rst.getString("user_id");
                passwordV = rst.getString("password");
                if(nameV.equals(name) && passwordV.equals(password)){
                    return true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public Object[] getAboutUsers(String id){
        Object[] temp = new String[4];
        GregorianCalendar calendar = new GregorianCalendar();
        int day,year,month;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        SQL = "SELECT name,group_id,sold FROM users WHERE user_id='"+id+"'";
        String gr_id;
        try {
            rst = stm.executeQuery(SQL);
            while(rst.next()){
                temp[0] = rst.getString("name");
                //temp[1] = rst.getString("sold");
                temp[1] = day+"/"+(month+1)+"/"+year;
                temp[2] = rst.getString("sold");
                gr_id = rst.getString("group_id");
                SQL = "SELECT name FROM groups WHERE group_id='"+gr_id+"'";
                rst = stm.executeQuery(SQL);
                while(rst.next()){
                    temp[3] = rst.getString("name");
                }
                  
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return temp;
    }
    
    public void vote(String item,String userName,int value){
        GregorianCalendar calendar = new GregorianCalendar();
        int day,month,year,hour,min,sec;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        hour = calendar.get(Calendar.HOUR);
        min = calendar.get(Calendar.MINUTE);
        sec = calendar.get(Calendar.SECOND);
        String date = day+"/"+(month+1)+"/"+year+"/"+hour+":"+min+":"+sec;
        
        SQL = "INSERT INTO votes (userName,item,points,date) values('"+userName+"','"+item+"','"+value+"','"+date+"')";
        try {
            stm.executeUpdate(SQL);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int substraction(String id_user,int value){
        SQL = "SELECT sold FROM users WHERE user_id='"+id_user+"'";
        int oldSold = 0,newSold;
        try {
            rst = stm.executeQuery(SQL);
            while(rst.next()){
                oldSold = rst.getInt("sold");
                if(oldSold != 0){
                    newSold = oldSold - value;
                    SQL = "UPDATE users SET sold='"+newSold+"' WHERE user_id='"+id_user+"'";
                    stm.executeUpdate(SQL);
                    System.out.println(oldSold-value);
             
                }
            }
             
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (oldSold-value);
    }
    
    public void addUsers(String userId,String groupId,String name,String password,int sold){
        SQL = "INSERT INTO users (user_id,group_id,name,password,sold) values ("
                + "'"+userId+"','"+groupId+"','"+name+"','"+password+"','"+sold+"')";
        try {
            stm.executeUpdate(SQL);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void deleteUser(String userId){
        SQL = "DELETE FROM users WHERE user_id='"+userId+"'";
        try {
            stm.executeUpdate(SQL);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addPointsToGroup(String groupId,int value){
        try {
            SQL = "UPDATE users SET sold='"+value+"' WHERE group_id='"+groupId+"'";
            stm.executeUpdate(SQL);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addPointsToUser(String userId,int value){
        try {
            SQL = "UPDATE users SET sold='"+value+"' WHERE user_id='"+userId+"'";
            stm.executeUpdate(SQL);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addProducts(String itemId,String catId,String name,int price){
        try {
            SQL = "INSERT INTO items (item_id,cat_id,name,price) values('"+itemId+"','"+catId+"','"+name+"','"+price+"')";
            stm.executeUpdate(SQL);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addGroup(String id,String name){
        try {
            SQL = "INSERT INTO groups (group_id,name) values('"+id+"','"+name+"')";
            stm.executeUpdate(SQL);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addCatProd(String id,String name){
        try{
            SQL = "INSERT INTO categories (cat_id,name) values('"+id+"','"+name+"')";
            stm.executeUpdate(SQL);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addPriceProd(String id,double price){
        try {
            SQL = "UPDATE items SET price='"+price+"' WHERE item_id='"+id+"'";
            stm.executeUpdate(SQL);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void deleteGroup(String id){
        try {
            SQL = "DELETE FROM groups WHERE group_id='"+id+"'";
            stm.executeUpdate(SQL);
            SQL = "DELETE FROM users WHERE group_id='"+id+"'";
            stm.executeUpdate(SQL);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void deleteCatProd(String id){
        try {
            SQL = "DELETE FROM categories WHERE cat_id='"+id+"'";
            stm.executeUpdate(SQL);
            SQL = "DELETE FROM items WHERE cat_id='"+id+"'";
            stm.executeUpdate(SQL);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void deleteProduct(String id){
        SQL = "DELETE FROM items WHERE item_id='"+id+"'";
        try {
            stm.executeUpdate(SQL);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Object[][] getReport(){
        Object[][] temp = null;
        int size = 0;
        int i = 0;
        try {
            
            SQL = "SELECT * FROM votes";
            rst = stm.executeQuery(SQL);
            while(rst.next()){
                ++size;
            }
           SQL = "SELECT * FROM votes";
           rst = stm.executeQuery(SQL);
           temp = new Object[size][5];
           while(rst.next()){
               temp[i][0] = rst.getString("id_vote");
               temp[i][1] = rst.getString("userName");
               temp[i][2] = rst.getString("item");
               temp[i][3] = rst.getString("points");
               temp[i++][4] = rst.getString("date");
           }
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return temp;
    }
    
    public String[] getProducts(){
        String[] temp = null;
        int size = 0,i = 0;
        SQL = "SELECT name FROM items ";
        try {
            rst = stm.executeQuery(SQL);
            while(rst.next()){
                ++size;
            }
            SQL = "SELECT name FROM items ";
            rst = stm.executeQuery(SQL);
            temp = new String[size];
            
            while(rst.next()){
                temp[i++] = rst.getString("name");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return temp;
    }
    
    public String[] getDate(){
        String[] temp = null;
        String[] date = new String[2];
        int size = 0,i=0;
        SQL = "SELECT date FROM votes";
        try {
            rst = stm.executeQuery(SQL);
            while(rst.next()){
                ++size;
            }
            SQL = "SELECT date FROM votes";
            rst = stm.executeQuery(SQL);
            temp = new String[size];
            while(rst.next()){
                temp[i++] = rst.getString("date");
            }
            date[0] = temp[0];
            date[1] = temp[(temp.length)-1];
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return date;
    }
}
