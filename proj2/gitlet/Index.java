package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static gitlet.Repository.GITLET_INDEX;
import static gitlet.Utils.*;


public class Index implements Serializable {

    Map<File, Blob> blobs;
    Map<File, Blob> deleteBlobs;

    /** When the Repository.init() be called this constructor will save an empty List in Index */
    Index(){
        this.blobs = new HashMap<>();
        this.deleteBlobs = new HashMap<>();
        this.saveIndex();
    }

    /** blobSet getter */
    public Map<File, Blob> getBlobSet() {
        return this.blobs;
    }

    /** deleteBlobs getter */
    public Map<File, Blob> getDeleteBlobs() {
        return this.deleteBlobs;
    }

    static Index getCurrentIndex(){
        return readObject(GITLET_INDEX,Index.class);
    }

    /** Serialize the index obj */
    void saveIndex() {
        writeObject(GITLET_INDEX,this);
    }

    /** Reset the index */
    static void resetIndex(){
        writeObject(GITLET_INDEX,new Index());
    }

    void removeInBlobSet(File file) {
        this.getBlobSet().remove(file);
        this.saveIndex();
    }

    void removeInDeleteBlobSet(File file) {
        this.getDeleteBlobs().remove(file);
        this.saveIndex();
    }

    /** Add a new blob into the blobArray */
    void addInBlobSet(File file,Blob blob) {
        Index curIndex = getCurrentIndex();
        if(blob == null){
            return;
        }
        curIndex.getBlobSet().put(file,blob);
        curIndex.saveIndex();
    }

    void addInDeleteBlobSet(File file, Blob blob) {
        Index curIndex = getCurrentIndex();
        curIndex.getDeleteBlobs().put(file, blob);
        curIndex.saveIndex();
    }



}
