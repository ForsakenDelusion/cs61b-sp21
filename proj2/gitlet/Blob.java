package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Repository.CWD;
import static gitlet.Repository.GITLET_OBJECTS;
import static gitlet.Utils.*;

public class Blob implements Serializable {
    String fileName;
    String content;

    public String getContent(){
        return this.content;
    }

    public Blob(String fileName, String content){
        this.fileName = fileName;
        this.content = content;
    }

    static Blob createBlob(String fileName){
        File CWDFile = new File(CWD,fileName);
        String content = readContentsAsString(CWDFile);
        Blob newBlob =  new Blob(fileName,content);
        newBlob.saveBlob();
        return newBlob;

    }

    static String getBlobSHA1(String content){
        return sha1(content);
    }

    public void saveBlob(){
        writeObject(join(GITLET_OBJECTS,getBlobSHA1(this.getContent())),this);
    }
}
