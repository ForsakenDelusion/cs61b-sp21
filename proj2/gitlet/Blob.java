package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static gitlet.Repository.CWD;
import static gitlet.Repository.GITLET_OBJECTS;
import static gitlet.Utils.*;

public class Blob implements Serializable {
    String fileName;
    String content;
    String hashId;

    /** content getter */
    public String getContent(){
        return this.content;
    }

    /** Blob constructor */
    public Blob(String fileName, String content){
        this.fileName = fileName;
        this.content = content;
        setID();
    }


    public Blob(String fileName){
        this.fileName = fileName;
        this.content = readContentsAsString(join(CWD,fileName));
        setID();
    }


    /** create a new Blob obj and save it in the objects dictionary then return this Blob obj */
    static Blob createBlob(String fileName){
        File CWDFile = new File(CWD,fileName);
        String content = readContentsAsString(CWDFile);
        Blob newBlob =  new Blob(fileName,content);
        File blobObj = join(GITLET_OBJECTS,newBlob.getHashId());
        if (!blobObj.exists()) newBlob.saveBlob();
        return newBlob;
    }

   /** Set the hashID */
   void setID() {
       List<Object> vals = new ArrayList<Object>();
       vals.add(fileName);
       vals.add(content);
       this.hashId = sha1(vals);
   }

   /** HashId getter */
   String getHashId(){
       return this.hashId;
   }

   String getFileName(){
       return this.fileName;
   }

    /** Serialize current Blob */
    public void saveBlob() {
        writeObject(join(GITLET_OBJECTS,this.getHashId()),this);
    }

    /** Find the corresponding Blob based on the file name of the workspace */
    static Blob findBlobByFileName(String fileName) {
        File CWDFile = new File(CWD,fileName);
        String content = readContentsAsString(CWDFile);
        Blob newBlob =  new Blob(fileName,content);
        File blobObj = join(GITLET_OBJECTS,newBlob.getHashId());
        if (blobObj.exists())  return readObject(blobObj,Blob.class);
        return null;
    }

    /** Delete the Blob in objects dictionary */
    static void deleteBlobInObject(Blob blob) {
        String hashId = blob.getHashId();
        File searchBlob = join(GITLET_OBJECTS,hashId);
        if(searchBlob.exists()) {
            Utils.restrictedDelete(searchBlob);
        } else {
            System.out.println("No reason to remove the file.");
        }
    }

}
