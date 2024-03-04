package uk.co.asepstrath.bank;
import java.util.UUID;

public class Business {

    String buisnessID;

    String buisnessName;

    String category;

    Boolean beenSanctioned;


    public Business(String buisnessID, String buisnessName, String category, boolean beenSanctioned){

        this.buisnessID = buisnessID;
        this.buisnessName = buisnessName;
        this.category = category;
        this.beenSanctioned = beenSanctioned;
    }

    String getbusinessID(){
        return buisnessID;
    }
    String getBuisnessName(){
        return buisnessName;
    }

    String getCategory(){
        return category;
    }

    boolean getBeenSanctioned(){
        return beenSanctioned;
    }

    void setBuisnessID(String buisnessID){
        this.buisnessID = buisnessID;
    }
    void setCategory(String category){
        this.category = category;
    }
    void setBuisnessName(String buisnessName){
        this.buisnessName = buisnessName;
    }
    void setBeenSanctioned(boolean beenSanctioned){
        this.beenSanctioned = beenSanctioned;
    }

    @Override
    public String toString() {
        return "Buisness: " +
                "ID: " + buisnessID + ' ' +
                "Category: " + category + ' ' +
                "Business: " + buisnessName + ' ' +
                "Sanctioned?: " + beenSanctioned;
    }
}
