package gitlet;

import java.io.Serializable;
import java.util.ArrayList;

import static gitlet.Repository.GITLET_INDEX;
import static gitlet.Utils.*;


public class Index implements Serializable {

    ArrayList<Blob> blobArry = new ArrayList<>();

    Index(){
        this.saveIndex();
    }

    public ArrayList<Blob> getBlobArry() {
        return this.blobArry;
    }

    static void add(Blob blob){
        readObject(GITLET_INDEX,Index.class).getBlobArry().add(blob);
    }

    void saveIndex(){
        writeObject(GITLET_INDEX,Index.class);
    }
}
