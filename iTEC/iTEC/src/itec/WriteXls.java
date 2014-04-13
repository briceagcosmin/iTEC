/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package itec;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author cosmin
 */
public class WriteXls {
    
    
    HSSFSheet sheet;
    HSSFWorkbook workBook ;
    
    public WriteXls(){}
    
    
    public void writeData(Object[][] data,String path) throws IOException{
        workBook = new HSSFWorkbook();
        sheet = workBook.createSheet("iTEC");
        
        int rowCnt = 0;

        for(Object[] obj: data){
            Row row  = sheet.createRow(rowCnt++);
            int cell = 0;
            for(Object o : obj){
                Cell cell1 = row.createCell(cell++);
                if(o instanceof Integer)
                cell1.setCellValue((Integer) o);
                else if(o instanceof Double)
                    cell1.setCellValue((Double) o);
                else
                    cell1.setCellValue((String) o);
            }
        }
        
        try {
            FileOutputStream out = new FileOutputStream(new File(path));
            workBook.write(out);
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WriteXls.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
