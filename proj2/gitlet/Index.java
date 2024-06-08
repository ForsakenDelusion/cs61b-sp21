package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static gitlet.Repository.GITLET_INDEX;
import static gitlet.Utils.*;


public class Index implements Serializable {

    Map<File, Blob> blobs;

    /** When the Repository.init() be called this constructor will save an empty List in Index */
    Index(){
        this.blobs = new HashMap();
        this.saveIndex();
    }

    /** blobArray getter */
    public Map<File, Blob> getBlobArray() {
        return this.blobs;
    }

    /** Add a new blob into the blobArray */
    void addInBlobArray(File file,Blob blob) {
        Index curIndex = getCurrentIndex();
        if(blob == null){
            return;
        }
        curIndex.blobs.put(file,blob);
        curIndex.saveIndex();
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

    /** Remove the same Hash obj in BlobArray */
    void removeInBlobSet(Blob blob) {
        String hashId = blob.getHashId();
        for (Blob b : this.blobs.values()) {
            if (b.getHashId().equals(hashId)) {
                this.blobs.remove(b);
            }
        }
        this.saveIndex();
    }
}
