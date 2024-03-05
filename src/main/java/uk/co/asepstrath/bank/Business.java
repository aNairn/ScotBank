package uk.co.asepstrath.bank;

public class Business {

    String businessID;

    String businessName;

    String category;

    Boolean beenSanctioned;


    public Business(String businessID, String businessName, String category, boolean beenSanctioned){

        this.businessID = businessID;
        this.businessName = businessName;
        this.category = category;
        this.beenSanctioned = beenSanctioned;
    }

    String getBusinessID(){
        return businessID;
    }
    String getBusinessName(){
        return businessName;
    }

    String getCategory(){
        return category;
    }

    boolean getBeenSanctioned(){
        return beenSanctioned;
    }

    //void setBusinessID(String businessID){ this.businessID = businessID;}
    //void setCategory(String category){ this.category = category;}
    //void setBusinessName(String businessName){ this.businessName = businessName;}
    //void setBeenSanctioned(boolean beenSanctioned){ this.beenSanctioned = beenSanctioned;}

    @Override
    public String toString() {
        return "Business: " +
                "ID: " + businessID + ' ' +
                "Category: " + category + ' ' +
                "Business: " + businessName + ' ' +
                "Sanctioned?: " + beenSanctioned;
    }
}
