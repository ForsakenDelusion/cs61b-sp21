package gitlet;

import java.io.Serializable;
import java.util.ArrayList;

import static gitlet.Repository.GITLET_INDEX;
import static gitlet.Utils.*;


public class Index implements Serializable {

    ArrayList<Blob> blobArray = new ArrayList<>();

    /** When the Repository.init() be called this constructor will save an empty List in Index */
    Index(){
        this.saveIndex();
    }

    /** blobArray getter */
    public ArrayList<Blob> getBlobArray() {
        return this.blobArray;
    }

    /** Add a new blob into the blobArray */
    static void add(Blob blob) {
        if(blob == null){
            return;
        }
        readObject(GITLET_INDEX,Index.class).getBlobArray().add(blob);
    }

    /** Serialize the index obj */
    void saveIndex() {
        writeObject(GITLET_INDEX,Index.class);
    }
}
