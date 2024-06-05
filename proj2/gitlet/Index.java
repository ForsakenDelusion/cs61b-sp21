package gitlet;

import java.io.Serializable;
import java.util.ArrayList;

import static gitlet.Repository.GITLET_INDEX;
import static gitlet.Utils.*;


public class Index implements Serializable {

    ArrayList<Blob> blobArray;

    /** When the Repository.init() be called this constructor will save an empty List in Index */
    Index(){
        this.blobArray = new ArrayList<>();
        this.saveIndex();
    }

    /** blobArray getter */
    public ArrayList<Blob> getBlobArray() {
        return this.blobArray;
    }

    /** Add a new blob into the blobArray */
    void addInBlobArray(Blob blob) {
        Index curIndex = getCurrentIndex();
        if(blob == null){
            return;
        }
        curIndex.blobArray.add(blob);
        curIndex.saveIndex();
    }

    static Index getCurrentIndex(){
        return readObject(GITLET_INDEX,Index.class);
    }

    /** Serialize the index obj */
    void saveIndex() {
        writeObject(GITLET_INDEX,this);
    }

    /** IsEmpty function */
    boolean blobArrayIsEmpty(){
        return this.blobArray.isEmpty();
    }

    /** Reset the index */
    static void resetIndex(){
        writeObject(GITLET_INDEX,new Index());
    }
}
