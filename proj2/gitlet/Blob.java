package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Repository.CWD;
import static gitlet.Repository.GITLET_OBJECTS;
import static gitlet.Utils.*;

public class Blob implements Serializable {
    String fileName;
    String content;

    /** content getter */
    public String getContent(){
        return this.content;
    }

    /** Blob constructor */
    public Blob(String fileName, String content){
        this.fileName = fileName;
        this.content = content;
    }


    /** create a new Blob obj and save it in the objects dictionary then return this Blob obj */
    static Blob createBlob(String fileName){
        File CWDFile = new File(CWD,fileName);
        if(!CWDFile.exists()){
            System.out.println("File does not exist.");
            return null;
        }
        String content = readContentsAsString(CWDFile);
        Blob newBlob =  new Blob(fileName,content);
        newBlob.saveBlob();
        return newBlob;
    }

    /** Get current Blob`s sha1 hash */
    static String getBlobSHA1(String content) {
        return sha1(content);
    }

    /** Serialize current Blob */
    public void saveBlob() {
        writeObject(join(GITLET_OBJECTS,getBlobSHA1(this.getContent())),this);
    }
}
