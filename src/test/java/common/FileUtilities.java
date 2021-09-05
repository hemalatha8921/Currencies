package common;

import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

public class FileUtilities {
    final static File homedir = new File(System.getProperty("user.dir"));

    public static void createOutputFolderAndStoreTheExchangeRates(String date,String currencyCode, JsonNode response) throws IOException{
        String dirName = "output_"+date;
        //Create folder
        final File dir = new File(homedir,dirName);
        if(!dir.exists() && !dir.mkdirs()){
            throw new IOException("Unable to create Folder : "+dir.getAbsolutePath());
        }
        File jsonFile = new File("./"+dirName+"/"+date+"_"+currencyCode+".json");
        System.out.println("While Create folder path is :"+jsonFile.getAbsolutePath());
        if(!jsonFile.exists()){
            FileWriter fileWriter = new FileWriter(jsonFile);
            fileWriter.write(response.toString());
            fileWriter.flush();
            fileWriter.close();
        }else {
            System.out.println("The Exchange rates are already stored with todays date so we are not storing again");
        }

    }

    public static String readFile(String filePath) throws IOException{
        byte[] encodedValue = Files.readAllBytes(Paths.get(filePath));
        return new String(encodedValue, Charset.defaultCharset());
    }
}
